package org.apache.cassandra.utils;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

	private static Logger logger_ = LoggerFactory.getLogger(FBUtilities.class);

	public static void skipBytesFully(DataInput in, int skip) {
		// TODO Auto-generated method stub
		
	}

    public static void createDirectory(String directory) throws IOException
    {
        createDirectory(new File(directory));
    }

    public static void createDirectory(File directory) throws IOException
    {
        if (!directory.exists())
        {
            if (!directory.mkdirs())
            {
                throw new IOException("unable to mkdirs " + directory);
            }
        }
    }

	public static void closeQuietly(Closeable c) {
        try
        {
            if (c != null)
                c.close();
        }
        catch (Exception e)
        {
            logger_.warn("Failed closing " + c, e);
        }
	}

	public static void deleteRecursive(File dir) throws IOException {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (String child : children)
                deleteRecursive(new File(dir, child));
        }

        // The directory is now empty so now it can be smoked
        deleteWithConfirm(dir);// TODO Auto-generated method stub
	}
	
    public static void deleteWithConfirm(File file) throws IOException
    {
        assert file.exists() : "attempted to delete non-existing file " + file.getName();
        if (logger_.isDebugEnabled())
            logger_.debug("Deleting " + file.getName());
        if (!file.delete())
        {
            throw new IOException("Failed to delete " + file.getAbsolutePath());
        }
    }
}
