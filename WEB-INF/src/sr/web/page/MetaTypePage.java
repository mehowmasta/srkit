package sr.web.page;

import java.util.List;

import sr.data.RaceRec;

/**
 * Controls similarly named jsp file
 */
public class MetaTypePage extends AppBasePage {

	public List<RaceRec> races;
	//public List<SpellCategoryRec> spellCategory;
	@Override
	protected void init() throws Exception {
		races = RaceRec.selectAll(db);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Races",races.toString());
	}
}
