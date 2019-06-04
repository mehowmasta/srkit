package sr.web.page;


import java.util.ArrayList;
import java.util.List;

import ir.web.JControl;
import sr.data.CharacterArmorRec;
import sr.data.CharacterRec;
import sr.data.GroupCharacterRec;
import sr.data.GroupRec;
import sr.data.RaceRec;

/**
 * Controls similarly named jsp file
 */
public class GroupDetailPage extends AppBasePage {

	public JControl ctlRow;
	public JControl ctlName;
	public JControl ctlInactive;
	public JControl ctlPrivate;
	public JControl ctlShareKey;
	private GroupRec currentGroup = new GroupRec();
	public JControl ctlCharacters;
	private List<GroupCharacterRec> cList;
	@Override
	protected void init() throws Exception {
		if(currentUser.isGuest())
		{
			setError("Geeked!");
		  	setRedirectBack();
		  	return;
		}
		if(hasParm("ctlRow"))
		{
			int row = readInt("ctlRow");
			if(row>0)
			{
				currentGroup = GroupRec.selectGroup(db,row);
				if(currentGroup.Row==0)
				{
					setError("Failed to retrieve group.");
				  	setRedirectBack();
				  	return;
				}
				if(currentGroup.User != currentUser.Row)
				{
					setError("Sneaky sneaky.");
				  	setRedirectBack();
				  	return;
				}
			}
		}
		else
		{
			setError("Missing group parameters.");
	  	  	setRedirectBack();
	  	  	return;
		}
		mapControl(ctlRow,currentGroup,GroupRec.ROW);
		mapControl(ctlName,currentGroup,GroupRec.NAME);
		mapControl(ctlShareKey, currentGroup, GroupRec.SHAREKEY);
		mapControl(ctlInactive, currentGroup, GroupRec.INACTIVE);
		mapControl(ctlPrivate, currentGroup, GroupRec.PRIVATE);
		ctlRow.setHidden();
		ctlCharacters.setHidden();
		if(currentGroup.Row > 0 && currentGroup.User != currentUser.Row)
		{
			ctlName.setDisabled();
			ctlInactive.setDisabled();
			ctlPrivate.setDisabled();
		}
	}
	@Override
	protected void read() throws Exception {
		readCharacters();
	}

	private void readCharacters() throws Exception
	{
		cList = new ArrayList<GroupCharacterRec>();
		if(hasParm("ctlCharacters"))
		{
			String chars = readString("ctlCharacters",2000);
			if(chars.length()>0)
			{
				String[] ss = chars.split(DELIMITER);				
				for(String s : ss)
				{
					String[] data = s.split(SPLITTER);
					if(data.length==2)
					{
						GroupCharacterRec cs = new GroupCharacterRec();
						cs.GroupRow = currentGroup.Row;
						cs.CharacterRow = Integer.parseInt(data[0]);
						cs.Quantity = Integer.parseInt(data[1]);
						cList.add(cs);
					}
				}
			}
		}
	}
	@Override
	protected void update() throws Exception {
		if(currentUser.isGuest())
		{
			setError("Buzz!.");
			return;
		}
		if(hasParm(DELETE_BUTTON))
		{
			db.delete(currentGroup);
			setStatus("{0} has been deleted.",currentGroup.Name);
		  	setRedirectBack();
		    return;
		}

		if(currentGroup.Row==0)
		{
			currentGroup.User = currentUser.Row;
			if(currentGroup.Name.isEmpty())
			{
				setError("Group must have a name before it can be created.");
				return;
			}
			db.insert(currentGroup);
			updateGroupCharacters();
			setStatus("Created group {0}.",currentGroup.Name);
		    return;
		}
		else
		{
			if (db.update(currentGroup))
		    {
			  updateGroupCharacters();
		      setStatus("{0} has been updated.",currentGroup.Name);
		      return;
		    }
		}
	}
	private void updateGroupCharacters() throws Exception
	{
		currentGroup.updateCharacters(db,cList);
	}
	@Override
	protected void write() throws Exception {
		set("ShareKey",currentGroup.ShareKey);
		set("GroupCharacters",GroupRec.selectCharacters(db, currentGroup.Row).toString());
		set("Types",CharacterRec.CharacterType.selectJson());
		set("GroupRow",currentGroup.Row);
		writeButtons();
		
	}	
	private void writeButtons() {
		StringBuilder btns = new StringBuilder("");
		if(currentGroup.Row==0)
		{
			btns.append(submit(SUBMIT_BUTTON,"Create",currentUser.isGuest()?" disabled " : ""));			
		}
		else
		{
			if(currentGroup.Row>0 && currentGroup.User == currentUser.Row)
			{
				btns.append(eventButton(DELETE_BUTTON, "Delete",  "view.deleteGroup()",currentUser.isGuest()?" disabled " : ""));
				btns.append(submit(SUBMIT_BUTTON,"Update",currentUser.isGuest()?" disabled " : ""));		
			}
		}
		set("Buttons", btns.toString());
	}
}
