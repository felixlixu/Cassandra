package org.apache.cassandra.io.compress;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOError;
import java.io.IOException;

import org.apache.cassandra.io.sstable.Component;
import org.apache.cassandra.io.sstable.Descriptor;

public class CompressionMetadata {

	public final long dataLength;
	public final long compressedFileLength;
	private final String indexFilePath;
	
	public CompressionMetadata(String indexFilePath,long compressedLength) throws IOException{
		this.indexFilePath=indexFilePath;
		
		DataInputStream stream=new DataInputStream(new FileInputStream(indexFilePath));
		String compressorName=stream.readUTF();
		
		
		dataLength=stream.readLong();
		this.compressedFileLength=compressedLength;
	}

	public static CompressionMetadata create(String path) {
		Descriptor desc=Descriptor.fromFilename(path);
		try{
			return new CompressionMetadata(desc.filenameFor(Component.COMPRESSION_INFO),new File(path).length());
			
		}catch(IOException e){
			throw new IOError(e);
		}
	}

}
