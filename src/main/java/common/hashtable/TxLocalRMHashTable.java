package common.hashtable;

import java.util.ArrayList;

public class TxLocalRMHashTable extends RMHashtable {

    private final RMHashtable rmHashtable;
    private final ArrayList<Object> modifiedKeys = new ArrayList<>();

    public TxLocalRMHashTable(RMHashtable rmHashtable) {
        this.rmHashtable = rmHashtable;
    }

    public Object[] getModifiedKeys() {
        return modifiedKeys.toArray();
    }

    public RMHashtable getDb() {
        return rmHashtable;
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        modifiedKeys.add(key);
        return rmHashtable.put(key, value);
    }

    @Override
    public synchronized Object remove(Object key) {
        modifiedKeys.add(key);
        return rmHashtable.remove(key);
    }

    @Override
    public String toString() {
        return rmHashtable.toString();
    }

    @Override
    public void dump() {
        rmHashtable.dump();
    }
}
