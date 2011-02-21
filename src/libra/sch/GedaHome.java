package libra.sch;

import java.io.File;

/**
 * gEDA installation singleton
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class GedaHome {

    private static File Directory;
    static {
	File search;
	search = new File("/usr/share/gEDA");
	if (search.isDirectory())
	    Directory = search;
	/*
	 */
	search = new File("/usr/local/share/gEDA");
	if (search.isDirectory())
	    Directory = search;
	/*
	 */
	String env = System.getenv("GEDA_HOME");
	if (null != env){
	    search = new File(env);
	    if (search.isDirectory()){
		Directory = search;
	    }
	}
    }

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
