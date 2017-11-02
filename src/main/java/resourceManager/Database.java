package resourceManager;

import com.esotericsoftware.kryo.Kryo;
import common.hashtable.RMHashtable;
import common.resource.RMItem;

import java.io.Serializable;

public class Database implements Serializable {

    private static Database db = new Database();


    private RMHashtable m_itemHT = new RMHashtable();
    private Kryo kryo = new Kryo();


    private Database() {
    }

    public synchronized RMHashtable cloneDb() {
        return kryo.copy(m_itemHT);
    }

    // Reads a data item
    public synchronized RMItem readData(int id, String key) {
        return (RMItem) m_itemHT.get(key);
    }

    // Writes a data item
    public synchronized void writeData(int id, String key, RMItem value) {
        m_itemHT.put(key, value);
    }

    // Remove the item out of storage
    public synchronized RMItem removeData(int id, String key) {
        return (RMItem) m_itemHT.remove(key);
    }

    public synchronized void revertDb(RMHashtable oldVersion) {
        m_itemHT = oldVersion;
    }


    public static Database getActiveDb() {
        return db;
    }

}
