package libra;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Toolkit;

/**
 * An experimental approach to determining string width for labels and
 * other text -- designed for possible future extensions to accomodate
 * any platform variance.
 */
public class LayoutFont {

    public final static LayoutFont Instance = new LayoutFont();



    private final Font font;

    private final FontMetrics fm;

    private final int height, ascent, leading, em;


    private LayoutFont(){
	super();
	this.font = new Font(Font.SANS_SERIF,Font.PLAIN,145);
	final Toolkit tk = Toolkit.getDefaultToolkit();

	this.fm = tk.getFontMetrics(this.font);
	this.height = this.fm.getHeight();
	this.leading = this.fm.getLeading()+30;
	this.ascent = this.fm.getAscent();
	this.em = this.width("m");
    }


    public int em(){
	return this.em;
    }
    /*
     * This value should be "105" for the "leading" line touching the
     * top of a string of caps, eg, "ABC", and the "ascent" line
     * touching the bottom -- as seen in "LayoutFont$Test".
     * 
     * In this case, we've got a font to approximate what we see in
     * GSCHEM -- and can use to calculate string widths.
     */
    public int height(){
	return (this.ascent-this.leading);
    }
    public int width(String string){

	if (string.startsWith("\\_") && string.endsWith("\\_"))
	    string = string.substring(2,string.length()-2);

	return this.fm.stringWidth(string);
    }


    public static class Test
	extends javax.swing.JFrame
    {
	private final String string;

	private final Font msg = new Font(Font.MONOSPACED,Font.BOLD,24);

	private final Color ok = new Color(0x10,0xaf,0x10);
	private final Color er = new Color(0xaf,0x10,0x10);

	private final int width, height;


	public Test(String[] argv){
	    super("test");
	    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	    this.setBackground(java.awt.Color.white);
	    this.setForeground(java.awt.Color.blue);
	    this.setSize(600,600);

	    final Toolkit tk = Toolkit.getDefaultToolkit();

	    this.string = argv[0];
	    this.width = LayoutFont.Instance.width(this.string);
	    this.height = LayoutFont.Instance.height();

	    java.awt.Dimension screen = tk.getScreenSize();

	    int px = (screen.width>>1)-600;
	    int py = (screen.height>>1)-600;

	    this.setLocation(px,py);
	    this.show();

	    System.out.printf("%s (%d x %d)%n",this.string,this.width,this.height);
	}

	public void paint(java.awt.Graphics g) {
	    this.paint( (java.awt.Graphics2D) g);
	}
	public void update(java.awt.Graphics g) {
	    this.paint( (java.awt.Graphics2D) g);
	}
	private void paint(java.awt.Graphics2D g) {

	    int x = 10, y = 200;

	    final java.awt.Dimension thisSize = this.getSize();
	    final java.awt.Insets thisInsets = this.getInsets();
	    {
		x += thisInsets.left+10;
		y += thisInsets.top+10;
		thisSize.width -= (thisInsets.left+thisInsets.right);
		thisSize.height -= (thisInsets.top+thisInsets.bottom);
	    }
	    g.setColor(this.getBackground());
	    {
		g.fillRect(thisInsets.top, thisInsets.left, thisSize.width, thisSize.height);
	    }
	    g.setColor(this.getForeground());
	    /*
	     * Use Layout Font
	     */
	    g.setFont(LayoutFont.Instance.font);

	    g.drawString(this.string,x,y);

	    g.setColor(Color.red);

	    Rectangle r = new Rectangle(x,(y-this.height),this.width,this.height);

	    g.drawRect(r.x,r.y,r.width,r.height);

	    if (105 == this.height){

		g.setColor(this.ok);
		g.setFont(this.msg);

		g.drawString("OK 105",400,80);
	    }
	    else {

		g.setColor(this.er);
		g.setFont(this.msg);

		g.drawString(String.format("ER %d",this.height),400,80);
	    }
	}


	public static void main(String[] argv){

	    if (0 < argv.length)
		new Test(argv);
	    else {
		System.err.println("Usage" );
		System.err.println();
		System.err.println("  java -cp sch.jar libra.LayoutFont$Test <string>" );
		System.err.println();
		System.exit(1);
	    }
	}
    }
}
