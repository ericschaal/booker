package middleware.tx

import common.io.Logger
import common.resource.EndPointResourceManager
import common.resource.Resource
import middleware.lockManager.LockManager
import middleware.rmi.RMIManager
import middleware.tx.error.TransactionAbortedException
import middleware.tx.persistent.TxRecordManager
import java.io.Serializable
import java.rmi.RemoteException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet


class Transaction : Serializable {

    var id: Int
    private var involved: HashSet<Resource> = HashSet()
    @Transient private var timer: Timer
    private var recordManager: TxRecordManager;


    companion object {
        fun sendCommit(txId: Int, rms: ArrayList<EndPointResourceManager>) {
            rms.forEach {
                it.commitTransaction(txId);
            }
        }
        fun sendAbort(txId: Int, rms: ArrayList<EndPointResourceManager>) {
            rms.forEach {
                it.abortTransaction(txId);
            }
        }
    }

    constructor(txId: Int, timerTask: TimerTask, ttl: Long, recordManager: TxRecordManager) {
        this.id = txId
        this.recordManager = recordManager;
        this.timer = Timer()
        this.timer.schedule(timerTask, ttl)
        Logger.print().info("Alive", "Transaction:" + id)
    }

    fun setInvolved(resource: Resource) {
            involved.add(resource);
            RMIManager.getRmForResource(resource).newTransaction(id)
            Logger.print().info(resource.name + " is involved. Sending create.", "Transaction:" + id)
    }

    @Throws(TransactionAbortedException::class)
    fun commitAndUnlock(prepare: Boolean) {
        var tries = 0
        while (true) {
            try {
                Logger.print().info("Setting tx record to START", "[2PC]Transaction:" + id)
                recordManager.setStart(id)

                var voteResult = true

                Logger.print().info("Sending VOTE_REQ", "[2PC]Transaction:" + id)

                if (prepare) {
                    involved.forEach({
                        if (!RMIManager.getRmForResource(it).prepare(id))
                            voteResult = false
                    })
                }
                Logger.print().info("Vote Result is " + voteResult, "[2PC]Transaction:" + id)

                if (voteResult) {
                    Logger.print().info("Setting tx record to COMMIT", "[2PC]Transaction:" + id)
                    recordManager.setCommit(id)
                    commit()
                } else {
                    Logger.print().info("Setting tx record to ABORT", "[2PC]Transaction:" + id)
                    recordManager.setAbort(id)
                    abort()
                    throw TransactionAbortedException("Vote failed");
                }
                break
            } catch (e: RemoteException) {
                tries++
                if (tries > 5) break
                Logger.print().error("Can't connect to RM...Retrying", "Tx" + id)
                Thread.sleep(1000 * 8)
            } finally {
                LockManager.get().UnlockAll(id)

                if (timer != null) {
                    timer.cancel()
                    timer.purge()
                }
            }
        }
    }


    fun abortAndUnlock(prepare: Boolean) {
        var tries = 0
        while (true) {
            try {
                Logger.print().info("Setting tx record to START", "[2PC]Transaction:" + id)
                recordManager.setStart(id)

                var voteResult = true

                Logger.print().info("Sending VOTE_REQ", "[2PC]Transaction:" + id)

                if (prepare) {
                    involved.forEach({
                        if (!RMIManager.getRmForResource(it).prepare(id))
                            voteResult = false
                    })
                }
                Logger.print().info("Vote Result is " + voteResult, "[2PC]Transaction:" + id)

                if (voteResult) {
                    Logger.print().info("Setting tx record to ABORT", "[2PC]Transaction:" + id)
                    recordManager.setAbort(id)
                    abort()
                } else {
                    Logger.print().info("Setting tx record to COMMIT", "[2PC]Transaction:" + id)
                    recordManager.setCommit(id)
                    commit()
                }
                break
            } catch (e: RemoteException) {
                tries++
                if (tries > 5) break
                Logger.print().error("Can't connect to RM...Retrying", "Tx" + id)
                Thread.sleep(1000 * 8)
            } finally {
                LockManager.get().UnlockAll(id)
                if (timer != null) {
                    timer.cancel()
                    timer.purge()
                }
            }
        }
    }

    private fun commit() {
        var tries = 0;
        while(true) {
            try {
                involved.forEach {
                    RMIManager.getRmForResource(it).commitTransaction(id)
                }
                Logger.print().info("Commit sent to all involved RMs", "Transaction:" + id)
                break
            } catch (e: RemoteException) {
                tries++
                if (tries > 5) {
                    Logger.print().error("Timeout! Transaction will be aborted at RM restart.")
                    break
                }
                Logger.print().error("Can't connect to RM...Retrying", "Tx" + id)
                Thread.sleep(1000 * 8)
            }
        }

    }

    private fun abort() { //TODO abort procedure.
        var tries = 0;
        while (true) {
            try {
                involved.forEach {
                    RMIManager.getRmForResource(it).abortTransaction(id)
                }
                Logger.print().info("Abort sent", "Transaction:" + id)
                break
            } catch (e: RemoteException) {
                tries++
                if (tries > 5) {
                    Logger.print().error("Timeout! Transaction will be aborted at RM restart.");
                    break
                }
                Logger.print().error("Can't connect to RM...Retrying", "Tx" + id)
                Thread.sleep(1000 * 8)
            }
        }
    }
}