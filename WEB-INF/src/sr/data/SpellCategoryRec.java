package sr.data;

import java.util.List;

import ir.data.Database;

/**
 * UserRec is a data structure of user information
 */
public class SpellCategoryRec extends AppRec {
	public static final String DESCRIPTION = "Description";
	public static final String NAME = "Name";
	public final static String ROW = "Row";
	public final static String TABLE = "tSpellCategory";
	//
	public String Description = "";
	public String Name = "";
	public int Row = 0;
	//
	public static List<SpellCategoryRec> selectAll(Database db) throws Exception
	{
		return db.selectList("SpellCategoryRec.selectAll", SpellCategoryRec.class);
	}
	public SpellCategoryRec() {
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}
