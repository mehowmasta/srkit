package ir.util;

import ir.data.IRow;
import java.util.ArrayList;
import java.util.List;

public class XmlBuilder
{
    public static final String FileHeader = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
    private StringBuilder _data = new StringBuilder("");
    private List<String> _openTags = new ArrayList<String>();
    //
    public XmlBuilder(String topTag)
    {
        startTag(topTag);
    }
    //
    public XmlBuilder(String topTag, boolean bFormat)
    {
        startTag(topTag);
    }
    private void addAttributes(List<String> attrNames, List<Object> attrVals)
    {
        if (attrNames != null && attrNames.size() > 0)
        {
            for (int i = 0; i < attrNames.size(); i++)
            {
                _data.append(" ").append(attrNames.get(i)).append("=\"");
                if (i < attrVals.size() && attrVals.get(i) != null)
                {
                    _data.append(attrVals.get(i));
                }
                _data.append("\"");
            }
        }
    }
    public void addRow(IRow r) throws Exception
    {
        startRow(r);
        endTag();
    }
    public XmlBuilder append(String v)
    {
        _data.append(v);
        return this;
    }
    public void cdataNode(String tagName, Object value)
    {
        cdataNode(tagName, value, null, null);
    }
    public void cdataNode(String tagName, Object value, List<String> attrNames, List<Object> attrVals)
    {
        textNode(tagName, "<![CDATA[" + xstr(value) + "]]>", attrNames, attrVals);
    }
    public void endAllTags()
    {
        while (_openTags.size() > 0)
        {
            _data.append("</").append(_openTags.remove(_openTags.size() - 1)).append(">\n");
        }
    }
    public void endTag()
    {
        String tag = _openTags.remove(_openTags.size() - 1);
        if (_data.length() > 0 && _data.charAt(_data.length() - 1) == '\n')
        {
            for (int i = 0; i < _openTags.size(); i++)
                _data.append("\t");
        }
        _data.append("</").append(tag).append(">\n");
    }
    public void startRow(IRow r) throws Exception
    {
        startTag(r.getClass().getName());
        String[] sa = r.getFields();
        for (int i = 0; i < sa.length; i++)
        {
            Object v = r.getValue(i);
            if (v instanceof String)
                cdataNode(sa[i], v);
            else
                textNode(sa[i], v);
        }
    }
    public void startTag(String tagName)
    {
        startTag(tagName, null, null);
    }
    public void startTag(String tagName, List<String> attrNames, List<Object> attrVals)
    {
        if (_data.length() > 0 && _data.charAt(_data.length() - 1) != '\n')
            _data.append("\n");
        for (int i = 0; i < _openTags.size(); i++)
            _data.append("\t");
        _openTags.add(tagName);
        _data.append("<").append(tagName);
        addAttributes(attrNames, attrVals);
        _data.append(">");
    }
    public void textNode(String tagName, Object value)
    {
        textNode(tagName, value, null, null);
    }
    public void textNode(String tagName, Object value, List<String> attrNames, List<Object> attrVals)
    {
        startTag(tagName, attrNames, attrVals);
        _data.append(xstr(value));
        endTag();
    }
    public String toString()
    {
        if (_data.indexOf("<xml") == -1)
        {
            _data.insert(0, FileHeader);
        }
        return _data.toString();
    }
    public String toString(String islandName)
    {
        _data.insert(0, "\n<xml id='" + islandName + "' style='display:none;'>\n");
        _data.append("\n</xml>\n");
        return _data.toString();
    }
    private String xstr(Object v)
    {
        if (v == null)
            return "";
        if (v instanceof Boolean)
            return ((boolean) (Boolean) v) ? "1" : "0";
        return v.toString();
    }
}
