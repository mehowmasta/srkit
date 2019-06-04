package ir.web;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ir.data.IValidator;
import ir.util.FileKit;
/**
 * UploadReader wraps the Apache Jakarta commons file uploader.
 */
public class UploadReader
{
private static final int storeToDiskThreshold=4000;
public final static Set<String> extensionWhitelist = new HashSet<String>(Arrays.asList(new String[] {
        "bmp","doc","docx","esx","gif","jpeg","jpg","json","pdf","png","txt","xls","xlsx","zip"}));
//    
private static List<FileItem> readMultipart(HttpServletRequest req, String tempDir, int maxSizeBytes) throws Exception
{
  FileItemFactory fileItemFactory = new DiskFileItemFactory(storeToDiskThreshold,new File(tempDir));  
  ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
  servletFileUpload.setFileSizeMax(maxSizeBytes);
  servletFileUpload.setSizeMax(maxSizeBytes);
  servletFileUpload.setHeaderEncoding("UTF-8");
  Thread.currentThread().setName("UploadReader.readMultipart");
  return servletFileUpload.parseRequest(req);
}
public List<UploadFile> read(IValidator iv,HttpServletRequest req, String tempDir,int maxSizeBytes) throws Exception
{
    List<FileItem> fileItems = readMultipart(req, tempDir, maxSizeBytes);
    File tempFolder=new File(tempDir);
    List<UploadFile> a = new ArrayList<UploadFile>();
    for (FileItem fileItem : fileItems)
    {
        if (fileItem.isFormField())
        {
            String raw = fileItem.getString();
            String value = new String(raw.getBytes("ISO-8859-1"), "UTF-8");
            String name = fileItem.getFieldName();
            req.setAttribute(name,value);
        }
        else
        {
            if (! fileItem.getName().equals(""))
            {
		        String fileName = fileItem.getName();
                if (! fileName.equals(FileKit.cleanFileName(fileName)))
                {
                    throw new Exception("Upload file '" + fileName + "' not allowed - can only include letters, digits, dashes-,underscores_,spaces, dots.");
                }
                String nameWithoutExtension = FileKit.nameWithoutExtension(fileName);
                if (nameWithoutExtension.equals(""))
                {
                    throw new Exception("Upload file " + fileName + " not allowed - all uploaded files must have name.extension form.");
                }
                String extension = FileKit.extension(fileName);
                if (extension.equals(""))
                {
                    throw new Exception("Upload file " + fileName + " not allowed - all uploaded files must have an extension.");
                }
                else if (! extensionWhitelist.contains(extension.toLowerCase()))
                {
                    throw new Exception("Upload file " + fileName + " not allowed - extension ." + extension + " not allowed.");
                }
		        File uploadedFile = FileKit.createTemp("up_",tempFolder);
		        uploadedFile.delete();
		        fileItem.write(uploadedFile);
		        if (! uploadedFile.exists())
		        {//on a windows box - anti-virus might refuse to create file
                    throw new Exception("Upload file " + fileName + " not allowed - possible virus infection.");
		        }
		        a.add(new UploadFile(uploadedFile,fileItem.getFieldName(),fileItem.getName()));
            }
        }
    }
    return a;
}
}