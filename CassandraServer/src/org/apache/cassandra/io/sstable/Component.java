package org.apache.cassandra.io.sstable;


import java.io.File;
import java.util.EnumSet;

import org.apache.cassandra.utils.Pair;

import com.google.common.base.Objects;

public class Component {

	public static final char separator='-';
	
	final static EnumSet<Type> TYPES=EnumSet.allOf(Type.class);
	enum Type{
		DATA("Data.db"),
		PRIMARY_INDEX("Index.db"),
		COMPACTED_MARKER("Compacted"),
		COMPRESSION_INFO("CompressionInfo.db"),
		STATS("Statistics.db");
		final String repr;
		Type(String repr){
			this.repr=repr;
		}
		
		static Type fromRepreentation(String repr){
			for(Type type:TYPES)
				if(repr.equals(type.repr))
					return type;
			throw new RuntimeException("Invalid SSTable component: '" + repr + "'");
		}
	}

	public final static Component DATA = new Component(Type.DATA, -1);

	public static final Component COMPACTED_MARKER = new Component(Type.COMPACTED_MARKER, -1);

	public static final Component PRIMARY_INDEX = new Component(Type.PRIMARY_INDEX,-1);

	public static final Component STATS = new Component(Type.STATS,-1);

	public static final Component COMPRESSION_INFO=new Component(Type.COMPRESSION_INFO,-1);

	public final Type type;

	private int id;

	private int hashCode;
	
	public Component(Type type, int id) {
		this.type=type;
		this.id=id;
		this.hashCode=Objects.hashCode(type,id);
	}

	/**
	 * Filename of the form "ksname/cfname-tmp- version-gen-component
	 * name is ksname
	 * */
	public static Pair<Descriptor, Component> fromFilename(File dir, String name) {
		Pair<Descriptor,String> path=Descriptor.fromFilename(dir,name);
		Type type=Type.fromRepreentation(path.right);
		Component component;
		switch(type){
			case DATA: component=Component.DATA; break;
			default:
				throw new IllegalStateException();
		}
		
		return new Pair<Descriptor,Component>(path.left,component);
		
	}

	public String name() {
		return type.repr;
	}

}
