package libra.sch;

import libra.Attribute;
import libra.GedaHome;
import libra.Lib;
import libra.Pin;
import libra.Rectangle;
import libra.Symbol;

import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.io.PrintStream ;

/**
 * Read schematic table for schematic generation.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Main
    extends libra.io.FileIO
{

    public static void Usage(){
	System.out.println("Usage");
	System.out.println();
	System.out.println("    sch -p symbol+ [-t schematic.csv]");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Conglomerate a schematic description table from the");
	System.out.println("    symbols named as arguments to option '-p'.  ");
	System.out.println();
	System.out.println("    The generated table needs to be edited from multiple to");
	System.out.println("    single signals per pin, and pin rows deleted for not");
	System.out.println("    generating nets.");
	System.out.println();
	System.out.println("    The option '-l' may be employed to identify the ");
	System.out.println("    directory containing the named symbols.");
	System.out.println();
	System.out.println("  For example");
	System.out.println();
	System.out.println("        java -jar sch.jar -p ADXL345 HMC5834 -l test/sym/src \\");
	System.out.println("           -t test/sch/dst/schematic-0.csv");
	System.out.println();
	System.out.println("Usage");
	System.out.println();
	System.out.println("    Sch -i schematic.csv [-s schematic.sch]");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Generate a gEDA schematic from a schematic description table.");
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
	System.out.println("    -i schematic.csv      Schematic table for generating");
	System.out.println("                          schematic file.");
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
	System.out.println("  For example");
	System.out.println();
	System.out.println("        java -jar sch.jar -i test/sch/src/schematic-0.csv \\");
	System.out.println("           -l test/sym/src -s test/sch/dst/schematic-0.sch");
	System.out.println();
	System.out.println("Bugs");
	System.out.println();
	System.out.println("  Lib");
	System.out.println();
	System.out.println("    Only accepts one lib dir.");
	System.out.println();
	System.exit(1);
    }
    public enum Opt {
	G, H, I, L, P, S, T;

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

	String[] symbolNames = null;

	File generateTableTo = null, generateSchematicFrom = null, schematicFile = null;

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
		    generateSchematicFrom = new File(arg);
		    if (!generateSchematicFrom.exists()){
			System.err.printf("Error, file not found '%s'.%n",generateSchematicFrom.getPath());
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
	    case P:
		cc += 1;
		if (cc < argc){
		    arg = argv[cc];
		    File p = new File(arg);
		    if (p.isDirectory()){
			File[] plist = ListFiles(p,".sym");
			if (null != plist){
			    final int count = plist.length;
			    symbolNames = new String[count];
			    for (int c = 0; c < count; c++){
				symbolNames[c] = Lib.Basename(plist[c].getName());
			    }
			}
			else {
			    System.err.printf("Error, symbol files '*.sym' not found in directory '%s'.%n",arg);
			    System.exit(1);
			}
		    }
		    else {
			arg = Lib.Basename(argv[cc]);
			if (0 < arg.length() && '-' != arg.charAt(0)){
			    symbolNames = new String[]{arg};
			    for (int tt = (cc+1); tt < argc; tt++){
				arg = Lib.Basename(argv[tt]);
				if (0 < arg.length()){
				    if ('-' == arg.charAt(0))
					break;
				    else {
					symbolNames = Pin.Add(symbolNames,arg);
					cc = tt;
				    }
				}
			    }
			}
			else
			    Usage();
		    }
		}
		else
		    Usage();
		break;
	    case S:
		cc += 1;
		if (cc < argc){
		    arg = argv[cc];
		    schematicFile = new File(arg);
		}
		else
		    Usage();
		break;
	    case T:
		cc += 1;
		if (cc < argc){
		    arg = argv[cc];
		    generateTableTo = new File(arg);
		}
		else
		    Usage();
		break;
	    default:
		throw new Error(opt.name());
	    }
	}
	/*
	 * Generate Schematic
	 */
	if (null != generateSchematicFrom){
	    if (GedaHome.Found()){
		if (Lib.Found()){
		    if (generateSchematicFrom.isDirectory()){
			File dst;
			for (File src: ListFiles(generateSchematicFrom,".csv")){

			    try {
				Schematic schematic = new Schematic(src);

				if (null != schematicFile){

				    if (schematicFile.isDirectory()){
					dst = FilenameMap(src,schematicFile,"sch");
				    }
				    else
					dst = schematicFile;

				    schematic.write(dst);

				    System.err.printf("Wrote '%s'%n",dst);
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
		    }
		    else {
			try {
			    Schematic schematic = new Schematic(generateSchematicFrom);

			    if (null != schematicFile){

				if (schematicFile.isDirectory()){
				    schematicFile = FilenameMap(generateSchematicFrom,schematicFile,"sch");
				}

				schematic.write(schematicFile);

				System.err.printf("Wrote '%s'%n",schematicFile);
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
	/*
	 * Generate Table
	 */
	else if (null != symbolNames){
	    final int count = symbolNames.length;
	    /*
	     */
	    Symbol[] symbols = new Symbol[count];
	    for (int cc = 0; cc < count; cc++){
		symbols[cc] = Lib.For(symbolNames[cc]);
	    }
	    Rectangle.SortDescending(symbols);
	    /*
	     */
	    PrintStream out = System.out;
	    try {
		if (null != generateTableTo){
		    if (generateTableTo.isDirectory()){
			String filename = Cat("schematic",'-',symbolNames,".csv");
			generateTableTo = new File(generateTableTo,filename);
			out = new PrintStream(new FileOutputStream(generateTableTo));
		    }
		    else
			out = new PrintStream(new FileOutputStream(generateTableTo));
		}
		try {
		    out.println("Title,");
		    out.println("Author,");
		    out.println("Logo,");
		    out.println("Size,");
		    out.println("Component,Pin,Signal");
		    for (int cc = 0; cc < count; cc++){
			Symbol symbol = symbols[cc];
			for (Attribute ap: symbol.pins()){
			    Pin pin = (Pin)ap;
			    if (pin.isNotPassive())
				out.printf("%s,%s,%s%n",symbol.part,pin.number,pin.getName(0));
			}
		    }
		}
		finally {
		    if (null != generateTableTo)
			out.close();
		}
	    }
	    catch (IOException exc){

		exc.printStackTrace();
		System.exit(1);
	    }
	}
	else
	    Usage();
    }

}
