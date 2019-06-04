package ir.data;

public interface DatabaseUpdateListener
{
    public void afterDelete(Database db, IRecord rec) throws Exception;
    public void afterInsert(Database db, IRecord rec) throws Exception;
    public void afterUpdate(Database db, IRecord rec, IRecord rOldValue) throws Exception;
}
