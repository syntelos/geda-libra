package libra;

import java.io.BufferedReader ;
import java.io.File ;
import java.io.FileInputStream ;
import java.io.InputStream ;
import java.io.InputStreamReader ;
import java.io.IOException ;
import java.io.Reader ;
import java.util.StringTokenizer;

/**
 * Parse a gEDA/gaf file into a list of attributes.
 */
public class GAF
    extends Object
    implements Iterable<Attribute>
{

    public Attribute[] attributes;


    public GAF(File file)
	throws IOException
    {
	this(new FileInputStream(file));
    }
    private GAF(InputStream in)
	throws IOException
    {
	this(new InputStreamReader(in,"US-ASCII"));
    }
    private GAF(Reader in)
	throws IOException
    {
	this(new BufferedReader(in));
    }
    private GAF(BufferedReader in)
	throws IOException
    {
	super();
	int lno = 0;
	try {
	    String line;
	    Attribute[] list = null;
	    Attribute current = null, last = null;
	    boolean children = false;
	    while (null != (line = in.readLine())){
		lno += 1;
		if (null != last && last.textCompleteNot())
		    last.add(line);
		else {
		    switch (line.charAt(0)){
		    case '{':
			if (null != current){
			    if (1 == line.length())
				children = true;
			    else
				throw new IllegalStateException(String.format("File format error on line %d: '%s'",lno,line));
			}
			else
			    throw new IllegalStateException(String.format("File format error on line %d: '%s'",lno,line));
			break;
		    case '}':
			if (null != current){
			    if (1 == line.length())
				children = false;
			    else
				throw new IllegalStateException(String.format("File format error on line %d: '%s'",lno,line));
			}
			else
			    throw new IllegalStateException(String.format("File format error on line %d: '%s'",lno,line));
			break;
		    default:
			try {
			    last = new Attribute(line);
			    if (children)
				current.children = Attribute.Add(current.children,last);
			    else {
				list = Attribute.Add(list,last);
				current = last;
			    }
			}
			catch (RuntimeException skip){
			}
			break;
		    }
		}
	    }
	    this.attributes = list;
	}
	finally {
	    in.close();
	}
    }


    public int size(){
	if (null != this.attributes)
	    return this.attributes.length;
	else
	    return 0;
    }
    public Attribute get(int idx){
	if (-1 < idx && idx < this.size())
	    return this.attributes[idx];
	else
	    return null;
    }
    public Attribute first(){
	if (null != this.attributes)
	    return this.attributes[0];
	else
	    return null;
    }
    public Attribute[] tail(){
	if (null != this.attributes)
	    return Attribute.Tail(this.attributes);
	else
	    return null;
    }
    public java.util.Iterator<Attribute> iterator(){
	return new Attribute.Iterator(this.attributes);
    }
}
