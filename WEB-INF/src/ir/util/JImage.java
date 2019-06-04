package ir.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

public class JImage implements Serializable
{
  public final static int BitmapPrefixSize = 78;
  public final static Set<String> imageExtensions = new HashSet<String>(
      Arrays.asList(new String[] { "bmp", "jpg", "jpeg", "gif", "png" }));
  private static final long serialVersionUID = 1L;
  protected byte[] _data = null;
  protected BufferedImage _img = null;
  public static String getExtensionString()
  {
    return StringKit.join(",", new ArrayList<String>(imageExtensions));
  }
  //
  public static boolean isImageExtension(String ext)
  {
    return ext != null && imageExtensions.contains(ext.toLowerCase());
  }
  public static boolean isImageFileName(String fn)
  {
    return isImageExtension(StringKit.getExtension(fn));
  }
  /**
   * Scales the image to
   */
  public static BufferedImage scale(BufferedImage bi, double w, double h) throws Exception
  {
    AffineTransform at = AffineTransform.getScaleInstance(w / bi.getWidth(), h / bi.getHeight());
    AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    return op.filter(bi, null);
  }
  //
  public JImage()
  {
  }
  public JImage(BufferedImage bi)
  {
    _img = bi;
  }
  public JImage(byte[] data)
  {
    _data = data;
  }
  public JImage(File f) throws Exception
  {
    _data = FileKit.readToBytes(f);
    ImageIO.setUseCache(false);
    _img = ImageIO.read(new ByteArrayInputStream(_data));
    //
    if (_img.getColorModel().getTransparency() != Transparency.OPAQUE)
    {// replace transparent pixels with white
      int w = _img.getWidth();
      int h = _img.getHeight();
      Graphics2D g = null;
      try
      {
        BufferedImage image2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        g = image2.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.drawRenderedImage(_img, null);
        g.dispose();
        g = null;
        _img = image2;
      }
      catch (Exception ee)
      {
        StringKit.println("Transparency replacement failed for {0}:\n{1}", f.getAbsolutePath(), ee.getMessage());
      }
      if (g != null)
      {
        try
        {
          g.dispose();
        }
        catch (Exception giveUp)
        {
        }
      }
    }
  }
  public File flipHorizontal() throws Exception
  {
    JImage flip = rotate180();
    File ft = File.createTempFile("flip_" + JDateTime.now().toCompactString(), ".jpg");
    ImageIO.write(flip.getBufferedImage(), "jpeg", ft);
    return ft;
  }
  public float getAspectRatio() throws Exception
  {
    return ((float) getWidth()) / ((float) getHeight());
  }
  public BufferedImage getBufferedImage() throws Exception
  {
    if (_img == null)
    {
      ByteArrayInputStream is = null;
      if (isBitmap())
      {
        is = new ByteArrayInputStream(_data, BitmapPrefixSize, _data.length - BitmapPrefixSize);
      }
      else
      {
        is = new ByteArrayInputStream(_data);
      }
      ImageIO.setUseCache(false);
      _img = ImageIO.read(is);
    }
    return _img;
  }
  public byte[] getData()
  {
    if (_data == null)
    {
      return null;
    }
    byte[] to = new byte[_data.length];
    System.arraycopy(_data, 0, to, 0, _data.length);
    return to;
  }
  public int getHeight() throws Exception
  {
    return getBufferedImage().getHeight();
  }
  public int getWidth() throws Exception
  {
    return getBufferedImage().getWidth();
  }
  public boolean isBitmap() throws Exception
  {
    if (_data == null)
    {
      return false;
    }
    StringBuilder sb = new StringBuilder("");
    for (int i = 0; i < BitmapPrefixSize && i < _data.length; i++)
    {
      sb.append((char) _data[i]);
    }
    return sb.length() == BitmapPrefixSize && sb.indexOf("Bitmap Image") > 0;
  }
  public boolean isNull()
  {
    return _data == null || _data.length == 0;
  }
  public JImage rotate180() throws Exception
  {
    BufferedImage sourceImage = _img == null ? getBufferedImage() : _img;
    //
    int w = sourceImage.getWidth();
    int h = sourceImage.getHeight();
    BufferedImage targetImage = new BufferedImage(w, h, sourceImage.getType());
    Graphics2D targetGraphic = targetImage.createGraphics();
    try
    {
      targetGraphic.drawImage(sourceImage, 0, 0, w, h, w, 0, 0, h, null);
      return new JImage(targetImage);
    }
    finally
    {
      targetGraphic.dispose();
    }
  }
  public JImage rotateLeft90() throws Exception
  {
    BufferedImage inputImage = _img == null ? getBufferedImage() : _img;
    int width = inputImage.getWidth();
    int height = inputImage.getHeight();
    BufferedImage returnImage = new BufferedImage(height, width, inputImage.getType());
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        returnImage.setRGB(y, width - x - 1, inputImage.getRGB(x, y));
      }
    }
    return new JImage(returnImage);
  }
  public JImage rotateRight90() throws Exception
  {
    BufferedImage inputImage = _img == null ? getBufferedImage() : _img;
    int width = inputImage.getWidth();
    int height = inputImage.getHeight();
    BufferedImage returnImage = new BufferedImage(height, width, inputImage.getType());
    for (int x = 0; x < width; x++)
    {
      for (int y = 0; y < height; y++)
      {
        returnImage.setRGB(height - y - 1, x, inputImage.getRGB(x, y));
      }
    }
    return new JImage(returnImage);
  }
  /**
   * Scales the image to
   */
  public void scaleNoGreater(int maxDim) throws Exception
  {
    _img = getBufferedImage();
    if (_img.getHeight() > maxDim || _img.getWidth() > maxDim)
    {
      double scaleFactor = Math.max(_img.getWidth(), _img.getHeight()) / 1.0 / maxDim;
      int w = (int) Math.round(_img.getWidth() / scaleFactor);
      int h = (int) Math.round(_img.getHeight() / scaleFactor);
      _img = scale(_img, w, h);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ImageIO.write(_img, "jpg", bos);
      _data = bos.toByteArray();
    }
  }
  public void setData(byte[] data)
  {
    _data = data;
    _img = null;
  }

  public void write(File f) throws Exception
  {
    if (f.exists())
    {
      f.delete();
    }
    String extension = StringKit.getExtension(f.getName()).toLowerCase();
    if (extension.equals("jpg"))
    {
      extension = "jpeg";
    }
    ImageIO.write(getBufferedImage(), extension, f);
  }
  public void write(String fileName) throws Exception
  {
    write(new File(fileName));
  }
}
