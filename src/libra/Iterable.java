package libra;

public interface Iterable<T>
    extends java.lang.Iterable<T>
{

    public T get(int idx);

    public int length();
    /**
     * Component type must be comparable
     */
    public Iterable<T> sort();
}
