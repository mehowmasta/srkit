package ir.data;

import java.io.File;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Set;
import ir.util.JDate;

/**
 * IReader wraps functionality around ResultSet, provides interaction with IRow
 * implementers.
 */
public interface IReader extends AutoCloseable
{
  /**
   * Positions to the row requested, fills rowToFill on success, returns
   * whether a row as retrieved.
   */
  // boolean absolute(int nRow,IRow rowToFill) throws Exception;
  /**
   * Closes the object and releases resources.
   */
  @Override
  void close();
  boolean getBoolean(int iCol) throws Exception;
  byte[] getBytes(int iCol) throws Exception;
  /**
   * Returns the number of rows available.
   */
  int getCount() throws Exception;
  double getDouble(int iCol) throws Exception;
  float getFloat(int iCol) throws Exception;
  int getInt(int iCol) throws Exception;
  JDate getJDate(int iCol) throws Exception;
  ir.util.JDateTime getJDateTime(int iCol) throws Exception;
  long getLong(int iCol) throws Exception;
  ResultSetMetaData getMetaData() throws Exception;
  Object getObject(int iCol) throws Exception;
  String getResolvedSQL() throws Exception;
  public int getRow() throws Exception;
  String getString(int iCol) throws Exception;
  /**
   * Advances to the next row, fills rowToFill on success, returns whether a
   * row as retrieved. Pass in null rowToFill to use getXXX methods.
   */
  boolean next(IRow rowToFill) throws Exception;
  /**
   * Provides method for record count to be done as separate command.
   */
  void setCount(int c);
  /**
   * Places values from reader onto row passed.
   */
  public void setValues(IRow rowToFill) throws Exception;
  /**
   * Fills a file with all rows in the result set like
   *
   * <pre>
   * "y",1,2
   * </pre>
   *
   * Note that this operation will close the reader.
   *
   * @param IRow
   *            r can be null
   * @param File
   *            f must be provided
   * @param Set
   *            <String> can be null, else contains columns to exclude
   */
  public void toCsv(IRow r, File f, Set<String> excludes) throws Exception;
  /**
   * Fills an excel file with all rows in the result set
   *
   *
   * Note that this operation will close the reader.
   *
   * @param IRow
   *            r can be null
   * @param File
   *            f must be provided
   * @param Set
   *            <String> can be null, else contains columns to exclude
   */
  public void toExcel(IRow r, File f, Set<String> excludes) throws Exception;
  /**
   * Returns a JSON string for all rows in the result set like
   *
   * <pre>
   * [{x:&quot;y&quot;;z:1},{x:&quot;e&quot;;z:4}]
   * </pre>
   *
   * Note that this operation will close the reader.
   */
  public String toJson(IRow row) throws Exception;
  public <A extends IRow> List<A> toList(Class<A> c) throws Exception;
  /**
   * Returns a List of Lists of objects, representing column values. Any
   * element could be null. Note that this operation will close the reader.
   */
  public List<List<Object>> toMatrix() throws Exception;
}
