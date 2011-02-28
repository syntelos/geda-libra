package libra.io;

import java.io.File;

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
    public final static File FilenameMap(File src, File tgt, String fext){
	if (tgt.isDirectory() && src.isFile()){

	    if ('.' == fext.charAt(0))
		return new File(tgt, Basename(src.getName())+fext);
	    else
		return new File(tgt, Basename(src.getName())+'.'+fext);
	}
	else
	    throw new IllegalArgumentException();
    }
    public final static class FilenameFilter
	extends Object
	implements java.io.FilenameFilter
    {
	public final String suffix;

	public FilenameFilter(String suffix){
	    super();
	    if (null != suffix)
		this.suffix = suffix;
	    else
		throw new IllegalArgumentException();
	}

	public boolean accept(File dir, String name){
	    return name.endsWith(this.suffix);
	}
    }
    public final static File[] ListFiles(File dir, String suffix){
	return dir.listFiles(new FilenameFilter(suffix));
    }
    public final static String Cat(String prefix, char sep, String[] infix, String suffix){
	if (null != prefix){
	    StringBuilder string = new StringBuilder();
	    string.append(prefix);
	    if (null != infix){
		for (String in: infix){
		    string.append(sep);
		    string.append(in);
		}
	    }
	    if (null != suffix){

		string.append(suffix);
	    }
	    return string.toString();
	}
	else
	    throw new IllegalArgumentException();
    }
}
