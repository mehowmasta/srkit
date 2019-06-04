package sr.web.page;

import java.util.List;

import sr.data.QualityRec;
import sr.data.QualityRec.QualityType;

/**
 * Controls similarly named jsp file
 */
public class PositiveQualitiesListPage extends AppBasePage {

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
		qualities = QualityRec.selectType(db,QualityType.Positive);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Qualities",qualities.toString());
	}
}
