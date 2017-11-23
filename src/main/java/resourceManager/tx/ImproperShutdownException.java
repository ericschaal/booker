package resourceManager.tx;

public class ImproperShutdownException extends Exception {

    private final int[] pendingTransactions;

    public ImproperShutdownException(String message, int[] pendingTransactions) {
        super(message);
        this.pendingTransactions = pendingTransactions;
    }

    public ImproperShutdownException(int[] pendingTransactions) {
        this.pendingTransactions = pendingTransactions;
    }

    public int[] getPendingTransactions() {
        return pendingTransactions;
    }
}
