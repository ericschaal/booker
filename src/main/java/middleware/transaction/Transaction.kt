package middleware.transaction

import common.io.Logger
import common.resource.RemoteRevertibleResourceManager
import common.resource.Resource
import middleware.lockManager.LockManager
import java.io.Serializable
import java.util.*


class Transaction : Serializable {

    var id: Int
    private var involved: Array<RemoteRevertibleResourceManager?> = Array(4, { null })
    private var timer: Timer


    constructor(txId: Int, timerTask: TimerTask, ttl: Long) {
        id = txId
        timer = Timer()
        timer.schedule(timerTask, ttl)
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
        try {
            involved.forEach {
                it?.commitTransaction(id)
            }

            Logger.print().info("Commit sent", "Transaction:" + id)
        } finally {
            LockManager.get().UnlockAll(id)

            timer.cancel()
            timer.purge()
        }


    }

    fun abort() { //TODO abort procedure.

        try {

            involved.forEach {
                it?.abortTransaction(id)
            }

            Logger.print().info("Abort sent", "Transaction:" + id)

        } finally {
            LockManager.get().UnlockAll(id)

            timer.cancel()
            timer.purge()
        }

    }


}