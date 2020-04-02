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
		
		if(currentUser.isWife())
		{
			b.append(imageButton(Page.CharacterList.titleKey,Images.get(Page.CharacterList.image),"sr5.go(\""+Page.CharacterList.jsp()+"\")"));
			b.append(imageButton(Page.Budget.titleKey,Images.get(Page.Budget.image),"sr5.go(\""+Page.Budget.jsp()+"\")"));
			b.append(imageButton(Page.UserList.titleKey,Images.get(Page.UserList.image),"sr5.go(\""+Page.UserList.jsp()+"\")"));
			b.append(imageButton(Page.Preferences.titleKey,Images.get(Page.Preferences.image),"sr5.go(\""+Page.Preferences.jsp()+"\")"));
		}
		else
		{

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
		}
		set("Buttons",b.toString());
		set("FriendRequest",currentUser.hasFriendRequest(db));
		set("TransferRequest",currentUser.hasTransferRequest(db));
	}
}
