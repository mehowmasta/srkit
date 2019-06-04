package sr.web.page;

import sr.web.Images;

/**
 * Controls similarly named jsp file
 */
public class HomePage extends AppBasePage {

	//public List<SpellCategoryRec> spellCategory;
	@Override
	protected void init() throws Exception {
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		StringBuilder b = new StringBuilder();
		for(Page p : Page.Home.getChildren())
		{
			if(p.sysAdminOnly() && !currentUser.isSysAdmin())
			{
				continue;
			}
			else if (!p.allowGuest() && currentUser.isGuest())
			{
				continue;
			}
			b.append(imageButton(p.titleKey,Images.get(p.image),"sr5.go(\""+p.jsp()+"\")"));
		}
		set("Buttons",b.toString());
	}
}
