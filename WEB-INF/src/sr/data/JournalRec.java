package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.JDateTime;

public class JournalRec extends AppRec {
	
	public static final String ROW = "Row";
	public static final String TABLE = "tJournal";
	public static final String TEXT = "Text";
	public static final String TIME = "Time";
	public static final String TITLE = "Title";
	public static final String TYPE = "Type";
	public static final String USER = "User";
	//
	public int Row = 0;
	public String Text = "";
	public JDateTime Time = JDateTime.zero();
	public String Title = "";
	public String Type = "";
	public int User = 0;
	//
	@Override
	public String getTable() {
		return TABLE;
	}
	//
	public static List<JournalRec> selectAll(Database db, int userRow) throws Exception
	{
		return db.selectList("JournalRec.selectAll", JournalRec.class, userRow);
	}
}