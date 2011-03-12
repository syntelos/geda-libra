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
	B, C, L, P, T, H, V, v;

	private final static Type[] Values = Type.values();
	public final static Type For(int ordinal){
	    return Values[ordinal];
	}
	public final static Type For(String ordinal){
	    return For(Integer.parseInt(ordinal));
	}
    }
    public enum Show {
	NameValue, Value, Name;

	private final static Show[] Values = Show.values();
	public final static Show For(int ordinal){
	    return Values[ordinal];
	}
	public final static Show For(String ordinal){
	    return For(Integer.parseInt(ordinal));
	}
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

	private final static Color[] Values = Color.values();
	public final static Color For(int ordinal){
	    return Values[ordinal];
	}
	public final static Color For(String ordinal){
	    return For(Integer.parseInt(ordinal));
	}
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

    public String componentName;
    public Symbol componentSymbol;

    public Show show = Show.NameValue;

    public Attribute[] children;

    protected Rectangle bounds;


    public Attribute(Type t){
	super();
	if (null != t)
	    this.type = t;
	else
	    throw new IllegalArgumentException();
    }
    /**
     * Parse attribute
     */
    public Attribute(String line){
	super();
	StringTokenizer strtok = new StringTokenizer(line," ");
	this.type = Type.valueOf(strtok.nextToken());
	switch(this.type){
	case B:
	    this.x1 = Integer.parseInt(strtok.nextToken());
	    this.y1 = Integer.parseInt(strtok.nextToken());
	    this.width = Integer.parseInt(strtok.nextToken());
	    this.height = Integer.parseInt(strtok.nextToken());
	    this.color = Color.For(strtok.nextToken());
	    this.linewidth = Integer.parseInt(strtok.nextToken());
	    this.capstyle = Integer.parseInt(strtok.nextToken());
	    this.dashtype = Integer.parseInt(strtok.nextToken());
	    this.dashlength = Integer.parseInt(strtok.nextToken());
	    this.dashspace = Integer.parseInt(strtok.nextToken());
	    this.filltype = Integer.parseInt(strtok.nextToken());
	    this.fillwidth = Integer.parseInt(strtok.nextToken());
	    this.angle1 = Integer.parseInt(strtok.nextToken());
	    this.pitch1 = Integer.parseInt(strtok.nextToken());
	    this.angle2 = Integer.parseInt(strtok.nextToken());
	    this.pitch2 = Integer.parseInt(strtok.nextToken());
	    this.rect(true);
	    this.num_lines = 0;
	    break;
	case C:
	    this.x1 = Integer.parseInt(strtok.nextToken());
	    this.y1 = Integer.parseInt(strtok.nextToken());
	    this.selectable = Integer.parseInt(strtok.nextToken());
	    this.angle = Integer.parseInt(strtok.nextToken());
	    this.mirror = Integer.parseInt(strtok.nextToken());
	    this.componentName = strtok.nextToken();
	    this.num_lines = 0;
	    break;
	case L:
	    this.x1 = Integer.parseInt(strtok.nextToken());
	    this.y1 = Integer.parseInt(strtok.nextToken());
	    this.x2 = Integer.parseInt(strtok.nextToken());
	    this.y2 = Integer.parseInt(strtok.nextToken());
	    this.color = Color.For(strtok.nextToken());
	    this.linewidth = Integer.parseInt(strtok.nextToken());
	    this.capstyle = Integer.parseInt(strtok.nextToken());
	    this.dashtype = Integer.parseInt(strtok.nextToken());
	    this.dashlength = Integer.parseInt(strtok.nextToken());
	    this.dashspace = Integer.parseInt(strtok.nextToken());
	    break;
	case P:
	    this.x1 = Integer.parseInt(strtok.nextToken());
	    this.y1 = Integer.parseInt(strtok.nextToken());
	    this.x2 = Integer.parseInt(strtok.nextToken());
	    this.y2 = Integer.parseInt(strtok.nextToken());
	    this.color = Color.For(strtok.nextToken());
	    this.pintype = Integer.parseInt(strtok.nextToken());
	    this.whichend = Integer.parseInt(strtok.nextToken());
	    this.rect(true);
	    this.num_lines = 0;
	    break;
	case T:
	    this.x1 = Integer.parseInt(strtok.nextToken());
	    this.y1 = Integer.parseInt(strtok.nextToken());
	    this.color = Color.For(strtok.nextToken());
	    this.textsize = Integer.parseInt(strtok.nextToken());
	    this.visibility = Integer.parseInt(strtok.nextToken());
	    this.show = Show.For(strtok.nextToken());
	    this.angle = Integer.parseInt(strtok.nextToken());
	    this.alignment = Integer.parseInt(strtok.nextToken());
	    this.num_lines = Integer.parseInt(strtok.nextToken());
	    break;
	case H:
	    this.color = Color.For(strtok.nextToken());
	    this.linewidth = Integer.parseInt(strtok.nextToken());
	    this.capstyle = Integer.parseInt(strtok.nextToken());
	    this.dashtype = Integer.parseInt(strtok.nextToken());
	    this.dashlength = Integer.parseInt(strtok.nextToken());
	    this.dashspace = Integer.parseInt(strtok.nextToken());
	    this.filltype = Integer.parseInt(strtok.nextToken());
	    this.fillwidth = Integer.parseInt(strtok.nextToken());
	    this.angle1 = Integer.parseInt(strtok.nextToken());
	    this.pitch1 = Integer.parseInt(strtok.nextToken());
	    this.angle2 = Integer.parseInt(strtok.nextToken());
	    this.pitch2 = Integer.parseInt(strtok.nextToken());
	    this.num_lines = Integer.parseInt(strtok.nextToken());
	    break;
	case V:
	    this.x1 = Integer.parseInt(strtok.nextToken());
	    this.y1 = Integer.parseInt(strtok.nextToken());
	    this.radius = Integer.parseInt(strtok.nextToken());
	    this.color = Color.For(strtok.nextToken());
	    this.linewidth = Integer.parseInt(strtok.nextToken());
	    this.capstyle = Integer.parseInt(strtok.nextToken());
	    this.dashtype = Integer.parseInt(strtok.nextToken());
	    this.dashlength = Integer.parseInt(strtok.nextToken());
	    this.dashspace = Integer.parseInt(strtok.nextToken());
	    this.filltype = Integer.parseInt(strtok.nextToken());
	    this.fillwidth = Integer.parseInt(strtok.nextToken());
	    this.angle1 = Integer.parseInt(strtok.nextToken());
	    this.pitch1 = Integer.parseInt(strtok.nextToken());
	    this.angle2 = Integer.parseInt(strtok.nextToken());
	    this.pitch2 = Integer.parseInt(strtok.nextToken());
	    this.num_lines = 0;
	    break;
	case v:
	    this.num_lines = 0;
	    break;
	default:
	    throw new Error(this.type.name());
	}
    }


    public Rectangle getBounds(){
	Rectangle bounds = this.bounds;
	if (null == bounds){
	    bounds = this.normalize();
	    if (null != this.children){
		for (Attribute child: this.children){
		    if (child.isNotEmpty())
			bounds = bounds.union(child);
		}
	    }
	    this.bounds = bounds;
	}
	return bounds;
    }
    public Rectangle getBounds(Attribute.Type type){
	Attribute[] list = Attribute.List(this.children,type);
	if (null != list){
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
	    return new Rectangle();
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
    public Symbol getComponentSymbol(){
	if (null != this.componentName){
	    Symbol component = this.componentSymbol;
	    if (null == component){
		component = Lib.For(this.componentName);
		this.componentSymbol = component;
	    }
	    return component;
	}
	else
	    return null;
    }
    public Symbol getComponentTitleblock(){
	if (null != this.componentName){
	    Symbol component = this.componentSymbol;
	    if (null == component){
		component = GedaHome.Titleblock(this.componentName);
		this.componentSymbol = component;
	    }
	    return component;
	}
	else
	    return null;
    }
    public Symbol getComponentConnector(){
	if (null != this.componentName){
	    Symbol component = this.componentSymbol;
	    if (null == component){
		component = GedaHome.Connector(this.componentName);
		this.componentSymbol = component;
	    }
	    return component;
	}
	else
	    return null;
    }
    public Symbol getComponentIo(){
	if (null != this.componentName){
	    Symbol component = this.componentSymbol;
	    if (null == component){
		component = GedaHome.Io(this.componentName);
		this.componentSymbol = component;
	    }
	    return component;
	}
	else
	    return null;
    }
    public Symbol getComponentPower(){
	if (null != this.componentName){
	    Symbol component = this.componentSymbol;
	    if (null == component){
		component = GedaHome.Power(this.componentName);
		this.componentSymbol = component;
	    }
	    return component;
	}
	else
	    return null;
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
    public Attribute show(Show show){
	if (null != show){
	    this.show = show;
	    return this;
	}
	else
	    throw new IllegalArgumentException();
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
    public Attribute text(String name, String value, int color, int size, int visibility){
	return this.text(name,value,Color.For(color),size,visibility);
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
    public Attribute line(int x1, int y1, int x2, int y2, int c){
	return this.line(x1,y1,x2,y2,Color.For(c));
    }
    public Attribute line(int x1, int y1, int x2, int y2, Color c){
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
	this.color = c;
	return this;
    }
    public String text(){
	switch(this.type){
	case T:
	    switch(this.show){
	    case NameValue:
		return this.name+'='+this.value;
	    case Name:
		return this.name;
	    case Value:
		if (null != this.value)
		    return this.value.toString();
		else
		    return "";
	    default:
		throw new Error(this.show.name());
	    }
	case H:
	    if (null != this.value)
		return this.value.toString();
	    else
		return "";
	default:
	    throw new IllegalStateException(this.type.name());
	}
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
    /**
     * @return Generic text values status
     */
    public boolean textComplete(){
	if (Type.T == this.type){
	    switch(this.show){
	    case NameValue:
		return (null != this.name && null != this.value);
	    case Name:
		return (null != this.name);
	    case Value:
		return (null != this.value);
	    default:
		throw new Error(this.show.name());
	    }
	}
	else if (Type.H == this.type)
	    return (null != this.value);
	else
	    return (0 == this.num_lines);
    }
    /**
     * @return Text values status for use with {@link #add(String)} as
     * <code>if (textCompleteNot()){ add(text);}</code> -- the add
     * method throws an exception for unsupported num lines.
     */
    public boolean textCompleteNot(){
	if (Type.T == this.type){
	    switch(this.show){
	    case NameValue:
		return (null != this.name && null != this.value) || (1 < this.num_lines);
	    case Name:
		return (null != this.name) || (1 < this.num_lines);
	    case Value:
		return (null != this.value) || (1 < this.num_lines);
	    default:
		throw new Error(this.show.name());
	    }
	}
	else if (Type.H == this.type)
	    return (null == this.value || 1 < this.num_lines);
	else
	    return (0 < this.num_lines);
    }
    public Attribute add(String text){
	switch(this.type){
	case T:
	    StringTokenizer strtok = new StringTokenizer(text,"=");
	    if (2 == strtok.countTokens()){
		this.name = strtok.nextToken();
		this.value = strtok.nextToken();
		return this;
	    }
	    else {
		switch(this.show){
		case NameValue:
		    throw new IllegalStateException(String.format("Missing 'name=value' syntax in type 'T' value '%s'",text));
		case Name:
		    this.name = strtok.nextToken();
		    return this;
		case Value:
		    this.value = strtok.nextToken();
		    return this;
		default:
		    throw new Error(this.show.name());
		}
	    }

	case H:
	    if (null == this.value){
		this.value = new Path(text);
		return this;
	    }
	    else
		throw new IllegalStateException(String.format("Adding multiple text lines to attribute type '%s'",this.type.name()));
	default:
	    throw new IllegalStateException(String.format("Adding lines to attribute type '%s'",this.type.name()));
	}
    }
    public Rectangle wh(boolean init){
	this.bounds = null;
	return super.wh(init);
    }
    public Rectangle xy(boolean init){
	this.bounds = null;
	return super.xy(init);
    }
    public Rectangle rect(boolean init){
	this.bounds = null;
	return super.rect(init);
    }
    public Rectangle xy(int x1, int y1){
	this.bounds = null;
	return super.xy(x1,y1);
    }
    public Rectangle xyxy(int x1, int y1, int x2, int y2){
	this.bounds = null;
	return super.xyxy(x1,y1,x2,y2);
    }
    public Attribute copy(Attribute at){
	if (null != at){
	    super.copy(at);
	    this.type = at.type;
	    this.name = at.name;
	    this.value = at.value;
	    this.pintype = at.pintype;
	    this.whichend = at.whichend;
	    this.radius = at.radius;
	    this.color = at.color;
	    this.textsize = at.textsize;
	    this.linewidth = at.linewidth;
	    this.visibility = at.visibility;
	    this.capstyle = at.capstyle;
	    this.dashtype = at.dashtype;
	    this.dashlength = at.dashlength;
	    this.dashspace = at.dashspace;
	    this.filltype = at.filltype;
	    this.fillwidth = at.fillwidth;
	    this.angle1 = at.angle1;
	    this.pitch1 = at.pitch1;
	    this.angle2 = at.angle2;
	    this.pitch2 = at.pitch2;
	    this.angle = at.angle;
	    this.alignment = at.alignment;
	    this.num_lines = at.num_lines;
	    this.selectable = at.selectable;
	    this.mirror = at.mirror;
	    this.componentName = at.componentName;
	    this.componentSymbol = at.componentSymbol;
	    this.show = at.show;
	    this.children = at.children;
	    this.bounds = null;
	}
	return this;
    }
    public Attribute copy(Attribute[] children){
	this.children = children;
	this.bounds = null;
	return this;
    }
    public Attribute add(Attribute at){
	this.children = Attribute.Add(this.children,at);
	/*
	 */
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
    public java.util.Iterator<Attribute> iterator(){
	return new Attribute.Iterator(this.children);
    }
    public java.lang.Iterable<Attribute> iterator(Attribute.Type type){
	return new Attribute.Iterator(this.children,type);
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
				   this.selectable,      this.angle,           this.mirror,          Symbol.Name(this.componentName));
	    break;
	case L:
	    parent = String.format("%s"+
				   " %d %d %d %d"+
				   " %d %d %d"+
				   " %d %d %d",
				   this.type,
				   this.x1,              this.y1,              this.x2,              this.y2,
				   this.color.ordinal(), this.linewidth,       this.capstyle,
				   this.dashtype,        this.dashlength,      this.dashspace);
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
				   this.text());
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
    public final static Attribute[] List(Attribute[] list, Attribute.Type type){
	if (null == list || null == type)
	    return null;
	else if (null == type)
	    return list;
	else {
	    Attribute[] typelist = null;
	    for (Attribute at: list){
		if (type == at.type)
		    typelist = Attribute.Add(typelist,at);
	    }
	    return typelist;
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
	implements java.lang.Iterable<Attribute>, java.util.Iterator<Attribute>
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
	public Iterator(Attribute[] list, Attribute.Type type){
	    super();
	    list = Attribute.List(list,type);
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
	public java.util.Iterator<Attribute> iterator(){
	    return this;
	}
    }
    /**
     * 
     */
    public static class Iterable
	extends Object
	implements java.lang.Iterable<Attribute>
    {
	private final Attribute[] list;


	public Iterable(Attribute[] list){
	    super();
	    this.list = list;
	}
	public Iterable(Attribute[] list, Attribute.Type type){
	    super();
	    this.list = Attribute.List(list,type);
	}

	public java.util.Iterator<Attribute> iterator(){
	    return new Attribute.Iterator(this.list);
	}
    }
}
