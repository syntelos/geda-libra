package libra.sym;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Write 'tragesym' source table output.
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public class Tragesym
    extends java.io.PrintStream
{

    public final File src;


    public Tragesym(File src)
	throws IOException
    {
	super(new FileOutputStream(src));
	this.src = src;
    }


    public File run() 
	throws InterruptedException, IOException
    {

	File tgt = Target(this.src);

	String[] cmd = {
	    "tragesym",
	    this.src.getPath(),
	    tgt.getPath()
	};

	Process p = Runtime.getRuntime().exec(cmd);
	try {
	    if (0 == p.waitFor())
		return tgt;
	    else
		throw new IOException(String.format("Error running 'tragesym': %s",Error(p)));
	}
	finally {
	    p.destroy();
	}
    }

    public void write(Sym sym) throws IOException {

	this.printf("# tragesym source%n");
	this.printf("%n");
	this.printf("[options]%n");
	this.printf("wordswap=no%n");
	this.printf("rotate_labels=yes%n");
	this.printf("sort_labels=no%n");
	this.printf("generate_pinseq=no%n");
	this.printf("sym_width=1400%n");
	this.printf("pinwidthvertical=400%n");
	this.printf("%n");
	this.printf("[geda_attr]%n");
	this.printf("version=%s%n",sym.version);
	this.printf("name=%s%n",sym.part);
	this.printf("device=%s%n",sym.part);
	this.printf("refdes=U?%n");
	this.printf("footprint=%s%n",sym.getFootprint());
	if (null != sym.description){
	    this.printf("description=%s%n",sym.description);
	}
	else {
	    this.printf("description=%n");
	}
	if (null != sym.documentation){
	    this.printf("documentation=%s%n",sym.documentation);
	}
	else {
	    this.printf("documentation=%n");
	}
	if (null != sym.author){
	    this.printf("author=%s%n",sym.author);
	}
	else {
	    this.printf("author=%n");
	}
	if (null != sym.license){
	    this.printf("dist-license=%s%n",sym.license);
	    this.printf("use-license=%s%n",sym.license);
	}
	else {
	    this.printf("dist-license=%n");
	    this.printf("use-license=%n");
	}
	this.printf("numslots=0%n");
	this.printf("#slot=1%n");
	this.printf("#slotdef=1:%n");
	this.printf("#slotdef=2:%n");
	this.printf("#comment=%n");
	this.printf("%n");
	this.printf("[pins]%n");
	this.printf("# tabseparated list of pin descriptions%n");
	this.printf("# ----------------------------------------%n");
	this.printf("# pinnr is the physical number of the pin%n");
	this.printf("# seq is the pinseq= attribute, leave it blank if it doesn't matter%n");
	this.printf("# type can be (in, out, io, oc, oe, pas, tp, tri, clk, pwr)%n");
	this.printf("# style can be (line,dot,clk,dotclk,none). none if only want to add a net%n");
	this.printf("# posit. can be (l,r,t,b) or empty for nets%n");
	this.printf("# net specifies the name of the net. Vcc or GND for example.%n");
	this.printf("# label represents the pinlabel.%n");
	this.printf("#	negation lines can be added with \"\\_\" example: \\_enable\\_ %n");
	this.printf("#-----------------------------------------------------%n");
	this.printf("#pinnr	seq	type	style	posit.	net	label	%n");
	this.printf("#-----------------------------------------------------%n");
	/*
	 */
	for (libra.Pin pin: sym.pins){

	    this.write(sym,pin);
	}
    }
    public void write(Sym sym, libra.Pin pin) throws IOException {

	this.printf("%d\t%d\t%s\t%s\t%s\t%s\t%s\n", pin.number, pin.sequence, pin.getType(), pin.style, sym.getPosition(pin), pin.net, pin.getName());
    }

    public final static File Target(File src) {
	return Target(src.getPath());
    }
    public final static File Target(String src) {
	int fx = src.lastIndexOf('.');
	if (0 < fx){
	    return new File(src.substring(0,fx)+".sym");
	}
	else
	    throw new IllegalArgumentException(src);
    }
    public final static String Error(Process p) throws IOException {

	return (new BufferedReader(new InputStreamReader(p.getErrorStream()))).readLine();
    }
}
