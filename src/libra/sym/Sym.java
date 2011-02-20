package libra.sym;

import libra.Layout;

/**
 * 
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Sym
    extends libra.Symbol
{


    public Sym(Layout layout, Input in, String a, String d, String u, String l){
	super(layout,a,d,u,l);

	if (in.isPart())
	    this.part = in.getName();

	else if (in.isPath())
	    this.path = in.getData();
	else
	    throw new IllegalArgumentException("Malformed input file missing 'Part' row.");
    }


    public void add(Input in){

	switch(in.row){
	case Part:
	    if (null == this.part)
		this.part = in.getName();
	    break;
	case Package:
	    if (null == this.pack)
		this.pack = in.getName();
	    break;
	case Layout:
	    if (null == this.layout)
		try {
		    this.layout = new Layout(in.getName());
		}
		catch (RuntimeException exc){
		    exc.printStackTrace();
		}
	    break;
	case Description:
	    if (null == this.description)
		this.description = in.getName();
	    break;
	case Documentation: 
	    if (null == this.documentation)
		this.documentation = in.getName();
	    break;
	case Author:
	    if (null == this.author)
		this.author = in.getName();
	    break;
	case License:
	    if (null == this.license)
		this.license = in.getName();
	    break;
	case Footprint:
	    if (null == this.footprint)
		this.footprint = in.getName();
	    break;
	case Number:
	    break;
	case Pin:
	    if (null == this.pins)
		this.add(new Pin(in));
	    else {
		Pin last = (Pin)this.pins[this.pins.length-1];
		if (!last.add(in))
		    this.add(new Pin(in));
	    }
	    break;
	case Path:
	    if (null == this.path)
		this.path = in.getData();
	    break;
	default:
	    throw new Error(in.row.name());
	}
    }
}
