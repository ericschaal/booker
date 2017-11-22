package resourceManager.storage;

import com.esotericsoftware.kryo.Kryo;
import common.hashtable.PersistentRMHashTable;
import common.hashtable.RMHashtable;
import common.hashtable.TxLocalRMHashTable;
import common.io.Logger;
import common.resource.RMItem;
import resourceManager.io.RMIOManager;
import resourceManager.io.RMIOManagerException;

import java.io.Serializable;
import java.util.Hashtable;

public class Database implements Serializable {

    private static Database db = new Database();

    private Hashtable<Integer, TxLocalRMHashTable> txLocal = new Hashtable<>();

    private String master;

    private PersistentRMHashTable htOne;
    private PersistentRMHashTable htTwo;

    private Kryo kryo = new Kryo();


    private Database() {
        try {
            if (RMIOManager.getInstance().isMasterRecordAvailable()) {
                master = RMIOManager.getInstance().readMasterRecord();
            } else {
                master = RMIOManager.DB_ONE;
            }
            htOne = new PersistentRMHashTable(RMIOManager.DB_ONE);
            htTwo = new PersistentRMHashTable(RMIOManager.DB_TWO);
        } catch (RMIOManagerException e) {
            Logger.print().error("File System Error. Please delete all files and try again");
            throw new RuntimeException();
        }
    }

    public synchronized void newLocalCopy(int txId) {
        txLocal.put(txId, new TxLocalRMHashTable(kryo.copy(getMaster().getHT())));
    }

    /**
     * Side effect: removes local copy.
     * @param txId
     * @throws DatabaseException
     */
    public synchronized void writeBackLocalCopyToDiskAndRemove(int txId) throws DatabaseException {
        if (!txLocal.containsKey(txId)) {
            throw new DatabaseException("No tx local copy with id: " + txId);
        } else {
            TxLocalRMHashTable local = txLocal.get(txId);
            for (Object raw : local.getModifiedKeys()) {
                String key = String.valueOf(raw);
                if (local.getDb().contains(key)) {
                    getSlave().put(key, local.getDb().get(key));
                } else {
                    getSlave().remove(key);
                }
            }
            removeTxLocalCopy(txId);
        }
    }

    public void removeTxLocalCopy(int txId) throws DatabaseException {
        if (!txLocal.containsKey(txId)) {
            throw new DatabaseException("No tx local copy with id: " + txId);
        } else {
            txLocal.remove(txId);
        }
    }

    public synchronized void swapMaster() throws DatabaseException {
        try {
            if (master.equals(RMIOManager.DB_ONE)) {
                RMIOManager.getInstance().setMaster(RMIOManager.DB_TWO);
                master = RMIOManager.DB_TWO;
            } else {
                RMIOManager.getInstance().setMaster(RMIOManager.DB_ONE);
                master = RMIOManager.DB_ONE;
            }
        } catch (Exception e) {
            Logger.print().error(e.getMessage(), "Database");
            throw new DatabaseException("Failed to swap master");
        }
    }


    private PersistentRMHashTable getSlave() {
        if (master.equals(RMIOManager.DB_ONE))
            return htTwo;
        else return htOne;
    }

    private PersistentRMHashTable getMaster() {
        if (master.equals(RMIOManager.DB_ONE))
            return htOne;
        else return htTwo;
    }


    public synchronized RMHashtable cloneDb(boolean master) {
        if (master)
            return kryo.copy(getMaster().getHT());
        else
            return kryo.copy(getSlave().getHT());
    }

    // Reads a data item
    public synchronized RMItem readData(int id, String key) {
        if (id >= 0) {
            return (RMItem) txLocal.get(id).get(key);
        } else {
            return (RMItem) getMaster().get(key);
        }

    }

    // Writes a data item
    public synchronized void writeData(int id, String key, RMItem value) {
        if (id >= 0) {
            txLocal.get(id).put(key, value);
        } else {
            getMaster().put(key, value);
        }
    }

    // Remove the item out of storage
    public synchronized RMItem removeData(int id, String key) {
        if (id >= 0) {
            return (RMItem) txLocal.get(id).remove(key);
        } else {
            return (RMItem) getMaster().remove(key);
        }
    }

    public synchronized void revertDb(RMHashtable oldVersion, boolean master) {
        if (master) {
            getMaster().setHT(oldVersion);
        } else {
            getSlave().setHT(oldVersion);
        }
    }


    public static Database getActiveDb() {
        return db;
    }

}
