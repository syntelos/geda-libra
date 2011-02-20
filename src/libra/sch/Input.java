package libra.sch;

import java.util.StringTokenizer;

/**
 * CSV file input line string
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Input {

    public enum Row {
	Title, Author, Logo, Size, Headline, Pin;

	public final static Row For(String arg, boolean once){
	    if ('#' == arg.charAt(0))
		throw new IllegalArgumentException();
	    else {
		try {
		    return Row.valueOf(arg);
		}
		catch (RuntimeException exc){

		    if (once)
			return Row.Headline;
		    else
			return Row.Pin;
		}
	    }
	}
    }


    public final Row row;

    public final String string;

    public final String[] terms;



    public Input(String line, boolean once){
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

		this.row = Row.For(this.terms[0],once);
	    }
	    else
		throw new IllegalArgumentException();
	}
    }


    public boolean isTitle(){
	return (Row.Title == this.row);
    }
    public boolean isAuthor(){
	return (Row.Author == this.row);
    }
    public boolean isLogo(){
	return (Row.Logo == this.row);
    }
    public boolean isSize(){
	return (Row.Size == this.row);
    }
    public boolean isHeadline(){
	return (Row.Headline == this.row);
    }
    public boolean isPin(){
	return (Row.Pin == this.row);
    }
    public String getComponent(){
	if (0 < this.terms.length)
	    return this.terms[0];
	else
	    return null;
    }
    public String getPin(){
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
    public String getSignal(){
	if (3 < this.terms.length)
	    return this.terms[3];
	else
	    return null;
    }
    public String toString(){
	return this.string;
    }
}
