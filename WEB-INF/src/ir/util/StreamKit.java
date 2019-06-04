package ir.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamKit {

  public static void copy(InputStream is,OutputStream os) throws Exception
  {
    byte[] ba = new byte[4096];
    int bytesRead=0;
    while (0 < (bytesRead = is.read(ba)))
    {
      os.write(ba,0,bytesRead);
    }
    is.close();
    os.close();
  }
  public static byte[] toByteArray(InputStream inStr) throws Exception
  {
    try
    {
      ByteArrayOutputStream bb = new ByteArrayOutputStream();
      final int blockSize=8000;
      int bytesRead=0;
      byte[] ba = new byte[blockSize];
      while (-1 < (bytesRead=inStr.read(ba)))
      {
        bb.write(ba,0,bytesRead);
      }
      return bb.toByteArray();
    }
    finally
    {
      try{inStr.close();}catch(Exception ignore){}
    }
  }
  public static String toString(InputStream inStr) throws Exception
  {
    return new String(toByteArray(inStr));
  }
}