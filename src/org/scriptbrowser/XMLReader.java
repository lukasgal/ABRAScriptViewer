package org.scriptbrowser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Lukáš Gál
 */
public class XMLReader {

    private Document xmlDoc;
    private final URL fileURL;

    public XMLReader(URL fileName) {
        this.fileURL = fileName;
        this.xmlDoc = null;
    }

    public void load() {
        try {
            System.out.println(fileURL.toString());
            InputStream istream;
            
            istream = fileURL.openStream();
            istream = checkForUtf8BOM(istream);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            this.xmlDoc = (Document) dBuilder.parse(istream);
            this.xmlDoc.normalizeDocument();
            System.out.println(this.xmlDoc.getXmlEncoding());

        } catch (IOException | ParserConfigurationException | SAXException ex) {
            ex.printStackTrace();
        }
    }

    public Document getXmlDoc() {
        return xmlDoc;
    }

    public void setXmlDoc(Document xmlDoc) {
        this.xmlDoc = xmlDoc;
    }

    /**
     * Odstraňuje BOM z dokumentu, předaného proudem
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static InputStream checkForUtf8BOM(InputStream inputStream) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
        byte[] bom = new byte[3];
        if (pushbackInputStream.read(bom) != -1) {
            if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
                pushbackInputStream.unread(bom);
            }
        }

        return pushbackInputStream;
    }

}
