package libra.sch;

import java.io.File;

/**
 * gEDA installation singleton
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Lib {

    private static File Directory = new File("lib");

    public final static boolean Define(File dir){

	if (dir.isDirectory()){
	    Directory = dir;
	    return true;
	}
	else
	    return false;
    }
    public final static File Directory(){
	return Directory;
    }
    public final static boolean Found(){
	return Directory.isDirectory();
    }
}
