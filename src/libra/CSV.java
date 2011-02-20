package libra;

import java.io.BufferedReader ;
import java.io.File ;
import java.io.FileInputStream ;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.IOException ;
import java.io.Reader ;
import java.util.StringTokenizer;

public class CSV
    extends Object
{

    protected final String[][] content;


    public CSV(File file)
	throws IOException
    {
	this(new FileInputStream(file));
    }
    private CSV(InputStream in)
	throws IOException
    {
	this(new InputStreamReader(in,"US-ASCII"));
    }
    private CSV(Reader in)
	throws IOException
    {
	this(new BufferedReader(in));
    }
    private CSV(BufferedReader in)
	throws IOException
    {
	super();
	try {
	    String line;
	    String[][] list = null;
	    while (null != (line = in.readLine())){
		String[] input;
		try {
		    input = Parse(line);
		    if (null == list)
			list = new String[][]{input};
		    else {
			final int len = list.length;
			String[][] copier = new String[len+1][];
			System.arraycopy(list,0,copier,0,len);
			copier[len] = input;
			list = copier;
		    }
		}
		catch (RuntimeException skip){
		    continue;
		}
	    }
	    this.content = list;
	}
	finally {
	    in.close();
	}
    }


    public int size(){
	if (null != this.content)
	    return this.content.length;
	else
	    return 0;
    }
    public String[] get(int idx){
	if (-1 < idx && idx < this.size())
	    return this.content[idx];
	else
	    return null;
    }
    public java.util.Iterator<String[]> content(){
	return new CSV.Iterator(this.content);
    }


    public final static String[] Parse(String line)
	throws IllegalArgumentException
    {
	if (null == line)
	    throw new IllegalArgumentException();
	else if (1 > line.length() || '#' == line.charAt(0))
	    throw new IllegalArgumentException(String.format("\"%s\"",line));
	else {
	    StringTokenizer strtok = new StringTokenizer(line,",\"");
	    final int count = strtok.countTokens();
	    if (0 < count){
		String[] terms = new String[count];
		for (int cc = 0; cc < count; cc++){
		    terms[cc] = strtok.nextToken();
		}
		return terms;
	    }
	    else
		throw new IllegalArgumentException(String.format("\"%s\"",line));
	}
    }

    public static class Iterator
	extends Object
	implements java.util.Iterator<String[]>
    {

	private final String[][] list;
	private final int length;
	private int index;


	public Iterator(String[][] list){
	    super();
	    if (null == list){
		this.list = null;
		this.length = 0;
	    }
	    else {
		this.list = list;
		this.length = list.length;
	    }
	}


	public boolean hasNext(){
	    return (this.index < this.length);
	}
	public String[] next(){
	    return this.list[this.index++];
	}
	public void remove(){
	    throw new UnsupportedOperationException();
	}
    }
}
