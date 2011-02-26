package libra;

import java.io.File;

/**
 * gEDA installation singleton
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class GedaHome
    extends libra.io.FileIO
{

    private static File Top, Sym, Titleblock, Connector, Io, Power;
    static {
	File search;
	/*
	 * Lowest precedence, first built-in search location
	 */
	search = new File("/usr/share/gEDA");
	Define(search);
	/*
	 * Override the package location with a local build
	 */
	search = new File("/usr/local/share/gEDA");
	Define(search);
	/*
	 * Highest precedence, last built-in search location
	 */
	String env = System.getenv("GEDA_HOME");
	if (null != env){
	    Define(new File(env));
	}
    }

    public final static boolean Define(File top){
	File sym = new File(top,"sym");
	File titleblock = new File(sym,"titleblock");
	File connector = new File(sym,"connector");
	File io = new File(sym,"io");
	File power = new File(sym,"power");

	if (top.isDirectory() && sym.isDirectory() && titleblock.isDirectory()){
	    Top = top;
	    Sym = sym;
	    Titleblock = titleblock;
	    Connector = connector;
	    Io = io;
	    Power = power;
	    return true;
	}
	else
	    return false;
    }
    public final static boolean Found(){
	return (Top.isDirectory() && Sym.isDirectory() && Titleblock.isDirectory());
    }

    private final static java.util.Map<String,Symbol> Map = new java.util.HashMap<String,Symbol>();

    public final static Symbol Titleblock(String name){
	name = Basename(name);

	Symbol sym = Map.get(name);
	if (null == sym)
	    return Init(name,new File(Titleblock,name+".sym"));
	else
	    return sym;
    }
    public final static Symbol Connector(String name){
	name = Basename(name);

	Symbol sym = Map.get(name);
	if (null == sym)
	    return Init(name,new File(Connector,name+".sym"));
	else
	    return sym;
    }
    public final static Symbol Io(String name){
	name = Basename(name);

	Symbol sym = Map.get(name);
	if (null == sym)
	    return Init(name,new File(Io,name+".sym"));
	else
	    return sym;
    }
    public final static Symbol Power(String name){
	name = Basename(name);

	Symbol sym = Map.get(name);
	if (null == sym)
	    return Init(name,new File(Power,name+".sym"));
	else
	    return sym;
    }

    private final static Symbol Init(String name, File file){
	try {
	    Symbol sym = new Symbol(file);
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

	    return sym;
	}
	catch (java.io.IOException iox){
	    throw new IllegalArgumentException(String.format("Symbol not found '%s'",name),iox);
	}
    }
}
