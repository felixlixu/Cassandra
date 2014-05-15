package org.apache.cassandra.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

public class GuidGenerator {
    private static Random myRand;
    private static SecureRandom mySecureRand;
    private static String s_id;

    static {
        if (System.getProperty("java.security.egd") == null) {
            System.setProperty("java.security.egd", "file:/dev/urandom");
        }
        mySecureRand = new SecureRandom();
        long secureInitializer = mySecureRand.nextLong();
        myRand = new Random(secureInitializer);
        try {
            s_id = InetAddress.getLocalHost().toString();
        }
        catch (UnknownHostException e) {
            throw new AssertionError(e);
        }
    }

    public static String guid() {
        ByteBuffer array = guidAsBytes();
        
        StringBuilder sb = new StringBuilder();
        for (int j = array.position(); j < array.limit(); ++j)
        {
            int b = array.get(j) & 0xFF;
            if (b < 0x10) sb.append('0');
            sb.append(Integer.toHexString(b));
        }

        return convertToStandardFormat( sb.toString() );
    }
    
    public static String guidToString(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < bytes.length; ++j) {
            int b = bytes[j] & 0xFF;
            if (b < 0x10) sb.append('0');
            sb.append(Integer.toHexString(b));
        }

        return convertToStandardFormat( sb.toString() );
    }
    
    public static ByteBuffer guidAsBytes()
    {
        StringBuilder sbValueBeforeMD5 = new StringBuilder();
        long time = System.currentTimeMillis();
        long rand = 0;
        rand = myRand.nextLong();
        sbValueBeforeMD5.append(s_id)
        				.append(":")
        				.append(Long.toString(time))
        				.append(":")
        				.append(Long.toString(rand));

        String valueBeforeMD5 = sbValueBeforeMD5.toString();
        return ByteBuffer.wrap(FBUtilities.threadLocalMD5Digest().digest(valueBeforeMD5.getBytes()));
    }

    /*
        * Convert to the standard format for GUID
        * Example: C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
    */

    private static String convertToStandardFormat(String valueAfterMD5) {
        String raw = valueAfterMD5.toUpperCase();
        StringBuilder sb = new StringBuilder();
        sb.append(raw.substring(0, 8))
          .append("-")
          .append(raw.substring(8, 12))
          .append("-")
          .append(raw.substring(12, 16))
          .append("-")
          .append(raw.substring(16, 20))
          .append("-")
          .append(raw.substring(20));
        return sb.toString();
    }
}