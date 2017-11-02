package middleware.transaction

import common.Logger
import common.RemoteConcurrentResourceManager
import common.Resource
import middleware.MiddlewareResourceManager
import middleware.lockManager.DeadlockException
import java.rmi.RemoteException

class TxManager {

    private var concurrentRM: MiddlewareConcurrentResourceManager
    private var transactionCounter: Int = 0
    private var liveTransaction: HashMap<Int, Transaction> = HashMap()
    private var rm: MiddlewareResourceManager

    private var ttl: Long

    constructor(rm: MiddlewareResourceManager, ttl: Long) {
        this.concurrentRM = MiddlewareConcurrentResourceManager(rm, this)
        this.rm = rm
        this.ttl = ttl
    }


    @Synchronized
    fun enlist(txId: Int, resource: Resource): Boolean {
        var transaction = liveTransaction.get(txId)
        return if (transaction != null) {

            when(resource) {
                Resource.CAR-> transaction.setInvolved(resource, rm.carRM)
                Resource.CUSTOMER-> transaction.setInvolved(resource, rm.customerRM)
                Resource.FLIGHT->transaction.setInvolved(resource, rm.flightRM)
                Resource.ROOM->transaction.setInvolved(resource, rm.roomRM)
            }

            true
        } else {
            Logger.print().warning("Setting enlist on unknown transaction with id " + txId, "TxManager")
            false
        }
    }

    @Synchronized
    private fun commitTransaction(transaction: Transaction) {
        transaction.commit()
        liveTransaction.remove(transaction.id)
        Logger.print().info("Transaction " + transaction.id + " committed", "TxManager")
    }

    @Synchronized
    private fun newTransaction(): Transaction {
        var transactionId = transactionCounter++
        var transaction = Transaction(transactionId, ttl)
        liveTransaction.put(transactionId, transaction)
        Logger.print().info("Transaction " + transaction.id + " started", "TxManager")
        return transaction
    }

    fun runInTransaction(body: TransactionBody<RemoteConcurrentResourceManager, Int, TransactionResult, () -> Unit, TransactionResult>): TransactionResult {

        var transaction = newTransaction()

        try {

            var result = body.apply(concurrentRM, transaction.id, TransactionResult(TransactionStatus.OK), {
                transaction.abort()
                throw TransactionAbortedException("Transaction aborted")
            })
            commitTransaction(transaction)

            return result

        } catch (e: DeadlockException) {
            Logger.print().error(e.message, "TxManager")
            transaction.abort()
            liveTransaction.remove(transaction.id)
            return TransactionResult(TransactionStatus.ABORT)
        } catch (e: RemoteException) {
            Logger.print().error(e.message, "TxManager")
            transaction.abort()
            liveTransaction.remove(transaction.id)
            return TransactionResult(TransactionStatus.ABORT)
        } catch (e: TransactionAbortedException) {
            Logger.print().error(e.message, "TxManager")
            liveTransaction.remove(transaction.id)
            return TransactionResult(TransactionStatus.ABORT)
        }
    }
}

