package sr.data;

import java.io.File;
import java.util.List;

import sr.data.CharacterRec.CharacterType;
import sr.web.App;
import ir.data.Database;
import ir.data.PairRow;
import ir.util.FileKit;
import ir.util.JDate;
import ir.util.StringKit;

/**
 * LoginDb is used for login and at startup, before any particular site number
 * is active.
 */
public class LoginDb extends Database {
	public static final String Salt = "MageChummer"; //encryption key for encrypted database columns - different in production;

	//
	public static List<UserRec> getAdminUsers() throws Exception {
		LoginDb db = new LoginDb();
		db.open();
		try {
			return db.selectAdminUsers();
		} finally {
			db.close();
		}
	}

	public static UserRec login(String u, String p) throws Exception {
		return login(u, p, "");
	}

	public static UserRec login(String u, String p, String a) throws Exception {
		LoginDb db = new LoginDb();
		db.open();
		try {
			return db.loginRegular(u, p);
		} finally {
			db.close();
		}
	}

	/**
	 * construction
	 */
	public LoginDb() {
		super();
	}
	public void addPageMethod(String pageName, String method) throws Exception {
		execute("LoginDb.updateAuthNewMethod", pageName, method, method);
	}

	protected void checkRoutine(String procName, String fileName, JDate updateIfOlderThan) throws Exception {
		if (selectRoutineDate(procName).isBefore(updateIfOlderThan)) {
			try {
				executeScriptFile(fileName);
				StringKit.println("Updated routine {0} from {1}.", procName, fileName);
			} catch (Exception e) {
				StringKit.println("Failed to update routine {0} from {1}: {2}", procName, fileName, e.getMessage());
			}
		}
	}

	private void executeScriptFile(String fileName) throws Exception {
		File f = new File(App.getRealPath("/croute_sql/" + fileName));
		if (!f.exists()) {
			throw new Exception("Script file " + f.getAbsolutePath() + " not found.");
		}
		String sqlSource = FileKit.readToString(f);
		executeScript(sqlSource, "$$", "DELIMITER");
		StringKit.println("Script file " + fileName + " executed.");
	}

	/**
	 * Returns user on success, else returns null
	 */
	private UserRec loginRegular(String uid, String pwd) throws Exception {
		UserRec theUser = new UserRec();
		boolean b = selectFirst("LoginDb.loginRegular", theUser, uid, pwd, Salt, uid, pwd, Salt);
		return b ? theUser : null;
	}

	public void open() throws Exception {
		String url = AppDb.enhanceUrl(App.getDbUrl());
		open(App.getDbDriver(), url, App.getDbUser(), App.getDbPwd(), App.getDbPoolSize());
		loadNamedStatements(Sql.getMap());
	}

	public List<UserRec> selectAdminUsers() throws Exception {
		return selectList("LoginDb.selectAdminUsers", UserRec.class);
	}

	public String selectForeignKeyName(String fromTable, String fromColumn) throws Exception {// note you may need to
																								// run "grant process on
																								// *.* to ?dbuser?" for
																								// this to run
		String sql = "select right(k.id,length(k.id)-instr(k.id,'/')) as fkn"
				+ " from information_schema.innodb_sys_foreign k"
				+ " inner join information_schema.innodb_sys_foreign_cols c on k.id=c.id"
				+ " where k.for_name=concat(schema(),'/',?) " + "  and c.for_col_name=?";
		return selectScalar(sql, "", fromTable, fromColumn);
	}

	public String selectIndexName(String table, String column1, String... otherColumns) throws Exception {// note you
																											// may need
																											// to run
																											// "grant
																											// process
																											// on *.* to
																											// ?dbuser?"
																											// for this
																											// to run
		String columnList = column1;
		for (int i = 0; otherColumns != null && i < otherColumns.length; i++) {
			columnList += "," + otherColumns;
		}
		String sql = "SELECT index_name" + " FROM information_schema.STATISTICS"
				+ " where table_schema=schema() and table_name=?" + " group by table_name,index_name"
				+ " having group_concat(column_name order by seq_in_index)=?";
		return selectScalar(sql, "", table, columnList);
	}

	public List<PairRow> selectIpAddressPairs() throws Exception {
		return selectList("LoginDb.selectIpAddressPairs", PairRow.class);
	}

	public JDate selectRoutineDate(String procName) throws Exception {
		return selectScalar("SELECT date(greatest(last_altered,created))" + " FROM information_schema.ROUTINES"
				+ " where routine_schema=schema() and routine_name=?", JDate.zero(), procName);
	}

	public String selectSessionCsv() throws Exception {
		return StringKit.join("\n", selectScalarArray("LoginDb.selectSessionCsv", String.class));
	}

	public void startup() throws Exception {
		setStatementTiming(false);
		startupTables();
		startupColumns();
		startupPages();
		startupPrecision();
		startupEnums();
		startupViews();
		startupRoutines();
	}

