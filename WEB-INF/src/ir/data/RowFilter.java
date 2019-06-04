package ir.data;

public interface RowFilter<R>
{
    boolean include(R r);
}
