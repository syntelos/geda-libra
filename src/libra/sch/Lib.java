package libra.sch;

import libra.Symbol;

import java.io.File;

/**
 * Containing directory location of symbol CSV files.
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


    private final static java.util.Map<String,Symbol> Map = new java.util.HashMap<String,Symbol>();

    public final static Symbol For(String name){
	name = Clean(name);

	Symbol sym = Map.get(name);
	if (null == sym){
	    File symf = new File(Directory,name+".csv");
	    try {
		sym = new Symbol(symf);
		if (sym.layout()){
		    if (sym.markup())
			Map.put(name,sym);
		    else
			throw new IllegalArgumentException(String.format("Bad symbol layout '%s'",name));
		}
		else if (sym.markup())
		    Map.put(name,sym);
		else
		    throw new IllegalArgumentException(String.format("Bad symbol layout '%s'",name));
	    }
	    catch (java.io.IOException iox){
		throw new IllegalArgumentException(String.format("Symbol not found '%s'",name),iox);
	    }
	}
	return sym;
    }
    /**
     * String basename
     */
    public final static String Clean(String name){
	int[] indeces = CleanScan(name);
	if (null != indeces)
	    return name.substring(indeces[0],indeces[1]);
	else
	    return name;
    }
    private final static int[] CleanScan(String name){
	/*
	 * single pass basename scan
	 */
	final char[] cary = name.toCharArray();
	final int count = cary.length;
	final int fext = (count-4); // .sym
	int[] indeces = null;
	scan:
	for (int cc = 0; cc < count; cc++){
	    switch(cary[cc]){
	    case '/':
	    case '\\':
		indeces = new int[]{cc+1,count};
		indeces[0] = cc+1;
		break;
	    case '.':
		if (cc >= fext){
		    if (null == indeces)
			indeces = new int[]{0,cc};
		    else
			indeces[1] = cc;
		    break scan;
		}
		else
		    break;
	    default:
		break;
	    }
	}
	return indeces;
    }
}
