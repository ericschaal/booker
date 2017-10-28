package middleware.transaction

import common.Logger
import middleware.lockManager.DeadlockException
import middleware.lockManager.LockManager
import middleware.MiddlewareConcurrentResourceManager
import java.rmi.RemoteException

class TxManager {

    private var rm: MiddlewareConcurrentResourceManager
    private var transactionCounter: Int = 0
    private var lockManager: LockManager = LockManager()

    constructor(rm: MiddlewareConcurrentResourceManager) {
        this.rm = rm
    }

    private fun abort(txId: Int) : Boolean {
        //TODO force transaction to abort
        // Ask all involved RM to revert transaction changes.
        println("Force abort transaction " + txId)
        throw TransactionAborted(txId.toString())
    }

    open fun runInTransaction(body: TransactionBody<MiddlewareConcurrentResourceManager, Int, TransactionResult, () -> Unit, TransactionResult>) : TransactionResult {
        var transactionId = transactionCounter++
        return try {
            body.apply(rm, transactionId, TransactionResult(TransactionStatus.OK), { abort(transactionId)})
        } catch (e: DeadlockException) {
            Logger.print().error(e.message, "TxManager")
            TransactionResult(TransactionStatus.ABORT)
        } catch (e: RemoteException) {
            Logger.print().error(e.message, "TxManager")
            TransactionResult(TransactionStatus.ABORT)
        } catch (e: TransactionAborted) {
            Logger.print().error(e.message, "TxManager")
            TransactionResult(TransactionStatus.ABORT)
        }
        finally {
            lockManager.UnlockAll(transactionId)
            //here commit protocol.
        }
    }
}

