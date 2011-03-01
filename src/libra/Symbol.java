package libra;

import libra.io.FileType;

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

/**
 * Symbol tool.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Symbol
    extends Attribute
{

    public enum Row {
	Part, Package, Layout, Description, Documentation, Author, License, Footprint, Path,
	Headline, 
	Pin;

	public final static Row For(String arg, boolean once){
	    if (once){
		try {
		    return Row.valueOf(arg);
		}
		catch (RuntimeException exc){
		    try {
			Integer.parseInt(arg);
			return Row.Pin;
		    }
		    catch (NumberFormatException nfx){
			return Row.Headline;
		    }
		}
	    }
	    else
		return Row.Pin;
	}
    }


    public Layout layout;

    public Pin[] pins;

    public String part, pack, footprint, version, 
	author, description, documentation, license, path;

    private java.lang.Iterable<Attribute> left, bottom, right, top;

    private Rectangle[] intersects;

    private java.lang.Iterable<Rectangle> intersectsI;


    public Symbol(){
	super(Attribute.Type.B);
	this.version = DF.format(new Date());
    }
    public Symbol(File file)
	throws IOException
    {
	this();
	final FileType ft = FileType.For(file);
	switch(ft){
	case CSV:
	    CSV csv = new CSV(file);
	    boolean once = true;
	    for (String[] line: csv.content){
		once = this.add(line,once);
	    }
	    break;
	case SYM:
	    GAF gaf = new GAF(file);
	    this.copy(gaf.first());
	    this.copy(gaf.tail());
	    break;
	default:
	    throw new IllegalStateException(String.format("Unsupported file type '%s' (%s)",file.getPath(),ft.name()));
	}
    }
    /**
     * Generic attribute
     */
    public Symbol(String line){
	super(line);
    }


    public Rectangle getBounds(){
	Rectangle bounds = this.bounds;
	if (null == bounds){
	    bounds = super.getBounds();
	    if (null != this.pins){
		for (Pin pin: this.pins){
		    bounds = bounds.union(pin);
		}
	    }
	    this.bounds = bounds;
	}
	return bounds;
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

		out.printf("v %s %s%n",Vdate,Vnumber);

		out.println(this);

		for (Pin pin: this.pins){

		    out.println(pin);
		}
		return out;
	    }
	    else
		throw new IllegalStateException("Missing layout.");
	}
	else if (this.markup()){

	    out.printf("v %s %s%n",Vdate,Vnumber);

	    out.println(this);

	    return out;
	}
	else
	    throw new IllegalStateException("Missing layout.");
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
    public boolean add(String[] line, boolean once){

	final Row row = Row.For(line[0],once);

	final String value;
	if (1 < line.length)
	    value = line[1];
	else
	    value = null;

	switch(row){
	case Part:
	    if (null == this.part)
		this.part = value;
	    return true;
	case Package:
	    if (null == this.pack)
		this.pack = value;
	    return true;
	case Layout:
	    if (null == this.layout)
		this.layout = new Layout(value);
	    return true;
	case Description:
	    if (null == this.description)
		this.description = value;
	    return true;
	case Documentation: 
	    if (null == this.documentation)
		this.documentation = value;
	    return true;
	case Author:
	    if (null == this.author)
		this.author = value;
	    return true;
	case License:
	    if (null == this.license)
		this.license = value;
	    return true;
	case Footprint:
	    if (null == this.footprint)
		this.footprint = value;
	    return true;
	case Headline:
	    return false;
	case Pin:
	    if (null == this.pins)
		this.add(new Pin(line));
	    else {
		Pin last = (Pin)this.pins[this.pins.length-1];
		if (!last.add(line))
		    this.add(new Pin(line));
	    }
	    return false;
	case Path:
	    if (null == this.path)
		this.path = Tail(line);
	    return true;
	default:
	    throw new Error(row.name());
	}
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

	for (Attribute ap: this.pins()){
	    Pin p = (Pin)ap;
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
    public java.lang.Iterable<Attribute> pins(){
	return this.iterator(Attribute.Type.P);
    }
    public java.lang.Iterable<Attribute> iterator(Attribute.Type type){
	if (Attribute.Type.P == type && null != this.pins)
	    return new Attribute.Iterable(this.pins);
	else
	    return super.iterator(type);
    }
    public Rectangle getBounds(Attribute.Type type){
	if (Attribute.Type.P == type && null != this.pins){
	    Attribute[] list = this.pins;

	    Rectangle bounds = list[0].normalize();
	    final int count = list.length;
	    for (int cc = 1; cc < count; cc++){
		Attribute child = list[cc];
		if (child.isNotEmpty())
		    bounds = bounds.union(child);
	    }
	    return bounds;
	}
	else
	    return super.getBounds(type);
    }
    public String toString(){
	if (0 < Debug){

	    StringBuilder string = new StringBuilder();

	    string.append( super.toString());

	    for (Rectangle r: this.intersects()){
		Attribute b = new Attribute(Attribute.Type.B).box(r);

		string.append(String.format("%n%s",b));
	    }

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

		if (2 < Debug){

		    Rectangle r = this.getBounds();

		    Attribute b = new Attribute(Attribute.Type.B).box(r);

		    string.append(String.format("%n%s",b));
		}
	    }

	    return string.toString();
	}
	else {
	    return super.toString();
	}
    }
    public java.lang.Iterable<Attribute> left(){
	if (null == this.left)
	    this.left = new Label.Iterable(this.pins,Layout.Position.L);
	return this.left;
    }
    public java.lang.Iterable<Attribute> bottom(){
	if (null == this.bottom)
	    this.bottom = new Label.Iterable(this.pins,Layout.Position.B);
	return this.bottom;
    }
    public java.lang.Iterable<Attribute> right(){
	if (null == this.right)
	    this.right = new Label.Iterable(this.pins,Layout.Position.R);
	return this.right;
    }
    public java.lang.Iterable<Attribute> top(){
	if (null == this.top)
	    this.top = new Label.Iterable(this.pins,Layout.Position.T);
	return this.top;
    }
    public java.lang.Iterable<Rectangle> intersects(){
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
    /**
     * Get the interior open space from a title block
     */
    public Rectangle getInnerBoundsTitleblock(){
	Rectangle bounds = null;
	for (Attribute box : this.iterator(Attribute.Type.B)){
	    if (box.x1 == box.y1){
		if (null == bounds)
		    bounds = box.normalize();
		else {
		    Rectangle tmp = box.normalize();
		    if (tmp.x1 > bounds.x1)
			bounds = tmp;
		}
	    }
	    else if (null != bounds){
		bounds.y1 +=  box.height;
		bounds.height -= box.height;
		bounds.xy(false);
	    }
	}
	return bounds;
    }
    /**
     * Get the title box from a titlebox symbol.
     */
    public Rectangle getTitlebox(){
	Rectangle bounds = null;
	for (Attribute box : this.iterator(Attribute.Type.B)){
	    if (box.x1 != box.y1){
		if (null == bounds)
		    bounds = box.normalize();
		else {
		    Rectangle tmp = box.normalize();
		    if (tmp.x1 > bounds.x1)
			bounds = tmp;
		}
	    }
	}
	return bounds;
    }
    public Symbol copy(Symbol that){
	super.copy(that);

	this.layout = that.layout;
	this.pins = that.pins;
	this.part = that.part;
	this.pack = that.pack;
	this.footprint = that.footprint;
	this.version = that.version;
	this.author = that.author;
	this.description = that.description;
	this.documentation = that.documentation;
	this.license = that.license;
	this.path = that.path;

	this.left = that.left;
	this.bottom = that.bottom;
	this.right = that.right;
	this.top = that.top;
	this.intersects = that.intersects;
	this.intersectsI = that.intersectsI;

	return this;
    }


    public final static DateFormat DF = new SimpleDateFormat("yyyyMMdd");

    public final static Symbol[] Add(Symbol[] list, Symbol item){
	if (null == item)
	    return list;
	else if (null == list)
	    return new Symbol[]{item};
	else {
	    int len = list.length;
	    Symbol[] copier = new Symbol[len+1];
	    System.arraycopy(list,0,copier,0,len);
	    copier[len] = item;
	    return copier;
	}
    }
    public final static String Tail(String[] terms){
	final int count = (terms.length-1);
	if (0 < count){
	    StringBuilder strbuf = new StringBuilder();
	    strbuf.append(terms[1]);
	    for (int cc = 2; cc < count; cc++){
		strbuf.append(',');
		strbuf.append(terms[cc]);
	    }
	    return strbuf.toString();
	}
	else
	    return null;
    }
    /**
     * Component attribute output name for symbol name.
     */
    public final static String Name(String name){
	if (null != name){
	    int idx = name.lastIndexOf('.');
	    if (0 < idx){
		if (name.substring(idx+1).equalsIgnoreCase("sym")){
		    return name;
		}
		else if (idx == name.length()-4){
		    return name.substring(0,idx)+".sym";
		}
	    }
	    return name+".sym";
	}
	else
	    return "";
    }
}
