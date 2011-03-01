package libra;

import libra.Symbol;

import java.io.File;

/**
 * Containing directory location of symbol CSV files.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Lib
    extends libra.io.FileIO
{

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
	name = Basename(name);

	Symbol sym = Map.get(name);
	if (null == sym){
	    File symf = new File(Directory,name+".csv");
	    try {
		sym = new Symbol(symf);

		if (null == sym.part)
		    sym.part = name;

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
}
