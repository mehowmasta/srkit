package ir.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
/**
 * TableDef is a table definition object
 */
public class TableDef
{
    protected String _auto = null;
    protected String _catalog = "";
    protected Map<String, ColDef> _columns = new HashMap<String, ColDef>(16);
    protected String[] _key = null;
    protected String _name = null;
    protected String _schema = "";
    /**
     * initializes a table object
     * 
     * @param Connection
     *            to use
     * @param String
     *            table name
     * @throws Exception
     *             if parameters are goofy
     */
    public TableDef(Connection conn, String table) throws Exception
    {
        if (table.indexOf('.') > 0)
        {
            String[] st = table.split("\\.");
            _catalog = st[0];
            _name = st[1];
        }
        else
        {
            _name = table;
        }
        ResultSet rs = conn.createStatement().executeQuery("select * from " + table + " where 0=1");
        ResultSetMetaData md = rs.getMetaData();
        for (int i = 1; i <= md.getColumnCount(); i++)
        {
            ColDef f = new ColDef(md, i);
            _columns.put(f.getName().toLowerCase(), f);
            if (f.isAutoIncrement())
                _auto = f.getName();
        }
        rs.close();
        //
        if (_auto != null)
            _key = new String[] { _auto };
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet rsK = null;
        try
        { // get primary keys
            Map<Integer, String> km = new TreeMap<Integer, String>();
            rsK = dmd.getPrimaryKeys(_catalog, _schema, _name);
            while (rsK.next())
            {
                km.put(rsK.getInt("KEY_SEQ"), rsK.getString("COLUMN_NAME"));
            }
            rsK.close();
            if (km.size() > 0)
            {
                _key = km.values().toArray(new String[0]);
            }
        }
        catch (Exception e)
        {// success is optional
            if (_key == null)
            {
                System.out.println("failed to get pk for " + table + ": " + e.getMessage());
            }
        }
        try
        {
            if (rsK != null)
                rsK.close();
        }
        catch (Exception ignore)
        {
        }
    }
    /**
     * Counts records on the passed table meeting the passed where clause
     * 
     * @param Connection
     * @param String
     *            table name
     * @param String
     *            where clause
     * @throws Exception
     *             with explanation in message
     */
    protected long count(Connection c, String t, String w) throws Exception
    {
        String sql = "select count(*) from " + t + w;
        ResultSet rs = c.createStatement().executeQuery(sql);
        rs.next();
        long ret = rs.getLong(1);
        rs.close();
        return ret;
    }
    /**
     * Retrieves a table's auto increment column, if any
     * 
     * @return String column name, or null
     */
    public String getAutoColumn()
    {
        return _auto;
    }
    /**
     * Retrieves a column info object for the named field
     * 
     * @return String[] or null
     */
    public ColDef getColumn(String name)
    {
        return _columns.get(name.toLowerCase());
    }
    /**
     * Gets columns on this table
     */
    public Collection<ColDef> getColumns() throws Exception
    {
        return _columns.values();
    }
    /**
     * Returns the table's name
     * 
     * @return String
     */
    public String getName()
    {
        return _name;
    }
    /**
     * Retrieves an array of columnn names that make up the primary key
     * 
     * @return String[] or null
     */
    public String[] getPrimaryKey()
    {
        return _key;
    }
    /**
     * Gets a List of columns that can be updated on this table
     * 
     * @return List of Column objects
     */
    public List<ColDef> getUpdateColumns() throws Exception
    {
        Collection<ColDef> it = _columns.values();
        List<ColDef> lst = new ArrayList<ColDef>(_columns.size());
        for (ColDef col : it)
        {
            if (!col.isReadOnly() && !col.isAutoIncrement())
                lst.add(col);
        }
        return lst;
    }
}
