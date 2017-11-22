package middleware.io;

import common.io.FileManager;

public class MiddlewareIOManager {

    private static String PREFIX = "middleware_data/";
    private static final String TX_COMMIT = "tx_commit.log";
    private static final String TX_ABORT = "tx_abort.log";

    private FileManager fm = new FileManager();

    public MiddlewareIOManager() {
        this.PREFIX = "./" + this.PREFIX;
    }

    public MiddlewareIOManager(String dataDir) {
        if (dataDir.charAt(dataDir.length() - 1) != '/')
            throw new IllegalArgumentException("Data directory path not ending with '/' ");
        this.PREFIX = dataDir + this.PREFIX;
    }

}
