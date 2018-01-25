package middleware.io;

import common.io.FileManager;
import common.io.Logger;
import middleware.tx.Transaction;
import middleware.tx.persistent.TxRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

public class MiddlewareIOManager {

    private static MiddlewareIOManager instance;

    private static String PREFIX = "middleware_data/";
    private static final String TX_RECORD = "txRecord";
    public static final String TX_LIVE = "txLive";

    private FileManager fm = new FileManager();


    private MiddlewareIOManager(String dataDir) throws IOException {
        if (dataDir.charAt(dataDir.length() - 1) != '/')
            throw new IllegalArgumentException("Data directory path not ending with '/' ");
        this.PREFIX = dataDir + this.PREFIX;

        if (!Files.isDirectory(Paths.get(PREFIX))) {
            Files.createDirectories(Paths.get(PREFIX));
        }

        fm.openFile(TX_RECORD, PREFIX + "/" + TX_RECORD, false);
        fm.openFile(TX_LIVE, PREFIX + "/" + TX_LIVE, false);


    }

    public static void init(String dataDir) throws IOException {
        if (instance == null) {
            instance = new MiddlewareIOManager(dataDir);
        } else {
            throw new RuntimeException("MiddlewareIOManager already initialized");
        }
    }

    public static MiddlewareIOManager getInstance() {
        return instance;
    }

    public TxRecord readTxRecord() throws MiddlewareIOManagerException {
        try {
            return (TxRecord) fm.readObject(TX_RECORD);
        } catch (IOException e) {
            Logger.print().warning("Could not read Transaction Record", "MiddlewareIOManager");
            throw new MiddlewareIOManagerException("Failed to read Transaction Record ");
        } catch (ClassNotFoundException e) {
            Logger.print().warning("Could not read Transaction Record", "MiddlewareIOManager");
            throw new MiddlewareIOManagerException("Failed to read Transaction Record ");
        }
    }

    public boolean writeTxRecord(TxRecord txRecord) throws MiddlewareIOManagerException {
        try {
            return fm.writeObject(TX_RECORD, txRecord);
        } catch (IOException e) {
            Logger.print().error(e.getMessage(), "MiddlewareIOManager");
            e.printStackTrace();
            throw new MiddlewareIOManagerException("Failed to write TX Record changes.");
        }
    }

    public Hashtable<Integer, Transaction> readDB() throws MiddlewareIOManagerException {
        try {
            return (Hashtable<Integer, Transaction>) fm.readObject(TX_LIVE);
        } catch (IOException e) {
            Logger.print().warning("Could not read stored database", "MiddlewareIOManager");
            throw new MiddlewareIOManagerException("Failed to read Database " + TX_LIVE);
        } catch (ClassNotFoundException e) {
            Logger.print().error(e.getMessage(), "MiddlewareIOManager");
            throw new MiddlewareIOManagerException("Failed to read Database " + TX_LIVE);
        }
    }

    public boolean writeDatabase(Hashtable<Integer, Transaction> db) throws MiddlewareIOManagerException {
        try {
            return fm.writeObject(TX_LIVE, db);
        } catch (IOException e) {
            Logger.print().error(e.getMessage(), "MiddlewareIOManager");
            e.printStackTrace();
            throw new MiddlewareIOManagerException("Failed to write db changes.");
        }
    }

}
