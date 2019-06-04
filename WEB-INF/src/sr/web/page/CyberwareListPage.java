package sr.web.page;

import java.util.List;

import sr.data.CyberwareRec;

/**
 * Controls similarly named jsp file
 */
public class CyberwareListPage extends AppBasePage {

	public List<CyberwareRec> cyberware;
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
		cyberware = CyberwareRec.selectCyberware(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Cyberware",cyberware.toString());
	}
}
