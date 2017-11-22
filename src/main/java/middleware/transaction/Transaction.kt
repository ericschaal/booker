package middleware.transaction

import common.io.Logger
import common.resource.EndPointResourceManager
import common.resource.Resource
import middleware.lockManager.LockManager
import java.io.Serializable
import java.util.*


class Transaction : Serializable {

    var id: Int
    private var involved: Array<EndPointResourceManager?> = Array(4, { null })
    private var timer: Timer


    constructor(txId: Int, timerTask: TimerTask, ttl: Long) {
        id = txId
        timer = Timer()
        timer.schedule(timerTask, ttl)
        Logger.print().info("Alive", "Transaction:" + id)
    }

    fun setInvolved(resource: Resource, revertibleResourceManager: EndPointResourceManager) {
        if (involved[resource.ordinal] == null) {
            revertibleResourceManager.newTransaction(id)
            involved[resource.ordinal] = revertibleResourceManager
            Logger.print().info(resource.name + " is involved. Sending create.", "Transaction:" + id)
        }
    }

    fun commitAndUnlock() {
        try {

            Logger.print().info("Starting 2PL.", "Transaction:" + id)
            Logger.print().info("Sending VOTE_REQ", "[2PC]Transaction:" + id)

            val voteResult = involved.fold(true) { acc, element ->
                if (element != null) {
                    acc && element.voteRequest(id)
                } else true
            }

            Logger.print().info("Vote Result is " + voteResult, "[2PC]Transaction:" + id)

            if (voteResult) {
                commit()
            } else {
                abort()
            }


        } finally {
            LockManager.get().UnlockAll(id)

            timer.cancel()
            timer.purge()
        }
    }


    fun abortAndUnlock() {
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

    private fun commit() {

        involved.forEach {
            it?.commitTransaction(id)
        }
        Logger.print().info("Commit sent to all involved RMs", "Transaction:" + id)

    }

    private fun abort() { //TODO abort procedure.

        involved.forEach {
            it?.abortTransaction(id)
        }
        Logger.print().info("Abort sent", "Transaction:" + id)

    }


}