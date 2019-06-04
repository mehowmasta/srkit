package ir.data;

import java.util.List;

/**
 * IRecCommand describes the behavior of an object used to execute batch database
 * updates to IRecords
 * 
 * @see Database#prepareDelete(),Database#prepareInsert()...
 */
public interface IRecCommand
{
  /**
   * Adds values from the passed record appropriate for the command.
   */
  void addBatch(IRecord rec) throws Exception;
  /**
   * Executes the update requests.If no batches
   * has been added, returns an empty array.
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
}
