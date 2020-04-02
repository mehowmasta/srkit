package sr.web.page;

import java.io.File;
import java.util.List;

import ir.util.FileKit;
import ir.util.JDateTime;
import ir.util.JImage;
import ir.util.StringKit;
import ir.web.JControl;
import ir.web.UploadFile;
import sr.data.CharacterRec;
import sr.data.ImageRec;
import sr.data.SourceBook;
import sr.data.ThemeRec;
import sr.data.UserRec;
import sr.data.ImageRec.ImageType;

/**
 * Controls similarly named jsp file
 */
public class PreferencesPage extends AppBasePage {

	public JControl ctlEMail;
	public JControl ctlLogin;
	public JControl ctlName;
	public JControl ctlShortName;
	public JControl ctlCharacter;
	public JControl ctlTheme;
	public JControl ctlAutoRoll;
	public JControl ctlUseJournal;
    public ImageRec image = new ImageRec();

	private void disableIfGuest() throws Exception {
		if (currentUser.isGuest()) {
			ctlEMail.setDisabled();
			ctlName.setDisabled();
			ctlShortName.setDisabled();
			ctlLogin.setDisabled();
			ctlCharacter.setDisabled();
			ctlTheme.setDisabled();
		}
	}

	@Override
	protected void init() throws Exception {
		mapControl(ctlEMail, currentUser, UserRec.EMAIL);
		mapControl(ctlName, currentUser, UserRec.NAME);
		mapControl(ctlLogin, currentUser, UserRec.LOGIN);
		mapControl(ctlShortName, currentUser, UserRec.SHORTNAME);
		mapControl(ctlCharacter,currentUser,UserRec.PLAYERCHARACTER);
		mapControl(ctlTheme,currentUser,UserRec.THEMEROW);
		mapControl(ctlAutoRoll,currentUser,UserRec.AUTOROLL);
		mapControl(ctlUseJournal,currentUser,UserRec.USEJOURNAL);
		disableIfGuest();
	}

    @Override
    protected boolean isMultiPart()
    {
       return true;
    }
	@Override
	protected void read() throws Exception {
		StringBuilder b = new StringBuilder();
		String comma ="";
		for(SourceBook sb : SourceBook.values())
		{
			if(hasParm("ctl"+sb.name()))
			{
				b.append(comma).append(sb.code);
				comma=",";
			}
		}
		currentUser.SourceBooks = b.toString();
	}
	@Override
	protected void update() throws Exception {
		if (db.update(currentUser))
	    {
		  setTheme(db);
	      setStatus("Preferences updated.");
	      if(!hasParm("portraitSubmit"))
	      {
		  	  setRedirectBack();
	      }
	      return;
	    }
	}
	
	@Override
	protected void write() throws Exception {
		writeSourceBooks();
		ctlUseJournal.setTitle("Use Journal");
		//ctlUseJournal.addData("hover", "Enable and disable Journal Icon from top right header.");
		ctlCharacter.addValue(0,"[none]");
		ctlTheme.addValues(ThemeRec.selectNameRows(db));
		List<CharacterRec> characters = CharacterRec.selectForUser(db, currentUser.Row);
		for(CharacterRec c : characters)
		{
			ctlCharacter.addValue(c.Row,c.Name);
		}
		set("Buttons",eventButton("changePwdBtn","Password","sr5.go(\"changepassword.jsp\")",currentUser.isGuest()?" disabled " : "") + (currentUser.isGuest()?"":submit(SUBMIT_BUTTON,"Update",currentUser.isGuest()?" disabled " : "")));
	}
	private void writeSourceBooks() throws Exception
	{
		StringBuilder b = new StringBuilder("<div style='display:grid;width:100%;grid-template-columns: repeat(auto-fill, minmax(20rem,1fr));'>");
		for(SourceBook sb : SourceBook.values())
		{
			b.append(JControl.check("ctl" + sb.name(),sb.text,currentUser.SourceBooks.indexOf(sb.code)>-1,currentUser.isGuest()?"disabled":""));
		}
		set("SourceBooks",b.append("</div>").toString());
	}
}
