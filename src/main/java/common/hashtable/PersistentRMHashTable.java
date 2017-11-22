package common.hashtable;

import common.io.Logger;
import resourceManager.io.RMIOManager;
import resourceManager.io.RMIOManagerException;

public class PersistentRMHashTable extends RMHashtable {

    private RMHashtable rmHashtable;
    private final String id;

    public PersistentRMHashTable(String id) throws RMIOManagerException {
        this.id = id;
        try {
            rmHashtable =  RMIOManager.getInstance().readDB(id);
        } catch (Exception e) {
            rmHashtable = new RMHashtable();
            RMIOManager.getInstance().writeDatabase(id, rmHashtable);
            Logger.print().warning("Initializing fresh database.", "PersistentRMHashTable " + id);
        }
    }

    @Override
    public String toString() {
        return rmHashtable.toString();
    }

    @Override
    public void dump() {
        rmHashtable.dump();
    }

    @Override
    public Object put(Object key, Object value) {
        Object result = rmHashtable.put(key, value);
        try {
            RMIOManager.getInstance().writeDatabase(id, rmHashtable);
        } catch (RMIOManagerException e) {
            Logger.print().error("File System Error! Data is probably corrupted!", "PersistentRMHashTable");
        }
        return result;
    }

    @Override
    public Object remove(Object key) {
        Object result = rmHashtable.remove(key);
        try {
            Logger.print().info("Removing " + key + "from " + id + ".");
            RMIOManager.getInstance().writeDatabase(id, rmHashtable);
        } catch (RMIOManagerException e) {
            Logger.print().error("File System Error! Data is probably corrupted!", "PersistentRMHashTable");
        }
        return result;
    }

    @Override
    public synchronized Object get(Object key) {
        return rmHashtable.get(key);
    }

//    public void setHT(RMHashtable old) {
//        rmHashtable = old;
//        try {
//            RMIOManager.getInstance().writeDatabase(id, rmHashtable);
//        } catch (RMIOManagerException e) {
//            Logger.print().error("STOP! Data is probably corrupted!");
//        }
//    }

    public RMHashtable getHT() {
        return rmHashtable;
    }

    public String getId() {
        return id;
    }
}
