package ir.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public abstract class ZipKit
{
    /**
     * Extracts zip entry to file.
     */
    public static void toFile(ZipFile zf, ZipEntry ze, File tf) throws Exception
    {
        FileOutputStream os = null;
        InputStream is = null;
        try
        {
            os = new FileOutputStream(tf);
            is = zf.getInputStream(ze);
            byte[] block = new byte[8192];
            int bytesRead;
            while (0 < (bytesRead = is.read(block)))
                os.write(block, 0, bytesRead);
            is.close();
            os.close();
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (Exception ee)
                {
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (Exception ee)
                {
                }
            }
        }
    }
}
