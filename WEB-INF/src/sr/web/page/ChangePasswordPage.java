package sr.web.page;
import ir.web.JControl;
import sr.data.LoginDb;
/**
 * Controls  similarly named jsp file
 */
public class ChangePasswordPage extends AppBasePage
{
private String confirmPassword="";
private String newPassword="";
private String oldPassword="";
private final String ctlCnf="cnf";
private final String ctlNew="new";
private final String ctlOld="old";
@Override
protected void init() throws Exception
{
	if(currentUser.isGuest())
	{
        setError("Not so fast chummer.");
	  	setRedirectBack();
	  	return;
	}
}
@Override
protected void read() throws Exception
{
	oldPassword = readString(ctlOld,60);
	newPassword = readString(ctlNew,60);
	confirmPassword = readString(ctlCnf,60);
    if (oldPassword.equals(""))
    {
        setError("Current password is required.");
    }
    else if (null == LoginDb.login(currentUser.Login, oldPassword))
    {
    	setError("Incorrect current password.");
    }
	if (currentUser.validatePassword(this,newPassword))
	{
	    if (! newPassword.equals(confirmPassword))
	    {
	    	setError("Confirm password must equal new password.");
	    }   
	}
}

@Override
protected void update() throws Exception
{
	currentUser.updatePwd(db,newPassword);
    db.update(currentUser);
	setStatus("Password changed.");
	setRedirectBack();
}

//
@Override
public void write() throws Exception
{
    String attrs=" maxlength='" + 60 + "' class='mandatory' ";
	set("Old",JControl.password(ctlOld,"Old Password",attrs));
    set("New",JControl.password(ctlNew,"New Password",attrs));
    set("Confirm",JControl.password(ctlCnf,"Confirm Password",attrs));
    set("Buttons",currentUser.isGuest()?"":submit(SUBMIT_BUTTON,"Update"));
}
}
