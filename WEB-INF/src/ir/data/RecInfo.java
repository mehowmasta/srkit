package ir.data;

import ir.util.StringKit;

class RecInfo
{
  protected String _columns = null;
  protected String _delete = null;
  protected String _exists = null;
  protected String _insert = null;
  protected String _insertNext = null;
  protected String _insertNoGen = null;
  protected String _select = null;
  protected String _update = null;
  //
  RecInfo(Database db, IRecord rec) throws Exception
  {
    buildSQL(db, rec);
  }
  private void buildSQL(Database db, IRecord rec) throws Exception
  {
    String tbl = rec.getTable();
    String where = getWhere(db, rec);
    //
    _delete = StringKit.format("delete from {0} where {1}", tbl, where);
    _exists = StringKit.format("select 1 from {0} where {1}", tbl, where);
    //
    String[] cols = rec.getColumns();
    _columns = StringKit.join(",", cols);
    int autoColumnIndex = db.getAutoIndex(rec);
    int sequenceColumnIndex = -1;
    String sequenceColumnName=rec.getSequenceField(db);
    if (sequenceColumnName!=null && sequenceColumnName.length()>0)
    {
      sequenceColumnIndex = rec.getColumnIndex(sequenceColumnName);
    }
    StringBuilder sel = new StringBuilder("select ");
    StringBuilder insertNoGen = new StringBuilder("insert into ");
    StringBuilder insertNoGen2 = new StringBuilder(") values (");
    StringBuilder insertNext = new StringBuilder("insert into ");
    StringBuilder insertNext2 = new StringBuilder(") values (");
    StringBuilder ins = new StringBuilder("insert into ");
    StringBuilder ins2 = new StringBuilder(") values (");
    insertNoGen.append(tbl).append(" (");
    insertNext.append(tbl).append(" (");
    ins.append(tbl).append(" (");
    String comma = "", comma2 = "";
    for (int i = 0; i < cols.length; i++)
    {
      String colName = cols[i];
      sel.append(comma).append("`").append(colName).append("`");
      insertNoGen.append(comma).append("`").append(colName).append("`");
      insertNoGen2.append(comma).append('?');
      insertNext.append(comma).append("`").append(colName).append("`");
      comma = ",";
      if (i != autoColumnIndex)
      {
        ins.append(comma2).append("`").append(colName).append("`");
        ins2.append(comma2).append('?');
        if (i == sequenceColumnIndex)
        {
          insertNext2.append(comma2).append("nextVal('").append(rec.getTable()).append("')");
        }
        else
        {
          insertNext2.append(comma2).append('?');
        }
        comma2 = ",";
      }
    }
    _insertNoGen = insertNoGen.append(insertNoGen2).append(")").toString();
    _insertNext = insertNext.append(insertNext2).append(")").toString();
    _insert = ins.append(ins2).append(")").toString();
    _select = sel.append(" from ").append(tbl).append(" where ").append(where).toString();
    //
    comma = "";
    StringBuilder upd = new StringBuilder("update ");
    upd.append(tbl).append(" set ");
    for (int i = 0; i < cols.length; i++)
    {
      if (i != autoColumnIndex)
      {
        upd.append(comma).append("`").append(cols[i]).append("`").append("=?");
        comma = ",";
      }
    }
    _update = upd.append(" where ").append(where).toString();
  }
  public String getColumns()
  {
    return _columns;
  }
  public String getDelete()
  {
    return _delete;
  }
  public String getExists()
  {
    return _exists;
  }
  public String getInsert()
  {
    return _insert;
  }
  public String getInsertNext()
  {
    return _insertNext;
  }
  public String getInsertNoGen()
  {
    return _insertNoGen;
  }
  public String getSelect()
  {
    return _select;
  }
  public String getUpdate()
  {
    return _update;
  }
  private String getWhere(Database db, IRecord rec) throws Exception
  {
    String overrideWhereClause = rec.getUniqueWhere();
    if (overrideWhereClause != null)
    {
      return overrideWhereClause;
    }
    String[] primaryKeys = db.getKey(rec);
    if (primaryKeys.length < 1)
    {
      System.out.println("No primary key fields found for " + rec.getTable() + " at " + db.getConnection().getMetaData().getURL());
      return "0=1";
    }
    StringBuilder where = new StringBuilder("");
    String and = "";
    for (String k : primaryKeys)
    {
      where.append(and).append(k).append("=?");
      and = " and ";
    }
    return where.toString();
  }
}