	private void startupColumns() throws Exception {
		addColumn(CharacterRec.TABLE,CharacterRec.REGISTER,"tinyint(1) default '0'");
		addColumn(CharacterRec.TABLE,CharacterRec.PROFESSIONALRATING,"int(3) default '0'");
		addColumn(QualityRec.TABLE,QualityRec.REQUIRETEXT,"tinyint(1) default '0'");
		addColumn(QualityRec.TABLE,QualityRec.BONUS,"varchar(100) default ''");
		addColumn(CharacterRec.TABLE,CharacterRec.IMPORTNOTES,"varchar(500) default ''");
		addColumn(CharacterRec.TABLE,CharacterRec.ISIMPORT,"tinyint(1) default 0");
		addColumn(UserRec.TABLE,UserRec.THEMEROW,"int(3) default 1");
		addColumn(MessageThreadUserRec.TABLE,MessageThreadUserRec.SHAREROLL,"tinyint(1) default 0");
		addColumn(CharacterRec.TABLE,CharacterRec.INACTIVE,"tinyint(1) default 0");
		addColumn(CharacterSkillRec.TABLE,CharacterSkillRec.SPECIAL,"varchar(50) default ''");
		addColumn(CharacterCyberwareRec.TABLE,CharacterCyberwareRec.RATING,"int(2) default 1");
		addColumn(CharacterVehicleRec.TABLE,CharacterVehicleRec.CURRENTAMOUNT,"int(3) default 0");
		addColumn(CharacterCyberdeckRec.TABLE,CharacterCyberdeckRec.CURRENTAMOUNT,"int(3) default 0");
		addColumn(CharacterDroneRec.TABLE,CharacterDroneRec.CURRENTAMOUNT,"int(3) default 0");
		addColumn(CharacterWeaponRec.TABLE,CharacterWeaponRec.CURRENTAMOUNT,"int(3) default 0");
		addColumn(CharacterCyberdeckRec.TABLE,EquipRec.EQUIPPED,"tinyint(1) default 0");
		addColumn(CharacterDroneRec.TABLE,EquipRec.EQUIPPED,"tinyint(1) default 0");
		addColumn(CyberwareRec.TABLE,CyberwareRec.BONUS,"varchar(100) default ''");
		addColumn(CyberwareRec.TABLE,CyberwareRec.BASE,"tinyint(1) default 0");
		addColumn(CyberwareRec.TABLE,CyberwareRec.CONTAINER,"tinyint(1) default 0");
		addColumn(CharacterRec.TABLE,CharacterRec.PORTRAIT,"int(11) default 0");
		addColumn(CharacterRec.TABLE,CharacterRec.INITIATIVEDICE,"int(2) default 1");
		addColumn(RaceRec.TABLE,RaceRec.DESCRIPTION,"varchar(2000)");
		addColumn(CharacterRec.TABLE,CharacterRec.MOVEMENT,"varchar(45) after "+CharacterRec.JUDGEINTENTIONS);
		addColumn(UserRec.TABLE,UserRec.PLAYERCHARACTER,"int(11) not null default '0'");
		addColumn(RaceRec.TABLE,RaceRec.ADDITIONALKARMA,"int(4) not null default '0'");
	}

	private void startupEnums() throws Exception {
		CharacterType.align(this);
		CharacterKnowledgeRec.KnowledgeType.align(this);
		MapDataRec.ObjectType.align(this);
		MapDataRec.LayerType.align(this);
		MessageRec.MessageType.align(this);
		MessageThreadRec.MessageThreadType.align(this);
		MessageThreadUserRec.MessageThreadUserRole.align(this);
		GearRec.GearType.align(this);
	}

	private void startupPages() throws Exception {
	}

	private void startupPrecision() throws Exception {
	}

	private void startupRoutines() throws Exception {
	}

	private void startupTables() throws Exception {
		if(!tableExists(JournalRec.TABLE))
		{
            execute("JournalRec.createTable");
		}
		if(!tableExists(CharacterKnowledgeRec.TABLE))
		{
            execute("CharacterKnowledgeRec.createTable");
		}
		if(!tableExists(FriendRec.TABLE))
		{
            execute("FriendRec.createTable");
		}
		if(!tableExists(CharacterContactRec.TABLE))
		{
            execute("CharacterContactRec.createTable");
		}
		if(!tableExists(ThemeRec.TABLE))
		{
            execute("ThemeRec.createTable");
		}
		if(!tableExists(MapDataRec.TABLE))
		{
            execute("MapDataRec.createTable");
		}
		if(!tableExists(CharacterSettingRec.TABLE))
		{
            execute("CharacterSettingRec.createTable");
		}
		if(!tableExists(MessageRec.TABLE))
		{
            execute("MessageRec.createTable");
		}
		if(!tableExists(MessageThreadRec.TABLE))
		{
            execute("MessageThreadRec.createTable");
		}
		if(!tableExists(MessageThreadUserRec.TABLE))
		{
            execute("MessageThreadUserRec.createTable");
		}
	}

	private void startupViews() throws Exception {
	}

	public void updateStringAllSites(String k, String v) throws Exception {
	}

	enum PageDealer {
		No(0), Yes(1);
		public final int v;

		PageDealer(int v) {
			this.v = v;
		}
	}

	enum PageIsMenu {
		No(0), Yes(1);
		public final int v;

		PageIsMenu(int v) {
			this.v = v;
		}
	}
}