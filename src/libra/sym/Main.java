package libra.sym;

import libra.Symbol;

import java.io.File ;

/**
 * Read pin table for symbol generation.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Main {

    public static void Usage(){
	System.out.println("Usage");
	System.out.println();
	System.out.println("    Sym -i symbol.csv [options]");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Produce gEDA symbol from pin description table.");
	System.out.println();
	System.out.println("  Input table");
	System.out.println();
	System.out.println("    The input argument is a CSV file (as from TI), for example");
	System.out.println("    in the following.");
	System.out.println();
	System.out.println("        Part,LM3S9B90,,,");
	System.out.println("        Package,100LQFP,,,");
	System.out.println("        Number,Name,Type,Buffer,Description");
	System.out.println("        1,PE7,I/O,TTL,GPIO port E bit 7.");
	System.out.println("        1,AIN0,I,Analog,Analog-to-digital converter input 0.");
	System.out.println("        1,C2o,O,TTL,Analog comparator 2 output.");
	System.out.println();
	System.out.println("    The input CSV is expanded with metadata, as in the");
	System.out.println("    following example.");
	System.out.println();
	System.out.println("        Part,LM3S9B90,,,");
	System.out.println("        Package,100LQFP,,,");
	System.out.println("        Layout,lbrt:25/25/25/25,,,");
	System.out.println("        Description,Stellaris ARM Cortex-M3,,,");
	System.out.println("        Documentation,http://www.ti.com/lit/gpn/lm3s9b90,,,");
	System.out.println("        Author,John Pritchard <jdp@ulsf.net>,,,");
	System.out.println("        License,GPL,,,");
	System.out.println("        Footprint,LQFP100_14,,,");
	System.out.println("        Number,Name,Type,Buffer,Description");
	System.out.println("        1,PE7,I/O,TTL,GPIO port E bit 7.");
	System.out.println("        1,AIN0,I,Analog,Analog-to-digital converter input 0.");
	System.out.println("        1,C2o,O,TTL,Analog comparator 2 output.");
	System.out.println();
	System.out.println("    The input CSV pins may be ordered by number, ascending");
	System.out.println("    from one to N.  Multiple records for each number are");
	System.out.println("    merged onto one generalized pin for output.");
	System.out.println();
	System.out.println("    This program only employs the first three columns: Number, ");
	System.out.println("    Name and Type.  This program ignores lines or pin numbers ");
	System.out.println("    starting with '#'.");
	System.out.println();
	System.out.println("  Meta data");
	System.out.println();
	System.out.println("    The first value found is employed.  Additional definitions");
	System.out.println("    are ignored.  Command line input overrides file input as");
	System.out.println("    the primary definition.");
	System.out.println();
	System.out.println("  Pin types");
	System.out.println();
	System.out.println("    Pin types may be gEDA types or TI types.");
	System.out.println();
	System.out.println("    The TI types include 'I', 'O', and 'I/O' for Buffer 'TTL'");
	System.out.println("    or 'Analog', and '-' for Buffer 'Power'.");
	System.out.println();
	System.out.println("  Pin layout");
	System.out.println();
	System.out.println("    Pin layout is defined in a counter-clockwise sequence");
	System.out.println("    starting from TOP, LEFT, BOTTOM or RIGHT sides via the");
	System.out.println("    case insensitive acronym TLBR, LBRT, BRTL, and RTLB.");
	System.out.println();
	System.out.println("    Pin layout is further described in a number of pins ");
	System.out.println("    per side, as in the following example.");
	System.out.println();
	System.out.println("        lbrt:25/25/25/25");
	System.out.println();
	System.out.println("Options");
	System.out.println();
	System.out.println("  Output");
	System.out.println();
	System.out.println("    -s symbol.sym   Write gEDA symbol output to file.  Requires");
	System.out.println("                    full metadata in expanded CSV format.");
	System.out.println();
	System.out.println("Bugs");
	System.out.println();
	System.out.println("  Path");
	System.out.println();
	System.out.println("    Handled as a logo graphic, but not yet as a part outline");
	System.out.println("    with pin layout on a path.");
	System.out.println();
	System.out.println("  License");
	System.out.println();
	System.out.println("    Incomplete handling of license needs additional use and");
	System.out.println("    distribution distinction cases.");
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
	if (null != inf){
	    try {
		Symbol symbol = new Symbol(inf);

		if (null != sf){

		    symbol.write(sf);

		    System.err.printf("Wrote '%s'%n",sf);
		}
		else {

		    symbol.write(System.out);
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
