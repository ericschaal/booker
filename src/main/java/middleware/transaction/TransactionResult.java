package middleware.transaction;


public class TransactionResult {

    private TransactionStatus status;
    private String result;
    private String reason;

    public TransactionResult(TransactionStatus status) {
        this.status = status;
    }

    public TransactionResult setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public TransactionResult setStatus(TransactionStatus status) {
        this.status = status;
        return this;
    }

    public TransactionResult setResult(boolean result) {
        this.result = String.valueOf(result);
        return this;
    }
    public TransactionResult setResult(int result) {
        this.result = String.valueOf(result);
        return this;
    }
    public TransactionResult setResult(String result) {
        this.result = result;
        return this;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public String getStringResult() {
        return result;
    }
    public boolean getBooleanResult() {
        return Boolean.parseBoolean(result);
    }
    public int getIntResult() throws NumberFormatException {
        return Integer.parseInt(result);
    }

    public String getReason() {
        return reason;
    }
}
