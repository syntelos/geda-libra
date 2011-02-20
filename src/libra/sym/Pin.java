package libra.sym;

import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

/**
 * 
 * 
 * <h3>Implementation notes</h3>
 * 
 * Pin extends rectangle for some computational convenience in layout
 * programming.  It employs this (the super) rectangle as a line, like
 * the pin attribute type.
 * 
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Pin
    extends libra.Pin
{

    public Pin(Input in){
	super(in.getPin(),in.pinInverted);

	this.namein = new String[]{
	    in.getName()
	};
	this.typein = new Type[]{
	    Type.For(in.getType())
	};
    }


    public boolean add(Input in){
	if (this.number == in.getPin()){

	    this.namein = Add(this.namein,in.getName());

	    this.typein = Type.Add(this.typein,Type.For(in.getType()));

	    return true;
	}
	else
	    return false;
    }
}
