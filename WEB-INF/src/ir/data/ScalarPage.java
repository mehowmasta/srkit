package ir.data;

public class ScalarPage
{
    private int _count = 0;
    private int _start = 0;
    private Object[] _values = null;

    ScalarPage(Object[] values, int startValue, int valueCount)
    {
        _values = values;
        _start = startValue;
        _count = valueCount;
    }
    public int getStartValue()
    {
        return _start;
    }
    public int getValueCount()
    {
        return _count;
    }
    @SuppressWarnings({ "unchecked" })
    public <E extends Object> E[] getValues(Class<E> c)
    {
        return (E[]) _values;
    }
}
