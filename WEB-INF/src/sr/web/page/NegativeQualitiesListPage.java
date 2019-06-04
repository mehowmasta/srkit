package sr.web.page;

import java.util.List;

import sr.data.QualityRec;
import sr.data.QualityRec.QualityType;

/**
 * Controls similarly named jsp file
 */
public class NegativeQualitiesListPage extends AppBasePage {

	public List<QualityRec> qualities;
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
		qualities = QualityRec.selectType(db,QualityType.Negative);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Qualities",qualities.toString());
	}
}
