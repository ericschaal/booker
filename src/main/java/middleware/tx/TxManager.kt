package middleware.tx

import common.io.Logger
import common.resource.EndPointResourceManager
import common.resource.Resource
import common.tx.error.ImproperShutdownException
import common.tx.error.UndecidableStateException
import common.tx.model.TxRecoveryAction
import middleware.MiddlewareResourceManager
import middleware.io.MiddlewareIOManager.TX_LIVE
import middleware.io.MiddlewareIOManagerException
import middleware.perf.MiddlewareStatistics
import middleware.lockManager.DeadlockException
import middleware.storage.PersistentMiddlewareHashTable
import middleware.tx.error.InvalidTransactionException
import middleware.tx.error.TransactionAbortedException
import middleware.tx.model.TransactionBody
import middleware.tx.model.TransactionResult
import middleware.tx.model.TransactionStatus
import middleware.tx.persistent.TxRecordManager
import java.rmi.RemoteException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

class TxManager {

    private var concurrentRM: MiddlewareResourceManager
    @Volatile private var transactionCounter: Int = 0
    private var recordManager: TxRecordManager
    private var liveTransaction: Hashtable<Int, Transaction> = Hashtable()
    private var rm: MiddlewareResourceManager

    private var ttl: Long

    constructor(rm: MiddlewareResourceManager, ttl: Long) {
        this.concurrentRM = rm
        this.rm = rm
        this.ttl = ttl
        this.recordManager = TxRecordManager()
    }


    @Synchronized
    fun enlist(txId: Int, resource: Resource): Boolean {
        var transaction = liveTransaction.get(txId)
        return if (transaction != null) {

            when (resource) {
                Resource.CAR -> transaction.setInvolved(resource)
                Resource.CUSTOMER -> transaction.setInvolved(resource)
                Resource.FLIGHT -> transaction.setInvolved(resource)
                Resource.ROOM -> transaction.setInvolved(resource)
            }

            true
        } else {
            Logger.print().warning("Setting enlist on unknown tx with id " + txId, "TxManager")
            false
        }
    }

    @Synchronized public fun healthCheck() {
        try {
            val pending = recordManager.healthCheck();

            if (pending.isNotEmpty()) {
                pending.forEach({ (txId, el) ->
                    val rms = ArrayList<EndPointResourceManager>();
                    rms.add(concurrentRM.roomRM)
                    rms.add(concurrentRM.customerRM)
                    rms.add(concurrentRM.carRM)
                    rms.add(concurrentRM.flightRM)

                    when (el) {
                        TxRecoveryAction.SEND_ABORT -> {
                            Logger.print().info("Tx $txId, need to SEND_ABORT", "TxManager")
                            Transaction.sendAbort(txId,rms)
                            recordManager.setAbort(txId)
                        }
                        TxRecoveryAction.SEND_COMMIT -> {
                            Logger.print().info("Tx $txId, need to SEND_COMMIT", "TxManager")
                            Transaction.sendCommit(txId,rms)
                            recordManager.setCommit(txId)
                        }
                        TxRecoveryAction.VOTE_REQ -> {
                            Logger.print().info("Tx $txId, need to SEND_ABORT", "TxManager")
                            Transaction.sendAbort(txId,rms)
                            recordManager.setAbort(txId)
                        }
                    }
                })
            }
        }
        catch (e: UndecidableStateException) {
            Logger.print().error("Undecidable transaction state. Aborting start.")
            e.printStackTrace()
        }
    }

    @Synchronized private fun startT2PC(txId: Int) {
        Logger.print().info("Starting 2PL...", "Transaction:" + txId)
        Logger.print().info("New tx record entry", "Transaction:" + txId)
        recordManager.newRecord(txId)
    }

    @Throws(TransactionAbortedException::class)
    @Synchronized
    private fun commitTransaction(transaction: Transaction, prepare: Boolean = true) {
        val start = System.currentTimeMillis()

        transaction.commitAndUnlock(prepare)
        liveTransaction.remove(transaction.id)

        Logger.print().info("Transaction " + transaction.id + " committed", "TxManager")

        MiddlewareStatistics.instance.averageCommitTime.addValue((System.currentTimeMillis() - start).toDouble())
        MiddlewareStatistics.instance.transactionCommitted++
    }

    @Synchronized
    private fun abortTransaction(transaction: Transaction, prepare: Boolean = true) {
        val start = System.currentTimeMillis()

        transaction.abortAndUnlock(prepare)
        liveTransaction.remove(transaction.id)

        Logger.print().warning("Transaction " + transaction.id + " aborted", "TxManager")

        MiddlewareStatistics.instance.averageAbortTime.addValue((System.currentTimeMillis() - start).toDouble())
        MiddlewareStatistics.instance.transactionAborted++
    }

    @Synchronized
    private fun newTransaction(): Transaction {
        var transactionId = transactionCounter++
        val transaction = Transaction(transactionId, timerTask {
            Logger.print().info("Timeout", "Transaction:" + transactionId)
            abortTransaction(transactionId)
        }, ttl, recordManager)
        liveTransaction.put(transactionId, transaction)
        Logger.print().info("Transaction " + transaction.id + " started", "TxManager")
        return transaction
    }

    fun startNewTransaction(): Int = newTransaction().id

    @Throws(RemoteException::class)
    @Synchronized
    fun abortTransaction(txId: Int, prepare: Boolean = true): Boolean {

        val transaction = liveTransaction[txId]


        return if (transaction != null) {
            if (prepare) startT2PC(txId)
            abortTransaction(transaction, prepare)
            true
        } else false
    }

    @Throws(RemoteException::class, TransactionAbortedException::class)
    @Synchronized
    fun commitTransaction(txId: Int, prepare: Boolean = true): Boolean {

        val transaction = liveTransaction[txId]

        return if (transaction != null) {
            if (prepare) startT2PC(txId)
            commitTransaction(transaction, prepare)
            true
        } else false
    }


    fun transactionExists(txId: Int): Boolean = liveTransaction.containsKey(txId)

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

