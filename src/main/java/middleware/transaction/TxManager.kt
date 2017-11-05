package middleware.transaction

import common.Logger
import common.RemoteConcurrentResourceManager
import common.Resource
import middleware.MiddlewareResourceManager
import middleware.MiddlewareStatistics
import middleware.lockManager.DeadlockException
import java.rmi.RemoteException
import kotlin.concurrent.timerTask

class TxManager {

    private var concurrentRM: MiddlewareResourceManager
    private var transactionCounter: Int = 0
    private var liveTransaction: HashMap<Int, Transaction> = HashMap()
    private var rm: MiddlewareResourceManager

    private var ttl: Long

    constructor(rm: MiddlewareResourceManager, ttl: Long) {
        this.concurrentRM = rm
        this.rm = rm
        this.ttl = ttl
    }


    @Synchronized
    fun enlist(txId: Int, resource: Resource): Boolean {
        var transaction = liveTransaction.get(txId)
        return if (transaction != null) {

            when (resource) {
                Resource.CAR -> transaction.setInvolved(resource, rm.carRM)
                Resource.CUSTOMER -> transaction.setInvolved(resource, rm.customerRM)
                Resource.FLIGHT -> transaction.setInvolved(resource, rm.flightRM)
                Resource.ROOM -> transaction.setInvolved(resource, rm.roomRM)
            }

            true
        } else {
            Logger.print().warning("Setting enlist on unknown transaction with id " + txId, "TxManager")
            false
        }
    }

    @Synchronized
    private fun commitTransaction(transaction: Transaction) {
        val start =  System.currentTimeMillis()
        transaction.commit()
        liveTransaction.remove(transaction.id)
        Logger.print().info("Transaction " + transaction.id + " committed", "TxManager")
        MiddlewareStatistics.instance.averageCommitTime.addValue((System.currentTimeMillis() - start).toDouble())
        MiddlewareStatistics.instance.transactionCommitted++
    }

    @Synchronized
    private fun abortTransaction(transaction: Transaction) {
        val start =  System.currentTimeMillis()
        transaction.abort()
        liveTransaction.remove(transaction.id)
        Logger.print().warning("Transaction " + transaction.id + " aborted", "TxManager")
        MiddlewareStatistics.instance.averageAbortTime.addValue((System.currentTimeMillis() - start).toDouble())
        MiddlewareStatistics.instance.transactionAborted++
    }

    @Synchronized
    private fun newTransaction(): Transaction {
        var transactionId = transactionCounter++
        var transaction = Transaction(transactionId, timerTask {
            Logger.print().info("Timeout", "Transaction:" + transactionId)
            abortTransaction(transactionId)
        }, ttl)
        liveTransaction.put(transactionId, transaction)
        Logger.print().info("Transaction " + transaction.id + " started", "TxManager")
        return transaction
    }

    fun startNewTransaction(): Int {
        return newTransaction().id
    }

    @Synchronized
    fun abortTransaction(txId: Int): Boolean {
        val transaction = liveTransaction[txId]
        return if (transaction != null) {
            abortTransaction(transaction)
            true
        } else false
    }

    @Synchronized
    fun commitTransaction(txId: Int): Boolean {
        val transaction = liveTransaction[txId]
        return if (transaction != null) {
            commitTransaction(transaction)
            true
        } else false
    }


    @Synchronized
    fun transactionExists(txId: Int): Boolean {
        return liveTransaction.containsKey(txId)
    }

    fun runInTransaction(body: TransactionBody<MiddlewareResourceManager, Int, TransactionResult, () -> Unit, TransactionResult>): TransactionResult {

        var transaction = newTransaction()

        try {

            var result = body.apply(concurrentRM, transaction.id, TransactionResult(TransactionStatus.OK), {
                abortTransaction(transaction)
                throw TransactionAbortedException("Transaction aborted")
            })

            commitTransaction(transaction)

            return result

        } catch (e: DeadlockException) {
            Logger.print().error(e.message, "TxManager")
            abortTransaction(transaction)
            return TransactionResult(TransactionStatus.ABORT)
        } catch (e: RemoteException) {
            Logger.print().error(e.message, "TxManager")
            abortTransaction(transaction)
            return TransactionResult(TransactionStatus.ABORT)
        } catch (e: TransactionAbortedException) {
            Logger.print().warning(e.message, "TxManager")
            return TransactionResult(TransactionStatus.ABORT)
        } catch (e: InvalidTransactionException) {
            Logger.print().warning(e.message, "TxManager")
            return TransactionResult(TransactionStatus.ABORT)
        }
    }
}

