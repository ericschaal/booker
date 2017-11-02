package middleware.transaction

import common.Logger
import common.RemoteRevertibleResourceManager
import common.Resource
import middleware.lockManager.LockManager
import java.io.Serializable
import java.util.*
import kotlin.concurrent.timerTask


class Transaction : Serializable {

    var id : Int
    private var involved: Array<RemoteRevertibleResourceManager?> = Array(4, { null })
    private var timer: Timer


    constructor(txId: Int, ttl: Long) {
        id = txId
        timer = Timer()
        timer.schedule(timerTask {
            Logger.print().info("Timeout", "Transaction:" + id)
            abort()
        }, ttl)
        Logger.print().info("Alive", "Transaction:" + id)
    }

    fun setInvolved(resource: Resource, revertibleResourceManager: RemoteRevertibleResourceManager) {
        if (involved[resource.ordinal] == null) {
            revertibleResourceManager.newTransaction(id)
            involved[resource.ordinal] = revertibleResourceManager
            Logger.print().info(resource.name + " is involved. Sending create.", "Transaction:" + id)
        }
    }

    fun commit() {

        involved.forEach {
            it?.commitTransaction(id)
        }

        LockManager.get().UnlockAll(id)

        timer.cancel()
        timer.purge()


        Logger.print().info("Commit sent", "Transaction:" + id)

    }

    @Throws(TransactionAbortedException::class)
    fun abort() { //TODO abort procedure.


        involved.forEach {
            it?.abortTransaction(id)
        }

        timer.cancel()
        timer.purge()

        Logger.print().info("Abort sent", "Transaction:" + id)
    }



}