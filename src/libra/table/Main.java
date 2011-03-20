package libra.table;

import java.io.File;

/**
 * Manage an entire project in one ODS spreadsheet.
 */
public class Main
{

    public enum Cmd {
	HELP, RUN;

	public final static Cmd For(String[] argv){
	    if (0 < argv.length)
		return Cmd.For(argv[0]);
	    else
		return Cmd.RUN;
	}
	public final static Cmd For(String string){
	    if (null == string)
		return Cmd.RUN;
	    else {
		while (0 < string.length() && '-' == string.charAt(0))
		    string = string.substring(1);
		try {
		    return Cmd.valueOf(string.toUpperCase());
		}
		catch (RuntimeException exc){
		    return Cmd.HELP;
		}
	    }
	}
    }

    public static void usage(){
	System.out.println("Usage");
	System.out.println();
	System.out.println("  table [file.ods]");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("  Manage an entire project in one ODF spreadsheet");
	System.out.println("  (ODS file).");
	System.out.println();
        System.exit(1);
    }
    public static void main(String[] argv){
	final int argc = argv.length;
	final Cmd cmd = Cmd.For(argv);
	switch(cmd){
	case HELP:
	    usage();
	    break;
	case RUN:
	    if (1 < argc){
		File ods = new File(argv[1]);
		if (ods.isFile())
		    new Gui(ods);
		else {
		    System.err.printf("File not found '%s'.%n",ods.getPath());
		    System.exit(1);
		}
	    }
	    else
		new Gui();
	    break;
	default:
	    throw new Error(cmd.name());
	}
    }
}
