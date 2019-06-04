package sr.web.page;

import java.util.List;

import sr.data.ProgramRec;
import sr.data.SrRec;

/**
 * Controls similarly named jsp file
 */
public class ProgramsPage extends AppBasePage {

	public List<ProgramRec> programs;
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		programs = SrRec.selectAll(db,ProgramRec.class);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Programs",programs.toString());
	}
}
