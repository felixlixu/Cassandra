package org.apache.cassandra.utils;

public abstract class WrappedRunnable implements Runnable {
    public final void run()
    {
        try
        {
            runMayThrow();
        }
        catch (Exception e)
        {
            throw FBUtilities.unchecked(e);
        }
    }

    abstract protected void runMayThrow() throws Exception;
}
