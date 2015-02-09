package kml;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class SAXXMLParser {
    public static List<ParsingStructure> parse(InputStream is) {
        List<ParsingStructure> parsingStru = null;
        try {
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            SAXXMLHandler saxHandler = new SAXXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(new InputSource(is));
            // get the `get list`
            parsingStru = saxHandler.getParsingvalues();
        } catch (Exception ex) {
         
        }
        return parsingStru;
    }
}
