package sr.web.page;

import java.io.File;
import java.util.List;

import ir.util.FileKit;
import ir.util.JDateTime;
import ir.util.JImage;
import ir.util.StringKit;
import ir.web.UploadFile;
import sr.data.ImageRec;
import sr.data.ImageRec.ImageType;

/**
 * Controls similarly named jsp file
 */
public class PortraitListPage extends AppBasePage {

	public static final String ADDPORTRAITBUTTON = "btnAddPortrait";
	public List<ImageRec> portraits;
	public ImageRec image = new ImageRec();
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		portraits = ImageRec.selectForUser(db,currentUser.Row,ImageType.Face,false);
	}

    @Override
    protected boolean isMultiPart()
    {
       return true;
    }
	@Override
	protected void read() throws Exception {
		if (uploadFiles.size()==0)
        {    
            setError("No file uploaded.");
        }
        else
        {
            UploadFile uf = uploadFiles.get(0);
            File f = new File(uf.localName);
            if (f.length() == 0)
            {
                setError("File uploaded is empty.");
                return;
            }
            image.Name = readString("portraitName",100);   
            if(image.Name.isEmpty())
            {
            	setError("File not uploaded, name is required.");
                return;
            }
            image.Type = ImageType.Face;
            image.User = currentUser.Row;
            image.Extension = FileKit.extension(uf.remoteName);
            if (image.isDupName(db))
            {
                image.Name += JDateTime.now().toCompactString();       
            }
            if (!uf.isImage())
            {
                setError("Uploads must have extension {0}.",StringKit.join(",",JImage.imageExtensions));
            }
        }
	}
	@Override
    protected void update() throws Exception
    {
        if (uploadFiles.size()>0)
        {
            image.write(db,new File(uploadFiles.get(0).localName));
            setStatus("Portrait {0} added.",image.Name);
            portraits.add(image);
        }
    }
	@Override
	protected void write() throws Exception {
		set("Portraits",portraits.toString());
		set("Buttons",eventButton("btnAdd","Add","view.addMap()",currentUser.isGuest()?"disabled class='parentBtn'" : "class='parentBtn'"));
		set("AddPortrait",eventButton(ADDPORTRAITBUTTON,"Add","view.addPortrait()",currentUser.isGuest()?"disabled class='parentBtn'" : "class='parentBtn'"));
	}
}
