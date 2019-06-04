package ir.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Random;
import sr.web.App;

public abstract class FileKit
{
private static final int BlockSize = 16384;
public static String addSuffix(String fn, String sfx)
{
    int dotAt = fn.lastIndexOf('.');
    if (dotAt == -1)
    {
        return fn + sfx;
    }
    return fn.substring(0, dotAt) + sfx + fn.substring(dotAt);
}
/**
 * Replaces any extension on passed name, INCLUDING the dot, so you can change
 * nnnnn.xxx to nnnnn_timestamp.xxx or the like. If the passed name does not
 * contain a dot it will return name.newExt
 */
public static String changeExtension(String n, String newExt)
{
    int iDot = n.lastIndexOf('.');
    if (iDot > 0)
    {
        n = n.substring(0, iDot);
        if (newExt.length() > 0 && newExt.indexOf('.') == -1)
        {
            n += '.';
        }
    }
    else if (iDot < 0)
    {
        n += '.';
    }
    return n + newExt;
}
public static String cleanFileName(String v)
{
    if (v == null || v.equals(""))
    {
        return "";
    }
    v = v.replace("\n", "").replace("\r", "");
    StringBuilder b = new StringBuilder();
    char[] ca = v.toCharArray();
    for (char c : ca)
    {
        if (Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == '_' || c == ' ')
        {
            b.append(c);
        }
    }
    return b.toString();
}
public static void copy(File from, File to) throws Exception
{
    byte[] ba = new byte[BlockSize];
    try (InputStream is = new FileInputStream(from); OutputStream os = new FileOutputStream(to))
    {
        int read = 0;
        while (0 < (read = is.read(ba)))
        {
            os.write(ba, 0, read);
        }
    }
}
public static File createTemp(String prefix, File folder) throws Exception
{
    return createTemp(prefix, ".tmp", folder);
}
public static File createTemp(String prefix, String extension, File folder) throws Exception
{
    File file;
    String xt = cleanFileName(extension);
    if (!xt.startsWith("."))
    {
        xt = "." + xt;
    }
    if (folder == null)
    {
        File dir = new File(App.getRealUploadDir());
        file = File.createTempFile(StringKit.lpad(prefix, 3, '0') + getRandomInt(), xt, dir);
    }
    else
    {
        file = File.createTempFile(StringKit.lpad(prefix, 3, '0') + getRandomInt(), xt, folder);
    }
    return file;
}
public static File ensureFolder(String name)
{
    File f = new File(name);
    if (f.exists())
    {
        if (!f.isDirectory())
        {
            f.delete();
            f.mkdir();
        }
    }
    else
    {
        f.mkdir();
    }
    return f;
}
/**
 * Returns the file extension NOT including the dot. Returns empty string if
 * filename contains no dot. Returns empty string if filename is empty
 */
public static String extension(String fileName)
{
    if (fileName == null || fileName.isEmpty())
    {
        return "";
    }
    int iDot = fileName.lastIndexOf('.');
    if (iDot < 0)
    {
        return "";
    }
    return fileName.substring(iDot + 1);
}
private static int getRandomInt()
{
    Random ranGen = new SecureRandom();
    int randomInt = ranGen.nextInt(400000000);
    return randomInt;
}
public static void move(File f, File t)
{
    if (!f.equals(t))
    {
        if (t.exists())
        {
            t.delete();
        }
        f.renameTo(t);
    }
}
public static void move(File from, String toName) throws Exception
{
    File t = new File(cleanFileName(toName));
    move(from, t);
}
public static String name(String fullName)
{
    String n = fullName.replace('\\', '/').replace("\n\r", "");
    int lastSlash = n.lastIndexOf('/');
    return lastSlash == -1 ? n : n.substring(lastSlash + 1);
}
public static String nameWithoutExtension(String fn)
{
    String n = name(fn);
    int iDot = n.lastIndexOf('.');
    if (iDot < 0)
    {
        return n;
    }
    return n.substring(0, iDot);
}
public static byte[] readToBytes(File f) throws Exception
{
    if (f.length() > Integer.MAX_VALUE)
    {
        throw new Exception("File " + f.getAbsolutePath() + " is too long to read all at once.");
    }
    byte[] ba = new byte[(int) f.length()];
    ;
    try (FileInputStream fis = new FileInputStream(f))
    {
        fis.read(ba);
        return ba;
    }
}
public static String readToString(Class<?> c, String resourceName) throws Exception
{
    java.net.URL u = c.getResource(resourceName);
    if (u == null)
    {
        throw new Exception("Resource name " + resourceName + " not found.");
    }
    return readToString(new File(u.getFile()));
}
public static String readToString(File f) throws Exception
{
    if (f.length() > Integer.MAX_VALUE)
    {
        throw new Exception("File " + f.getAbsolutePath() + " is too long to read all at once.");
    }
    char[] ca = new char[(int) f.length()];
    try (InputStreamReader fr = new InputStreamReader(new FileInputStream(f), "UTF8"))
    {
        fr.read(ca);
        return new String(ca).trim();
    }
}
public static String sizeText(File f)
{
    return sizeText(f.length());
}
public static String sizeText(long v)
{
    long k = v / 1024;
    long m = k / 1024;
    long g = m / 1024;
    return g == 0 ? m == 0 ? k == 0 ? v + "b" : k + "Kb" : m + "Mb" : g + "Gb";
}
/*Shocking but this is never used in TROMIS
public static void write(File file, byte[] data) throws Exception
{
    try (OutputStream fr = new FileOutputStream(file))
    {
        fr.write(data);
    }
}
public static void write(File file, String data) throws Exception
{
    write(file, data.getBytes());
}
public static void write(String fileName, byte[] data) throws Exception
{
    write(new File(fileName), data);
}
public static void write(String fileName, String data) throws Exception
{
    write(fileName, data.getBytes());
}
*/
}
