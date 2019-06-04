package sr.web.page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import ir.util.FileKit;
import ir.util.JDateTime;
import ir.util.JsonObj;
import ir.util.StringKit;
import ir.web.UploadFile;
import sr.data.CharacterRec;
import sr.data.RaceRec;
import sr.util.ChummerImport;
import sr.web.App;

/**
 * Controls similarly named jsp file
 */
public class CharacterListPage extends AppBasePage {

	public List<CharacterRec> characters;
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		characters = CharacterRec.selectForUser(db, currentUser.Row);
	}
    @Override
    protected boolean isMultiPart()
    {
       return true;
    }
	@Override
	protected void read() throws Exception {
		if(hasParm("submitImport"))
		{
			if(currentUser.isGuest())
			{
				setError("Not for you chummer!");
				return;
			}
			if (uploadFiles.size()==0)
	        {    
	            setError("Import file not detected.");
	        }
			else
			{
				UploadFile uf = uploadFiles.get(0);
				String fileName = FileKit.cleanFileName(uf.remoteName);
	            StringBuilder contentBuilder = new StringBuilder();
	            try (BufferedReader br = new BufferedReader(new FileReader(uf.localName)))
	            {
	                String sCurrentLine;
	                while ((sCurrentLine = br.readLine()) != null)
	                {
	                    contentBuilder.append(sCurrentLine);
	                }
	            }
	            catch (IOException e)
	            {
	                e.printStackTrace();
	            }
	            String content = contentBuilder.toString();
	            JsonObj json =  new JsonObj(content);
	            ChummerImport ci = new ChummerImport(db,currentUser,json.getJsonObject());
	            ci.insert();
	            if(ci.hasNoMatch())
	            {
		            setStatusRaw("Some values could not be imported. Check Settings Tab - Import Notes for details.");
	            }
	            if(ci.hasError())
	            {
	            	StringKit.println("Chummer Import Error - file {0} had errors.", fileName);
	            }
	            File destination = new File(App.getRealImportDir() + File.separator + currentUser.Row + "_" + fileName);
	            if(destination.exists())
	            {
	            	destination = new File(App.getRealImportDir() + File.separator + currentUser.Row + "_" + JDateTime.now().getTime() + "_" +  fileName);
	            }
	            FileKit.move(uf.file, destination);
	            setRedirect(Page.CharacterDetail,"ctlRow",ci.c.Row);
	            return;
			}
		}
	}

	@Override
	protected void write() throws Exception {
		set("Characters",characters.toString());
		set("Metatypes",RaceRec.selectAll(db).toString());
		set("Sex",CharacterRec.CharacterSex.selectJson());
		set("Types",CharacterRec.CharacterType.selectJson());
		set("Buttons",eventButton("btnAdd","Add","view.addCharacter()",currentUser.isGuest()?" class='parentBtn hover' disabled data-hover='Disabled for guest accounts'" : "class='parentBtn hover' data-hover='Add new runner'")
				+ eventButton("btnImport","Import","view.addImport()",currentUser.isGuest()?"disabled class='parentBtn hover' data-hover='Disabled for guest accounts'" : "class='parentBtn hover' data-hover='Import from Chummer5 json'"));
	}
}
