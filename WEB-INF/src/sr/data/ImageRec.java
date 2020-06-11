package sr.data;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.imageio.ImageIO;

import ir.data.Database;
import ir.data.NameRow;
import ir.util.JDateTime;
import ir.util.StringKit;
import sr.web.App;

public class ImageRec extends AppRec{
	public enum ImageType{
		Map,Face;
	}
	public final static int MaxDim =120;
	public final static int BitmapPrefixSize= 78;
	public final static String IMAGE= "image";
	public final static String THUMB= "thumb";
	
	public static String ASPECTRATIO = "AspectRatio";
	public static String DATA = "Data";
	public static String DIVISION = "Division";
	public static String EXTENSION = "Extension";
	public static String INACTIVE = "Inactive";
	public static String ISOMETRIC = "Isometric";
	public static String NAME = "Name";
	public static String ROW = "Row";
	public static String TABLE = "tImage";
	public static String TYPE = "Type";
	public static String USER = "User";
	public static String ZOOM = "Zoom";
	//
	public float AspectRatio = 1.0f;
	public String Data = "";
	public int Division = 16;
	public String Extension = "";
	public boolean Inactive = false;
	public boolean Isometric = false;
	public String Name = "";
	public int Row = 0;
	public ImageType Type = ImageType.Map;
	public int User = 0;
	public float Zoom = 1.0f;
	//
	public static void deleteAllMapData(Database db,int mapRow) throws Exception
	{
		db.execute("MapDataRec.deleteAllMapData", mapRow);
		return;
	}
	public static List<NameRow> selectFaceRows(Database db, int user,boolean showInactive) throws Exception
	{
		return db.selectList("ImageRec.selectMapRows", NameRow.class,user,ImageType.Face.name(),showInactive?2:1);
	}
	public static List<ImageRec> selectForUser(Database db,int user,ImageType type,boolean showInactive) throws Exception
	{
		return db.selectList("ImageRec.selectForUser",ImageRec.class,user,type.name(),showInactive?2:1);
	}
	public static ImageRec selectImage(Database db,int user, int row) throws Exception
	{
		ImageRec c = new ImageRec();
		db.selectFirst("ImageRec.selectImage",c, user,row);
		return c;
	}
	public static List<NameRow> selectMapRows(Database db, int user,boolean showInactive) throws Exception
	{
		return db.selectList("ImageRec.selectMapRows", NameRow.class,user,ImageType.Map.name(),showInactive?2:1);
	}
	public static boolean isDupName(Database db,int currentRow,String name) throws Exception
	{
		return 0 < db.selectScalar("ImageRec.isDupName", -1, currentRow,name);
	}
	@Override
    public void afterDelete(Database db) throws Exception
    {
		try {
			File thumb = new File(getFilePath(IMAGE));
			File image = new File(getFilePath(IMAGE));
			if(thumb!=null)
			{
				thumb.delete();
			}
			if(image!=null)
			{
				image.delete();
			}
		}
		catch(Exception e)
		{
			//w/e
		}
		if(Type.equals(ImageType.Map))
		{
			deleteAllMapData(db,Row);
		}
    }
	@Override
	public void beforeInsert(Database db) throws Exception {
	}
	@Override
	public void beforeUpdate(Database db) throws Exception {
	}
	public String getFileName(String prefix)
	{
	    return prefix + "_" + User + "_" + Row + "." + Extension;
	}
	public String getFilePath(String prefix) throws Exception
	{
		String path = App.getRealImageDir() + "/" + Type.name();
		File dir = new File(path);        
        // if the directory does not exist, create it
        if (!dir.exists()) {
            System.out.println("creating directory: " + dir.getName());
            try{
                dir.mkdir();
            } 
            catch(SecurityException se){
                //handle it
            }  
        }
	    return path + "/" + getFileName(prefix);
	}
	public String getRelativeImage()
	{
		return getRelativePath(IMAGE);
	}
	public String getRelativePath(String prefix) {
		return "images/" + Type.name() + "/" + getFileName(prefix);
	}
	public String getRelativeThumb()
	{
		return getRelativePath(THUMB);
	}
	@Override
	public String getTable() {
		return TABLE;
	}
	public boolean isDupName(Database db) throws Exception
	{
		return isDupName(db,Row,Name);
	}
	public void write(Database db,File f) throws Exception
	{
	    BufferedImage bi=null;
	    FileInputStream fi = new FileInputStream(f);
	    byte[] imageBytes = new byte[fi.available()];
	    fi.read(imageBytes);
	    fi.close();
	    try
	    {
	        ByteArrayInputStream is= new ByteArrayInputStream(imageBytes);
	        ImageIO.setUseCache(false); 
	        bi = ImageIO.read(is);
	    }  
	    catch (Exception e)
	    {
	        if (f.getName().toLowerCase().endsWith(".bmp"))
	        {
	            ByteArrayOutputStream os= new ByteArrayOutputStream(imageBytes.length - BitmapPrefixSize);
	            os.write(imageBytes, BitmapPrefixSize, imageBytes.length - BitmapPrefixSize);
	            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
	            ImageIO.setUseCache(false); 
	            bi = ImageIO.read(is);
	        }
	        else
	        {
	            throw e;
	        }
	    }
	    double arWas = AspectRatio;
	    boolean nu = Row==0;
	    AspectRatio = bi.getWidth() / (float) bi.getHeight();
	    int colSize = db.getColSize(TABLE,NAME);
	    Name = StringKit.left(Name,colSize);
	    if (nu)
	    {
	        if (isDupName(db))
	        {
	            String ts = JDateTime.now().toCompactString();
	            Name = StringKit.left(Name,colSize - ts.length()) + ts;
	        }
	        db.insert(this);
	    }

	    ImageIO.write(bi,Extension,new File(getFilePath(IMAGE)));
	    //resize image for thumbnail
        double scaleFactor = Math.max(bi.getWidth(),bi.getHeight()) / 1.0 / MaxDim;
        int w = (int)Math.round(bi.getWidth()/scaleFactor);
        int h = (int)Math.round(bi.getHeight()/scaleFactor);
        AffineTransform at = AffineTransform.getScaleInstance((double)w/bi.getWidth(),(double)h/bi.getHeight());    
        AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bi = op.filter(bi, null);
        ImageIO.write(bi,Extension,new File(getFilePath(THUMB)));
	    if (! nu && arWas != AspectRatio)
	    {//
	        db.update(this);
	    }
	}
}