package sr.data;

import java.util.List;

import ir.data.Database;

/**
 * UserRec is a data structure of user information
 */
public class QualityRec extends SrRec {
	public enum QualityType {
		Positive,Negative;
	}
	public static final String BONUS = "Bonus";
	public static final String KARMA = "Karma";
	public static final String MAXRATING = "MaxRating";
	public static final String PREREQUISITES = "Prerequisites";
	public static final String REQUIRETEXT = "RequireText";
	public static final String SKILL = "Skill";
	public final static String TABLE = "tQuality";
	public final static String TYPE = "Type";
	//
	public String Bonus = "";
	public String Karma = "";
	public int MaxRating = 1;
	public String Prerequisites = "";
	public boolean RequireText = false;
	public String Skill = "";
	public QualityType Type = QualityType.Positive;
	//
	public static List<QualityRec> selectForCharacter(Database db, int characterRow) throws Exception
	{
		return db.selectList("QualityRec.selectForCharacter",QualityRec.class,characterRow);
	}
	public static List<QualityRec> selectType(Database db, QualityType type) throws Exception
	{
		return db.selectList("QualityRec.selectType", QualityRec.class, type.name());
	}
	
	public QualityRec() {
	}

	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}
