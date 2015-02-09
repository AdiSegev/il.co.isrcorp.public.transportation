package kml;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXXMLHandler extends DefaultHandler {

    private List<ParsingStructure> parsingStructure;
    private StringBuilder tempVal;
    private ParsingStructure tempStr;

    public SAXXMLHandler() {
      parsingStructure = new ArrayList<ParsingStructure>();
    }

    public List<ParsingStructure> getParsingvalues() {
        return parsingStructure;
    }
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
      
        tempVal = new StringBuilder();
        if (qName.equalsIgnoreCase("Placemark")) {
            tempStr = new ParsingStructure();
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
      if (tempVal != null) {
            for (int i=start; i<start+length; i++) {
                  tempVal.append(ch[i]);
            }
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("Placemark")) {
            // add it to the list
            parsingStructure.add(tempStr);
        } else if (qName.equalsIgnoreCase("name")) {
            if(tempStr != null)
                  tempStr.setName(tempVal.toString());
        } else if (qName.equalsIgnoreCase("description")) {
            if(tempStr != null)
                  tempStr.setDescription(tempVal.toString());
        } else if (qName.equalsIgnoreCase("coordinates")) {
            if(tempStr != null)
                  tempStr.setCoordinates(tempVal.toString());
        } else if(qName.equalsIgnoreCase("point")) {
        	tempStr.setStation(true);
        }
    }
   
    public void warning(SAXParseException e) throws SAXException {
     
    }
   
    public void error(SAXParseException e) throws SAXException {
     
    }
   
    public void fatalError(SAXParseException e) throws SAXException {
   
    }
}