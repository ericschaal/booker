package middleware.io;

import common.io.FileManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MiddlewareIOManager {

    private static MiddlewareIOManager instance;

    private static String PREFIX = "middleware_data/";
    private static final String TX_COMMIT = "tx_commit.log";
    private static final String TX_ABORT = "tx_abort.log";

    private FileManager fm = new FileManager();


    private MiddlewareIOManager(String dataDir) throws IOException {
        if (dataDir.charAt(dataDir.length() - 1) != '/')
            throw new IllegalArgumentException("Data directory path not ending with '/' ");
        this.PREFIX = dataDir + this.PREFIX;

        if (!Files.isDirectory(Paths.get(PREFIX))) {
            Files.createDirectories(Paths.get(PREFIX));
        }

    }

    public static void init(String dataDir) throws IOException {
        if (instance == null) {
            instance = new MiddlewareIOManager(dataDir);
        } else {
            throw new RuntimeException("MiddlewareIOManager already initialized");
        }
    }

}
