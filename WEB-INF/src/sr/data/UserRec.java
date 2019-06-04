package sr.data;

import java.util.List;

import javax.mail.internet.InternetAddress;
import ir.data.Database;
import ir.data.IValidator;
import ir.util.JDateTime;
import ir.util.JMap;
import ir.util.StringKit;

/**
 * UserRec is a data structure of user information
 */
public class UserRec extends AppRec {
	public static final String CREATEDAT = "CreatedAt";
	public static final String DEFAULT_PASS = "deck1234";
	public static final String EMAIL = "EMail";
	public static final String LOGIN = "Login";
	public static final String NAME = "Name";
	public static final String PLAYERCHARACTER = "PlayerCharacter";
	public final static String ROW = "Row";
	public final static String ROWSPERPAGE = "RowsPerPage";
	public final static String SHORTNAME = "ShortName";
	public final static String SOURCEBOOKS = "SourceBooks";
	public final static String TABLE = "tUser";
	public final static String THEMEROW = "ThemeRow";
	//
	public JDateTime CreatedAt = JDateTime.now();
	public String EMail = "";
	public boolean Inactive = false;
	public String Login = "";
	public String Name = "";
	public String PageSettings = "";
	public int PlayerCharacter = 0;
	public UserRole Role = UserRole.Runner;
	public int Row = 0;
	public int RowsPerPage = 20;
	public String ShortName = "";
	public String SourceBooks = "Core";
	public int ThemeRow = 1;
	
	//
	public static List<UserRec> selectAll(Database db) throws Exception
	{
		return db.selectList("UserRec.selectAll", UserRec.class);
	}
	public static UserRec selectByLogin(Database db,String login) throws Exception
	{
		UserRec rec = new UserRec();
		db.selectFirst("UserRec.selectByLogin", rec,login);
		return rec;
	}
	public static List<UserRec> selectFriends(Database db, int userRow) throws Exception
	{
		return db.selectList("UserRec.selectFriends", UserRec.class, userRow);
	}
	public static List<UserRec> selectRequests(Database db, int userRow) throws Exception
	{
		return db.selectList("UserRec.selectRequests", UserRec.class, userRow, userRow);
	}
	public UserRec() {
	}

	public UserRec(int row) {
		this.Row = row;
	}

	@Override
	public void afterDelete(Database db) throws Exception {
		//TODO: create stored procedure to delete all character data, all map/image data, all team data
	}
	@Override
	public void beforeUpdate(Database db) throws Exception {
	}

	@Override
	protected boolean excludeFromJson(String fieldName) {
		return fieldName.equalsIgnoreCase("PageSettings") || super.excludeFromJson(fieldName);
	}

	public String getEmail() {
		return EMail;
	}

	public String getLogon() {
		return Login;
	}

	public String getMailFrom(AppDb db) throws Exception {
		return StringKit.coalesce(this.EMail);
	}

	public JMap getPageSettingsMap() {
		return new JMap(PageSettings);
	}

	@Override
	public int getRow() {
		return Row;
	}

	public static String getSortTypeJson() {
		StringBuilder b = new StringBuilder("[");
		String comma = "";
		String[] t = new String[]{NAME,"LastLogin",ROW};
		String[] s = new String[]{"Name","Last Login","Created"};
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

	/**
	 * Indicates whether email address has a hope of being valid
	 */
	public boolean hasEmail() {
		if (EMail.length() > 4) {
			int atAt = EMail.indexOf('@');
			if (atAt > 0) {
				return EMail.lastIndexOf('.') > atAt;
			}
		}
		return false;
	}

	public boolean isAdmin() {
		return Role == UserRole.Admin || Role == UserRole.SysAdmin;
	}

	public boolean isDupLogin(Database db) throws Exception {
		return 0 < db.selectScalar("UserRec.isDupLogin", 0, Login, Row);
	}

	protected boolean isDupName(Database db) throws Exception {// note we pass cn in here so we can check from
																// switchCompany
		return 0 < db.selectScalar("UserRec.isDupName", 0, Row, Name);
	}

	protected boolean isDupShortName(Database db) throws Exception {// note we pass cn in here so we can check from
																	// switchCompany
		return 0 < db.selectScalar("UserRec.isDupShortName", 0, Row, ShortName);
	}
	public boolean isFriendsWith(Database db,int userRow) throws Exception
	{
		return 0 < db.selectScalar("UserRec.isFriendsWith", 0,Row, userRow);
	}
	public boolean isGuest() {
		return Role == UserRole.Guest;
	}
	/**
	 * @return whether user is System Admin role
	 */
	public boolean isSysAdmin() {
		return Role == UserRole.SysAdmin;
	}

	public boolean selectByLogon(Database db, String lo) throws Exception {// note this is not limited to current
																			// company
		return db.selectFirst(Sql.get("UserRec.selectByLogin"), this, lo);
	}
	public CharacterRec selectPlayerCharacter(Database db) throws Exception {
		return CharacterRec.selectPlayerCharacter(db,Row,PlayerCharacter);
	}
	public void setPageSettings(JMap map) {
		this.PageSettings = map.toString();
	}

	public String toJson() throws Exception {
		StringBuilder b = new StringBuilder("");
		b.append("{Row:").append(jsq(Row))
		 .append(",Name:").append(jsq(Name))
		 .append(",PlayerCharacter:").append(PlayerCharacter)
		 .append(",Portrait:").append(getTemp("Portrait", Integer.class))
		 .append(",Extension:").append(jsq(getTemp("Extension", String.class)))
		 .append(",UserImage:").append(getTemp("UserImage", Integer.class))
		 .append("}");
		return b.toString();
		
	}
	@Override
	public String toString() {
		return super.toString();
	}

	public void updatePageSettings(AppDb db, JMap map) throws Exception {
		String before = this.PageSettings;
		setPageSettings(map);
		if (!before.equals(this.PageSettings)) {
			db.update(this);
		}
	}

	/**
	 * Changes the users password
	 */
	public void updatePwd(Database db, String p) throws Exception {
		db.execute("UserRec.updatePwd", p, LoginDb.Salt, Row);
		boolean wasClean = isDirty();
		if (wasClean) {
			this.makeClean();
		}
	}

	@Override
	public boolean validate(IValidator iv) throws Exception {
		Database db = iv.getDatabase();
		if (StringKit.eq(Login.trim(), "")) {
			iv.addFieldError("Login", "value is required");
		} else if (isDupLogin(db)) {
			iv.addFieldError("Login", "value already assigned. Try FirstName.LastName or FirstName.Company.", Login);
		}
		if (StringKit.eq(Name, "")) {
			iv.addFieldError("Name", "value is required");
		} else if (isDupName(db)) {
			iv.addFieldError("Name", "value already assigned");
		}
		if (StringKit.eq(ShortName, "")) {
			iv.addFieldError("ShortName", "value is required");
		} else if (isDupShortName(db)) {
			iv.addFieldError("ShortName", "value already assigned");
		}
		if (EMail.length() > 0 && !isSysAdmin()) {
			try {
				new InternetAddress(EMail).validate();
			} catch (Exception e) {
				iv.addFieldError("EMail", "address is not valid");
			}
		}
		return iv.isOK();
	}

public boolean validatePassword(IValidator iv, String pwd) throws Exception
{
    if (isSysAdmin())
    {
        return true;
    }
    if (pwd.length() < 8)
    {
    	iv.addFieldError("Pwd", "value must be at least 8 characters long.");
        return false;
    }
    return true;
}
}
