package libra;

import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

/**
 * 
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Attribute
    extends Rectangle
    implements Iterable<Attribute>
{
    public final static String Vdate = "20100214", Vnumber = "2";

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

    public enum Type {
	B, C, P, T, H, V;
    }
    public enum Show {
	NameValue, Value, Name;
    }
    public enum Color {
	Background, Pin, NetEndpoint,
	Graphic, Net, Attribute,
	LogicBubble, DotsGrid, DetachedAttribute,
	Text, Bus, Select,
	Boundingbox, ZoomBox, Stroke,
	Lock, OutputBackground, Freestyle1,
	Freestyle2, Freestyle3, Freestyle4,
	Junction, MeshGridMajor, MeshGridMinor;
    }


    public Type type;

    public String name;

    public Object value;

    public int pintype, whichend, radius;
    public Color color = Color.Pin;
    public int textsize = 10, linewidth, visibility;
    public int capstyle, dashtype, dashlength = -1, dashspace = -1, filltype;
    public int fillwidth = -1, angle1 = -1, pitch1 = -1, angle2 = -1, pitch2 = -1;
    public int angle, alignment, num_lines = 1;
    public int selectable, mirror;
    public String basename;
    public Show show = Show.NameValue;

    public Attribute[] children;


    public Attribute(Type t){
	super();
	if (null != t)
	    this.type = t;
	else
	    throw new IllegalArgumentException();
    }


    public boolean isPinSeq(){
	if (Attribute.Type.T == this.type)
	    return (null != this.name && "pinseq".equals(this.name));
	else
	    return false;
    }
    public boolean isPinType(){
	if (Attribute.Type.T == this.type)
	    return (null != this.name && "pintype".equals(this.name));
	else
	    return false;
    }
    public boolean isPinNumber(){
	if (Attribute.Type.T == this.type)
	    return (null != this.name && "pinnumber".equals(this.name));
	else
	    return false;
    }
    public boolean isPinLabel(){
	if (Attribute.Type.T == this.type)
	    return (null != this.name && "pinlabel".equals(this.name));
	else
	    return false;
    }
    public Attribute pin(int x1, int y1, int x2, int y2){

	this.xyxy(x1,y1,x2,y2);

	this.color = Color.Pin;

	return this.attachChildren();
    }
    public Attribute pin(){

	this.color = Color.Pin;

	return this.attachChildren();
    }
    public Attribute loc(Attribute at){

	this.x1 = at.x1;
	this.y1 = at.y1;

	return this.attachChildren();
    }
    public Attribute rel(Attribute at, int dx, int dy){

	this.x1 = Math.max(0,(at.x1+dx));
	this.y1 = Math.max(0,(at.y1+dy));

	return this.attachChildren();
    }
    public Attribute bot(Attribute at, int dx, int y){

	this.x1 = Math.max(0,(at.x1+dx));
	this.y1 = y;

	return this.attachChildren();
    }
    public Attribute aa(int angle, int alignment){
	this.angle = angle;
	this.alignment = alignment;
	return this;
    }
    /**
     * @return Geometric dimension of text string defined by show.
     */
    public int length(){
	if (Attribute.Type.T == this.type){

	    switch(this.show){
	    case NameValue:
		return Layout.Dimension.Label((this.name.length()+this.value.toString().length()+1));

	    case Value:
		return Layout.Dimension.Label(this.value.toString().length());

	    case Name:
		return Layout.Dimension.Label(this.name.length());

	    default:
		throw new Error(this.show.name());
	    }
	}
	else
	    throw new IllegalStateException();
    }
    public Attribute center(int cx, int cy){

	this.width = this.length();
	this.height = 50;

	final int dx = (this.width>>1), dy = 50; 

	this.xy(cx-dx,cy-dy);

	return this.attachChildren();
    }
    /**
     * Layout text from the definition of X, Y and Strings and Show.
     * 
     * Define signed width and height for compatibility with
     * superclass.  Also define corresponding (x2,y2).
     */
    public Attribute layoutText(){
	int length = this.length();
	if (0 < length){
	    switch(this.angle){
	    case 0:
		switch(this.alignment){
		case 0:
		    this.width = length;
		    this.height = 200;
		    break;
		case 6:
		    this.width = -(length);
		    this.height = 200;
		    break;
		default:
		    throw new IllegalStateException(String.format("Unimplemented case alignment %d",this.alignment));
		}
		break;
	    case 90:
		switch(this.alignment){
		case 0:
		    this.width = -200;
		    this.height = length;
		    break;
		case 6:
		    this.width = -200;
		    this.height = -(length);
		    break;
		default:
		    throw new IllegalStateException(String.format("Unimplemented case alignment %d",this.alignment));
		}
		break;
	    default:
		throw new IllegalStateException(String.format("Unimplemented case angle %d",this.angle));
	    }
	    /*
	     * Compatible with superclass Rectangle
	     */
	    this.x2 = (this.x1 + this.width);
	    this.y2 = (this.y1 + this.height);
	}
	return this.attachChildren();
    }
    public Attribute text(String name, String value, Color color, int size, int visibility){
	this.name = name;
	this.value = value;
	if (null != color)
	    this.color = color;
	else
	    this.color = Color.Text;

	if (-1 < size)
	    this.textsize = size;
	if (-1 < visibility)
	    this.visibility = visibility;
	return this;
    }
    public Attribute text(String name, String value, Show show){
	this.text(name,value,null,-1,1);
	if (null != show)
	    this.show = show;
	return this;
    }
    public Attribute text(String name, int value){
	return this.text(name,String.valueOf(value));
    }
    public Attribute text(String name, Object value){
	if (null != value)
	    return this.text(name,value.toString());
	else
	    return this.text(name,"");
    }
    public Attribute text(String name, int value, Show show){
	return this.text(name,String.valueOf(value),show);
    }
    public Attribute text(String name, String value){
	return this.text(name,value,null,-1,-1);
    }
    public Attribute box(int x, int y, int w, int h){
	
	this.x1 = x;
	this.y1 = y;
	this.width = w;
	this.height = h;
	this.x2 = (this.x1 + this.width);
	this.y2 = (this.y1 + this.height);
	this.color = Color.Graphic;

	return this.attachChildren();
    }
    public Attribute box(Rectangle r){

	r = r.normalize();

	this.x1 = r.x1;
	this.y1 = r.y1;
	this.width = r.width;
	this.height = r.height;

	this.x2 = (r.x1+r.width);
	this.y2 = (r.y1+r.height);

	this.color = Color.Graphic;

	return this.attachChildren();
    }
    public Attribute path(String d){
	if (null != d){
	    this.type = Attribute.Type.H;
	    this.color = Color.Graphic;
	    this.value = new Path(d);
	    this.filltype = 1;

	    return this;
	}
	else
	    throw new IllegalStateException();
    }
    /**
     * Replace X2,Y2 with the returned logic bubble.
     * @param pos Position of this pin.
     * @return A logic bubble on this pin.
     */
    public Attribute logicbubble(Layout.Position pos){

	Attribute logicbubble = new Attribute(Attribute.Type.V);
	logicbubble.color = Color.LogicBubble;
	logicbubble.radius = 50;
	logicbubble.x1 = this.x2;
	logicbubble.y1 = this.y2;

	switch(pos){
	case T:
	    logicbubble.dy1(50);
	    this.dy2(100);
	    break;
	case L:
	    logicbubble.dx1(-50);
	    this.dx2(-100);
	    break;
	case B:
	    logicbubble.dy1(-50);
	    this.dy2(-100);
	    break;
	case R:
	    logicbubble.dx1(50);
	    this.dx2(100);
	    break;
	}
	return logicbubble;
    }
    public String text(){
	if (null != name && null != value){
	    StringBuilder strbuf = new StringBuilder();
	    strbuf.append(this.name);
	    strbuf.append('=');
	    strbuf.append(this.value);
	    return strbuf.toString();
	}
	else if (null != name)
	    return name;
	else
	    return "";
    }
    public Attribute attachChildren(){

	for (Attribute child: this){
	    if (Attribute.Type.T == child.type){
		child.x1 = this.x1;
		child.y1 = this.y1;
	    }
	}
	return this;
    }
    public void clear(){
	this.children = null;
    }
    public Attribute add(Attribute at){
	this.children = Attribute.Add(this.children,at);
	return at;
    }
    public Attribute add(Attribute.Type type){
	return this.add(new Attribute(type));
    }
    public Attribute get(Attribute.Type type){
	int idx = Attribute.IndexOf(this.children,type);
	if (-1 < idx)
	    return this.children[idx];
	else {
	    return null;
	}
    }
    public Attribute last(Attribute.Type type){
	return Attribute.Last(this.children,type);
    }
    public Attribute set(Layout.Dimension box){

	return this.set(300,300,box.w,box.h);
    }
    public Attribute set(int x, int y, int w, int h){

	return this.set(Attribute.Type.B).box(x,y,w,h);
    }
    public Attribute set(Attribute.Type type){
	if (null != type){
	    this.type = type;
	    return this;
	}
	else
	    throw new IllegalArgumentException();
    }
    public Attribute.Iterator iterator(){
	return new Attribute.Iterator(this.children);
    }
    public String toString(){
	/*
	 * Parent
	 */
	String parent;
	switch(this.type){
	case B:
	    parent = String.format("%s"+
				   " %d %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d",
				   this.type,
				   this.x1,              this.y1,              this.width,           this.height,
				   this.color.ordinal(), this.linewidth,       this.capstyle,
				   this.dashtype,        this.dashlength,      this.dashspace,
				   this.filltype,        this.fillwidth,       this.angle1,
				   this.pitch1,          this.angle2,          this.pitch2);
	    break;
	case C:
	    parent = String.format("%s"+
				   " %d %d"+
				   " %d %d %d %s",
				   this.type,
				   this.x1,              this.y1, 
				   this.selectable,      this.angle,           this.mirror,          this.basename);
	    break;
	case P:
	    parent = String.format("%s"+
				   " %d %d %d %d"+
				   " %d %d %d",
				   this.type,
				   this.x1,              this.y1,              this.x2,              this.y2,
				   this.color.ordinal(), this.pintype,         this.whichend);
	    break;
	case T:
	    parent = String.format("%s"+
				   " %d %d"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d"+
				   "%n%s",
				   this.type,
				   this.x1,  this.y1,  
				   this.color.ordinal(), this.textsize,        this.visibility,
				   this.show.ordinal(),  this.angle,           this.alignment,
				   this.num_lines,
				   this.text());
	    break;
	case H:
	    parent = String.format("%s"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d"+
				   "%n%s",
				   this.type,
				   this.color.ordinal(), this.linewidth,       this.capstyle,
				   this.dashtype,        this.dashlength,      this.dashspace,
				   this.filltype,        this.fillwidth,       this.angle1, 
				   this.pitch1,          this.angle2,          this.pitch2, 
				   this.num_lines,
				   this.value);
	    break;
	case V:
	    parent = String.format("%s"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d",
				   this.type,
				   this.x1,              this.y1,              this.radius,
				   this.color.ordinal(), this.linewidth,       this.capstyle,
				   this.dashtype,        this.dashlength,      this.dashspace,
				   this.filltype,        this.fillwidth,       this.angle1,
				   this.pitch1,          this.angle2,          this.pitch2);
	    break;
	default:
	    throw new Error(this.type.name());
	}
	/*
	 * Children
	 */
	if (null != this.children)

	    return Attribute.ToString(parent,this.children);
	else
	    return parent;
    }


    public final static Attribute[] Add(Attribute[] list, Attribute item){
	if (null == item)
	    return list;
	else if (null == list)
	    return new Attribute[]{item};
	else {
	    int len = list.length;
	    Attribute[] copier = new Attribute[len+1];
	    System.arraycopy(list,0,copier,0,len);
	    copier[len] = item;
	    return copier;
	}
    }
    public final static Attribute[] Tail(Attribute[] list){
	if (null == list)
	    return null;
	else {
	    int len = (list.length-1);
	    if (0 < len){
		Attribute[] copier = new Attribute[len];
		System.arraycopy(list,1,copier,0,len);
		return copier;
	    }
	    else
		return null;
	}
    }
    public final static int IndexOf(Attribute[] list, Attribute.Type type){
	if (null == type)
	    return -1;
	else if (null == list)
	    return -1;
	else {
	    final int len = list.length;
	    for (int cc = 0; cc < len; cc++){
		if (type == list[cc].type)
		    return cc;
	    }
	    return -1;
	}
    }
    public final static Attribute First(Attribute[] list, Attribute.Type type){
	int idx = IndexOf(list,type);
	if (-1 < idx)
	    return list[idx];
	else {
	    return null;
	}
    }
    public final static Attribute Last(Attribute[] list, Attribute.Type type){
	if (null == type)
	    return null;
	else if (null == list)
	    return null;
	else {
	    Attribute re = null;
	    final int len = list.length;
	    for (int cc = 0; cc < len; cc++){
		Attribute at = list[cc];
		if (type == at.type)
		    re = at;
	    }
	    return re;
	}
    }
    public final static Attribute[][] Attributes(Attribute[] list){
	if (null == list)
	    return null;
	else {
	    Attribute[] attributes = null, nattributes = null;
	    final int len = list.length;
	    for (int cc = 0; cc < len; cc++){
		Attribute at = list[cc];
		if (Attribute.Type.T == at.type)
		    attributes = Attribute.Add(attributes,at);
		else
		    nattributes = Attribute.Add(nattributes,at);
	    }
	    return new Attribute[][]{
		attributes,
		nattributes
	    };
	}
    }
    public final static String ToString(Attribute[] list){

	StringBuilder strbuf = new StringBuilder();
	if (null != list){
	    Attribute principal = list[0];
	    strbuf.append(principal);
	    list = Tail(list);
	    if (null != list){
		Attribute[][] alist = Attributes(list);
		Attribute[] attributes = alist[0];
		Attribute[] children = alist[1];

		if (null != attributes){
		    strbuf.append(String.format("%n%c",'{'));

		    final int count = attributes.length;

		    for (int cc = 0; cc < count; cc++){
			strbuf.append(String.format("%n%s",attributes[cc]));
		    }

		    strbuf.append(String.format("%n%c",'}'));
		}

		if (null != children){

		    final int count = children.length;

		    for (int cc = 0; cc < count; cc++){
			strbuf.append(String.format("%n%s",children[cc]));
		    }
		}
	    }
	}
	return strbuf.toString();
    }
    public final static String ToString(String parent, Attribute[] list){

	if (null != list){
	    StringBuilder strbuf = new StringBuilder();
	    strbuf.append(parent);
	    final boolean principal = (!parent.startsWith(Attribute.Type.T.name()));
	    if (principal){

		Attribute[][] alist = Attributes(list);
		Attribute[] attributes = alist[0];
		Attribute[] children = alist[1];
		
		if (null != attributes){
		    strbuf.append(String.format("%n%c",'{'));

		    final int count = attributes.length;

		    for (int cc = 0; cc < count; cc++){
			strbuf.append(String.format("%n%s",attributes[cc]));
		    }
		    
		    strbuf.append(String.format("%n%c",'}'));
		}

		if (null != children){

		    final int count = children.length;

		    for (int cc = 0; cc < count; cc++){
			strbuf.append(String.format("%n%s",children[cc]));
		    }
		}
	    }
	    else {

		final int count = list.length;

		for (int cc = 1; cc < count; cc++){
		    strbuf.append(String.format("%n%s",list[cc]));
		}
	    }

	    return strbuf.toString();
	}
	else
	    return parent;
    }
    /**
     * 
     */
    public static class Iterator
	extends Object
	implements java.util.Iterator<Attribute>
    {

	private final Attribute[] list;
	private final int length;
	private int index;


	public Iterator(Attribute[] list){
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
	public Attribute next(){
	    return this.list[this.index++];
	}
	public void remove(){
	    throw new UnsupportedOperationException();
	}
    }
}
