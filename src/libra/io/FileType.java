package libra.io;

import java.io.File;

/**
 * 
 */
public enum FileType {

    CSV(true,false), SYM(false,true), SCH(false,true), Unknown(false,false);

    public final static FileType For(File file){
	String name = file.getName();
	int idx = name.lastIndexOf('.');
	if (0 < idx){
	    try {
		return FileType.valueOf(name.substring(idx+1).toUpperCase());
	    }
	    catch (RuntimeException exc){
		return FileType.Unknown;
	    }
	}
	else
	    return FileType.Unknown;
    }


    public final boolean isCSV, isGAF;

    FileType(boolean isCSV, boolean isGAF){
	this.isCSV = isCSV;
	this.isGAF = isGAF;
    }
}
