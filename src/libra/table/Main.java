package libra.table;

public class Main
{

    public enum Cmd {
	HELP;

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
    }

    public static void main(String[] argv){
        System.exit(1);
    }
}
