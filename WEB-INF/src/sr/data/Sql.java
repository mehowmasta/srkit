package sr.data;

import ir.data.Database;
import ir.data.IReader;
import ir.data.IRecord;
import ir.util.StringKit;
import ir.util.XmlMapper;
import java.util.Map;
import java.util.TreeMap;

public abstract class Sql
{
    private static Map<String, String> _map = null;
    //
    public static String get(String... keys) throws Exception
    {
        if (_map == null || AppDb.isDev())
        {
            loadMap();
        }
        String space = "";
        StringBuilder b = new StringBuilder("");
        for (String key : keys)
        {
            b.append(space).append(get(key, true));
            space = " ";
        }
        return b.toString();
    }
    public static String get(String key, boolean excIfNotFound) throws Exception
    {
        if (_map == null || AppDb.isDev())
        {
            loadMap();
        }
        if (_map.containsKey(key))
        {
            return _map.get(key);
        }
        if (excIfNotFound)
        {
            throw new Exception("Sql for " + key + " not found.");
        }
        return null;
    }
    public static synchronized Map<String, String> getMap() throws Exception
    {
        if (_map == null)
        {
            loadMap();
        }
        return _map;
    }

    private static synchronized void loadMap() throws Exception
    {
        _map = XmlMapper.getMap(Sql.class.getResourceAsStream("sql.xml"));
    }
    public static void runAll(Database db) throws Exception
    {
        String sql = "";
        String key = "";
        Map<String, String> m = getMap();
        TreeMap<String, String> tm = new TreeMap<String, String>();
        tm.putAll(m);
        IRecord anyRec = new UserRec();
        int ec = 0;
        db.beginTransaction();
        for (String k : tm.keySet())
        {
            key = k;
            try
            {
                sql = tm.get(k).trim().toLowerCase().replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
                int pc = StringKit.count(sql, "?");
                Object[] p = new Object[pc];
                if (sql.startsWith("select"))
                {
                    IReader rdr = db.selectReader(sql, p);
                    rdr.next(anyRec);
                    rdr.close();
                    if (-1 == sql.indexOf(" where "))
                    {
                        System.out.println(k + " - no where clause.");
                    }
                }
                else if (sql.startsWith("insert") || sql.startsWith("update") || sql.startsWith("delete"))
                {
                    db.execute(sql, p);
                    if (-1 == sql.indexOf(" where "))
                    {
                        System.out.println(k + " - no where clause.");
                    }
                }
            }
            catch (Exception e)
            {
                ec++;
                System.out.println(key + " (" + StringKit.left(sql, 30) + "...):" + e.getMessage());
            }
        }
        db.rollback();
        System.out.println("Sql runAll " + m.size() + " statements, " + ec + " errors.");
    }
}
