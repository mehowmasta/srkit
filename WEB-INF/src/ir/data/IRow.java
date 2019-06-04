package ir.data;

import org.json.JSONObject;

/**
 * IRow defines the required behaviour of an Object to extract data from
 * IReader.
 */
public interface IRow extends Cloneable
{
  /**
   * Plug point to add processing after row data is read from result set.
   */
  void afterRead(Database db) throws Exception;
  /**
   * Returns a new instance of the IRow implementer
   */
  <E extends IRow> E copy() throws Exception;
  /**
   * Gets a column's index by name, or -1 if not found
   */
  int getColumnIndex(String colName);
  /**
   * Returns array of column names or aliases to be mapped to the fields
   * returned by getFields, and therefore must be parallel to getFields. This
   * method is provided in cases where you want to use different field names
   * from column names, and/or when a column name would be an illegal field
   * name.
   */
  String[] getColumns();
  /**
   * Gets a field's index by name, or -1 if not found
   */
  int getFieldIndex(String fldName);
  /**
   * Returns a array of fields to be mapped to columns returned by GetColumns,
   * and therefore must be parallel to GetColumns.
   */
  String[] getFields();
  /** returns the value stored on the object by setTemp, or null if not found */
  public <T> T getTemp(String k, Class<T> cReturnType) throws Exception;
  /** returns the value stored on the object by setTemp, or defaultValue if not found */
  public <T> T getTemp(String k, Class<T> cReturnType,T defaultValue) throws Exception;
  /**
   * Gets a the value from a field by its ordinal position within the list
   * returned by GetColumns
   */
  Object getValue(int Ordinal) throws Exception;
  /** places a value on the object in an internal hashmap */
  void setTemp(String k, Object o);
  /**
   * Assigns a value to a field by its ordinal position within the list
   * returned by GetColumns
   */
  void setValue(int Ordinal, Object oVal) throws Exception;
  /**
   * Assigns values to fields by matching on attributes on JSONObject.
   * JSONObject attributes not matching fields get loaded as temporary
   * values on the row.
   */
  public void setValuesFrom(JSONObject jso) throws Exception;
  /**
   * Sets values on passed JSONObject from this row's fields
   * and temporary values.
   */
  public void setValuesOn(JSONObject jso) throws Exception;
}
