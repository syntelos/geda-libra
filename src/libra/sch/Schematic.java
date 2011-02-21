package libra.sch;

import libra.Attribute;
import libra.CSV;

import java.io.File ;
import java.io.FileOutputStream ;
import java.io.IOException ;
import java.io.OutputStream ;
import java.io.PrintStream ;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

public class Schematic
    extends Attribute
{

    public enum Row {
	Title, Author, Size, Logo,
	Headline, 
	Net;

	public final static Row For(String arg, boolean once){
	    if (once){
		try {
		    return Row.valueOf(arg);
		}
		catch (RuntimeException exc){
		    try {
			Integer.parseInt(arg);
			return Row.Net;
		    }
		    catch (NumberFormatException nfx){
			return Row.Headline;
		    }
		}
	    }
	    else
		return Row.Net;
	}
    }


    public String title, author, size, logo;

    public Component[] components;


    public Schematic(){
	super(Attribute.Type.C);
    }
    public Schematic(File file)
	throws IOException
    {
	this();
	CSV csv = new CSV(file);
	boolean once = true;
	for (String[] line: csv.content){
	    once = this.add(line,once);
	}
    }


    public void write(File sym)
	throws IOException
    {
	OutputStream out = null;
	try {
	    out = this.write(new FileOutputStream(sym));
	}
	finally {
	    if (null != out)
		out.close();
	}
    }
    public OutputStream write(OutputStream sym)
	throws IOException
    {
	return this.write(new PrintStream(sym));
    }
    public PrintStream write(PrintStream out)
	throws IOException
    {

	if (this.layout()){

	    if (this.markup()){

		out.println(this);

		for (Component component: this.components){

		    out.println(component);
		}
		return out;
	    }
	    else
		throw new IllegalStateException("Missing layout.");
	}
	else
	    throw new IllegalStateException("Missing layout.");
    }
    public boolean add(String[] line, boolean once){

	final Row row = Row.For(line[0],once);

	final String value;
	if (1 < line.length)
	    value = line[1];
	else
	    value = null;

	switch(row){
	case Title:
	    if (null == this.title)
		this.title = value;
	    return true;
	case Author:
	    if (null == this.author)
		this.author = value;
	    return true;
	case Size:
	    if (null == this.size)
		this.size = value;
	    return true;
	case Logo:
	    if (null == this.logo)
		this.logo = value;
	    return true;
	case Headline:
	    return false;
	case Net:
	    if (null == this.components)
		this.add(new Component(line));
	    else {
		Component last = (Component)this.components[this.components.length-1];
		if (!last.add(line))
		    this.add(new Component(line));
	    }
	    return false;
	default:
	    throw new Error(row.name());
	}
    }
    public void add(Component c){

	this.components = Component.Add(this.components,c);
    }
    public boolean layout(){
	return true;
    }
    public boolean markup(){
	return true;
    }
    public java.lang.Iterable<Component> components(){
	return new Component.Iterable(this.components);
    }
}
