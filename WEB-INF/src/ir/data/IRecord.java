package ir.data;

/**
 * IRecord defines the required behaviour of an Object to be automatically
 * persisted by Database. The main design goal is to keep implementers as small
 * and simple as possible, so that they can be used as Data Value Objects passed
 * between layers, while still providing the necessary functionality to automate
 * persistence.
 */
public interface IRecord extends IRow
{
  /**
   * afterDelete can be used to trigger processing after the IRecord is
   * deleted
   */
  void afterDelete(Database db) throws Exception;
  /**
   * afterInsert can be used to trigger processing after the IRecord is
   * inserted
   */
  void afterInsert(Database db) throws Exception;
  /**
   * afterUpdate can be used to trigger processing after the IRecord is
   * updated
   */
  void afterUpdate(Database db, IRecord oldValue) throws Exception;
  /**
   * implement beforeDelete to perform processing before the IRecord is
   * deleted
   */
  void beforeDelete(Database db) throws Exception;
  /**
   * implement beforeInsert to perform processing before the IRecord is
   * inserted
   */
  void beforeInsert(Database db) throws Exception;
  /**
   * implement beforeUpdate to perform processing before the IRecord is
   * updated
   */
  void beforeUpdate(Database db) throws Exception;
  /**
   * Indicates whether record can be deleted. Must report reason(s) record
   * cannot be deleted by using IValidator.addError or addErrorX
   * 
   * @param IValidator
   *            to use in looking up rules or relations, and to report reasons
   * @return whether record can be deleted
   */
  boolean canDelete(IValidator iv) throws Exception;
  /**
   * Should return a key that can be assumed unique for caching purposes. May
   * return null if record is not cacheable, ie. has no primary key.
   */
  String getCacheKey(Database db) throws Exception;
  /**
   * getKey should return:
   * <ul>
   * <li>null if the database metadata should be used to discover the keys
   * <li>an array of columns to use as a key
   * </ul>
   * Because most jdbc drivers provide key metadata, getKey is seldom
   * necessary.
   * 
   * Reasons to use getKey:
   * <ul>
   * <li>the implementer may wish to force the use of certain columns as a key
   * <li>the table has no primary key defined
   * </ul>
   */
  String[] getKey();
  /**
   * Should yield a string appropriate to identify the record within it's type
   * context, ie. a name or description.
   */
  String getNameString(Database db) throws Exception;
  /**
   * getOldValue must return the object as it was the last time makeClean was
   * called.
   */
  <E extends IRecord> E getOldValue();
  /**
   * getOptionalReferences should return an array of column names that serve
   * as optional foreign keys. ie. if the column value is a blank string or a
   * numeric zero, it should instead be set to null, so that the database's
   * referential integrity rules will accept the record.
   */
  String[] getOptionalReferences(Database db) throws Exception;
  /**
   * Should return the name of the field to receive an Oracle-style sequence
   * number referred to by using getSequenceName, or null if not applicable.
   */
  String getSequenceField(Database db) throws Exception;
  /**
   * Should return the name of the Oracle-style sequence number related to the
   * table, or null if not applicable.
   */
  String getSequenceName(Database db) throws Exception;
  /**
   * Returns the name of the table from which the record comes.
   */
  String getTable();
  /**
   * getUniqueWhere should return a parameterized where clause that represents
   * a unique record identifier, NOT including the word where, or null to have
   * the framework use the typical key1=? and key2=? clause.
   * 
   * This method is provided for cases where key columns are padded or
   * otherwise specially formatted. eg. a table with a padded key:
   * t$item=lpad(?,16) and t$rqid=?
   * 
   * The number and sequence of parameters in the clause must correspond to
   * the column names returned by getKey
   */
  String getUniqueWhere();
  /**
   * Indicates whether the Object has been changed since it was last marked
   * clean.
   */
  boolean isDirty() throws Exception;
  /**
   * Indicates whether the Record exists in the database.
   */
  boolean isNew(Database db) throws Exception;
  /**
   * isOptionalForeignKey indicates whether passed columnName is an
   * optional foreign key.
   * ie.  if the column value is a blank string or a numeric zero,
   * it should instead be set to null, so that the database's referential
   * integrity rules will accept the record.
   */
  boolean isOptionalForeignKey(Database db,String columnName) throws Exception;
  /**
   * Resets the Object to an unchanged state.
   */
  void makeClean();
  /**
   * Validates record values for insert or update operation.
   */
  boolean validate(IValidator iv) throws Exception;
}
