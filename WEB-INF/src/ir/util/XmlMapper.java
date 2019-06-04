/*
 * Created on Oct 12, 2005 by zjcl153
 */
package ir.util;

import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
/**
 * XmlMapper is built to read xml text node values into a Map. This class was
 * originally built as a means to externalize sql statements. The Map's keys
 * will be built as dot-delimited paths not including the top level node name,
 * such as secondLevelNode.thirdLevelNode... For example:
 * <p>
 * &lt?xml version="1.0" encoding="UTF-8"?&gt <br>
 * &lt;sqlbase&gt; <br>
 * &lt;Chickens&gt; <br>
 * &lt;CountAll&gt; <br>
 * select count(*) from theHatchery <br>
 * &lt;/CountAll&gt; <br>
 * &lt;CountForBarn&gt; <br>
 * select count(*) from theHatchery where barn=? <br>
 * &lt;/CountForBarn&gt; <br>
 * &lt;/Chickens&gt; <br>
 * &lt;/sqlbase&gt;
 * <p>
 * ..would result in the following key-value pairs:
 * <ul>
 * <li>Chickens.CountAll = select count(*) from theHatchery
 * <li>Chickens.CountForBarn = select count(*) from theHatchery where barn=?
 * </ul>
 * One assumption of this class is that any full path will be unique. If the
 * input xml file has any duplicate paths, only the last one for a given key
 * will be in the generated map.
 */
public class XmlMapper
{
    public static Map<String, String> getMap(InputStream strm) throws Exception
    {
        XmlMapper inst = new XmlMapper();
        return inst.get(strm);
    }
    public static Map<String, String> getMap(URL xmlResourceURL) throws Exception
    {
        XmlMapper inst = new XmlMapper();
        return inst.get(xmlResourceURL);
    }
    private void explode(Map<String, String> map, Node no, String prefix)
    {
        if (no.hasChildNodes())
        {
            if (prefix.length() > 0)
            {
                prefix += ".";
            }
            prefix += no.getNodeName();
            NodeList nl = no.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++)
            {
                Node kid = nl.item(i);
                explode(map, kid, prefix);
            }
        }
        else if (no.getNodeType() == Node.TEXT_NODE || no.getNodeType() == Node.CDATA_SECTION_NODE)
        {
            String nv = no.getNodeValue();
            if (nv != null)
            {
                nv = nv.trim();
            }
            if (nv != null && nv.length() > 0)
            {
                map.put(prefix, nv);
            }
        }
        else
        {
            System.out.println(prefix + " : t=" + no.getNodeType());
        }
    }

    private Map<String, String> get(InputStream strm) throws Exception
    {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        db.setEntityResolver(new NullResolver());
        Document doc = db.parse(strm);
        Map<String, String> m = new HashMap<String, String>();
        Node no = doc.getFirstChild();
        NodeList nl = no.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++)
        {
            no = nl.item(i);
            explode(m, no, "");
        }
        return m;
    }
    /**
     * Returns the Map generated from the named file
     */
    private Map<String, String> get(URL xmlResourceURL) throws Exception
    {
        return get(xmlResourceURL.openStream());
    }
    class NullInputSource extends InputSource
    {
        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.InputSource#getByteStream()
         */
        public InputStream getByteStream()
        {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.InputSource#getCharacterStream()
         */
        public Reader getCharacterStream()
        {
            return null;
        }

    }
    class NullResolver implements EntityResolver
    {
        public InputSource resolveEntity(String publicId, String systemId)
        {
            return new NullInputSource();
        }
    }

    //
}
