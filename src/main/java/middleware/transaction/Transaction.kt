package middleware.transaction

import common.Resource
import middleware.lockManager.LockManager
import java.util.*
import kotlin.concurrent.timerTask


class Transaction {

    var id : Int
    private var involved: Array<Boolean> = Array(4, { false })
    private var timer: Timer


    constructor(txId: Int, ttl: Long) {
        id = txId
        timer = Timer()
        timer.schedule(timerTask {

        }, ttl)
    }

    fun isInvolved(resource: Resource) {
        involved[resource.ordinal] = true
    }

    fun commit() {
        LockManager.get().UnlockAll(id)

        timer.cancel()
        timer.purge()
    }

    @Throws(TransactionAbortedException::class)
    fun abort(txManager: TxManager) : Boolean {
        return true
    }



}