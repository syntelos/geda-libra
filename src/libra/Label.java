package libra;

/** 
 * Pin label layout tools
 * 
 * @author John Pritchard <jdp@ulsf.net>
 */
public abstract class Label
{

    public static class Iterable
	extends Object
	implements java.lang.Iterable<Attribute>
    {
	private final Attribute[] list;
	public final Layout.Position pos;

	public Iterable(Pin[] plist, Layout.Position pos){
	    super();
	    if (null != pos){
		this.pos = pos;
		if (null != plist){
		    Attribute[] llist = null;
		    for (Pin p: plist){
			if (pos == p.getPosition()){
			    Attribute label = p.getLabel();
			    if (null != label)
				llist = Attribute.Add(llist,label);
			    else
				throw new IllegalStateException();
			}
		    }
		    this.list = llist;
		}
		else
		    this.list = null;
	    }
	    else
		throw new IllegalArgumentException();
	}
	public Iterable(Attribute[] list){
	    super();
	    this.pos = null;
	    this.list = list;
	}
	public Iterable(Attribute[] list, java.lang.Iterable<Attribute> ilist){
	    super();
	    this.pos = null;

	    for (Attribute at: ilist){
		if (null == list)
		    list = new Attribute[]{at};
		else {
		    final int len = list.length;
		    Attribute[] copier = new Attribute[len+1];
		    System.arraycopy(list,0,copier,0,len);
		    copier[len] = at;
		    list = copier;
		}
	    }
	    this.list = list;
	}
	public Iterable(){
	    super();
	    this.pos = null;
	    this.list = null;
	}

	public java.util.Iterator<Attribute> iterator(){
	    return new Attribute.Iterator(this.list);
	}
    }
}
