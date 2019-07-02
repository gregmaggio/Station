/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.File;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

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
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DOMConfigurator.configure("src/test/resources/log4j.cfg.xml");
		BaseDAO.setDataPath((new File("src/test/resources/data")).getAbsolutePath());
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
						station.setWFO(wfo.get(0).getWFO());
						station.setRadar(wfo.get(0).getRadar());
						_logger.debug("station: " + gson.toJson(station));
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
