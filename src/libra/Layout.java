package libra;

import java.util.StringTokenizer;

/**
 * Pin layout tool.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Layout {

    public enum Order {
	TLBR, LBRT, BRTL, RTLB;

	public final static Order For(String arg){
	    if (null != arg)
		return Order.valueOf(arg.toUpperCase());
	    else
		return LBRT;
	}
    }
    /**
     * Schematic layout cursor
     */
    public static class Cursor {
	/**
	 * Schematic layout cursor direction
	 */
	public enum  Direction {
	    Horizontal, Vertical;
	}
	public enum Relation {
	    Horizontal, Vertical;


	    public boolean isHorizontal(){
		return (Horizontal == this);
	    }
	    public boolean isVertical(){
		return (Vertical == this);
	    }
	}

	public final static int X0 = 2000, Y0 = 3000, D0 = 3000, D2 = (D0<<1);


	public final int width_min, width_max, height_min, height_max;

	private int x, y, dx, dy;

	private Direction dir;

	private int countW, countH, countX, countY, columnW;


	public Cursor(int width_min, int width_max, int height_min, int height_max){
	    super();
	    this.width_min = width_min;
	    this.width_max = width_max;
	    this.height_min = height_min;
	    this.height_max = height_max;
	    this.x = X0;
	    this.y = Y0;
	    this.countW = 1;
	    this.countH = 1;
	    this.dir = Direction.Horizontal;
	}


	public Cursor small(boolean is){
	    if (is)
		this.dir = Direction.Vertical;
	    else
		this.dir = Direction.Horizontal;
	    return this;
	}
	public Layout.Cursor.Relation layout1(libra.sch.Component prev, libra.sch.Component next){

	    switch(this.dir){
	    case Horizontal:
		this.x += this.dx;
		this.y = Y0;
		next.xy(this.x,this.y);
		this.dx = (D0+next.width);
		this.dy = (D0+next.height);
		this.countX += 1;
		this.countW = Math.max(this.countW,this.countX);
		this.countY = 0;
		return Layout.Cursor.Relation.Horizontal;

	    case Vertical:
		this.y += this.dy;
		if (this.y > this.height_max){
		    this.y = Y0;
		    this.x += this.dx;
		    next.xy(this.x,this.y);
		    this.dx = (D0+next.width);
		    this.dy = (D0+next.height);
		    this.countX += 1;
		    this.countW = Math.max(this.countW,this.countX);
		    this.countY = 0;
		}
		else {
		    next.xy(this.x,this.y);
		    if (0 == this.countY)
			this.dx = (D0+next.width);
		    else
			this.dx = Math.max(this.dx,(D0+next.width));
		    this.dy = (D0+next.height);
		    this.countY += 1;
		    this.countH = Math.max(this.countH,this.countY);
		}
		return Layout.Cursor.Relation.Vertical;

	    default:
		throw new Error(this.dir.name());
	    }
	}
	public Cursor layout2(int sw, int sh){


	    this.dx = (Math.max(0,((sw - D0)/ this.countW)))>>2;
	    this.dy = (Math.max(0,((sh - D0)/ this.countH)))>>2;

	    this.x = this.dx;
	    this.y = this.dy;

	    this.dir = Direction.Horizontal;

	    this.countX = 0;
	    this.countY = 0;

	    return this;
	}
	public void layout3(libra.sch.Component prev, libra.sch.Component next){

	    next.dxy1( this.x, this.y);

	    if (next.isLayoutHorizontal()){

		this.x += this.dx;
		this.y = this.dy;
	    }
	    else {
		this.y += this.dy;
	    }
	}
    }
    public enum Position {
	T(1), L(2), B(4), R(8);


	public final int bit;

	Position(int bit){
	    this.bit = bit;
	}


	public boolean isHorizontal(){
	    return (L == this || R == this);
	}
	public boolean isVertical(){
	    return (T == this || B == this);
	}
	public boolean get(int state){
	    return (this.bit == (state & this.bit));
	}
	public int set(int state){
	    return (state | this.bit);
	}
	/**
	 * State history: enter state only once.
	 * @see Geda#layout
	 */
	public int once(int state){
	    if (0 == (state & this.bit))
		return (state | this.bit);
	    else
		throw new IllegalStateException(this.name());
	}
	public String toString(){
	    switch(this){
	    case T:
		return "t";
	    case L:
		return "l";
	    case B:
		return "b";
	    case R:
		return "r";
	    default:
		throw new Error(name());
	    }
	}
    }
    public static abstract class LayoutOrder
	extends Layout
    {

	public final Layout.Order order;

	private Layout.Dimension size;

	protected int inW = 0, inH = 0, inT = 0, inL = 0, inB = 0, inR = 0;


	public LayoutOrder(Layout.Order o){
	    super();
	    if (null != o)
		this.order = o;
	    else
		throw new IllegalArgumentException();
	}

	@Override
	public Layout.Order getOrder(){
	    return this.order;
	}
	@Override
	public abstract Layout.Position getPosition(Pin p);

	public abstract int first(Layout.Position s);

	public abstract int last(Layout.Position s);

	@Override
	public Dimension getSize(){
	    if (null != this.size)
		return this.size;
	    else
		throw new IllegalStateException();
	}

	public abstract Layout.Dimension createDimension();

	@Override
	public Layout.Dimension layout0(Symbol s){

	    if (null == this.size){

		for (Attribute ap: s.pins()){
		    Pin p = (Pin)ap;
		    Layout.Position lp = p.getPosition(this);
		    switch(lp){
		    case T:
			inT = Math.max( p.layout0(), inT);
			break;
		    case L:
			inL = Math.max( p.layout0(), inL);
			break;
		    case B:
			inB = Math.max( p.layout0(), inB);
			break;
		    case R:
			inR = Math.max( p.layout0(), inR);
			break;
		    default:
			throw new Error(lp.name());
		    }
		}

		inW = Math.max(inL,inR);
		inH = Math.max(inT,inB);

		inW = Math.max(400,inW);
		inH = Math.max(400,inH);

		this.size = this.createDimension();
	    }
	    return this.size;
	}
	@Override
	public Dimension layout1(int dw, int dh){

	    return (this.size = new Dimension(this.size,dw,dh));
	}


	public final static class TLBR
	    extends LayoutOrder
	{
	    public final int ct, cl, cb, cr;
	    public final int t, l, b, r;

	    public TLBR(String arg){
		super(Layout.Order.TLBR);
		StringTokenizer strtok = new StringTokenizer(arg,"/");
		if (4 == strtok.countTokens()){
		    this.ct = Integer.decode(strtok.nextToken());
		    this.t = ct;
		    this.cl = Integer.decode(strtok.nextToken());
		    this.l = this.cl+this.t;
		    this.cb = Integer.decode(strtok.nextToken());
		    this.b = this.cb+this.l;
		    this.cr = Integer.decode(strtok.nextToken());
		    this.r = this.cr+this.b;
		}
		else
		    throw new IllegalArgumentException("Bad layout '"+arg+"' @"+strtok.countTokens());
	    }

	    public Layout.Position getPosition(Pin p){

		if (p.number <= this.t)
		    return Position.T;
		else if (p.number <= this.l)
		    return Position.L;
		else if (p.number <= this.b)
		    return Position.B;
		else
		    return Position.R;
	    }
	    public int first(Layout.Position s){
		switch(s){
		case T:
		    return 0;
		case L:
		    return this.t;
		case B:
		    return this.l;
		case R:
		    return this.b;
		default:
		    throw new Error(s.name());
		}
	    }
	    public int last(Layout.Position s){
		switch(s){
		case T:
		    return this.t-1;
		case L:
		    return this.l-1;
		case B:
		    return this.b-1;
		case R:
		    return this.r-1;
		default:
		    throw new Error(s.name());
		}
	    }
	    public Layout.Dimension createDimension(){
		int cw = Math.max(this.ct,this.cb);
		int ch = Math.max(this.cl,this.cr);

		return new Dimension(this.inW,this.inH,cw,ch);
	    }
	}
	public final static class LBRT
	    extends LayoutOrder
	{
	    public final int cl, cb, cr, ct;
	    public final int l, b, r, t;

	    public LBRT(String arg){
		super(Layout.Order.LBRT);
		StringTokenizer strtok = new StringTokenizer(arg,"/");
		if (4 == strtok.countTokens()){
		    this.cl = Integer.decode(strtok.nextToken());
		    this.l = this.cl;
		    this.cb = Integer.decode(strtok.nextToken());
		    this.b = this.cb+this.l;
		    this.cr = Integer.decode(strtok.nextToken());
		    this.r = this.cr+this.b;
		    this.ct = Integer.decode(strtok.nextToken());
		    this.t = this.ct+this.r;
		}
		else
		    throw new IllegalArgumentException("Bad layout '"+arg+"' @"+strtok.countTokens());
	    }

	    public Layout.Position getPosition(Pin p){

		if (p.number <= this.l)
		    return Position.L;
		else if (p.number <= this.b)
		    return Position.B;
		else if (p.number <= this.r)
		    return Position.R;
		else
		    return Position.T;
	    }
	    public int first(Layout.Position s){
		switch(s){
		case T:
		    return this.r;
		case L:
		    return 0;
		case B:
		    return this.l;
		case R:
		    return this.b;
		default:
		    throw new Error(s.name());
		}
	    }
	    public int last(Layout.Position s){
		switch(s){
		case T:
		    return this.t-1;
		case L:
		    return this.l-1;
		case B:
		    return this.b-1;
		case R:
		    return this.r-1;
		default:
		    throw new Error(s.name());
		}
	    }
	    public Layout.Dimension createDimension(){
		int cw = Math.max(this.ct,this.cb);
		int ch = Math.max(this.cl,this.cr);

		return new Dimension(this.inW,this.inH,cw,ch);
	    }
	}
	public final static class BRTL
	    extends LayoutOrder
	{
	    public final int cb, cr, ct, cl;
	    public final int b, r, t, l;

	    public BRTL(String arg){
		super(Layout.Order.BRTL);
		StringTokenizer strtok = new StringTokenizer(arg,"/");
		if (4 == strtok.countTokens()){
		    this.cb = Integer.decode(strtok.nextToken());
		    this.b = this.cb;
		    this.cr = Integer.decode(strtok.nextToken());
		    this.r = this.cr+this.b;
		    this.ct = Integer.decode(strtok.nextToken());
		    this.t = this.ct+this.r;
		    this.cl = Integer.decode(strtok.nextToken());
		    this.l = this.cl+this.t;
		}
		else
		    throw new IllegalArgumentException("Bad layout '"+arg+"' @"+strtok.countTokens());
	    }

	    public Layout.Position getPosition(Pin p){

		if (p.number <= this.b)
		    return Position.B;
		else if (p.number <= this.r)
		    return Position.R;
		else if (p.number <= this.t)
		    return Position.T;
		else
		    return Position.L;
	    }
	    public int first(Layout.Position s){
		switch(s){
		case T:
		    return this.r;
		case L:
		    return this.t;
		case B:
		    return 0;
		case R:
		    return this.b;
		default:
		    throw new Error(s.name());
		}
	    }
	    public int last(Layout.Position s){
		switch(s){
		case T:
		    return this.t-1;
		case L:
		    return this.l-1;
		case B:
		    return this.b-1;
		case R:
		    return this.r-1;
		default:
		    throw new Error(s.name());
		}
	    }
	    public Layout.Dimension createDimension(){
		int cw = Math.max(this.ct,this.cb);
		int ch = Math.max(this.cl,this.cr);

		return new Dimension(this.inW,this.inH,cw,ch);
	    }
	}
	public final static class RTLB
	    extends LayoutOrder
	{
	    public final int cr, ct, cl, cb;
	    public final int r, t, l, b;

	    public RTLB(String arg){
		super(Layout.Order.RTLB);
		StringTokenizer strtok = new StringTokenizer(arg,"/");
		if (4 == strtok.countTokens()){
		    this.cr = Integer.decode(strtok.nextToken());
		    this.r = this.cr;
		    this.ct = Integer.decode(strtok.nextToken());
		    this.t = this.ct+this.r;
		    this.cl = Integer.decode(strtok.nextToken());
		    this.l = this.cl+this.t;
		    this.cb = Integer.decode(strtok.nextToken());
		    this.b = this.cb+this.l;
		}
		else
		    throw new IllegalArgumentException("Bad layout '"+arg+"' @"+strtok.countTokens());
	    }

	    public Layout.Position getPosition(Pin p){

		if (p.number <= this.r)
		    return Position.R;
		else if (p.number <= this.t)
		    return Position.T;
		else if (p.number <= this.l)
		    return Position.L;
		else
		    return Position.B;
	    }
	    public int first(Layout.Position s){
		switch(s){
		case T:
		    return this.r;
		case L:
		    return this.t;
		case B:
		    return this.l;
		case R:
		    return 0;
		default:
		    throw new Error(s.name());
		}
	    }
	    public int last(Layout.Position s){
		switch(s){
		case T:
		    return this.t-1;
		case L:
		    return this.l-1;
		case B:
		    return this.b-1;
		case R:
		    return this.r-1;
		default:
		    throw new Error(s.name());
		}
	    }
	    public Layout.Dimension createDimension(){
		int cw = Math.max(this.ct,this.cb);
		int ch = Math.max(this.cl,this.cr);

		return new Dimension(this.inW,this.inH,cw,ch);
	    }
	}

    }
    /**
     * Layout box has static dimensions
     * and dynamic coordinates for {@link Geda#layout}.
     */
    public static class Dimension {

	public final static int U = 100, X0 = 3*U, Y0 = 3*U, PinSep = 4*U;

	public final static int Pins(int c){
	    return PinSep+(PinSep*c);
	}
	public final static int Label(String s){
	    if (null != s)
		return LayoutFont.Instance().width(s);
	    else
		return 0;
	}
	/*
	public final static int Label(int slen){
	    return (LayoutFont.Instance.em() * slen);
	}
	*/
	public final static int Delta(int delta){

	    if (U < delta){

		final double u = U;

		final double d = delta;

		return (int)(Math.round( d / u) * u);
	    }
	    else
		return U;
	}

	public final int w, h, pw, ph, dw, dh;

	public final int cx, cy;

	public int x, y, history;

	public Layout.Position state;


	public Dimension(int iw, int ih, int cw, int ch){
	    super();
	    this.x = X0;
	    this.y = Y0;

	    this.pw = Dimension.Pins(cw);
	    this.ph = Dimension.Pins(ch);
	    this.w = (iw+this.pw);
	    this.h = (ih+this.ph);
	    this.cx = (this.w>>1)+this.x;
	    this.cy = (this.h>>1)+this.y;

	    this.dw = ((this.w-this.pw)>>1);
	    this.dh = ((this.h-this.ph)>>1);
	}
	public Dimension(Dimension d, int dw, int dh){
	    super();
	    this.x = X0;
	    this.y = Y0;
	    this.pw = d.pw;
	    this.ph = d.ph;
	    this.w = d.w+dw;
	    this.h = d.h+dh;

	    this.cx = (this.w>>1)+this.x;
	    this.cy = (this.h>>1)+this.y;

	    this.dw = ((this.w-this.pw)>>1);
	    this.dh = ((this.h-this.ph)>>1);
	}


	public Dimension init(){
	    this.x = X0;
	    this.y = Y0;
	    this.history = 0;
	    return this;
	}
	public Dimension top(){
	    /*
	     * asc right to left
	     */
	    if (Layout.Position.T != this.state){

		this.history = Layout.Position.T.once(history);
		this.state = Layout.Position.T;
		this.x = (X0 + this.w)-this.dw;
		this.y = (Y0 + this.h);
	    }
	    this.x -= PinSep;
	    return this;
	}
	public Dimension left(){
	    /*
	     * asc top to bottom
	     */
	    if (Layout.Position.L != this.state){

		this.history = Layout.Position.L.once(history);
		this.state = Layout.Position.L;
		this.x = (X0);
		this.y = (Y0 + this.h)-this.dh;
	    }
	    this.y -= PinSep;
	    return this;
	}
	public Dimension bottom(){
	    /*
	     * asc left to right
	     */
	    if (Layout.Position.B != this.state){

		this.history = Layout.Position.B.once(history);
		this.state = Layout.Position.B;
		this.x = (X0)+this.dw;
		this.y = (Y0);
	    }
	    this.x += PinSep;
	    return this;
	}
	public Dimension right(){
	    /*
	     * asc bottom to top
	     */
	    if (Layout.Position.R != this.state){

		this.history = Layout.Position.R.once(history);
		this.state = Layout.Position.R;
		this.x = (X0 + this.w);
		this.y = (Y0)+this.dh;
	    }
	    this.y += PinSep;
	    return this;
	}
	public String toString(){
	    return String.format("%d,%d",this.w,this.h);
	}
    }


    public final LayoutOrder layout;


    public Layout(String arg){
	super();
	if (null != arg){
	    Layout.Order order;

	    StringTokenizer strtok = new StringTokenizer(arg,":");
	    switch (strtok.countTokens()){
	    case 1:
		order = Layout.Order.LBRT;
		break;
	    case 2:
		order = Layout.Order.For(strtok.nextToken());
		break;
	    default:
		throw new IllegalArgumentException("Bad layout '"+arg+"' @"+strtok.countTokens());
	    }
	    switch(order){
	    case TLBR:
		this.layout = new LayoutOrder.TLBR(strtok.nextToken());
		break;
	    case LBRT:
		this.layout = new LayoutOrder.LBRT(strtok.nextToken());
		break;
	    case BRTL:
		this.layout = new LayoutOrder.BRTL(strtok.nextToken());
		break;
	    case RTLB:
		this.layout = new LayoutOrder.RTLB(strtok.nextToken());
		break;
	    default:
		throw new Error(order.name());
	    }
	}
	else
	    throw new IllegalArgumentException();
    }
    protected Layout(){
	super();
	this.layout = null;
    }


    public Dimension getSize(){
	return this.layout.getSize();
    }
    public Dimension layout0(Symbol sym){
	return this.layout.layout0(sym);
    }
    public Dimension layout1(int dw, int dh){
	return this.layout.layout1(dw,dh);
    }
    public Layout.Order getOrder(){
	return this.layout.order;
    }
    public Layout.Position getPosition(Pin p){
	return this.layout.getPosition(p);
    }
    public int first(Layout.Position s){
	return this.layout.first(s);
    }
    public int last(Layout.Position s){
	return this.layout.last(s);
    }
    public final java.lang.Iterable<Attribute> intersect(Symbol s, Position pos){
	switch(pos){
	case T:{
	    Attribute L0 = s.first(Position.L).getLabel();
	    Attribute RN = s.last(Position.R).getLabel();
	    return new Label.Iterable(new Attribute[]{L0,RN},s.bottom());
	}
	case L:
	    return new Label.Iterable(null,s.right());

	case B:{
	    Attribute LN = s.last(Position.L).getLabel();
	    Attribute R0 = s.first(Position.R).getLabel();
	    return new Label.Iterable(new Attribute[]{LN,R0},s.top());
	}
	case R:
	    return new Label.Iterable();
	default:
	    throw new Error(pos.name());
	}
    }
}
