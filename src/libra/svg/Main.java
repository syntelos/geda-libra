package libra.svg;

import libra.Path;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Path data manipulation tool.
 */
public class Main
    extends javax.swing.JFrame
{

    public final Stroke stroke;
    public final Color color;
    public final Shape shape;


    public Main(Shape shape, Dimension area){
	super("SVG Path");
	this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	if (null != shape && null != area){

	    this.stroke = new BasicStroke(1f);
	    this.color = Color.black;
	    this.shape = shape;

	    this.setSize(Math.max(60,area.width),Math.max(60,area.height));

	    this.show();
	}
	else
	    throw new IllegalArgumentException();
    }


    public void paint(Graphics g){

	this.update( (Graphics2D)g);
    }
    public void update(Graphics g){

	this.update( (Graphics2D)g);
    }
    public void update(Graphics2D g){
	Insets insets = this.getInsets();
	g.translate(insets.left,insets.top);

	g.setColor(Color.white);
	g.fillRect(0,0,this.getWidth(),this.getHeight());
	
	g.setColor(this.color);
	g.setStroke(this.stroke);
	g.fill(this.shape);
    }

    public enum Opt {
	A, F, G, H, I, T, V;

	public final static Opt For(String arg){
	    while (0 < arg.length() && '-' == arg.charAt(0))
		arg = arg.substring(1);

	    try {
		return Opt.valueOf(arg.toUpperCase());
	    }
	    catch (RuntimeException exc){
		return Opt.H;
	    }
	}
    }
    public static void usage(){
	System.out.println("Usage");
	System.out.println();
	System.out.println("  svg [-i] -g -a WxH -f file.p");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Display the transformed shape path from 'file.p' into a window area having ");
	System.out.println("    pixel dimensions width (W) and height (H).");
	System.out.println();
	System.out.println("    Optionally invert (with '-i') the data from Y+ down to Y+ up (horizontal");
	System.out.println("    flip).");
	System.out.println();
	System.out.println("    The input file contains raw path data, for example:");
	System.out.println();
	System.out.println("        M19.200000,334.080000L19.200000,302.080000L364.480000,302.080000");
	System.out.println("        L364.480000,334.080000L19.200000,334.080000ZM155.680000,207.840000");
	System.out.println("        L155.680000,237.760000L19.200000,237.760000L19.200000,205.600000");
	System.out.println("        L114.880000,205.600000C97.440000,187.840000,88.800000,167.840000,");
	System.out.println("        88.800000,145.760000C88.800000,118.240000,99.040000,94.720000,");
	System.out.println("        119.360000,74.880000C139.680000,55.040000,163.840000,45.120000,");
	System.out.println("        192.000000,45.120000C220.000000,45.120000,244.160000,55.040000,");
	System.out.println("        264.480000,74.880000C284.960000,94.720000,295.040000,118.240000,");
	System.out.println("        295.040000,145.760000C295.040000,167.840000,286.400000,187.840000,");
	System.out.println("        268.960000,205.600000L364.480000,205.600000L364.480000,237.760000");
	System.out.println("        L228.000000,237.760000L228.000000,207.840000C251.200000,192.800000,");
	System.out.println("        262.880000,172.640000,262.880000,147.360000C262.880000,128.160000,");
	System.out.println("        255.840000,111.840000,241.920000,98.080000C227.840000,84.160000,");
	System.out.println("        211.200000,77.280000,192.000000,77.280000C172.480000,77.280000,");
	System.out.println("        155.840000,84.160000,141.920000,98.080000C128.000000,111.840000,");
	System.out.println("        120.960000,128.160000,120.960000,147.360000C120.960000,172.640000,");
	System.out.println("        132.480000,192.800000,155.680000,207.840000Z");
	System.out.println();
	System.out.println("Usage");
	System.out.println();
	System.out.println("  svg [-g] -a AWxAH -v VWxVH -f file.p");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Apply area to view box scale transform on the shape from 'file.p'.");
	System.out.println();
	System.out.println("    Optionally display (with '-g') the data set visually.");
	System.out.println();
	System.out.println("Usage");
	System.out.println();
	System.out.println("  svg [-g -a AWxAH] -t sx,hx,hy,sy,tx,ty -f file.p");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Apply transform matrix (with '-t') to the shape from 'file.p'.");
	System.out.println();
	System.out.println("    Optionally display (with '-g') the data set into a window area (-a)");
	System.out.println("    having pixel dimensions width (AW) and height (AH).");
	System.out.println();
	System.out.println("Usage");
	System.out.println();
	System.out.println("  svg [-g] -a AWxAH -v VWxVH -t sx,hx,hy,sy,tx,ty -f file.p");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("    Apply view and matrix transforms to the shape from 'file.p'.");
	System.out.println();
	System.out.println("    Optionally display (with '-g') the data set visually.");
	System.out.println();
	System.exit(1);
    }
    public static void main(String[] argv){
	if (0 == argv.length)
	    usage();
	else {
	    File fin = null;
	    float[] a = null, v = null, t = null;
	    boolean show = false;
	    boolean invert = false;

	    for (int cc = 0, argc = argv.length; cc < argc; cc++){
		String arg = argv[cc];
		Opt opt = Opt.For(arg);
		switch(opt){
		case A:
		    cc += 1;
		    if (cc < argc){
			arg = argv[cc];
			a = Parse(arg);
			if (null == a || 2 != a.length){
			    System.err.println("Bad value for argument '-a'.");
			    System.exit(1);
			}
		    }
		    else
			usage();
		    break;
		case F:
		    cc += 1;
		    if (cc < argc){
			arg = argv[cc];
			fin = new File(arg);
		    }
		    else
			usage();
		    break;
		case G:
		    show = (!show);
		    break;
		case H:
		    usage();
		    break;
		case I:
		    invert = (!invert);
		    break;
		case T:
		    cc += 1;
		    if (cc < argc){
			arg = argv[cc];
			t = Parse(arg);
			if (null == t || 6 != t.length){
			    System.err.println("Bad value for argument '-t'.");
			    System.exit(1);
			}
		    }
		    else
			usage();
		    break;
		case V:
		    cc += 1;
		    if (cc < argc){
			arg = argv[cc];
			v = Parse(arg);
			if (null == v || 2 != v.length){
			    System.err.println("Bad value for argument '-v'.");
			    System.exit(1);
			}
		    }
		    else
			usage();
		    break;
		default:
		    throw new Error(opt.name());
		}
	    }
	    if (null != fin && fin.isFile()){
		try {
		    String pathString;
		    {
			StringBuilder buffer = new StringBuilder();
			InputStream in = new FileInputStream(fin);
			try {
			    int ch;
			    while (-1 < (ch = in.read())){
				buffer.append( (char)ch);
			    }
			}
			finally {
			    in.close();
			}
			pathString = buffer.toString();
		    }

		    Path path = new Path(pathString);

		    Dimension area = null;
		    if (null != a){
			area = new Dimension((int)a[0],(int)a[1]);
		    }

		    Shape shape = path.apply(new Path2D.Float());

		    if (invert){
			final double sx = 1;
			final double sy = -1;
			AffineTransform matrix = AffineTransform.getScaleInstance(sx,sy);

			if (null != v){

			    matrix.translate(0,-v[1]);
			}
			else if (null != a){

			    matrix.translate(0,-a[1]);
			}
			else {
			    Rectangle b = shape.getBounds();

			    matrix.translate(0,-b.getHeight());
			}

			shape = matrix.createTransformedShape(shape);
		    }

		    if (null != v){

			if (null != a){

			    Dimension view = new Dimension((int)v[0],(int)v[1]);

			    final double sx = area.getWidth()/view.getWidth();
			    final double sy = area.getHeight()/view.getHeight();
			    final double s = Math.min(sx,sy);

			    AffineTransform matrix = AffineTransform.getScaleInstance(s,s);

			    if (null != t){

				matrix.concatenate(new AffineTransform(t));
			    }

			    shape = matrix.createTransformedShape(shape);
			}
			else 
			    usage();
		    }
		    else if (null != t){

			shape = new AffineTransform(t).createTransformedShape(shape);
		    }

		    System.out.println(ToString(shape));

		    if (show){

			if (null != area)
			    new Main(shape,area);

			else {
			    System.exit(1);
			}
		    }
		}
		catch (Exception exc){
		    exc.printStackTrace();
		    System.exit(1);
		}
	    }
	    else
		usage();
	}
    }
    public final static float[] Parse(String arg){
	StringTokenizer strtok = new StringTokenizer(arg,",xX");
	final int count = strtok.countTokens();
	if (0 < count){
	    float[] re = new float[count];
	    for (int cc = 0; cc < count; cc++){
		try {
		    re[cc] = Float.parseFloat(strtok.nextToken());
		}
		catch (RuntimeException exc){
		    return null;
		}
	    }
	    return re;
	}
	else
	    return null;
    }

    public final static String ToString(Shape shape){
	StringBuilder string = new StringBuilder();
	PathIterator p = shape.getPathIterator(null);
	double[] s = new double[6];
	while (!p.isDone()){

	    switch(p.currentSegment(s)){
	    case PathIterator.SEG_MOVETO:
		if (0 < string.length())
		    string.append(' ');
		string.append('M');
		string.append(String.format(FFmt,s[0]));
		string.append(' ');
		string.append(String.format(FFmt,s[1]));
		break;
	    case PathIterator.SEG_LINETO:
		if (0 < string.length())
		    string.append(' ');
		string.append('L');
		string.append(String.format(FFmt,s[0]));
		string.append(' ');
		string.append(String.format(FFmt,s[1]));
		break;
	    case PathIterator.SEG_QUADTO:
		if (0 < string.length())
		    string.append(' ');
		string.append('Q');
		string.append(String.format(FFmt,s[0]));
		string.append(' ');
		string.append(String.format(FFmt,s[1]));
		string.append(' ');
		string.append(String.format(FFmt,s[2]));
		string.append(' ');
		string.append(String.format(FFmt,s[3]));
		break;
	    case PathIterator.SEG_CUBICTO:
		if (0 < string.length())
		    string.append(' ');
		string.append('C');
		string.append(String.format(FFmt,s[0]));
		string.append(' ');
		string.append(String.format(FFmt,s[1]));
		string.append(' ');
		string.append(String.format(FFmt,s[2]));
		string.append(' ');
		string.append(String.format(FFmt,s[3]));
		string.append(' ');
		string.append(String.format(FFmt,s[4]));
		string.append(' ');
		string.append(String.format(FFmt,s[5]));
		break;
	    case PathIterator.SEG_CLOSE:
		string.append('Z');
		break;
	    }
	    p.next();
	}
	return string.toString();
    }
    private final static String FFmt = "%f";
}
