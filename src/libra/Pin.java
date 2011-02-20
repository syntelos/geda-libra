package libra;

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
    extends Attribute
    implements Comparable<Pin>
{
    public enum Type {
	in, out, io, oc, oe, pas, tp, tri, clk, pwr;

	public final static Type For(String type){
	    if (null != type){
		try {
		    return Type.valueOf(type.toLowerCase());
		}
		catch (RuntimeException exc){

		    if ("I".equalsIgnoreCase(type))
			return Type.in;
		    else if ("O".equalsIgnoreCase(type))
			return Type.out;
		    else if ("I/O".equalsIgnoreCase(type))
			return Type.io;
		    else
			return Type.pas;
		}
	    }
	    else
		return null;
	}

	public final static Type[] Add(Type[] list, Type item){
	    if (null == item)
		return list;
	    else if (null == list)
		return new Type[]{item};
	    else {
		int len = list.length;
		Type[] copier = new Type[len+1];
		System.arraycopy(list,0,copier,0,len);
		copier[len] = item;
		return copier;
	    }
	}
    }

    public final int number, sequence;

    public final boolean inverted;

    protected String[] namein;

    protected Type[] typein;

    public String style = "line", net = "";

    protected Layout.Position posit;

    protected String name;

    protected Type type;

    protected Label label;


    public Pin(int number, boolean inverted){
	super(Attribute.Type.P);
	this.number = number;
	this.sequence = (this.number - 1);
	this.inverted = inverted;
    }


    public String getName(){
	if (null == this.name){
	    StringBuilder strbuf = new StringBuilder();
	    for (String name: this.namein){
		if (0 < strbuf.length())
		    strbuf.append('/');
		strbuf.append(name);
	    }
	    this.name = strbuf.toString();
	}
	return this.name;
    }
    public Pin.Type getType(){
	if (null == this.type){
	    if (1 == this.typein.length)
		return (this.type = this.typein[0]);
	    else {
		int io = 0;
		for (Type type: this.typein){

		    switch (type){
		    case in:
			io |= 1;
			break;
		    case out:
			io |= 2;
			break;

		    default:
			return (this.type = Type.io);
		    }
		}
		switch (io){
		case 1:
		    return (this.type = Type.in);
		case 2:
		    return (this.type = Type.out);
		case 3:
		    return (this.type = Type.io);
		}
	    }
	}
	return this.type;
    }
    public Layout.Position getPosition(Layout layout){
	if (null == this.posit)
	    this.posit = layout.getPosition(this);
	return this.posit;
    }
    public Layout.Position getPosition(){
	if (null == this.posit)
	    throw new IllegalStateException("undefined");
	else
	    return this.posit;
    }
    public Attribute getLabel(){

	for (Attribute at: this){
	    if (at.isPinLabel())
		return at;
	}
	return null;
    }
    public int layout0(){

	return Layout.Dimension.Label(this.getName());
    }
    public void layout1(Layout.Dimension b){

	if (null != this.posit){

	    switch(this.posit){
	    case T:
		/*
		 *    [0]
		 *     |
		 *  --[1]--
		 */
		this.xyxy( (b.x),                      (b.y+Layout.Dimension.Y0),  (b.x),   (b.y));
		break;
	    case L:
		/*
		 *       |
		 * [0]--[1]
		 *       |
		 */
		this.xyxy( (0),                        (b.y),                      (b.x),   (b.y));

		break;
	    case B:
		/*
		 *  --[1]--
		 *     |
		 *    [0]  
		 */
		this.xyxy( (b.x),                      (0),                        (b.x),   (b.y));

		break;
	    case R:
		/*
		 *   |
		 *  [1]--[0]
		 *   |     
		 */
		this.xyxy( (b.x+Layout.Dimension.X0),  (b.y),                      (b.x),   (b.y));

		break;
	    }

	    this.clear();

	    final Attribute pin = this.pin();
	    {
		this.add(Attribute.Type.T).text("pinseq",this.sequence).loc(pin);
		this.add(Attribute.Type.T).text("pintype",this.getType()).loc(pin);
	    }
	    switch(this.posit){
	    case T:
		this.add(Attribute.Type.T).text("pinnumber",this.number,Attribute.Show.Value).rel(pin,-50,-205).aa(90,0);
		this.add(Attribute.Type.T).text("pinlabel",this.getName(),Attribute.Show.Value).rel(pin,0,-355).aa(90,6).layoutText();
		break;
	    case L:
		this.add(Attribute.Type.T).text("pinnumber",this.number,Attribute.Show.Value).rel(pin,205,45).aa(0,6);
		this.add(Attribute.Type.T).text("pinlabel",this.getName(),Attribute.Show.Value).rel(pin,355,-5).aa(0,0).layoutText();
		break;
	    case B:
		this.add(Attribute.Type.T).text("pinnumber",this.number,Attribute.Show.Value).bot(pin,-50,205).aa(90,6);
		this.add(Attribute.Type.T).text("pinlabel",this.getName(),Attribute.Show.Value).bot(pin,0,355).aa(90,0).layoutText();
		break;
	    case R:
		this.add(Attribute.Type.T).text("pinnumber",this.number,Attribute.Show.Value).rel(pin,-205,45).aa(0,0);
		this.add(Attribute.Type.T).text("pinlabel",this.getName(),Attribute.Show.Value).rel(pin,-355,-5).aa(0,6).layoutText();
		break;
	    }
	    if (this.inverted)
		this.add(this.logicbubble(this.posit));
	}
	else
	    throw new IllegalStateException("Missing layout position");
    }
    public int compareTo(Pin that){
	if (this.number == that.number)
	    return 0;
	else if (this.number < that.number)
	    return -1;
	else
	    return 1;
    }

    public final static String[] Add(String[] list, String item){
	if (null == item)
	    return list;
	else if (null == list)
	    return new String[]{item};
	else {
	    int len = list.length;
	    String[] copier = new String[len+1];
	    System.arraycopy(list,0,copier,0,len);
	    copier[len] = item;
	    return copier;
	}
    }
    public final static Pin[] Add(Pin[] list, Pin item){
	if (null == item)
	    return list;
	else if (null == list)
	    return new Pin[]{item};
	else {
	    int len = list.length;
	    Pin[] copier = new Pin[len+1];
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
	implements java.util.Iterator<Pin>
    {

	private final Pin[] list;
	private final int length;
	private int index;


	public Iterator(Pin[] list){
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
	public Pin next(){
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
	implements java.lang.Iterable<Pin>
    {
	private final Pin[] list;


	public Iterable(Pin[] list){
	    super();
	    this.list = list;
	}

	public java.util.Iterator<Pin> iterator(){
	    return new Pin.Iterator(this.list);
	}
    }
}
