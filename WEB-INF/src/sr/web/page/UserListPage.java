package sr.web.page;

import java.util.List;

import sr.data.UserRec;

/**
 * Controls similarly named jsp file
 */
public class UserListPage extends AppBasePage {

	public List<UserRec> requests;
	public List<UserRec> users;
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
		if(currentUser.isGuest())
		{
			setRedirect(Page.Home);
			return;
		}
		if(currentUser.isSysAdmin())
		{
			users = UserRec.selectAll(db);
		}
		else
		{
			users = UserRec.selectFriends(db,currentUser.Row);
		}
		requests = UserRec.selectRequests(db,currentUser.Row);
	}

	@Override
	protected void read() throws Exception {
		
	}

	@Override
	protected void write() throws Exception {
		set("Users",users.toString());
		set("Requests",requests.toString());
		set("SortType",UserRec.getSortTypeJson());
	}
}
