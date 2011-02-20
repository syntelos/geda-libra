package libra.sch;

import java.io.BufferedReader ;
import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileOutputStream ;
import java.io.InputStreamReader ;
import java.io.IOException ;
import java.io.PrintStream ;

/**
 * Read schematic table for schematic generation.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Main {

    public static void Usage(){
	System.out.println("Usage");
	System.out.println();
	System.out.println("    Sch -i schematic.csv -s schematic.sch");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Produce gEDA schematic from schematic description table.");
	System.out.println();
	System.exit(1);
    }
    public enum Opt {
	H, I, S;

	public static Opt For(String arg){
	    while (0 < arg.length() && '-' == arg.charAt(0))
		arg = arg.substring(1);

	    try {
		return Opt.valueOf(arg.toUpperCase());
	    }
	    catch (RuntimeException exc){
		return Opt.H;
	    }
	}
    }
    public static void main(String[] argv){
	File inf = null, sf = null;

	for (int cc = 0, argc = argv.length; cc < argc; cc++){
	    String arg = argv[cc];
	    Opt opt = Opt.For(arg);
	    switch(opt){
	    case H:
		Usage();
		break;
	    case I:
		cc += 1;
		if (cc < argc){
		    arg = argv[cc];
		    inf = new File(arg);
		    if (!inf.isFile()){
			System.err.printf("Error, file not found '%s'.%n",inf.getPath());
			System.exit(1);
		    }
		}
		else
		    Usage();
		break;
	    case S:
		cc += 1;
		if (cc < argc){
		    arg = argv[cc];
		    sf = new File(arg);
		}
		else
		    Usage();
		break;
	    default:
		throw new Error(opt.name());
	    }
	}
	if (null != inf && null != sf){
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(inf),"US-ASCII"));
		boolean once = true;
		try {
		    String line;
		    while (null != (line = in.readLine())){
			Input input;
			try {
			    input = new Input(line,once);
			    if (input.isHeadline())
				once = false;

			}
			catch (RuntimeException skip){
			    continue;
			}

		    }
		}
		finally {
		    in.close();
		}


		System.exit(0);
	    }
	    catch (Exception exc){
		exc.printStackTrace();
		System.exit(1);
	    }
	}
	else
	    Usage();
    }

}
