package sr.web.page;

import java.util.List;

import sr.data.MentorSpiritRec;

/**
 * Controls similarly named jsp file
 */
public class MentorSpiritListPage extends AppBasePage {

	public List<MentorSpiritRec> spirits;
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		spirits = MentorSpiritRec.selectAll(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Spirits",spirits.toString());
	}
}
