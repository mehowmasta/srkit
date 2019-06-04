package ir.data;

import java.sql.Connection;

public interface TableDater
{
    public abstract long getLastUpdate(Connection c, java.util.Collection<String> schemaDotTableNames) throws Exception;
}