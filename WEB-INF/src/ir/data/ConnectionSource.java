package ir.data;

public interface ConnectionSource
{
    public java.sql.Connection getConnection() throws Exception;
}
