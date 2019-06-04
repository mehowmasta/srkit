package ir.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ir.util.StringKit;

public class DataPage<T extends IRow>
{
  private static int cachePages=1;
  private int displayOffset;
  private final int selectOffset;
  private final int selectCount;
  public final List<T> rows = new ArrayList<T>();
  private int recordCount = -1;
  private String resolvedSql="[not set yet]";
  //
  public DataPage(int displayOffset,int pageSize,int preCount)
  {
    this.displayOffset = displayOffset;
    if (cachePages > 1)
    {
      if (displayOffset < cachePages * pageSize * 2)
      {//hack for top of the list
        this.selectOffset = 0;
        this.selectCount = displayOffset + cachePages * pageSize;
      }
      else
      {
        this.selectOffset = Math.max(0,displayOffset - (pageSize * cachePages/2));
        this.selectCount = cachePages * pageSize;
      }
    }
    else
    {
      this.selectOffset = displayOffset;
      this.selectCount = pageSize;
    }
    this.recordCount = preCount;
  }
  public boolean contains(int newDisplayOffset,int newPageSize)
  {
    if (newDisplayOffset >= selectOffset && newDisplayOffset + newPageSize <= selectOffset + rows.size())
    {//silly structure for breakpoint planting.
      return true;
    }
    return false;
  }
  public T get(int displayIndex)
  {
    int rowIndex = displayOffset - selectOffset + displayIndex;
    if (rowIndex > rows.size())
    {
      throw new RuntimeException("Cannot get row " + displayIndex + " from " + this);
    }
    return rows.get(rowIndex);
  }
  public int getDisplayOffset()
  {
    return displayOffset;
  }
  public int getRecordCount()
  {
    return recordCount;
  }
  public void select(Database db,String sql, Class<T> rowClass, Object... parameters) throws Exception
  {
    String sup = sql.toUpperCase();
    List<Object> parms = new ArrayList<Object>(Arrays.asList(parameters));
    if (db.isMySQL())
    {
      if (!sup.contains(" LIMIT "))
      {
        sql += " limit ?,?";
        parms.add(selectOffset);
        parms.add(selectCount);
      }
    }
    rows.addAll(db.selectList(sql, rowClass, parms.toArray()));
    this.resolvedSql = Database.resolveSql(sql, parameters);
    if (recordCount < 0)
    {
      recordCount = db.selectScalar(Database.getCountSQL(resolvedSql), 0);
    }
  }
  public void setDisplayOffset(int offset)
  {
    displayOffset=offset;
  }
  @Override
  public String toString()
  {
    return "{selectOffset:" + selectOffset
        + ",selectCount:" + selectCount
        + ",recordCount:" + recordCount
        + ",displayOffset:" + displayOffset
        + ",rowCount:" + rows.size()
        + ",sql:\"" + resolvedSql + "\""
        + "}";
  }
}
