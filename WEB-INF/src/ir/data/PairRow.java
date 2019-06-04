package ir.data;

/**
 * A data structure used to fill select boxes. o1 will be used as option value,
 * o2 as option text
 * 
 * @see Database#selectPairRows
 */
public class PairRow extends BaseRow
{
    public Object o1;
    public Object o2;
    public PairRow()
    {
    }
    public PairRow(Object o, Object p)
    {
        o1 = o;
        o2 = p;
    }
}
