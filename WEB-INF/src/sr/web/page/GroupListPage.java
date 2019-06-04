package sr.web.page;

import java.util.List;

import sr.data.CharacterRec;
import sr.data.GroupRec;
import sr.data.RaceRec;

/**
 * Controls similarly named jsp file
 */
public class GroupListPage extends AppBasePage {

	public List<GroupRec> groups;
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		groups = GroupRec.selectForUser(db,currentUser.Row,true);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Groups",groups.toString());
		set("Buttons",eventButton("btnAdd","Add","view.addGroup()",currentUser.isGuest()?"disabled " : ""));
	}
}
