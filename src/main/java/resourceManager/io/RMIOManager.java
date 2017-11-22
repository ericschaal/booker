package resourceManager.io;

import common.hashtable.RMHashtable;
import common.io.FileManager;
import common.io.Logger;
import common.resource.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;

public class RMIOManager {

    private static RMIOManager instance;

    private static String PREFIX;
    private static final String MASTER = "master";

    public static final String DB_ONE = "dbOne";
    public static final String DB_TWO = "dbTwo";
    public static final String TX_RECORD = "txRecord";

    private final FileManager fm = new FileManager();

    private RMIOManager(Resource resource) throws IOException {
        this("./", resource);
    }

    private RMIOManager(String dataDir, Resource resource) throws IOException {
        if (dataDir.charAt(dataDir.length() - 1) != '/')
            throw new IllegalArgumentException("Data directory path not ending with '/' ");
        this.PREFIX = dataDir + "rm_"  + resource.name().toLowerCase();

        if (!Files.isDirectory(Paths.get(PREFIX))) {
            Files.createDirectories(Paths.get(PREFIX));
        }

        fm.openFile(MASTER, PREFIX + "/" + MASTER, false);
        fm.openFile(DB_ONE, PREFIX + "/" + DB_ONE, false);
        fm.openFile(DB_TWO, PREFIX + "/" + DB_TWO, false);
        fm.openFile(TX_RECORD, PREFIX + "/" + TX_RECORD, false);


    }

    public static void init(Resource resource) throws IOException {
        if (instance == null) {
            instance = new RMIOManager(resource);
        } else {
            throw new RuntimeException("RM IO Manager already initialized.");
        }
    }

    public static void init(String dataDir, Resource resource) throws IOException {
        if (instance == null) {
            instance = new RMIOManager(dataDir, resource);
        } else {
            throw new RuntimeException("RM IO Manager already initialized.");
        }
    }

    public static RMIOManager getInstance() {
        if (instance != null) {
            return instance;
        }
        throw new RuntimeException("RM IO Manager not initialized.");
    }

    public boolean isMasterRecordAvailable() throws RMIOManagerException {
        try {
            return !fm.read(MASTER).isEmpty();
        } catch (IOException e) {
            Logger.print().error(e.getMessage(), "RMIOManager");
            throw new RMIOManagerException("Failed to read master record.");
        }
    }

    public RMHashtable readDB(String id) throws RMIOManagerException {
        try {
            return (RMHashtable) fm.readObject(id);
        } catch (IOException e) {
            Logger.print().warning("Could not read stored database", "RMIOManager");
            throw new RMIOManagerException("Failed to read Database " + id);
        } catch (ClassNotFoundException e) {
            Logger.print().error(e.getMessage(), "RMIOManager");
            throw new RMIOManagerException("Failed to read Database " + id);
        }
    }

    public TxRecord readTxRecord() throws RMIOManagerException {
        try {
            return (TxRecord) fm.readObject(TX_RECORD);
        } catch (IOException e) {
            Logger.print().warning("Could not read Transaction Record", "RMIOManager");
            throw new RMIOManagerException("Failed to read Transaction Record ");
        } catch (ClassNotFoundException e) {
            Logger.print().warning("Could not read Transaction Record", "RMIOManager");
            throw new RMIOManagerException("Failed to read Transaction Record ");
        }
    }

//    public RMHashtable readMasterDB() throws RMIOManagerException {
//        try {
//            if (fm.read(MASTER).isEmpty()) {
//                Logger.print().warning("Master not defined.", "RMIOManager");
//                throw new RMIOManagerException("Master not defined");
//            }
//            return readDB((String) fm.read(MASTER).get(0));
//        } catch (IOException e) {
//            Logger.print().error(e.getMessage(), "RMIOManager");
//            throw new RMIOManagerException("Failed to read Database ");
//        }
//    }
//
//    public RMHashtable readSlaveDB() throws RMIOManagerException {
//        try {
//            if (fm.read(MASTER).isEmpty()) {
//                Logger.print().warning("Slave not defined.","RMIOManager");
//                throw new RMIOManagerException("Slave not defined");
//            }
//            if (fm.read(MASTER).get(0).equals(DB_ONE)) {
//                return readDB(DB_TWO);
//            } else {
//                return readDB(DB_ONE);
//            }
//        } catch (IOException e) {
//            Logger.print().error(e.getMessage(), "RMIOManager");
//            throw new RMIOManagerException("Failed to read Database ");
//        }
//    }

    public String readMasterRecord() throws RMIOManagerException {
        try {
            if (fm.read(MASTER).isEmpty()) {
                Logger.print().warning("Master record empty.", "RMIOManager");
                throw new RMIOManagerException("Master Record empty");
            }
            return (String) fm.read(MASTER).get(0);
        } catch (IOException e ) {
            Logger.print().error(e.getMessage(), "RMIOManager");
            throw new RMIOManagerException("Failed to read Master Record ");
        }

    }

    public boolean writeDatabase(String id, RMHashtable db) throws RMIOManagerException {
        try {
            return fm.writeObject(id, db);
        } catch (IOException e) {
            Logger.print().error(e.getMessage(), "RMIOManager");
            throw new RMIOManagerException("Failed to write db changes.");
        }
    }

    public boolean writeTxRecord(TxRecord txRecord) throws RMIOManagerException {
        try {
            return fm.writeObject(TX_RECORD, txRecord);
        } catch (IOException e) {
            Logger.print().error(e.getMessage(), "RMIOManager");
            throw new RMIOManagerException("Failed to write TX Record changes.");
        }
    }

    public boolean setMaster(String id) throws RMIOManagerException {
        try {
            return fm.write(MASTER, id);
        } catch (IOException e) {
            Logger.print().error(e.getMessage(), "RMIOManager");
            throw new RMIOManagerException("Failed to write master change.");
        }
    }



}
