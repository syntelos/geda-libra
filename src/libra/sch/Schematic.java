package libra.sch;

import libra.Attribute;
import libra.CSV;
import libra.GAF;
import libra.GedaHome;
import libra.Layout;
import libra.Rectangle;
import libra.Symbol;
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
 * Schematic tool
 */
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


    public String title, author, logo;

    public char size;


    public Schematic(){
	super(Attribute.Type.C);
    }
    public Schematic(File file)
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
    public Schematic(String line){
	super(line);
    }


    public void write(File sym)
	throws IOException
    {
	if (this.layout()){

	    if (this.markup(null)){

		PrintStream out = new PrintStream(new FileOutputStream(sym));
		try {
		    out.printf("v %s %s%n",Vdate,Vnumber);

		    out.println(this);

		    for (Component component: this.components()){

			out.println(component);
		    }
		    return;
		}
		finally {
		    out.close();
		}
	    }
	    else
		throw new IllegalStateException("Missing markup.");
	}
	else
	    throw new IllegalStateException("Missing layout.");
    }
    public boolean add(String[] line, boolean once)
	throws IOException
    {

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
	    this.setSize(value);
	    return true;
	case Logo:
	    if (null == this.logo)
		this.logo = value;
	    return true;
	case Headline:
	    return false;
	case Net:{
		Component last = (Component)this.last(Component.class);
		if (null == last)
		    this.add(new Component(line));
		else if (!last.add(line))
		    this.add(new Component(line));
	    }
	    return false;
	default:
	    throw new Error(row.name());
	}
    }
    @Override
    public Attribute add(Attribute.Type type){
	if (Attribute.Type.C == type)
	    return this.add(new Component());
	else
	    return this.add(new Attribute(type));
    }
    public char getSize(){
	return this.size;
    }
    public Schematic setSize(String size){
	if (null != size){
	    size = size.trim();
	    if (0 < size.length())
		return this.setSize(size.toUpperCase().charAt(0));
	}
	return this;
    }
    public Schematic setSize(char size){
	this.size = size;
	this.componentName = "title-bordered-"+size+".sym";
	return this;
    }
    public boolean layout(){
	/*
	 * In order to permit "Size" to be optional, we'll first do a
	 * layout and then re-layout into a titleblock.
	 */
	libra.Iterable<Component> list = this.components();
	final int count = list.length();
	if (0 < count){
	    Rectangle[] components = new Rectangle[count];
	    {
		for (int cc = 0; cc < count; cc++){
		    components[cc] = list.get(cc).getBounds();
		}
	    }
	    /*
	     * Collect membership statistics
	     */
	    int width_min = Integer.MAX_VALUE, width_max = Integer.MIN_VALUE;
	    int height_min = Integer.MAX_VALUE, height_max = Integer.MIN_VALUE;
	    boolean[] small = new boolean[count];
	    {
		for (int cc = 0; cc < count; cc++){
		    Rectangle component = components[cc];
		    int w = (800 + component.width);
		    int h = (800 + component.height);
		    width_min = Math.min(width_min,w);
		    width_max = Math.max(width_max,w);
		    height_min = Math.min(height_min,h);
		    height_max = Math.max(height_max,h);
		}
		final float t = 0.5f;
		final float width_t = (t * width_max);
		final float height_t = (t * height_max);
		for (int cc = 0; cc < count; cc++){
		    Rectangle component = components[cc];
		    float w = (800 + component.width);
		    float h = (800 + component.height);

		    if (w <= width_t && h <= height_t)

			small[cc] = true;
		    else
			small[cc] = false;
		}
	    }
	    Layout.Cursor cursor = new Layout.Cursor(width_min, width_max, height_min, height_max);
	    /*
	     * Layout is a horizontal, with contiguous small (<= 50%)
	     * components stacked vertically
	     */
	    {
		Component prev = null, next;

		for (int cc = 0; cc < count; cc++){

		    next = list.get(cc);

		    cursor.small(small[cc]);

		    next.layout1(prev,cursor);
		    prev = next;
		}
	    }
	    /*
	     * Size fitting & determination
	     */
	    Rectangle layout = this.getBounds(Attribute.Type.C);

	    Symbol titleblock = this.getComponentTitleblock();
	    if (null == titleblock){
		titleblock = GedaHome.Titleblock(layout);
		if (null == titleblock)
		    throw new IllegalStateException(String.format("Layout too large (%dx%d) for existing titleblocks",layout.width,layout.height));
		else {
		    this.componentName = titleblock.part;
		    this.componentSymbol = titleblock;
		}
	    }
	    /*
	     * Layout finishing
	     */
	    final Rectangle space = titleblock.getInnerBoundsTitleblock();

	    final int sw = (space.width-layout.width);
	    final int sh = (space.height-layout.height);

	    cursor.layout2(sw,sh);
	    {
		Component prev = null, next;

		for (int cc = 0; cc < count; cc++){

		    next = list.get(cc);

		    cursor.small(small[cc]);

		    next.layout3(prev,cursor);
		    prev = next;
		}
	    }
	    return true;
	}
	else
	    return false;
    }
    @Override
    public boolean markup(Attribute parent){

	boolean re = true;

	for (Component component: this.components()){

	    if (!component.markup(this))
		re = false;
	}
	return re;
    }
    public libra.Iterable<Component> components(){
	libra.Iterable list = this.list(Attribute.Type.C);
	return (libra.Iterable<Component>)list;
    }
}
