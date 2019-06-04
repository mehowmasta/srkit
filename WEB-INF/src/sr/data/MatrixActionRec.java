package sr.data;

import java.util.List;

import ir.data.Database;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class MatrixActionRec extends SrRec {
	public static final String ACTION = "Action";
	public static final String FUNCTION = "Function";
	public final static String LIMIT = "Limit";
	public static final String MARKS = "Marks";
	public final static String TABLE = "tMatrixAction";
	public final static String TECHNOMANCER = "Technomancer";
	public final static String TEST = "Test";
	//
	public String Action = "";
	public String Function = "";
	public String Limit = "";
	public String Marks = "";
	public boolean Technomancer = true;
	public String Test = "";
	//
	public static List<MatrixActionRec> selectAll(Database db) throws Exception
	{
		return db.selectList("MatrixActionRec.selectAll", MatrixActionRec.class);
	}
	public MatrixActionRec() {
	}
	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{ACTION,FUNCTION,LIMIT};
		String[] s = new String[]{"Action","",""};
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
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}
