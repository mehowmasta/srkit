package ir.data;

public class RowPage
{
    private int _count = 0;
    private IRow[] _rows = null;
    private int _start = 0;

    RowPage(IRow[] rows, int startRow, int rowCount)
    {
        _rows = rows;
        _start = startRow;
        _count = rowCount;
    }
    public int getRowCount()
    {
        return _count;
    }
    @SuppressWarnings({ "unchecked" })
    public <E extends IRow> E[] getRows(Class<E> c)
    {
        return (E[]) _rows;
    }
    public int getStartRow()
    {
        return _start;
    }
}
