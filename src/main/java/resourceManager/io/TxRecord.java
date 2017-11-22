package resourceManager.io;

import java.util.Hashtable;
import java.util.Map;

public class TxRecord extends Hashtable<Integer, TxRecordEntry> {
    public TxRecord(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TxRecord(int initialCapacity) {
        super(initialCapacity);
    }

    public TxRecord() {
    }

    public TxRecord(Map<? extends Integer, ? extends TxRecordEntry> t) {
        super(t);
    }
}
