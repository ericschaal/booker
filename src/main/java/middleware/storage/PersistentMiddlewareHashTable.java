package middleware.storage;

import common.io.Logger;
import middleware.io.MiddlewareIOManager;
import middleware.io.MiddlewareIOManagerException;
import middleware.tx.Transaction;

import java.util.Hashtable;

public class PersistentMiddlewareHashTable extends Hashtable<Integer, Transaction> {

    private Hashtable<Integer, Transaction> rmHashtable;

    public PersistentMiddlewareHashTable() throws MiddlewareIOManagerException {
        try {
            rmHashtable =  MiddlewareIOManager.getInstance().readDB();
        } catch (Exception e) {
            rmHashtable = new Hashtable<>();
            MiddlewareIOManager.getInstance().writeDatabase(rmHashtable);
            Logger.print().warning("Initializing fresh database.", "PersistentMiddlewareHashTable ");
        }
    }

    @Override
    public String toString() {
        return rmHashtable.toString();
    }



    @Override
    public Transaction put(Integer key, Transaction value) {
        Transaction result = rmHashtable.put(key, value);
        try {
            Logger.print().info("Adding value to " + key + ".", "PersistentMiddlewareHashTable");
            MiddlewareIOManager.getInstance().writeDatabase(rmHashtable);
        } catch (MiddlewareIOManagerException e) {
            Logger.print().error("File System Error! Data is probably corrupted!", "PersistentMiddlewareHashTable");
        }
        return result;
    }


    public Transaction remove(Integer key) {
        Transaction result = rmHashtable.remove(key);
        try {
            Logger.print().info("Removing " + key + ".", "PersistentMiddlewareHashTable");
            MiddlewareIOManager.getInstance().writeDatabase(rmHashtable);
        } catch (MiddlewareIOManagerException e) {
            Logger.print().error("File System Error! Data is probably corrupted!", "PersistentMiddlewareHashTable");
        }
        return result;
    }

    @Override
    public synchronized Transaction get(Object key) {
        return rmHashtable.get(key);
    }

    @Override
    public synchronized boolean contains(Object value) {
        return rmHashtable.contains(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return rmHashtable.containsValue(value);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return rmHashtable.containsKey(key);
    }
}
