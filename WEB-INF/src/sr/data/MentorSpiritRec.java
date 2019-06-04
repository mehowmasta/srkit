package sr.data;

import java.util.List;

import ir.data.Database;

public class MentorSpiritRec extends SrRec
{
	public static String ADVANTAGEADEPT = "AdvantageAdept";
	public static String ADVANTAGEALL = "AdvantageAll";
	public static String ADVANTAGEMAGICIAN = "AdvantageMagician";
	public static String DISADVANTAGE = "Disadvantage";
	public static String SIMILARARCHETYPES = "SimilarArchetypes";
	public static String TABLE = "tMentorSpirit";
	//
	public String AdvantageAdept = "";
	public String AdvantageAll = "";
	public String AdvantageMagician = "";
	public String Disadvantage = "";
	public String SimilarArchetypes = "";
	//
	public static List<MentorSpiritRec> selectAll(Database db) throws Exception
	{
		return db.selectList("MentorSpiritRec.selectAll", MentorSpiritRec.class);
	}
	@Override
	public String getTable() {
		return TABLE;
	}
	
}