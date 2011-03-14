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

    private static File Sources = new File("lib");

    private static File Targets = Sources;


    public final static boolean Sources(File dir){

	if (dir.isDirectory()){
	    Sources = dir;
	    return true;
	}
	else
	    return false;
    }
    public final static boolean Targets(File dir){

	if (dir.isDirectory()){
	    Targets = dir;
	    return true;
	}
	else
	    return false;
    }
    public final static File Sources(){
	return Sources;
    }
    public final static File Targets(){
	return Targets;
    }
    public final static boolean Found(){
	return (Sources.isDirectory() || Targets.isDirectory());
    }


    private final static java.util.Map<String,Symbol> Map = new java.util.HashMap<String,Symbol>();

    public final static Symbol For(String name){
	name = Basename(name);

	Symbol sym = Map.get(name);
	if (null == sym){
	    File symf = Lib.Sym(name);
	    if (symf.isFile()){
		/*
		 * Components with tabular sources
		 */
		try {
		    sym = new Symbol(symf);

		    if (null == sym.part)
			sym.part = name;

		    if (sym.layout()){
			if (sym.markup(null))
			    Map.put(name,sym);
			else
			    throw new IllegalArgumentException(String.format("Bad symbol layout '%s'",name));
		    }
		    else if (sym.markup(null))
			Map.put(name,sym);
		    else
			throw new IllegalArgumentException(String.format("Bad symbol layout '%s'",name));
		}
		catch (java.io.IOException iox){
		    throw new IllegalArgumentException(String.format("Error reading symbol '%s'",name),iox);
		}
	    }
	    else {
		/*
		 * Nets without tabular sources
		 */
		symf = Lib.Net(name);
		if (symf.isFile()){
		    try {
			sym = new Symbol(symf);

			if (null == sym.part)
			    sym.part = name;

			Map.put(name,sym);
		    }
		    catch (java.io.IOException iox){
			throw new IllegalArgumentException(String.format("Error reading symbol '%s'",name),iox);
		    }
		}
	    }
	}
	return sym;
    }
    /**
     * @return File for components with tabular sources
     */
    public final static File Sym(String name){
	return (new File(Sources,name.replace('\\','%')+".csv"));
    }
    /**
     * @return File for nets without tabular sources
     */
    public final static File Net(String name){
	return (new File(Targets,name.replace('\\','%')+".sym"));
    }
}
