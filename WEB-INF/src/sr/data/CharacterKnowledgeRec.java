package sr.data;

import ir.data.Database;
import ir.util.StringKit;

public class CharacterKnowledgeRec extends AppRec
{
	public enum KnowledgeType { 
		Academic("Logic","Biology,History,Literature,Magic Theory,Medicine,Music,Parabotony,Parazoology,Politics"),
		Interest("Intuition","20th Century Trivia,Combat Biking,Elven Wine,Extreme Sports,Matrix Security,Operating Systems,Poetry,Pop Music,Restaurants,Simsense Movies,Trideo Shows,Urban Brawl"),
		Language("Intuition","Aztlaner Spanish,Dakota,English,French,German,Italian,Japanese,Lakota,Mandarin,Navajo,Or'zet,Sperethiel,Russian,Spanish"),
		Professional("Logic","Business,Infiltration Techniques,Journalism,Law,Military Service,Police Procedures,UCAS Military Regulations"),
		Street("Intuition","BTL Dealers,Clinics,Criminal Organizations,Corporations,Deckers,Drug Dealers,Fences,Fixers,Gang Identification,Law Enforcement Tactics,Runner Hangouts,Smuggling Routes");
		public static String toJson() {
			StringBuilder b = new StringBuilder("[");
			String comma = "";
			for (KnowledgeType s : KnowledgeType.values()) {
				b.append(comma)
				.append("{name:").append(StringKit.jsq(s.name()))
				.append(",attribute:").append(StringKit.jsq(s.attribute))
				.append(",defaults:").append(StringKit.jsq(s.defaults))
				.append("}");
				comma = ",";
			}
			return b.append("]").toString();
		}
		public static void align(Database db) {
			StringBuilder b = new StringBuilder();
			String comma = "";
			b.append("alter table tCharacterKnowledge modify column Type enum(");
			for (KnowledgeType t : values()) {
				b.append(comma).append("'").append(t.name()).append("'");
				comma = ",";
			}
			b.append(") not null");
			try {
				db.execute(b.toString());
				StringKit.println("KnowledgeType.align ok.");
			} catch (Exception e) {
				StringKit.println("KnowledgeType.align: " + e.getMessage());
			}
		}
		public static KnowledgeType lookup(String name) {
			for (KnowledgeType t : values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return Academic;
		}
		private String defaults;
		private String attribute;
		private KnowledgeType(String attribute,String defaults)
		{
			this.attribute = attribute;
			this.defaults = defaults;
		}
	};
	public static final String CHARACTERROW = "CharacterRow";
	public static final String NAME = "Name";
	public static final String NOTE = "Note";
	public static final String RATING = "Rating";
	public static final String ROW = "Row";
	public static final String TABLE = "tCharacterKnowledge";
	public static final String TYPE = "Type";
	//
	//public CharacterAttribute Attribute = CharacterAttribute.Logic;
	public int CharacterRow = 0;
	public String Name = "";
	public boolean Native = false;
	public String Note = "";
	public int Rating = 0;
	public int Row = 0;
	public KnowledgeType Type = KnowledgeType.Academic;
	
	@Override
	public String getTable() {
		return TABLE;
	}
	
}