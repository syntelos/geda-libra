package libra;

import java.util.StringTokenizer;

/**
 * Rectangle for the domain (X,Y) greater than zero, where 
 * width and height relate as
 * 
 *    W = (X2 - X1) 
 *    H = (Y2 - Y1) 
 * 
 * and may be negative.  Likewise, X2 and Y2 relate as
 * 
 *    X2 = (X1 + W)
 *    Y2 = (Y1 + H)
 * 
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Rectangle
    extends Object
{
    /**
     * Rectangle plotter for test/debug inverts coordinates into
     * gEDA/GSCHEM space.
     */
    public final static class Plot
	extends javax.swing.JFrame
    {
	private Rectangle[] list;
	private java.awt.Color[] colors;


	public Plot(){
	    super("Rectangle Plotter");
	    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    this.setBackground(java.awt.Color.white);
	    this.setForeground(java.awt.Color.blue);
	    this.setSize(600,600);

	    java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

	    int px = (screen.width>>1)-600;
	    int py = (screen.height>>1)-600;

	    this.setLocation(px,py);
	    this.show();
	}


	public Rectangle getListBounds(){
	    Rectangle union = new Rectangle();
	    if (null != this.list){
		for (Rectangle item: this.list){
		    union = union.union(item);
		}
	    }
	    return union;
	}
	public void success(){
	    if (java.awt.Color.blue != this.getForeground()){
		this.setForeground(java.awt.Color.blue);
		this.repaint();
	    }
	}
	public void failure(){
	    if (java.awt.Color.red != this.getForeground()){
		this.setForeground(java.awt.Color.red);
		this.repaint();
	    }
	}
	public void clear(){
	    if (null != this.list){
		this.list = null;
		this.setForeground(java.awt.Color.blue);
		this.repaint();
	    }
	}
	public void add(Rectangle item){
	    if (null != item){
		if (null == this.list)
		    this.list = new Rectangle[]{item};
		else {
		    final int len = this.list.length;
		    Rectangle[] copier = new Rectangle[len+1];
		    System.arraycopy(this.list,0,copier,0,len);
		    copier[len] = item;
		    this.list = copier;
		}
		{
		    final int len = this.list.length;
		    java.awt.Color[] copier = new java.awt.Color[len];
		    if (null == this.colors)
			this.colors = copier;
		    else {
			System.arraycopy(this.colors,0,copier,0,this.colors.length);
			this.colors = copier;
		    }
		}
		this.repaint();
	    }
	}
	/**
	 * Define a color for the previous "add(Rectangle)"
	 */
	public void set(java.awt.Color color){
	    this.colors[this.colors.length-1] = color;
	}
	public void set(String color){
	    this.set(java.awt.Color.decode(color));
	}
	@Override
	public void paint(java.awt.Graphics g) {
	    this.paint( (java.awt.Graphics2D) g);
	}
	@Override
	public void update(java.awt.Graphics g) {
	    this.paint( (java.awt.Graphics2D) g);
	}
	private void paint(java.awt.Graphics2D g) {

	    final java.awt.Dimension thisSize = this.getSize();
	    final java.awt.Insets thisInsets = this.getInsets();
	    {
		thisSize.width -= (thisInsets.left+thisInsets.right);
		thisSize.height -= (thisInsets.top+thisInsets.bottom);
	    }
	    g.setColor(this.getBackground());
	    {
		g.fillRect(thisInsets.top, thisInsets.left, thisSize.width, thisSize.height);
	    }
	    g.setColor(this.getForeground());

	    if (null != this.list){
		Rectangle bounds = this.getListBounds();

		double sx = (thisSize.getWidth()/bounds.width);
		double sy = (thisSize.getHeight()/bounds.height);

		java.awt.geom.AffineTransform xform = new java.awt.geom.AffineTransform();
		xform.translate(0,(thisInsets.top+thisSize.height));
		xform.scale(sx,-sy);

		g.setTransform(xform);

		final int count = this.list.length;

		java.awt.Color color = null;

		for (int cc = 0; cc < count; cc++){

		    Rectangle item = this.list[cc];

		    if (null != this.colors[cc])
			g.setColor(this.colors[cc]);

		    Rectangle nitem = item.normalize();

		    g.drawRect(nitem.x1,nitem.y1,nitem.width,nitem.height);
		}
	    }
	}
    }


    public int x1, y1, x2, y2, width, height;


    public Rectangle(){
	super();
    }
    public Rectangle(int x1, int y1, int x2, int y2){
	super();
	this.xyxy(x1,y1,x2,y2);
    }
    /**
     * Parser of toString output.
     */
    public Rectangle(String s){
	this(new StringTokenizer(s,", ;:"));
    }
    public Rectangle(StringTokenizer s){
	this(s.nextToken(),s.nextToken(),s.nextToken(),s.nextToken());
    }
    public Rectangle(String x1, String y1, String x2, String y2){
	this(Integer.parseInt(x1),Integer.parseInt(y1),Integer.parseInt(x2),Integer.parseInt(y2));
    }


    public boolean isEmpty(){
	return (0 == this.width || 0 == this.height);
    }
    /*
     * Illustration of PIN case
     */
    public boolean isLine(){
	if (0 == this.width || 0 == this.height)
	    return (0 != this.x2 && 0 != this.y2);
	else
	    return false;
    }
    public boolean isNotEmpty(){
	return (0 != this.width && 0 != this.height);
    }
    public Rectangle copy(Rectangle that){
	this.x1 = that.x1;
	this.y1 = that.y1;
	this.x2 = that.x2;
	this.y2 = that.y2;
	this.width = that.width;
	this.height = that.height;
	return this;
    }
    public Rectangle wh(boolean init){
	if (init){
	    if (0 != this.x2 || 0 != this.y2){
		this.width = (this.x2 - this.x1);
		this.height = (this.y2 - this.y1);
	    }
	}
	else {
	    /*
	     * update
	     */
	    this.width = (this.x2 - this.x1);
	    this.height = (this.y2 - this.y1);
	}
	return this;
    }
    public Rectangle xy(boolean init){
	if (init){
	    if (0 != this.width || 0 != this.height){
		this.x2 = (this.x1 + this.width);
		this.y2 = (this.y1 + this.height);
	    }
	}
	else {
	    /*
	     * update
	     */
	    this.x2 = (this.x1 + this.width);
	    this.y2 = (this.y1 + this.height);
	}
	return this;
    }
    public Rectangle rect(boolean init){
	/*
	 * (init type op)
	 */
	if (0 == this.width && 0 == this.height)
	    return this.wh(init);
	else if (0 == this.x2 && 0 == this.y2)
	    return this.xy(init);
	else
	    return this;
    }
    public Rectangle xy(int x1, int y1){
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = (this.x1 + this.width);
	this.y2 = (this.y1 + this.height);
	return this;
    }
    public Rectangle xyxy(int x1, int y1, int x2, int y2){
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
	this.width = (this.x2 - this.x1);
	this.height = (this.y2 - this.y1);
	return this;
    }
    public Rectangle dx1(int dx1){
	this.x1 += dx1;
	return this.wh(false);
    }
    public Rectangle dy1(int dy1){
	this.y1 += dy1;
	return this.wh(false);
    }
    public Rectangle dx2(int dx2){
	this.x2 += dx2;
	return this.wh(false);
    }
    public Rectangle dy2(int dy2){
	this.y2 += dy2;
	return this.wh(false);
    }
    public Rectangle dxy1(int dx1, int dy1){
	this.x1 += dx1;
	this.y1 += dy1;
	return this.wh(false);
    }
    public Rectangle normalize(){

	int x1 = Math.min(this.x1,this.x2);
	int y1 = Math.min(this.y1,this.y2);
	int x2 = Math.max(this.x1,this.x2);
	int y2 = Math.max(this.y1,this.y2);

	return new Rectangle(x1,y1,x2,y2);
    }
    public Rectangle union(Rectangle that){

	int thisX1 = Math.min(this.x1,this.x2);
	int thisY1 = Math.min(this.y1,this.y2);
	int thisX2 = Math.max(this.x1,this.x2);
	int thisY2 = Math.max(this.y1,this.y2);

	int thatX1 = Math.min(that.x1,that.x2);
	int thatY1 = Math.min(that.y1,that.y2);
	int thatX2 = Math.max(that.x1,that.x2);
	int thatY2 = Math.max(that.y1,that.y2);

	int ux1 = Math.min(thisX1,thatX1);
	int uy1 = Math.min(thisY1,thatY1);
	int ux2 = Math.max(thisX2,thatX2);
	int uy2 = Math.max(thisY2,thatY2);

	return new Rectangle(ux1,uy1,ux2,uy2);
    }
    public Rectangle intersection(Rectangle that){

	Rectangle thisN = this.normalize();
	Rectangle thatN = that.normalize();

	int ix1 = Math.max(thisN.x1,thatN.x1);
	int iy1 = Math.max(thisN.y1,thatN.y1);
	int ix2 = Math.min(thisN.x2,thatN.x2);
	int iy2 = Math.min(thisN.y2,thatN.y2);

	if ((0 < (ix2-ix1))&&(0 < (iy2-iy1)))
	    return new Rectangle(ix1,iy1,ix2,iy2);
	else
	    return new Rectangle();
    }
    public boolean intersects(Rectangle that){

	Rectangle thisN = this.normalize();
	Rectangle thatN = that.normalize();

	int ix1 = Math.max(thisN.x1,thatN.x1);
	int iy1 = Math.max(thisN.y1,thatN.y1);
	int ix2 = Math.min(thisN.x2,thatN.x2);
	int iy2 = Math.min(thisN.y2,thatN.y2);

	return ((0 < (ix2-ix1))&&(0 < (iy2-iy1)));
    }
    public boolean equals(Rectangle that){
	return (this.x1 == that.x1 && this.y1 == that.y1 &&
		this.x2 == that.x2 && this.y2 == that.y2);
    }
    public boolean equals(java.awt.Rectangle that){
	return (this.x1 == that.x && this.y1 == that.y &&
		this.width == that.width && this.height == that.height);
    }
    public String rectString(){
	return String.format("%d,%d,%d,%d",this.x1,this.y1,this.x2,this.y2);
    }
    public String toString(){
	return this.rectString();
    }
    public int compareTo(Rectangle that){
	if (this == that)
	    return 0;
	else if (this.width < that.width)
	    return -1;
	else if (this.width == that.width){

	    if (this.height < that.height)
		return -1;
	    else if (this.height == that.height){

		if (this.x1 < that.x1)
		    return -1;
		else if (this.x1 == that.x1){

		    if (this.y1 < that.y1)
			return -1;
		    else if (this.y1 == that.y1)
			return 0;
		    else 
			return 1;
		}
		else 
		    return 1;
	    }
	    else
		return 1;
	}
	else
	    return 1;
    }


    public enum Op {

	help, intersection, plot, union, random, show;

	public final static Op For(String s){
	    while (0 < s.length() && '-' == s.charAt(0))
		s = s.substring(1);

	    try {
		return Op.valueOf(s);
	    }
	    catch (RuntimeException exc){
		for (Op op: Op.values()){
		    if (op.name().startsWith(s))
			return op;
		}
		return Op.help;
	    }
	}
    }
    private static void usage(){
	System.err.println("Usage");
	System.err.println();
	System.err.println("      Rectangle cmd");
	System.err.println();
	System.err.println("  Commands");
	System.err.println();
	System.err.println("      intersection a b");
	System.err.println();
	System.err.println("              Perform the intersection of 'a' and 'b' using this class.");
	System.err.println();
	System.err.println("      plot a*");
	System.err.println();
	System.err.println("              Graphically display zero or more following rectangles.  Any");
	System.err.println("              subsequent commands also will be displayed. ");
	System.err.println();
	System.err.println("      union a b");
	System.err.println();
	System.err.println("              ");
	System.err.println();
	System.err.println("      random [N [cmd]] ");
	System.err.println();
	System.err.println("              Produce N (default 10) operations (cmd default intersect).");
	System.err.println();
	System.err.println("      <cmd> show");
	System.err.println();
	System.err.println("              Optional suffix to enable the graphical display of (scaled)");
	System.err.println("              results.   Display is implied by the use of the 'plot' ");
	System.err.println("              operator.");
	System.err.println();
	System.err.println("  Argument rectangles, a & b");
	System.err.println();
	System.err.println("      The two arguments 'a' and 'b' each have the following");
	System.err.println("      format.");
	System.err.println("               x1,y1,x2,y2");
	System.err.println();
	System.err.println("  Implementation");
	System.err.println();
	System.err.println("      We compare this implementation to that in the AWT Rectangle.");
	System.err.println("      Note that the AWT is wrong in many cases, hence 'show'.");
	System.err.println();
	System.exit(1);
    }
    public static void main(String[] argv){
	if (0 < argv.length){
	    boolean show = false;
	    Rectangle.Plot plotter = null;

	    final int argc = argv.length;
	    for (int cc = 0; cc < argc; cc++){
		String arg = argv[cc];
		Op op = Op.For(arg);
		switch(op){
		case help:
		    usage();
		    break;
		case plot:{
		    show = true;
		    if (null == plotter)
			plotter = new Rectangle.Plot();

		    for (cc += 1; cc < argc; cc++){
			arg = argv[cc];
			if (Op.help == Op.For(arg)){
			    try {
				plotter.add(new Rectangle(arg));
			    }
			    catch (RuntimeException exc){

				plotter.set(arg);
			    }
			}
			else {
			    cc -= 1;
			    break;
			}
		    }
		    break;
		}
		case random:{
		    final java.util.Random random = new java.util.Random();

		    Op cmd = Op.intersection;
		    int N = 10;
		    cc += 1;
		    if (cc < argc){
			arg = argv[cc];
			if ("show".equals(arg)){
			    show = true;
			    plotter = new Rectangle.Plot();
			}
			else {
			    N = Integer.parseInt(arg);
			    cc += 1;
			    if (cc < argc){
				arg = argv[cc];
				if ("show".equals(arg)){
				    show = true;
				    plotter = new Rectangle.Plot();
				}
				else {
				    cmd = Op.For(arg);
				    cc += 1;
				    if (cc < argc){
					arg = argv[cc];
					if ("show".equals(arg)){
					    show = true;
					    plotter = new Rectangle.Plot();
					}
				    }
				}
			    }
			}
		    }
		    else
			N = 10;


		    int errors = 0;
		    for (int trial = 0; trial < N; trial++){
			Rectangle a = new Rectangle(random.nextInt(1000),random.nextInt(1000),random.nextInt(1000),random.nextInt(1000));
			Rectangle b = new Rectangle(random.nextInt(1000),random.nextInt(1000),random.nextInt(1000),random.nextInt(1000));
			java.awt.Rectangle ta, tb, tr;
			{
			    Rectangle an = a.normalize();
			    Rectangle bn = b.normalize();
			    ta = new java.awt.Rectangle(an.x1,an.y1,an.width,an.height);
			    tb = new java.awt.Rectangle(bn.x1,bn.y1,bn.width,bn.height);
			    if (show){
				plotter.clear();
				plotter.add(an);
				plotter.add(bn);
			    }
			}

			Rectangle r ;
			switch(cmd){
			case intersection:
			    r = a.intersection(b);
			    tr = ta.intersection(tb);
			    break;
			case union:
			    r = a.union(b);
			    tr = ta.union(tb);
			    break;
			default:
			    throw new Error(cmd.name());
			}
			if (show){
			    plotter.add(r);
			}
			if (r.equals(tr)){
			    if (show){
				plotter.success();
			    }
			    System.out.printf("OK A %s%n",a);
			    System.out.printf("OK B %s%n",b);
			    System.out.printf("OK SYM %s%n",r);
			    System.out.printf("OK AWT %d,%d,%d,%d%n",tr.x,tr.y,(tr.x+tr.width),(tr.y+tr.height));
			}
			else {
			    if (show){
				plotter.failure();
			    }
			    System.out.printf("ER A %s%n",a);
			    System.out.printf("ER B %s%n",b);
			    System.out.printf("ER SYM %s%n",r);
			    System.out.printf("ER AWT %d,%d,%d,%d%n",tr.x,tr.y,(tr.x+tr.width),(tr.y+tr.height));
			    errors += 1;
			}
		    }
		    if (!show)
			System.exit(errors);
		    break;
		}
		    /*
		     * Union & Intersection (operators having two operands)
		     */
		default:
		    cc += 1;
		    if (cc < argc){
			try {
			    Rectangle a = new Rectangle(argv[cc]);
			    cc += 1;
			    if (cc < argc){
				Rectangle b = new Rectangle(argv[cc]);

				cc += 1;
				if (cc < argc){
				    arg = argv[cc];
				    if ("show".equals(arg)){
					show = true;
					plotter = new Rectangle.Plot();
				    }
				}

				java.awt.Rectangle ta, tb, tr;
				{
				    Rectangle an = a.normalize();
				    Rectangle bn = b.normalize();
				    ta = new java.awt.Rectangle(an.x1,an.y1,an.width,an.height);
				    tb = new java.awt.Rectangle(bn.x1,bn.y1,bn.width,bn.height);
				    if (show){
					plotter.clear();
					plotter.add(an);
					plotter.add(bn);
				    }
				}


				Rectangle r ;
				switch(op){
				case intersection:
				    r = a.intersection(b);
				    tr = ta.intersection(tb);
				    break;
				case union:
				    r = a.union(b);
				    tr = ta.union(tb);
				    break;
				default:
				    throw new Error(op.name());
				}
				if (show){
				    plotter.add(r);
				}
				if (r.equals(tr)){
				    if (show){
					plotter.success();
				    }
				    System.out.printf("OK A %s%n",a);
				    System.out.printf("OK B %s%n",b);
				    System.out.printf("OK SYM %s%n",r);
				    System.out.printf("OK AWT %d,%d,%d,%d%n",tr.x,tr.y,(tr.x+tr.width),(tr.y+tr.height));
				    if (!show)
					System.exit(0);
				}
				else {
				    if (show){
					plotter.failure();
				    }
				    System.out.printf("ER A %s%n",a);
				    System.out.printf("ER B %s%n",b);
				    System.out.printf("ER SYM %s%n",r);
				    System.out.printf("ER AWT %d,%d,%d,%d%n",tr.x,tr.y,(tr.x+tr.width),(tr.y+tr.height));
				    if (!show)
					System.exit(1);
				}
			    }
			    else {
				System.err.println("Missing argument 'Rectangle b'.");
				System.exit(1);
			    }
			}
			catch (Exception exc){
			    System.err.println("Malformed argument 'Rectangle'.");
			    System.exit(1);
			}
		    }
		    else {
			System.err.println("Missing argument 'Rectangle a'.");
			System.exit(1);
		    }
		    break;
		}
	    }
	}
	else
	    usage();
    }

    public final static Rectangle[] Union(Rectangle[] list, Rectangle item){
	if (item.isEmpty())
	    return list;
	else if (null == list)
	    return new Rectangle[]{item};
	else {
	    final int idx = IndexOf(list,item);
	    if (-1 < idx){
		list[idx] = list[idx].union(item);
		return list;
	    }
	    else
		return Rectangle.Add(list,item);
	}
    }
    public final static int IndexOf(Rectangle[] list, Rectangle item){
	if (null == item)
	    return -1;
	else if (null == list)
	    return -1;
	else {
	    final int len = list.length;
	    for (int cc = 0; cc < len; cc++){
		if (item.intersects(list[cc]))
		    return cc;
	    }
	    return -1;
	}
    }
    public final static Rectangle[] Add(Rectangle[] list, Rectangle item){
	if (null == item)
	    return list;
	else if (null == list)
	    return new Rectangle[]{item};
	else {
	    int len = list.length;
	    Rectangle[] copier = new Rectangle[len+1];
	    System.arraycopy(list,0,copier,0,len);
	    copier[len] = item;
	    return copier;
	}
    }

    public final static void SortAscending(Rectangle[] list){

	java.util.Arrays.sort(list,Comparator.ASC);
    }
    public final static void SortDescending(Rectangle[] list){

	java.util.Arrays.sort(list,Comparator.DSC);
    }
    /**
     * 
     */
    public final static class Comparator
	extends Object
	implements java.util.Comparator<Rectangle>
    {
	public enum Order {
	    Ascending, Descending;
	}

	public final static Comparator ASC = new Comparator(Order.Ascending);
	public final static Comparator DSC = new Comparator(Order.Descending);

	public final static Comparator For(Order ord){
	    switch(ord){
	    case Ascending:
		return Comparator.ASC;
	    case Descending:
		return Comparator.DSC;
	    default:
		throw new Error(ord.name());
	    }
	}


	public final Order order;


	public Comparator(Order order){
	    super();
	    if (null != order)
		this.order = order;
	    else
		throw new IllegalArgumentException();
	}


	public int compare(Rectangle a, Rectangle b){
	    switch (this.order){
	    case Ascending:
		return a.compareTo(b);
	    case Descending:
		switch(a.compareTo(b)){
		case 0:
		    return 0;
		case 1:
		    return -1;
		default:
		    return 1;
		}
	    default:
		throw new Error(this.order.name());
	    }
	}
	public boolean equals(Rectangle a, Rectangle b){
	    return a.equals(b);
	}
    }
    /**
     * Branch namespace so subclasses don't see these names directly.
     */
    public interface R {

	public static class Iterator
	    extends Object
	    implements java.util.Iterator<Rectangle>
	{

	    private final Rectangle[] list;
	    private final int length;
	    private int index;


	    public Iterator(Rectangle[] list){
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
	    public Rectangle next(){
		return this.list[this.index++];
	    }
	    public void remove(){
		throw new UnsupportedOperationException();
	    }
	}
	public static class Iterable
	    extends Object
	    implements java.lang.Iterable<Rectangle>
	{
	    private final Rectangle[] list;

	    public Iterable(Rectangle[] list){
		super();
		this.list = list;
	    }

	    public java.util.Iterator<Rectangle> iterator(){
		return new R.Iterator(this.list);
	    }
	}

    }
}
