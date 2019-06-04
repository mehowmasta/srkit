package sr.data;

/**
 * UserRec is a data structure of user information
 */
public class ProgramRec extends SrRec {
	
	public enum ProgramType {
		Common,Hacking;
	}	
	public final static String TABLE = "tProgram";
	public final static String TYPE = "Type";
	//
	public ProgramType Type = ProgramType.Common;
	//
	public ProgramRec() {
	}
	/**
	 * @return String home table
	 */
	@Override
	public String getTable() {
		return TABLE;
	}
}
