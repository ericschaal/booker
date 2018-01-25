package middleware.lockManager;

/*
    The tx is deadlocked.  Somebody should abort it.
*/

public class DeadlockException extends Exception
{
    private int xid = 0;
    
    public DeadlockException (int xid, String msg)
    {
        super("The tx " + xid + " is deadlocked:" + msg);
        this.xid = xid;
    }
    
    int GetXId()
    {
        return xid;
    }
}
