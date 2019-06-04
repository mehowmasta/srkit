package sr.web.page;

import java.util.List;

import sr.data.CyberdeckRec;
import sr.data.SrRec;

/**
 * Controls similarly named jsp file
 */
public class CyberdeckListPage extends AppBasePage {

	public List<CyberdeckRec> cyberdecks;	
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
		cyberdecks = SrRec.selectAll(db,CyberdeckRec.class);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Cyberdecks",cyberdecks.toString());
	}
}
