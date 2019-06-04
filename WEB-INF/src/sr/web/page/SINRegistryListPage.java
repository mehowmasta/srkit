package sr.web.page;

import java.util.ArrayList;
import java.util.List;
import sr.data.CharacterRec;

/**
 * Controls similarly named jsp file
 */
public class SINRegistryListPage extends AppBasePage {

	public List<CharacterRec> characters;
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		if(currentUser.isGuest())
		{
			setError("UNREGISTERED USER. ACCESS DENIED.");
			characters = new ArrayList<CharacterRec>();
		}
		else
		{
			characters = CharacterRec.selectForRegistry(db);
		}
	}
    @Override
    protected boolean isMultiPart()
    {
       return true;
    }
	@Override
	protected void read() throws Exception {
		
	}
	@Override
	protected void write() throws Exception {
		set("Characters",characters.toString());
	}
}
