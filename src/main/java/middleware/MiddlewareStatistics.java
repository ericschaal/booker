package middleware;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class MiddlewareStatistics {

    public static MiddlewareStatistics instance = new MiddlewareStatistics();

    private DescriptiveStatistics averageReadLockGrant = new DescriptiveStatistics();
    private DescriptiveStatistics averageWriteLockGrant = new DescriptiveStatistics();
    private int deadlockCount = 0;

    private int transactionCommitted = 0;
    private int transactionAborted = 0;


    private DescriptiveStatistics averageCommitTime = new DescriptiveStatistics();
    private DescriptiveStatistics averageAbortTime = new DescriptiveStatistics();


    public DescriptiveStatistics getAverageReadLockGrant() {
        return averageReadLockGrant;
    }

    public DescriptiveStatistics getAverageWriteLockGrant() {
        return averageWriteLockGrant;
    }

    public int getDeadlockCount() {
        return deadlockCount;
    }

    public void setDeadlockCount(int deadlockCount) {
        this.deadlockCount = deadlockCount;
    }

    public int getTransactionExecuted() {
        return transactionCommitted + transactionAborted;
    }

    public int getTransactionCommitted() {
        return transactionCommitted;
    }

    public void setTransactionCommitted(int transactionCommitted) {
        this.transactionCommitted = transactionCommitted;
    }

    public int getTransactionAborted() {
        return transactionAborted;
    }

    public void setTransactionAborted(int transactionAborted) {
        this.transactionAborted = transactionAborted;
    }

    public DescriptiveStatistics getAverageCommitTime() {
        return averageCommitTime;
    }
    public DescriptiveStatistics getAverageAbortTime() {
        return averageAbortTime;
    }

}
