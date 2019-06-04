package ir.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileKitFilter implements FilenameFilter
{
    private boolean caseSensitiveNames = false;
    private final String[] suffixes;
    public FileKitFilter(boolean caseSensitive, String[] suffixes)
    {
        this(suffixes);
        this.caseSensitiveNames = caseSensitive;
    }
    public FileKitFilter(String[] suffixes)
    {
        if (caseSensitiveNames)
        {
            this.suffixes = suffixes;
        }
        else
        {
            this.suffixes = StringKit.toLower(suffixes);
        }
    }
    @Override
    public boolean accept(File dir, String fileName)
    {
        if (suffixes == null)
        {
            return true;
        }
        String fileNameTester = fileName;
        if (!caseSensitiveNames)
        {
            fileNameTester = fileName.toLowerCase();
        }
        for (String suffix : suffixes)
        {
            if (suffix != null && suffix.length() > 0)
            {
                if (fileNameTester.endsWith(suffix))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
