package sr.web.page;

import java.util.List;

import sr.data.SkillRec;

/**
 * Controls similarly named jsp file
 */
public class SkillListPage extends AppBasePage {

	public List<SkillRec> skills;
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
	public boolean hasSort()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		skills = SkillRec.selectAll(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Skills",skills.toString());
		set("SortType",SkillRec.getSortTypeJson());
	}
}
