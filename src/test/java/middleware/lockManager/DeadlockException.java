package middleware.lockManager;

/*
    The transaction is deadlocked.  Somebody should abort it.
*/

public class DeadlockException extends Exception
{
    private int xid = 0;
    private String msg;

    public DeadlockException (int xid, String msg)
    {
        super("The transaction " + xid + " is deadlocked:" + msg);
        this.xid = xid;
        this.msg = "The transaction " + xid + " is deadlocked:" + msg;

    }

    int GetXId()
    {
        return xid;
    }

    public void printMsg()
    {
        System.out.println(msg);
    }
}
