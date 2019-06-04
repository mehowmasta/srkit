package ir.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Collection;
/**
 * MyIsamTableDater provides the latest update time of a list of my isam tables
 */
public class MyIsamTableDater implements TableDater
{
    private final SimpleDateFormat _df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final String sql = "select date_format(max(update_time),'%Y-%m-%d %T') from information_schema.tables"
            + " where instr(?,concat(table_schema,'.',table_name,'|'))>0";
    //
    public long getLastUpdate(Connection c, Collection<String> schemaDotTable) throws Exception
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        java.util.Date ret = new java.util.Date(0);
        try
        {
            ps = c.prepareStatement(sql);
            StringBuilder b = new StringBuilder();
            for (String sdt : schemaDotTable)
                b.append(sdt).append("|");
            ps.setString(1, b.toString());
            rs = ps.executeQuery();
            if (rs.next())
            {
                try
                {
                    ret = _df.parse(rs.getString(1));
                }
                catch (Exception eBadDate)
                {// use zero date
                    System.out.println(getClass().getName() + " " + eBadDate.toString() + " " + b);
                }
            }
            return ret.getTime();
        }
        finally
        {
            JdbcKit.close(rs);
        }
    }
}