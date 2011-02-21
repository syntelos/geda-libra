package libra.sch;

import libra.Attribute;
import libra.Pin;
import libra.Symbol;


public class Component
    extends Attribute
{

    public final String name;

    private Symbol symbol;

    private Net[] nets;


    public Component(String[] line){
	super(Attribute.Type.P);
	if (2 < line.length){
	    this.name = line[0];
	    this.add(line);
	}
	else
	    throw new IllegalArgumentException();
    }


    public boolean add(String[] line){
	if (this.name.equals(line[0])){
	    this.add(new Net(line));
	    return true;
	}
	else
	    return false;
    }
    public void add(Net n){

	this.nets = Net.Add(this.nets,n);
    }
    public java.lang.Iterable<Net> nets(){
	return new Net.Iterable(this.nets);
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
