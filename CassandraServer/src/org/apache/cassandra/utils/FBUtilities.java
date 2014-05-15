package org.apache.cassandra.utils;

import java.io.FileDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.locator.IEndpointSnitch;
import org.apache.cassandra.net.IAsyncResult;

public class FBUtilities {

	public static final int MAX_UNSIGNED_SHORT = 0xFFFF;
	public static final BigInteger TWO = new BigInteger("2");
	private static volatile InetAddress broadcastInetAddress_;
	private static volatile InetAddress localInetAddress_;

	private static final ThreadLocal<MessageDigest> localMD5Digest = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			return newMessageDigest("MD5");
		}

		@Override
		public MessageDigest get() {
			MessageDigest digest = super.get();
			digest.reset();
			return digest;
		}
	};

	public static MessageDigest newMessageDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException nsae) {
			throw new RuntimeException("the requested digest algorithm ("
					+ algorithm + ") is not available", nsae);
		}
	}

	public static int compareUnsigned(byte[] array, byte[] array2, int i,
			int j, int remaining, int remaining2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static BigInteger hashToBigIngeger(ByteBuffer key) {
		byte[] result = hash(key);
		BigInteger hash = new BigInteger(result);
		return hash.abs();
	}

	public static byte[] hash(ByteBuffer... data) {
		MessageDigest messageDigest = localMD5Digest.get();
		for (ByteBuffer block : data) {
			messageDigest.update(block.duplicate());
		}

		return messageDigest.digest();
	}

	public static Pair<BigInteger, Boolean> midpoint(BigInteger left,
			BigInteger right, int sigbits) {
		BigInteger midpoint;
		boolean remainder;
		if (left.compareTo(right) < 0) {
			BigInteger sum = left.add(right);
			remainder = sum.testBit(0);
			midpoint = sum.shiftRight(1);
		} else {
			BigInteger max = TWO.pow(sigbits);
			// wrapping case
			BigInteger distance = max.add(right).subtract(left);
			remainder = distance.testBit(0);
			midpoint = distance.shiftRight(1).add(left).mod(max);
		}
		return new Pair<BigInteger, Boolean>(midpoint, remainder);
	}

	public static MessageDigest threadLocalMD5Digest() {
		return localMD5Digest.get();
	}

	public static <T> T construct(String classname, String readable)
			throws ConfigurationException {
		Class<T> cls = FBUtilities.classForName(classname, readable);
		try {
			return cls.getConstructor().newInstance();
		} catch (NoSuchMethodException e) {
			throw new ConfigurationException(String.format(
					"No default constructor for %s class '%s'.", readable,
					classname));
		} catch (IllegalAccessException e) {
			throw new ConfigurationException(String.format(
					"Default constructor for %s class '%s' is inaccessible.",
					readable, classname));
		} catch (InstantiationException e) {
			throw new ConfigurationException(String.format(
					"Cannot use abstract class '%s' as %s.", classname,
					readable));
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof ConfigurationException)
				throw (ConfigurationException) e.getCause();
			throw new ConfigurationException(String.format(
					"Error instantiating %s class '%s'.", readable, classname),
					e);
		}
	}

	public static <T> Class<T> classForName(String classname, String readable)
			throws ConfigurationException {
		try {
			return (Class<T>) Class.forName(classname);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException(String.format(
					"Unable to find %s class '%s'", readable, classname));
		}
	}

	/**
	 * Please use getBroadcastAddress instead. You need this only when you have
	 * to listen/connect.
	 */
	public static InetAddress getLocalAddress() {
		if (localInetAddress_ == null)
			try {
				localInetAddress_ = DatabaseDescriptor.getListenAddress() == null ? InetAddress
						.getLocalHost() : DatabaseDescriptor.getListenAddress();
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		return localInetAddress_;
	}

	public static InetAddress getBroadcastAddress() {
		if (broadcastInetAddress_ == null)
			broadcastInetAddress_ = DatabaseDescriptor.getBroadcastAddress() == null ? getLocalAddress()
					: DatabaseDescriptor.getBroadcastAddress();
		return broadcastInetAddress_;
	}

	public static <T extends Comparable> SortedSet<T> singleton(T column) {
		 return new TreeSet<T>(Arrays.asList(column));
	}

	public static RuntimeException unchecked(Exception e) {
		 return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
	}

	public static Field getProtectedField(
			Class<? extends FileDescriptor> klass, String fieldName) {
        Field field;

        try
        {
            field = klass.getDeclaredField(fieldName);
            field.setAccessible(true);
        }
        catch (Exception e)
        {
            throw new AssertionError(e);
        }

        return field;
	}

	public static int encodedUTF8Length(String st) {
        int strlen = st.length();
        int utflen = 0;
        for (int i = 0; i < strlen; i++)
        {
            int c = st.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F))
                utflen++;
            else if (c > 0x07FF)
                utflen += 3;
            else
                utflen += 2;
        }
        return utflen;
	}
	
    private static final ThreadLocal<Random> localRandom = new ThreadLocal<Random>()
    {
        @Override
        protected Random initialValue()
        {
            return new Random();
        }
    };

	public static Random threadLocalRandom() {
		return localRandom.get();
	}

	public static void waitOnFutures(List<IAsyncResult> repairResults,
			Long rpcTimeout) {
		// TODO Auto-generated method stub
		
	}

}
