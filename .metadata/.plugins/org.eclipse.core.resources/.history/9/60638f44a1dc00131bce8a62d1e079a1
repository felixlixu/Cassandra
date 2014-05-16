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
}
