/**
 * 
 */
package ca.datamagic.station.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import ca.datamagic.station.dto.StationDTO;

/**
 * @author Greg
 *
 */
public class StationParser extends DefaultHandler {
	private static Logger logger = LogManager.getLogger(StationParser.class);
	private static String stationNodeName = "station";
	private static String stationIdNodeName = "station_id";
	private static String stationNameNodeName = "station_name";
	private static String stateNodeName = "state";
	private static String latitudeNodeName = "latitude";
	private static String longitudeNodeName = "longitude";
	private StationHandler handler = null;
	private String currentElement = null;
	private StationDTO currentStation = null;
	
	public void parse(String fileName, StationHandler handler) throws ParserConfigurationException, SAXException, IOException {
		this.handler = handler;
		this.currentElement = null;
		this.currentStation = null;
		File file = new File(fileName);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(false);
		SAXParser parser = factory.newSAXParser();
		parser.parse(file, this);
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		this.currentElement = qName;
		if (this.currentElement != null) {
			if (this.currentElement.compareToIgnoreCase(stationNodeName) == 0) {
				this.currentStation = new StationDTO();
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String value = new String(ch, start, length);
		if ((this.currentElement != null) && (this.currentStation != null)) {
			if (this.currentElement.compareToIgnoreCase(stationIdNodeName) == 0) {
				this.currentStation.setStationId(value);
			} else if (this.currentElement.compareToIgnoreCase(stationNameNodeName) == 0) {
				this.currentStation.setStationName(value);
			} else if (this.currentElement.compareToIgnoreCase(stateNodeName) == 0) {
				this.currentStation.setState(value);
			} else if (this.currentElement.compareToIgnoreCase(latitudeNodeName) == 0) {
				this.currentStation.setLatitude(new Double(value));
			} else if (this.currentElement.compareToIgnoreCase(longitudeNodeName) == 0) {
				this.currentStation.setLongitude(new Double(value));
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName != null) {
			if (qName.compareToIgnoreCase(stationNodeName) == 0) {
				if (this.currentStation != null) {
					if (handler != null) {
						handler.station(this.currentStation);
					}
				}
				this.currentStation = null;
			}
		}
		this.currentElement = null;
	}

	@Override
	public void warning(SAXParseException ex) throws SAXException {
		logger.warn("SAXParseException", ex);
	}
}
