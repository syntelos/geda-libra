package libra.sch;

import libra.Attribute;
import libra.Layout;
import libra.Lib;
import libra.Pin;
import libra.Symbol;

import java.io.IOException;

/**
 * Schematic component
 */
public class Component
    extends Attribute
{

    public Layout.Cursor.Relation layout;


    /**
     * Schematic component
     */
    public Component(String[] line)
	throws IOException
    {
	this(line[0]);

	this.selectable = 1;

	this.copy(this.componentSymbol.getBounds());

	this.add(line);
    }
    public Component(String name){
	super(Attribute.Type.C);
	this.name = Lib.Basename(name);
	this.componentName = Lib.SymFileName(this.name);
	this.componentSymbol = Lib.For(this.name);
    }
    public Component(){
	super(Attribute.Type.C);
    }


    public boolean add(String[] line)
	throws IOException
    {
	if (this.name.equals(line[0])){
	    this.add(new Net(this.componentSymbol,line));
	    return true;
	}
	else
	    return false;
    }
    public void layout1(Component prev, Layout.Cursor cursor){

	this.layout = cursor.layout1(prev,this);
    }
    public void layout3(Component prev, Layout.Cursor cursor){

	cursor.layout3(prev,this);
    }
    @Override
    public boolean markup(Attribute parent){
	boolean re = true;
	for (Attribute net: this){

	    if (!net.markup(this))
		re = false;
	}
	return re;
    }
    public boolean isLayoutHorizontal(){
	if (null != this.layout)
	    return this.layout.isHorizontal();
	else
	    return false;
    }
    public boolean isLayoutVertical(){
	if (null != this.layout)
	    return this.layout.isVertical();
	else
	    return false;
    }


    public final static Component[] Add(Component[] list, Component item){
	if (null == item)
	    return list;
	else if (null == list)
	    return new Component[]{item};
	else {
	    int len = list.length;
	    Component[] copier = new Component[len+1];
	    System.arraycopy(list,0,copier,0,len);
	    copier[len] = item;
	    return copier;
	}
    }
    /**
     * 
     */
    public static class Iterator
	extends Object
	implements java.util.Iterator<Component>
    {

	private final Component[] list;
	private final int length;
	private int index;


	public Iterator(Component[] list){
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
	public Component next(){
	    return this.list[this.index++];
	}
	public void remove(){
	    throw new UnsupportedOperationException();
	}
    }
    /**
     * 
     */
    public static class Iterable
	extends Object
	implements java.lang.Iterable<Component>
    {
	private final Component[] list;


	public Iterable(Component[] list){
	    super();
	    this.list = list;
	}

	public java.util.Iterator<Component> iterator(){
	    return new Component.Iterator(this.list);
	}
    }
}
