package libra.sym;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Write gEDA GSCHEM symbol.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Geda
    extends java.io.PrintStream
{
    public final static String Vdate = "20100214", Vnumber = "2";




    public Geda(File sym)
	throws IOException
    {
	super(new FileOutputStream(sym));
    }
    public Geda(OutputStream out){
	super(out);
    }


    public boolean layout(Sym sym){

	if (sym.layout()){

	    return sym.markup();
	}
	else if (sym.markup()){

	    return false;
	}
	else
	    throw new IllegalStateException("Missing layout for gEDA symbol generation.");
    }
    public void write(Sym sym) throws IOException {

	if (this.layout(sym)){

	    this.println(this);

	    this.println(sym);

	    for (Object pin: sym.pins){

		this.println(pin);
	    }
	}
	else {

	    this.println(this);

	    this.println(sym);
	}
    }

    public String toString(){
	return String.format("v %s %s",Vdate,Vnumber);
    }
}
