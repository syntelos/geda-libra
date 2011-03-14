package libra.sch;

import libra.Attribute;
import libra.Layout;
import libra.Lib;
import libra.Pin;
import libra.Symbol;

import java.io.IOException;

/**
 * A net has a lib symbol and a schematic component use of that
 * symbol.
 */
public class Net
    extends Component
{

    public final int pin;

    public final String basename, netname;

    private Symbol targetSymbol;

    private Pin targetPin;


    public Net(Symbol target, String[] line)
	throws IOException
    {
	this(target,Integer.parseInt(line[1]),line[2]);
    }
    private Net(Symbol targetSymbol, int pin, String signal)
	throws IOException
    {
	super(SymName(signal));
	this.pin = pin;
	this.basename = BaseName(signal);
	this.netname = NetName(signal);
	this.targetSymbol = targetSymbol;
	if (null != this.targetSymbol){
	    this.targetPin = targetSymbol.getPin(this.pin);

	    this.angle = this.targetPin.angle;
	    this.alignment = this.targetPin.alignment;

	    if (null != this.targetPin){

		if (null == this.symbol){
		    this.generateNetSymbolTo(targetSymbol);
		}
	    }
	    else
		throw new IllegalStateException(String.format("Null pin '%d' for net '%s' from target '%s'.",pin,signal,targetSymbol.part));
	}
	else
	    throw new IllegalStateException(String.format("Missing target symbol for net '%s'.",signal));
    }


    @Override
    public boolean markup(Attribute ap){
	Component parent = (Component)ap;

	int tx = parent.x1;
	int ty = parent.y1;

	return true;
    }
    public Net generateNetSymbolTo(Symbol targetSymbol)
	throws IOException
    {

	if (null != this.symbol){
	    this.symbol.destroy();
	}

	this.symbol = new Symbol("P 200 0 200 200 1 0 0");
	this.symbol.add(Attribute.Type.T).text("pinseq",0).loc(this.symbol);
	this.symbol.add(Attribute.Type.T).text("pintype",this.targetPin.getType()).loc(this.symbol);
	this.symbol.add(Attribute.Type.T).text("pinnumber",1).loc(this.symbol);
	this.symbol.add(Attribute.Type.T).text("pinlabel",this.basename).loc(this.symbol);
	Attribute underbar = this.symbol.add(Attribute.Type.L).line(50, 200, 350, 200, 3);
	Attribute label = this.symbol.add(Attribute.Type.T).text(null,this.basename,9,8,1).show(Attribute.Show.Value).layoutTextToUnderline(underbar);
	this.symbol.add(Attribute.Type.T).text("net",this.netname,8,8,0).loc(label);
	this.symbol.write(Lib.Net(this.name));

	return this;
    }


    public final static String NetName(String string){
	int idx = string.indexOf(':');
	if (0 < idx)
	    return string;
	else
	    return string+":1";
    }
    public final static String BaseName(String string){
	int idx = string.indexOf(':');
	if (0 < idx)
	    return string.substring(0,idx);
	else
	    return string;
    }
    public final static String SymName(String string){
	int idx = string.indexOf(':');
	if (0 < idx)
	    return string.replace(':','-');
	else
	    return string+"-1";
    }
    public final static Net[] Add(Net[] list, Net item){
	if (null == item)
	    return list;
	else if (null == list)
	    return new Net[]{item};
	else {
	    int len = list.length;
	    Net[] copier = new Net[len+1];
	    System.arraycopy(list,0,copier,0,len);
	    copier[len] = item;
	    return copier;
	}
    }
    /**
     * 
     */
    public static class Iterator
	extends Object
	implements java.util.Iterator<Net>
    {

	private final Net[] list;
	private final int length;
	private int index;


	public Iterator(Net[] list){
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
	public Net next(){
	    return this.list[this.index++];
	}
	public void remove(){
	    throw new UnsupportedOperationException();
	}
    }
    /**
     * 
     */
    public static class Iterable
	extends Object
	implements java.lang.Iterable<Net>
    {
	private final Net[] list;


	public Iterable(Net[] list){
	    super();
	    this.list = list;
	}

	public java.util.Iterator<Net> iterator(){
	    return new Net.Iterator(this.list);
	}
    }
}
