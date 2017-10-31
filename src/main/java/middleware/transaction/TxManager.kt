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

    private var ttl: Long

    constructor(rm: MiddlewareResourceManager, ttl: Long) {
        this.concurrentRM = MiddlewareConcurrentResourceManager(rm, this)
        this.ttl = ttl
    }


    private fun abort(txId: Int): Boolean {
        //TODO force transaction to abort
        // Ask all involved RM to revert transaction changes.
        println("Force abort transaction " + txId)
        throw TransactionAbortedException(txId.toString())
    }

    @Synchronized
    fun enlist(txId: Int, resource: Resource) : Boolean {
        var transaction = liveTransaction.get(txId)
        return if (transaction != null) {
            transaction.isInvolved(resource)
            true
        } else false
    }

    @Synchronized
    private fun commitTransaction(transaction: Transaction) {
        transaction.commit()
        liveTransaction.remove(transaction.id)
    }

    @Synchronized
    private fun startTransaction() : Transaction {
        var transactionId = transactionCounter++
        var transaction = Transaction(transactionId, ttl)

        liveTransaction.put(transactionId, transaction)

        return transaction
    }

    fun runInTransaction(body: TransactionBody<RemoteConcurrentResourceManager, Int, TransactionResult, () -> Unit, TransactionResult>): TransactionResult {

        var transaction = startTransaction()

        return try {

            body.apply(concurrentRM, transaction.id, TransactionResult(TransactionStatus.OK), { transaction.abort(this) })

        } catch (e: DeadlockException) {
            Logger.print().error(e.message, "TxManager")
            TransactionResult(TransactionStatus.ABORT)
        } catch (e: RemoteException) {
            Logger.print().error(e.message, "TxManager")
            TransactionResult(TransactionStatus.ABORT)
        } catch (e: TransactionAbortedException) {
            Logger.print().error(e.message, "TxManager")
            TransactionResult(TransactionStatus.ABORT)
        } finally {
            commitTransaction(transaction)
        }
    }
}

