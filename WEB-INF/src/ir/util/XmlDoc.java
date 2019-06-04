package ir.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
/**
 * XmlDoc provides a non-validating non-resolving DOM document for xml.
 */
public class XmlDoc
{
    class NullErrorHandler implements ErrorHandler
    {
        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
         */
        public void error(SAXParseException arg0) 
        {
        }
        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
         */
        public void fatalError(SAXParseException arg0) 
        {
        }
        /* (non-Javadoc)
         * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
         */
        public void warning(SAXParseException arg0) 
        {
        }
    }
    class NullInputSource extends InputSource
    {
        /* (non-Javadoc)
         * @see org.xml.sax.InputSource#getByteStream()
         */
        public InputStream getByteStream()
        {
            return null;
        }
        /* (non-Javadoc)
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
    public static Document create() throws Exception
    {
        return javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .newDocument();
    }
    public static Document create(File fil, boolean validate) throws Exception
    {
        XmlDoc inst = new XmlDoc();
        return inst.getDoc(fil, validate);
    }
    public static Document create(String xml) throws Exception
    {
        return create(xml,false);
    }
    public static Document create(String xml, boolean validate) throws Exception
    {
        XmlDoc inst = new XmlDoc();
        return inst.getDoc(xml, validate);
    }
    /**
     * Returns the document passed as a string.
     */
    public static String toString(Document doc, boolean outputDocType) throws Exception
    {
        if (doc==null)
            return "[null]";
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        if (outputDocType)
        {
            DocumentType type = doc.getDoctype();
            if (type != null)
            {
                String publicId = type.getPublicId();
                if (publicId != null)
                {
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicId);
                }
                String systemId = type.getSystemId();
                if (systemId != null)
                {
                    transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemId);
                }
            }
        }
        transformer.setOutputProperty("indent", "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * Returns the node passed as a string.
     */
    public static String toString(Node nod) throws Exception
    {
        if (nod==null)
            return "[null]";
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("indent", "yes");
        transformer.transform(new DOMSource(nod), new StreamResult(writer));
        return writer.toString();
    }

    /**
     * Returns the Document created from the named file
     */
    private Document getDoc(File f, boolean validate) throws Exception
    {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (!validate)
        {
            db.setEntityResolver(new NullResolver());
            db.setErrorHandler(new NullErrorHandler());
        }
        return db.parse(f);
    }

    /**
     * Returns the Document created from the xml string
     */
    private Document getDoc(String xml, boolean validate) throws Exception
    {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (!validate)
        {
            db.setEntityResolver(new NullResolver());
            db.setErrorHandler(new NullErrorHandler());
        }
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        return db.parse(is);
    }
}
