package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.JDateTime;
import ir.util.StringKit;

public class JournalRec extends AppRec {
	public static final String ARCHIVE = "Archive";
	public static final String ROW = "Row";
	public static final String TABLE = "tJournal";
	public static final String TAGS = "Tags";
	public static final String TEXT = "Text";
	public static final String TIME = "Time";
	public static final String TITLE = "Title";
	public static final String TYPE = "Type";
	public static final String USER = "User";
	//
	public boolean Archive = false;
	public int Row = 0;
	public String Tags = "";
	public String Text = "";
	public JDateTime Time = JDateTime.now();
	public String Title = "";
	public String Type = "";
	public int User = 0;
	//
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{TIME,TYPE,TITLE};
		String[] s = new String[]{"","",""};
		for(int i=0,z=t.length;i<z;i++)
		{
			b.append(comma)
			 .append("{name:").append(StringKit.jsq(t[i]))
			 .append(",suffix:").append(StringKit.jsq(s[i]))
			 .append("}");
			comma=",";
		}
		return b.append("]").toString();
	}
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