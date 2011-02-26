package libra.io;

public abstract class FileIO
    extends Object
{

    /**
     * String basename
     */
    public final static String Basename(String name){
	int[] indeces = BasenameScan(name);
	if (null != indeces)
	    return name.substring(indeces[0],indeces[1]);
	else
	    return name;
    }
    private final static int[] BasenameScan(String name){
	/*
	 * single pass basename scan
	 */
	final char[] cary = name.toCharArray();
	final int count = cary.length;
	final int fext = (count-4); // .sym
	int[] indeces = null;
	scan:
	for (int cc = 0; cc < count; cc++){
	    switch(cary[cc]){
	    case '/':
	    case '\\':
		indeces = new int[]{cc+1,count};
		indeces[0] = cc+1;
		break;
	    case '.':
		if (cc >= fext){
		    if (null == indeces)
			indeces = new int[]{0,cc};
		    else
			indeces[1] = cc;
		    break scan;
		}
		else
		    break;
	    default:
		break;
	    }
	}
	return indeces;
    }
}
