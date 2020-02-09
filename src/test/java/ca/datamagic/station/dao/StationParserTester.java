/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import ca.datamagic.station.dto.StationDTO;
import ca.datamagic.station.dto.WFODTO;
import ca.datamagic.station.xml.StationHandler;
import ca.datamagic.station.xml.StationParser;

/**
 * @author Greg
 *
 */
public class StationParserTester {
	private static Logger _logger = LogManager.getLogger(StationParserTester.class);
	private static Hashtable<String, String> updatedRadar = new Hashtable<String, String>();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DOMConfigurator.configure("src/test/resources/log4j.cfg.xml");
		BaseDAO.setDataPath((new File("src/test/resources/data")).getAbsolutePath());
		InputStream inputStream = null;		
		try {
			inputStream = new FileInputStream("src/test/resources/data/stations_wfo/stations_wfo.csv");
			CsvParserSettings settings = new CsvParserSettings();
			CsvParser csvParser = new CsvParser(settings);
			List<String[]> lines = csvParser.parseAll(inputStream);
			for (int ii = 1; ii < lines.size(); ii++) {
				String[] currentLineItems = lines.get(ii);
				String stationID = currentLineItems[0];
				String radar = currentLineItems[4];
				if ((stationID != null) && (stationID.length() > 0) && (radar != null) && (stationID.length() > 0)) {
					updatedRadar.put(stationID, radar);
				}
			}			
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	@Test
	public void test1() throws Exception {
		final StationDAO stationDAO = new StationDAO(true);
		final WFODAO wfoDAO = new WFODAO();
		final Gson gson = new Gson();
		StationParser parser = new StationParser();
		StationHandler handler = new StationHandler() {			
			@Override
			public void station(StationDTO station) {
				try {					
					List<WFODTO> wfo = wfoDAO.read(station.getLatitude(), station.getLongitude());
					if ((wfo != null) && (wfo.size() > 0)) {
						_logger.debug("station: " + gson.toJson(station));
						station.setWFO(wfo.get(0).getWFO());
						station.setRadar(wfo.get(0).getRadar());
						if (updatedRadar.containsKey(station.getStationId())) {
							station.setRadar(updatedRadar.get(station.getStationId()));
						}
						stationDAO.add(station);
					}
				} catch (Throwable t) {
					_logger.warn("Exception", t);
				}
			}
		};
		parser.parse("src/test/resources/data/stations.xml", handler);
	}
}
