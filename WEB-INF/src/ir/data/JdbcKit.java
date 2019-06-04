package ir.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * JdbcKit
 */
class JdbcKit
{
    public static void close(Connection c)
    {
        try
        {
            c.close();
        }
        catch (Exception e)
        {
        }
    }
    public static void close(PooledConnection c)
    {
        try
        {
            c.fullClose();
        }
        catch (Exception e)
        {
        }
    }
    public static void close(ResultSet c)
    {
        try
        {
            c.close();
        }
        catch (Exception e)
        {
        }
    }
    public static void close(Statement c)
    {
        try
        {
            c.close();
        }
        catch (Exception e)
        {
        }
    }
}
