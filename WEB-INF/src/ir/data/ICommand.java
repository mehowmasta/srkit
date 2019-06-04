package ir.data;

import java.util.List;

/**
 * ICommand describes the behavior of an object used to execute batch database
 * updates. ICommand instances can be acquired from Database.prepare.
 * 
 * @see Database#prepare()
 */
public interface ICommand
{
  /**
   * Adds a batch of parameters for the ICommand's underlying Statement.
   */
  void addBatch(Object... parameters) throws Exception;
  /**
   * Executes the update requests.  If no batches have been added, returns an empty array.
   * 
   * @see java.sql.Statement#executeBatch()
   */
  int[] executeBatch() throws Exception;
  /**
   * Same as Statement, except the return type is List<Integer>.
   * 
   * @see java.sql.Statement#getGeneratedKeys()
   */
  List<Integer> getGeneratedKeys() throws Exception;
  /**
   * @return SQL source
   */
  String getSource();
}
