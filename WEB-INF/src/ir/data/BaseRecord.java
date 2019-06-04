package ir.data;

import ir.util.Coerce;
import ir.util.Reflector;
import ir.util.StringKit;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

/**
 * BaseRecord acts as an IRecord adapter, primarily to implement isDirty and
 * related methods from IRecord.
 */
public abstract class BaseRecord extends BaseRow implements IRecord
{
  public static void save(Database db, List<? extends IRecord> list) throws Exception
  {
    for (IRecord rec : list)
    {
      if (rec.isNew(db))
      {
        db.insert(rec);
      }
      else
      {
        db.update(rec);
      }
    }
  }

  /**
   * @see ir.data.IRecord#afterDelete(Database)
   */
  @Override
  public void afterDelete(Database db) throws Exception
  {
  }
  /**
   * @see ir.data.IRecord#afterInsert(Database)
   */
  @Override
  public void afterInsert(Database db) throws Exception
  {
  }
  /**
   * @see ir.data.IRecord#afterUpdate(Database,List)
   */
  @Override
  public void afterUpdate(Database db, IRecord oldValue) throws Exception
  {
  }
  @Override
  public void beforeDelete(Database db) throws Exception
  {
  }
  @Override
  public void beforeInsert(Database db) throws Exception
  {
  }
  @Override
  public void beforeUpdate(Database db) throws Exception
  {
  }
  /**
   * @see ir.data.IRecord#canDelete(Database,List)
   */
  @Override
  public boolean canDelete(IValidator iv) throws Exception
  {
    return iv.isOK();
  }
  protected String format(double d, int decs)
  {
    return StringKit.format(d, decs);
  }
  /**
   * Should return a key that can be assumed unique for caching purposes.
   */
  @Override
  public String getCacheKey(Database db) throws Exception
  {
    StringBuilder sb = new StringBuilder(getClass().getName());
    String[] ka = db.getKey(this);
    if (ka.length == 0)
    {
      return null;// not cacheable
    }
    for (String kf : ka)
    {
      sb.append('.').append(Coerce.toString(Reflector.getUpdatablePublicInstanceFieldString(this, kf)));
    }
    return sb.toString();
  }
  /**
   * @see ir.data.IRecord#getKey()
   */
  @Override
  public String[] getKey()
  {
    return null;
  }
  /**
   * @see ir.data.IRecord#getNameString(Database)
   */
  @Override
  public String getNameString(Database db) throws Exception
  {
    if (this instanceof NamedRow)
    {
      return db.xlate(getClass().getSimpleName().replace("Rec","")) +" " + ((NamedRow)this).getNameEn();
    }
    return  db.xlate(getClass().getSimpleName().replace("Rec"," record"));
  }
  /**
   * @see ir.data.IRecord#getOldValue()
   */
  @SuppressWarnings("unchecked")
  @Override
  public <E extends IRecord> E getOldValue()
  {
    try
    {
      if (null == previousValue)
      {
        previousValue = getClass().newInstance();
      }
    }
    catch (Throwable e)
    {// should never fail....??
    }
    return (E) previousValue;
  }
  /**
   * @see ir.data.IRecord#getOptionalReferences(Database)
   */
  @Override
  public String[] getOptionalReferences(Database db) throws Exception
  {
    return null;
  }
  /**
   * @see ir.data.IRecord#getSequenceField()
   */
  @Override
  public String getSequenceField(Database db) throws Exception
  {
    return null;
  }
  /**
   * @see ir.data.IRecord#getSequenceName()
   */
  @Override
  public String getSequenceName(Database db) throws Exception
  {
    return null;
  }
  public String getStatusIdentifier(Database db) throws Exception
  {
    if (this instanceof NamedRow)
    {
      return db.xlate(getClass().getSimpleName().replace("Rec","")) +" " + ((NamedRow)this).getNameEn();
    }
    return  db.xlate(getClass().getSimpleName().replace("Rec"," record"));
  }
  /**
   * @see ir.data.IRow#getTables()
   */
  public String getTables()
  {
    return getTable();
  }
  /**
   * @see ir.data.IRecord#getUniqueWhere()
   */
  @Override
  public String getUniqueWhere()
  {
    return null;
  }
  /**
   * @see ir.data.IRecord#isDirty()
   */
  @Override
  public boolean isDirty() throws Exception
  {
    try
    {
      if (previousValue == null)
      {
        return true;// we must assume so
      }
      List<Field> fa = Reflector.getUpdatablePublicInstanceFields(this);
      for (Field f : fa)
      {
        Object v1 = f.get(this);
        Object v2 = f.get(previousValue);
        if (v1 == v2)
        {
          continue;
        }
        if (v1 == null || v2 == null)
        {
          return true;
        }
        if (v1 instanceof BigDecimal)
        {
          if (0 != ((BigDecimal) v1).compareTo((BigDecimal) v2))
          {
            return true;
          }
        }
        else if (!v1.equals(v2))
        {
          return true;
        }
      }
      return false;
    }
    catch (Exception e)
    {
    }
    return true;// last resort -> force update
  }
  /**
   * Indicates whether the Record exists in the database.
   */
  @Override
  public boolean isNew(Database db) throws Exception
  {
    int auto = db.getAutoIndex(this);
    if (auto > -1)
    {
      return 0 == Coerce.toInt(getValue(auto));
    }
    return !db.exists(this);
  }
  @Override
  public boolean isOptionalForeignKey(Database db,String columnName) throws Exception
  {
    String[] ofka = getOptionalReferences(db);
    if (ofka==null)
    {
      return false;
    }
    for (String ofk : ofka)
    {
      if (ofk.equalsIgnoreCase(columnName))
      {
        return true;
      }
    }
    return false;
  }
  /**
   * @see ir.data.IRecord#makeClean()
   */
  @Override
  public void makeClean()
  {
    try
    {
      previousValue = getClass().newInstance();
      Reflector.setPublicInstanceValues(this, previousValue);
    }
    catch (Exception e)
    {
      previousValue = null;// force update
    }

  }
  /**
   * @see ir.data.IRecord#makeDirty()
   */
  public void makeDirty()
  {
    previousValue = null;
  }
  @Override
  public boolean validate(IValidator iv) throws Exception
  {
    return iv.isOK();
  }
}
