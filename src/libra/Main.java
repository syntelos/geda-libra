package libra;

public class Main
{

    public enum Cmd {
	SCH, SYM, SVG, TABLE, HELP;

	public final static Cmd For(String[] argv){
	    if (0 < argv.length)
		return Cmd.For(argv[0]);
	    else
		return Cmd.HELP;
	}
	public final static Cmd For(String string){
	    if (null == string)
		return Cmd.HELP;
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
	public final static int Invoke(String[] argv){
	    Cmd cmd = Cmd.For(argv);
	    argv = Cmd.Shift(argv);
	    switch(cmd){
	    case SCH:
		libra.sch.Main.main(argv);
		return 0;
	    case SYM:
		libra.sym.Main.main(argv);
		return 0;
	    case SVG:
		libra.svg.Main.main(argv);
		return 0;
	    case TABLE:
		libra.table.Main.main(argv);
		return 0;
	    default:
		System.out.println("Usage");
		System.out.println();
		System.out.println("  java -jar libra.jar cmd args*");
		System.out.println();
		System.out.println("Values of 'cmd'");
		System.out.println();
		for (Cmd c: Cmd.values()){
		    System.out.printf("  %s%n",c.name());
		}
		System.out.println();
		return 1;
	    }
	}
	public final static String[] Shift(String[] argv){
	    int count = argv.length;
	    if (1 < count){
		int nlen = (count-1);
		String[] copier = new String[nlen];
		System.arraycopy(argv,1,copier,0,nlen);
		return copier;
	    }
	    else if (0 == count)
		return argv;
	    else
		return new String[0];
	}
    }

    public static void main(String[] argv){

	System.exit(Cmd.Invoke(argv));
    }
}
