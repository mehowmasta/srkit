package sr.web.page;

import ir.web.JControl;
import sr.data.CharacterRec;
import sr.data.GroupRec;
import sr.data.ImageRec;

/**
 * Controls similarly named jsp file
 */
public class GmBoardPage extends AppBasePage {

	public JControl ctlMaps;
	@Override
	protected void init() throws Exception {
		ctlMaps.setTitle("Maps");
		ctlMaps.setEvents("onchange='view.changeMap()'");
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		ctlMaps.addValue(0,"[select map]");
		ctlMaps.addValues(ImageRec.selectMapRows(db, currentUser.Row, false));
		set("Groups",GroupRec.selectForUser(db, currentUser.Row,false));
	}
}
