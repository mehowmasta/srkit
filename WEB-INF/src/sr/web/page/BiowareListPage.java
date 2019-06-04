package sr.web.page;

import java.util.List;

import sr.data.CyberwareRec;

/**
 * Controls similarly named jsp file
 */
public class BiowareListPage extends AppBasePage {

	public List<CyberwareRec> bioware;
	@Override
	public boolean hasInfo()
	{
		return true;
	}
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		bioware = CyberwareRec.selectBioware(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Bioware",bioware.toString());
	}
}
