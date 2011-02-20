package libra.sym;

import java.util.StringTokenizer;

/**
 * CSV file input line string
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Input {

    public enum Row {
	Part, Package, Layout, Description, Documentation, Author, License, Footprint, Number, Pin, Path;

	public final static Row For(String arg){
	    if ('#' == arg.charAt(0))
		throw new IllegalArgumentException();
	    else {
		try {
		    return Row.valueOf(arg);
		}
		catch (RuntimeException exc){

		    return Row.Pin;
		}
	    }
	}
    }


    public final Row row;

    public final String string;

    public final String[] terms;

    public final int pin;

    public final boolean pinInverted;


    public Input(String line){
	super();
	this.string = line;
	if ('#' == line.charAt(0))
	    throw new IllegalArgumentException();
	else {
	    StringTokenizer strtok = new StringTokenizer(line,",\"");
	    final int count = strtok.countTokens();
	    if (0 < count){
		String[] terms = new String[count];
		for (int cc = 0; cc < count; cc++){
		    terms[cc] = strtok.nextToken();
		}
		this.terms = terms;

		this.row = Row.For(this.terms[0]);

		if (Row.Pin == this.row){
		    this.pin = Integer.parseInt(this.terms[0]);
		    this.pinInverted = (1 < this.terms.length && this.terms[1].startsWith("\\_") && this.terms[1].endsWith("\\_"));
		}
		else {
		    this.pin = -1;
		    this.pinInverted = false;
		}
	    }
	    else
		throw new IllegalArgumentException();
	}
    }


    public boolean isPart(){
	return (Row.Part == this.row);
    }
    public boolean isPath(){
	return (Row.Path == this.row);
    }
    public boolean isPackage(){
	return (Row.Package == this.row);
    }
    public boolean isLayout(){
	return (Row.Layout == this.row);
    }
    public boolean isDescription(){
	return (Row.Description == this.row);
    }
    public boolean isDocumentation(){
	return (Row.Documentation == this.row);
    }
    public boolean isAuthor(){
	return (Row.Author == this.row);
    }
    public boolean isLicense(){
	return (Row.License == this.row);
    }
    public boolean isHeadline(){
	return (Row.Number == this.row);
    }
    public boolean isPin(){
	return (Row.Pin == this.row);
    }
    public int getPin(){
	return this.pin;
    }
    public String getName(){
	if (1 < this.terms.length)
	    return this.terms[1];
	else
	    return null;
    }
    public String getType(){
	if (2 < this.terms.length)
	    return this.terms[2];
	else
	    return null;
    }
    public String getData(){
	final int count = (this.terms.length-1);
	if (0 < count){
	    StringBuilder strbuf = new StringBuilder();
	    strbuf.append(this.terms[1]);
	    for (int cc = 2; cc < count; cc++){
		strbuf.append(',');
		strbuf.append(this.terms[cc]);
	    }
	    return strbuf.toString();
	}
	else
	    return null;
    }
    public String toString(){
	return this.string;
    }
}
