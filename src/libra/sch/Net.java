package libra.sch;

public class Net {

    public final int pin;

    public final String signal;

    public Net(String[] line){
	super();

	this.pin = Integer.parseInt(line[1]);
	this.signal = line[2];
    }


    public final static Net[] Add(Net[] list, Net item){
	if (null == item)
	    return list;
	else if (null == list)
	    return new Net[]{item};
	else {
	    int len = list.length;
	    Net[] copier = new Net[len+1];
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
	implements java.util.Iterator<Net>
    {

	private final Net[] list;
	private final int length;
	private int index;


	public Iterator(Net[] list){
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
	public Net next(){
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
	implements java.lang.Iterable<Net>
    {
	private final Net[] list;


	public Iterable(Net[] list){
	    super();
	    this.list = list;
	}

	public java.util.Iterator<Net> iterator(){
	    return new Net.Iterator(this.list);
	}
    }
}
