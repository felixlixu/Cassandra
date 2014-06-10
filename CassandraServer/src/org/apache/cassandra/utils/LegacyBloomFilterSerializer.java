package org.apache.cassandra.utils;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.BitSet;

public class LegacyBloomFilterSerializer {
    public void serialize(LegacyBloomFilter bf, DataOutput dos)
            throws IOException
    {
        throw new UnsupportedOperationException("Shouldn't be serializing legacy bloom filters");
//        dos.writeInt(bf.getHashCount());
//        ObjectOutputStream oos = new ObjectOutputStream(dos);
//        oos.writeObject(bf.getBitSet());
//        oos.flush();
    }

    public LegacyBloomFilter deserialize(DataInputStream dis) throws IOException
    {
        int hashes = dis.readInt();
        ObjectInputStream ois = new ObjectInputStream(dis);
        try
        {
          BitSet bs = (BitSet) ois.readObject();
          return new LegacyBloomFilter(hashes, bs);
        } catch (ClassNotFoundException e)
        {
          throw new RuntimeException(e);
        }
    }

    public long serializedSize(LegacyBloomFilter legacyBloomFilter)
    {
        throw new UnsupportedOperationException();
    }
}
