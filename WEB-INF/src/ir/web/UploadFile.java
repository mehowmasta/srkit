package ir.web;

import ir.util.FileKit;
import ir.util.JImage;
import ir.util.StringKit;
import java.io.File;

/**
 *
 */
public class UploadFile
{
  public final String controlName;
  public final String extension;
  public File file;
  public String localName;
  public String remoteName;
  //
  public UploadFile(File f, String controlName, String remoteName)
  {
    file = f;
    this.localName = f.getAbsolutePath();
    this.controlName = controlName;
    this.remoteName = remoteName;
    extension = StringKit.getExtension(localName).toLowerCase();
  }
  //
  public boolean isImage()
  {
    return file.exists() && file.length() > 0 && JImage.isImageExtension(FileKit.extension(remoteName));
  }
  //
  @Override
  public String toString()
  {
    return "{c:'" + controlName + "',r:'" + remoteName + "',l:'" + localName + "'}";
  }
}