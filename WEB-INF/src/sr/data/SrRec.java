package sr.data;

import java.util.List;

import ir.data.Database;

public abstract class SrRec extends AppRec{

	public static final String DESCRIPTION = "Description";
	public static final String NAME = "Name";
	public static final String ROW = "Row";
	public static final String SOURCE = "Source";
	public String Description = "";
	public String Name = "";
	public int Row = 0;
	public String Source = "";


	public static <T extends SrRec> List<T> selectAll(Database db,Class<T> cls) throws Exception
	{
		return db.selectList(cls.getSimpleName()+ ".selectAll", cls);
	}
	public static <T extends SrRec> T selectByName(Database db, Class<T> cls, String name) throws Exception
	{
		T rec = cls.newInstance();
		if(db.selectFirst(cls.getSimpleName()+ ".selectByName",rec,makeContains(name)))
		{
			return rec;
		}
		return null;
	}
}