package libra;

import java.io.IOException ;
import java.io.PrintStream ;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * 
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Symbol
    extends Attribute
{
    public final static int Debug ;
    static {
	int debug = 0;
	try {
	    String config = System.getProperty("Debug");
	    if (null != config){
		debug = Integer.parseInt(config);
	    }
	}
	catch (Exception exc){
	}
	Debug = debug;
    }

    public Layout layout;

    public Pin[] pins;

    public String part, pack, footprint, version, 
	author, description, documentation, license, path;

    private Iterable<Attribute> left, bottom, right, top;

    private Rectangle[] intersects;

    private Iterable<Rectangle> intersectsI;


    public Symbol(Layout layout, String a, String d, String u, String l){
	super(Attribute.Type.B);
	this.layout = layout;
	this.author = a;
	this.description = d;
	this.documentation = u;
	this.license = l;
	this.version = DF.format(new Date());
    }
    public Symbol(Layout layout){
	super(Attribute.Type.B);
	this.layout = layout;
	this.version = DF.format(new Date());
    }
    public Symbol(){
	super(Attribute.Type.B);
	this.version = DF.format(new Date());
    }


    public int count(){
	if (null != this.pins)
	    return this.pins.length;
	else
	    return 0;
    }
    public String getPart(){
	if (null != this.part)
	    return this.part;
	else
	    return "";
    }
    public String getFootprint(){
	if (null != this.footprint)
	    return this.footprint;
	else if (null != this.pack)
	    return this.pack;
	else
	    return "";
    }
    public Layout.Position getPosition(Pin p){
	if (null != this.layout)
	    return p.getPosition(this.layout);
	else
	    return Layout.Position.L;
    }
    public void add(Pin p){

	this.pins = Pin.Add(this.pins,p);
    }
    public boolean layout(){
	if (null != this.pins){
	    java.util.Arrays.sort(this.pins);
	    if (null != this.layout){
		final Layout layout = this.layout;
		/*
		 * Initial layout of pins and their labels
		 */
		this.layoutPins(layout.layout0(this));

		/*
		 * Separation of labels
		 */

		this.intersects = null;

		int dL = 0, dR = 0, dT = 0, dB = 0;


		for (Attribute left: this.left()){

		    for (Attribute test: layout.intersect(this,Layout.Position.L)){

			Rectangle ir = left.intersection(test);
			if (ir.isNotEmpty()){

			    this.intersects = Rectangle.Add(this.intersects,ir);

			    dL = Math.max(dL,ir.width);
			}
		    }

		}


		for (Attribute right: this.right()){

		    for (Attribute test: layout.intersect(this,Layout.Position.R)){

			Rectangle ir = right.intersection(test);
			if (ir.isNotEmpty()){

			    this.intersects = Rectangle.Add(this.intersects,ir);

			    dR = Math.max(dR,ir.width);
			}
		    }
		}


		for (Attribute top: this.top()){

		    for (Attribute test: layout.intersect(this,Layout.Position.T)){

			Rectangle ir = top.intersection(test);
			if (ir.isNotEmpty()){

			    this.intersects = Rectangle.Add(this.intersects,ir);

			    dT = Math.max(dT,ir.height);
			}
		    }
		}

		for (Attribute bottom: this.bottom()){

		    for (Attribute test: layout.intersect(this,Layout.Position.B)){

			Rectangle ir = bottom.intersection(test);
			if (ir.isNotEmpty()){

			    this.intersects = Rectangle.Add(this.intersects,ir);

			    dB = Math.max(dB,ir.height);
			}
		    }
		}
		/*
		 * Final layout of pins
		 */
		if (null != this.intersects){

		    int dw = Layout.Dimension.Delta(Math.max(dL,dR)<<1);
		    int dh = Layout.Dimension.Delta(Math.max(dT,dB)<<1);

		    this.layoutPins(layout.layout1(dw,dh));
		}
		return true;
	    }
	    else
		throw new IllegalStateException("Missing layout");
	}
	else if (null != this.path)
	    return false;
	else
	    throw new IllegalStateException("Missing pins or path");
    }
    private void layoutPins(Layout.Dimension b){
	{
	    this.left = null;
	    this.bottom = null;
	    this.right = null;
	    this.top = null;
	}
	b.init();

	for (Pin p: this.pins()){
	    switch(p.getPosition(this.layout)){
	    case T:
		b.top();
		/*
		 *    [0]
		 *     |
		 *  --[1]--
		 */
		p.layout1(b);

		break;
	    case L:
		b.left();
		/*
		 *       |
		 * [0]--[1]
		 *       |
		 */
		p.layout1(b);

		break;
	    case B:
		b.bottom();
		/*
		 *  --[1]--
		 *     |
		 *    [0]  
		 */
		p.layout1(b);

		break;
	    case R:
		b.right();
		/*
		 *   |
		 *  [1]--[0]
		 *   |     
		 */
		p.layout1(b);

		break;
	    }
	}
    }
    public boolean markup(){

	if (null != this.pins){
	    java.util.Arrays.sort(this.pins);
	    if (null != this.layout){
		Layout.Dimension box = this.layout.getSize();

		final Attribute bb = this.set(box);

		this.add(Attribute.Type.T).text("refdes","U?").rel(bb,300,100);

		final Attribute cc = this.add(Attribute.Type.T).text("device",this.getPart(),Attribute.Show.Value).center(box.cx,box.cy);

		if (null != this.description)
		    this.add(Attribute.Type.T).text("description",this.description).loc(cc);

		if (null != this.documentation)
		    this.add(Attribute.Type.T).text("documentation",this.documentation).loc(cc);

		if (null != this.author)
		    this.add(Attribute.Type.T).text("author",this.author).loc(cc);

		if (null != this.license){
		    this.add(Attribute.Type.T).text("dist-license",this.license).loc(cc);
		    this.add(Attribute.Type.T).text("use-license",this.license).loc(cc);
		}

		this.add(Attribute.Type.T).text("numslots","0").loc(cc);

		this.add(Attribute.Type.T).text("footprint",this.getFootprint()).loc(cc);

		return true;
	    }
	    else
		return false;
	}
	else if (null != this.path){

	    final Attribute bb = this.path(this.path);

	    this.add(Attribute.Type.T).text("graphical",1);

	    if (null != this.description)
		this.add(Attribute.Type.T).text("description",this.description);

	    if (null != this.documentation)
		this.add(Attribute.Type.T).text("documentation",this.documentation);

	    if (null != this.author)
		this.add(Attribute.Type.T).text("author",this.author);

	    if (null != this.license){
		this.add(Attribute.Type.T).text("dist-license",this.license);
		this.add(Attribute.Type.T).text("use-license",this.license);
	    }

	    return true;
	}
	else
	    return false;
    }

    public java.lang.Iterable<Pin> pins(){
	return new Pin.Iterable(this.pins);
    }
    public String toString(){
	if (0 == Debug)
	    return super.toString();
	else {

	    StringBuilder string = new StringBuilder();

	    string.append( super.toString());

	    if (1 < Debug){

		for (Rectangle r: this.top()){
		    Attribute b = new Attribute(Attribute.Type.B).box(r);

		    string.append(String.format("%n%s",b));
		}
		for (Rectangle r: this.bottom()){
		    Attribute b = new Attribute(Attribute.Type.B).box(r);

		    string.append(String.format("%n%s",b));
		}
		for (Rectangle r: this.right()){
		    Attribute b = new Attribute(Attribute.Type.B).box(r);

		    string.append(String.format("%n%s",b));
		}
		for (Rectangle r: this.left()){
		    Attribute b = new Attribute(Attribute.Type.B).box(r);

		    string.append(String.format("%n%s",b));
		}
	    }

	    for (Rectangle r: this.intersects()){
		Attribute b = new Attribute(Attribute.Type.B).box(r);

		string.append(String.format("%n%s",b));
	    }

	    return string.toString();
	}
    }
    public Iterable<Attribute> left(){
	if (null == this.left)
	    this.left = new Label.Iterable(this.pins,Layout.Position.L);
	return this.left;
    }
    public Iterable<Attribute> bottom(){
	if (null == this.bottom)
	    this.bottom = new Label.Iterable(this.pins,Layout.Position.B);
	return this.bottom;
    }
    public Iterable<Attribute> right(){
	if (null == this.right)
	    this.right = new Label.Iterable(this.pins,Layout.Position.R);
	return this.right;
    }
    public Iterable<Attribute> top(){
	if (null == this.top)
	    this.top = new Label.Iterable(this.pins,Layout.Position.T);
	return this.top;
    }
    public Iterable<Rectangle> intersects(){
	if (null == this.intersectsI)
	    this.intersectsI = new Rectangle.R.Iterable(this.intersects);
	return this.intersectsI;
    }
    public Pin first(Layout.Position s){
	if (null != this.pins && null != this.layout)
	    return this.pins[this.layout.first(s)];
	else
	    throw new IllegalStateException();
    }
    public Pin last(Layout.Position s){
	if (null != this.pins && null != this.layout)
	    return this.pins[this.layout.last(s)];
	else
	    throw new IllegalStateException();
    }


    public final static DateFormat DF = new SimpleDateFormat("yyyyMMdd");

}
