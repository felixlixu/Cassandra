package org.apache.cassandra.io.sstable;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.cassandra.db.Table;
import org.apache.cassandra.utils.Pair;

import com.google.common.base.Objects;

import static org.apache.cassandra.io.sstable.Component.separator;

public class Descriptor {

	public static final String CURRENT_VERSION="hc";
	public  String ksname;
	public  String cfname;
	private String version;
	private File dir;
	public int generation;
	public boolean temporary;
	private int hashCode;
	private Object directory;
	public static final String LEGACY_VERSION="a";
	public Descriptor(String version, File dir, String ksname, String cfname,
			int generation, boolean temp) {
		assert version != null && dir != null && ksname != null && cfname != null;
		this.version=version;
		this.dir=dir;
		this.ksname=ksname;
		this.cfname=cfname;
		this.generation=generation;
		temporary=temp;
		hashCode=Objects.hashCode(dir,generation,ksname,cfname);
	}
	
    /**
     * Filename of the form "<ksname>/<cfname>-[tmp-][<version>-]<gen>-<component>"
     *
     * @param directory The directory of the SSTable files
     * @param name The name of the SSTable file
     *
     * @return A Descriptor for the SSTable, and the Component remainder.
     */
	public static Pair<Descriptor, String> fromFilename(File dir, String name) {
		String ksname=extractKeyspaceName(dir);
		StringTokenizer st=new StringTokenizer(name,String.valueOf(separator));
		String nexttok;
		
		String cfname=st.nextToken();
		nexttok=st.nextToken();
		boolean temporary=false;
		if(nexttok.equals(SSTable.TEMPFILE_MARKER)){
			temporary=true;
			nexttok=st.nextToken();
		}
		String version=LEGACY_VERSION;
		if(versionValidate(nexttok)){
			version=nexttok;
			nexttok=st.nextToken();
		}
		int generation=Integer.parseInt(nexttok);
		
		String component=st.nextToken();
		return new Pair<Descriptor,String>(new Descriptor(version, dir, ksname, cfname, generation, temporary),component);
	}
	
	static boolean versionValidate(String ver){
		return ver!=null&&ver.matches("[a-z]+");
	}
	
	public static String extractKeyspaceName(File dir) {
		if(isSnapshotInPath(dir)){
			return dir.getParentFile().getParentFile().getName();
		}
		return dir.getName();
	}
	//judge is snapshot file.
	private static boolean isSnapshotInPath(File dir) {
		File curDir=dir;
		while(curDir!=null){
			if(curDir.getName().equals(Table.SNAPSHOT_SUBDIR_NAME))
				return true;
			curDir=curDir.getParentFile();
		}
		return false;
	}

	public boolean isCompatible() {
		return version.charAt(0)<=CURRENT_VERSION.charAt(0);
	}

	public String filenameFor(Component component) {
		return filenameFor(component.name());
	}

	private String filenameFor(String suffix) {
		return baseFilename()+separator+suffix;
	}

	private String baseFilename() {
        StringBuilder buff = new StringBuilder();
        buff.append(directory).append(File.separatorChar);
        buff.append(cfname).append(separator);
        if (temporary)
            buff.append(SSTable.TEMPFILE_MARKER).append(separator);
        if (!LEGACY_VERSION.equals(version))
            buff.append(version).append(separator);
        buff.append(generation);
        return buff.toString();
	}

}
