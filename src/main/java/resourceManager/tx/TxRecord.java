package resourceManager.tx;

import common.io.Logger;

import java.util.ArrayList;
import java.util.Hashtable;

public class TxRecord extends Hashtable<Integer, TxRecordEntry> {


    public TxRecord() { }


    public int[] checkPending() {
        ArrayList<Integer> pending = new ArrayList<>();
        for (TxRecordEntry txEntry: this.values()) {
            try {
                if (txEntry.isPending()) {
                    pending.add(txEntry.getId());
                }
            } catch (UndecidableStateException e) {
                Logger.print().error(e.getMessage(), "TxRecord");
            }
        }
        return pending.stream().mapToInt((id) -> id).toArray();
    }





}
