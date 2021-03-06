package org.apache.cassandra.net;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

import org.apache.cassandra.io.IVersionedSerializer;
import org.apache.cassandra.service.StorageService;
import org.apache.cassandra.service.StorageService.Verb;
import org.apache.cassandra.utils.FBUtilities;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class Header {

	private static HeaderSerializer serializer_;
	private final InetAddress from_;
	private final StorageService.Verb verb_;
	final Map<String,byte[]> details_;
	
	static
	{
		serializer_=new HeaderSerializer();
	}
	
	public static IVersionedSerializer<Header> serializer(){
		return serializer_;
	}
	
    Header(InetAddress from, StorageService.Verb verb, Map<String, byte[]> details)
    {
        assert from != null;
        assert verb != null;

        from_ = from;
        verb_=verb;
        details_=ImmutableMap.copyOf(details);
    }
	
    public Header(InetAddress from, Verb verb) {
		this(from,verb,Collections.<String,byte[]>emptyMap());
	}

	InetAddress getFrom()
    {
        return from_;
    }

	public StorageService.Verb getVerb() {
		return verb_;
	}
	
	public byte[] getDetail(String key){
		return details_.get(key);
	}
	
	Header withDetailsAdded(String key,byte[] value){
		Map<String,byte[]> detailsCopy=Maps.newHashMap();
		detailsCopy.put(key, value);
		return new Header(from_,verb_,detailsCopy);
	}
	
	Header withDetailsRemoved(String key){
		if(!details_.containsKey(key)){
			return this;
		}
		Map<String,byte[]> detailsCopy=Maps.newHashMap();
		detailsCopy.remove(key);
		return new Header(from_,verb_,detailsCopy);
	}
	
	public int serializedSize(){
		int size=0;
		size +=CompactEndpointSerializationHelper.serializedSize(getFrom());
		size +=4;
		size +=4;
		for(String key:details_.keySet()){
			size +=2+FBUtilities.encodedUTF8Length(key);
			byte[] value=details_.get(key);
			size +=4+value.length;
		}
		return size;
	}
}

class HeaderSerializer implements IVersionedSerializer<Header>
{

	@Override
	public void serialize(Header header, DataOutput dos, int version)
			throws IOException {
		CompactEndpointSerializationHelper.serialize(header.getFrom(),dos);
		dos.writeInt(header.getVerb().ordinal());
		dos.writeInt(header.details_.size());
		for(String key:header.details_.keySet()){
			dos.writeUTF(key);
			byte[] value=header.details_.get(key);
			dos.writeInt(value.length);
			dos.write(value);
		}
	}

	@Override
	public Header deserialize(DataInput dis, int version) throws IOException {
		InetAddress from=CompactEndpointSerializationHelper.deserialize(dis);
		int verbOrdinal=dis.readInt();
		int size=dis.readInt();
		Map<String,byte[]> details=new Hashtable<String,byte[]>(size);
		for(int i=0;i<size;i++){
			String key=dis.readUTF();
			int length=dis.readInt();
			byte[] bytes=new byte[length];
			dis.readFully(bytes);
			details.put(key, bytes);
		}
		return new Header(from,StorageService.VERBS[verbOrdinal],details);
	}

	@Override
	public long serializedSize(Header header, int version) {
		throw new UnsupportedOperationException();
	}
}
