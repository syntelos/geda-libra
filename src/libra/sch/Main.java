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
	System.out.println("    Sch -i schematic.csv [-s schematic.sch]");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Produce gEDA schematic from schematic description table.");
	System.out.println();
	System.out.println("  Input table");
	System.out.println();
	System.out.println("    The input argument is a CSV file, for example");
	System.out.println("    in the following.");
	System.out.println();
	System.out.println("        Title,Libra Control");
	System.out.println("        Author,John Pritchard <jdp@ulsf.net>");
	System.out.println("        Logo,logo.sym");
	System.out.println("        Size,D");
	System.out.println("        Component,Pin,Signal");
	System.out.println("        ADXL345, 7,SPI_CS_ADXL345");
	System.out.println("        ADXL345,12,SPI_SDIO");
	System.out.println("        ADXL345,13,SPI_SDA");
	System.out.println("        ADXL345,14,SPI_CLK");
	System.out.println();
	System.out.println("  Symbol libraries");
	System.out.println();
	System.out.println("    The named components have symbol tables in the lib");
	System.out.println("    directory.");
	System.out.println();
	System.out.println("    gEDA titles and connectors are in the gEDA home");
	System.out.println("    directory.");
	System.out.println();
	System.out.println("Options");
	System.out.println();
	System.out.println("    -s schematic.sch      Write schematic file, default ");
	System.out.println("                          stdout.");
	System.out.println();
	System.out.println("    -g /usr/share/gEDA    Installation of gEDA contains ");
	System.out.println("                          directory 'sym' and friends.");
	System.out.println();
	System.out.println("                          This location defaults to");
	System.out.println("                              /usr/share/gEDA");
	System.out.println("                          or");
	System.out.println("                              /usr/local/share/gEDA");
	System.out.println("                          or may be maintained in an ");
	System.out.println("                          environment variable named");
	System.out.println("                              GEDA_HOME");
	System.out.println();
	System.out.println("    -l lib                Directory containing symbol");
	System.out.println("                          tables.  This location defaults");
	System.out.println("                          to");
	System.out.println("                              lib");
	System.out.println();
	System.exit(1);
    }
    public enum Opt {
	G, H, I, L, S;

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
	    case G:
		cc += 1;
		if (cc < argc){
		    arg = argv[cc];
		    if (!GedaHome.Define(new File(arg))){
			System.err.printf("Error, directory not found '%s'.%n",arg);
			System.exit(1);
		    }
		}
		else
		    Usage();
		break;
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
	    case L:
		cc += 1;
		if (cc < argc){
		    arg = argv[cc];
		    if (!Lib.Define(new File(arg))){
			System.err.printf("Error, directory not found '%s'.%n",arg);
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
	    if (GedaHome.Found()){
		if (Lib.Found()){
		    try {
			Schematic schematic = new Schematic(inf);

			if (null != sf){

			    schematic.write(sf);

			    System.err.printf("Wrote '%s'%n",sf);
			}
			else {

			    schematic.write(System.out);
			}
			System.exit(0);
		    }
		    catch (Exception exc){
			exc.printStackTrace();
			System.exit(1);
		    }
		}
		else {
		    System.err.println("Error, unable to locate project symbol library directory.");
		    System.exit(1);
		}
	    }
	    else {
		System.err.println("Error, unable to locate gEDA installation directory.");
		System.exit(1);
	    }
	}
	else
	    Usage();
    }

}
