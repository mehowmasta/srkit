package ir.data;

/**
 * data structure for field definitions
 */
import ir.util.Coerce;
import ir.util.StringKit;
import java.sql.ResultSetMetaData;
import java.sql.Types;
//
public class ColDef
{
  public final static String DoubleMinStep = ".0000001";
  protected boolean _autoincrement = false;
  protected String _className = "";
  protected int _len = 25;
  protected String _name = null;
  protected int _nullable = -1;
  protected int _precision = 0;
  protected boolean _readonly = false;
  protected int _scale = 0;
  protected String _schema = null;
  private final boolean _signed;
  protected String _table = null;
  protected int _type = Types.CHAR;
  protected String _typeName = "";
  //
  public static boolean isNumeric(int t)
  {
    return t == Types.BIGINT || t == Types.DECIMAL || t == Types.DOUBLE || t == Types.FLOAT || t == Types.INTEGER || t == Types.NUMERIC
        || t == Types.REAL || t == Types.SMALLINT || t == Types.TINYINT;
  }
  //
  public static void main(String[] args)
  {
    double v = Double.NaN;
    System.out.println("Double.NaN as primitive gives: " + v);
  }
  /**
   * create a field from metadata
   * 
   * @param ResultSetMetaData
   * @param int iCol
   */
  public ColDef(ResultSetMetaData m, int iCol) throws Exception
  {
    _name = m.getColumnName(iCol);
    _table = m.getTableName(iCol);
    _len = m.getColumnDisplaySize(iCol);
    _precision = m.getPrecision(iCol);
    _scale = m.getScale(iCol);
    _type = m.getColumnType(iCol);
    _readonly = m.isReadOnly(iCol);
    _nullable = m.isNullable(iCol);
    _autoincrement = m.isAutoIncrement(iCol);
    _className = m.getColumnClassName(iCol);
    _schema = m.getSchemaName(iCol);
    _signed = m.isSigned(iCol);
    _typeName = m.getColumnTypeName(iCol);
    if (isNumeric(_type) && _scale >= _precision)
    {// something sane to use if db col is double
      _scale = Math.max(_precision, _len) / 3;
    }
  }
  public String getInitialValue()
  {
    try
    {
      switch (_type)
      {
        case Types.BIT:
        case Types.BOOLEAN:
        case Types.TINYINT:
          return "false";
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
          return _len == 1 ? "' '" : "\"\"";
        case Types.DATE:
          return "JDate.zero()";
        case Types.BIGINT:
        case Types.DECIMAL:
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.NUMERIC:
        case Types.INTEGER:
        case Types.SMALLINT:
          return "0";
        case Types.TIME:
        case Types.TIMESTAMP:
          return "JDateTime.zero()";
        case Types.BINARY:
        case Types.VARBINARY:
        case Types.LONGVARBINARY:
          return "null";
      }
      return "new " + _className + "()";
    }
    catch (Exception e)
    {
      return "null";
    }
  }
  public String getJavaTypeName()
  {
    switch (_type)
    {
      case Types.BIGINT:
        return "long";
      case Types.BIT:
      case Types.BOOLEAN:
      case Types.TINYINT:
        return "boolean";
      case Types.CHAR:
      case Types.VARCHAR:
        return _len == 1 ? "char" : "String";
      case Types.DATE:
        return "JDate";
      case Types.DECIMAL:
      case Types.DOUBLE:
      case Types.FLOAT:
      case Types.NUMERIC:
        return "double";
      case Types.INTEGER:
      case Types.SMALLINT:
        return "int";
      case Types.LONGVARCHAR:
        return "String";
      case Types.TIME:
      case Types.TIMESTAMP:
        return "JDateTime";
    }
    return StringKit.getExtension(_className);
  }
  public double getMax()
  {
    if (!isNumeric())
    {
      return Double.NaN;
    }
    String v = StringKit.lpad("", _precision - _scale, '9') + (_scale <= 0 ? "" : "." + StringKit.lpad("", _scale, '9'));
    return Coerce.toDouble(v);
  }
  public double getMin()
  {
    if (!isNumeric())
    {
      return Double.NaN;
    }
    if (!_signed)
    {
      return 0;
    }
    String v = "-" + StringKit.lpad("", _precision - _scale, '9') + (_scale <= 0 ? "" : "." + StringKit.lpad("", _scale, '9'));
    return Coerce.toDouble(v);
  }
  /**
   * @return String column name
   */
  public String getName()
  {
    return _name;
  }
  /**
   * @return int column precision
   */
  public int getPrecision()
  {
    return _precision;
  }
  /**
   * @return int column scale
   */
  public int getScale()
  {
    return _scale;
  }
  /**
   * @return int column size
   */
  public int getSize()
  {
    return _len;
  }
  public String getStep()
  {
    if (!isNumeric())
    {
      return "";
    }
    if (_scale==0)
    {
      return "1";
    }
    if (_scale > 0)
    {
      return "." + StringKit.lpad("1", _scale, '0');
    }
    return DoubleMinStep;
  }
  /**
   * @return String table name
   */
  public String getTable()
  {
    return _table;
  }
  public int getType()
  {
    return _type;
  }
  public String getTypeName()
  {
    return _typeName;
  }
  /**
   * @return boolean whether field is auto-increment
   */
  public boolean isAutoIncrement()
  {
    return _autoincrement;
  }
  /**
   * @return boolean indicates whether field is a numeric type
   */
  public boolean isBoolean()
  {
    return (_type == Types.BIT || _type == Types.BOOLEAN || _type == Types.TINYINT);
  }
  /**
   * @return boolean indicates whether field is character type
   */
  public boolean isChar()
  {
    return (_type == Types.CHAR || _type == Types.CLOB || _type == Types.LONGNVARCHAR || _type == Types.LONGVARCHAR
        || _type == Types.NCHAR || _type == Types.NCLOB || _type == Types.NVARCHAR || _type == Types.VARCHAR);
  }
  /**
   * @return boolean indicates whether field is date type
   */
  public boolean isDate()
  {
    return (_type == Types.DATE || _type == Types.TIME || _type == Types.TIMESTAMP);
  }
  /**
   * @return boolean indicates whether field is float,double,real,decimal
   */
  public boolean isFloat()
  {
    return (_type == Types.DECIMAL || _type == Types.DOUBLE || _type == Types.FLOAT || _type == Types.REAL);
  }
  /**
   * @return boolean whether field is nullable
   */
  public boolean isNullable()
  {
    return (_nullable != ResultSetMetaData.columnNoNulls);
  }
  /**
   * @return boolean indicates whether field is a numeric type
   */
  public boolean isNumeric()
  {
    return isNumeric(_type);
  }
  /**
   * @return boolean whether field is read-only
   */
  public boolean isReadOnly()
  {
    return _readonly;
  }
  /**
   * @return boolean indicates whether field is a signed numeric type
   */
  public boolean isSigned()
  {
    return isNumeric(_type) && _signed;
  }
  public void setScale(int decs)
  {
    _scale = decs;
  }
  //
  @Override
  public String toString()
  {
    return new StringBuilder("").append("{table:'").append(_table).append("'").append(",column:'").append(_name).append("'")
        .append(",length:").append(_len).append(",scale:").append(_scale).append(",precision:").append(_precision)
        .append(",signed:").append(_signed).append(",nullable:").append(_nullable).append(",readonly:").append(_readonly)
        .append(",auto:").append(_autoincrement).append(",schema:'").append(_schema).append("'").append(",class:'")
        .append(_className).append("'").append(",type:'").append(_typeName).append("'").append(",sqltype:").append(_type)
        .append("}").toString();
  }
  /**
   * return a string wrapped as the database wants it in SQL statements
   * 
   * @param String
   *            value
   * @return String wrapped value
   */
  public String wrap(String value)
  {
    switch (_type)
    {
      case java.sql.Types.DATE:
      case java.sql.Types.TIME:
      case java.sql.Types.TIMESTAMP:
        return "#" + value + "#";
      case java.sql.Types.CHAR:
      case java.sql.Types.VARCHAR:
      case java.sql.Types.LONGVARCHAR:
        return "'" + value + "'";
      default:
        return value;
    }
  }
}
