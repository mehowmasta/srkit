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
public class MapListPage extends AppBasePage {

	public List<ImageRec> maps;
	public ImageRec image = new ImageRec();
	@Override
	public boolean hasSearch()
	{
		return true;
	}
	@Override
	protected void init() throws Exception {
		maps = ImageRec.selectForUser(db,currentUser.Row,ImageType.Map,true);
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
            image.Name = readString("mapName",100);   
            if(image.Name.isEmpty())
            {
            	setError("File not uploaded, name is required.");
                return;
            }
            image.Type = ImageType.Map;
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
            setStatus("Map {0} added.",image.Name);
            maps.add(image);
        }
    }
	@Override
	protected void write() throws Exception {
		set("Maps",maps.toString());
		set("Buttons",eventButton("btnAdd","Add","view.addMap()",currentUser.isGuest()?"disabled class='parentBtn'" : "class='parentBtn'"));
	}
}
