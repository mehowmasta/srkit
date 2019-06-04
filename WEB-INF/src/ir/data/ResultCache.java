package ir.data;

import ir.util.StringKit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.sql.rowset.CachedRowSet;
import com.sun.rowset.CachedRowSetImpl;
/**
 * MyIsamCache return resultsets for MyISAM back ends
 */
public class ResultCache
{
    private final boolean _debug = true;
    private String _defaultSchema = "";
    private final Map<String, Entry> _rs = new HashMap<String, Entry>();
    private final TableDater _td;
    //
    public ResultCache(String defaultSchema, TableDater td)
    {
        _td = td;
        _defaultSchema = defaultSchema;
    }
    private void debug(String fmt, Object... parms) throws Exception
    {
        if (_debug)
            System.out.println(getClass().getName() + " " + StringKit.format(fmt, parms));
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.ir.data.ResultCache#select(java.sql.Connection,
     * java.lang.String)
     */
    public ResultSet select(Connection c, String sql) throws Exception
    {
        String sk = sql.trim().toLowerCase();
        Entry en = _rs.get(sk);
        if (en != null && en.CacheTime >= _td.getLastUpdate(c, en.Tables))
        {// cache hit
            debug("hit - {0}", sql);
            return en.ResultData;
        }
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(sql);
        CachedRowSet crs = new CachedRowSetImpl();
        crs.populate(rs);
        rs.close();
        st.close();
        crs.setReadOnly(true);
        en = new Entry(sql, crs, _defaultSchema);
        _rs.put(sk, en);
        return crs;
    }
    class Entry
    {
        public long CacheTime = new java.util.Date().getTime();
        public ResultSet ResultData;
        public String Sql;
        public Collection<String> Tables = new HashSet<String>();
        //
        public Entry(String sql, ResultSet rs, String defaultSchema) throws Exception
        {
            Sql = sql;
            ResultData = rs;
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++)
            {
                String scn = md.getSchemaName(i);
                String tbl = md.getTableName(i);
                if (tbl != null && tbl.trim().length() > 0)
                {
                    if (scn == null || scn.trim().equals(""))
                    {
                        scn = defaultSchema;
                    }
                    Tables.add(scn + "." + md.getTableName(i));
                }
            }
        }
    }
}