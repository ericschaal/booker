package common.tx.persistent;

import common.tx.error.UndecidableStateException;
import common.tx.model.TxRecoveryAction;

import java.io.Serializable;

public interface TransactionRecordEntry extends Serializable {

    int getId();
    TxRecoveryAction recoveryAction();
}
