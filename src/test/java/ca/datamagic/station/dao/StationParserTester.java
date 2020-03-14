/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import ca.datamagic.station.dto.StateDTO;
import ca.datamagic.station.dto.StationDTO;
import ca.datamagic.station.xml.StationHandler;
import ca.datamagic.station.xml.StationParser;

/**
 * @author Greg
 *
 */
public class StationParserTester {
	private static Logger logger = LogManager.getLogger(StationParserTester.class);
	private static StateDAO stateDAO = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DOMConfigurator.configure("src/test/resources/log4j.cfg.xml");
		BaseDAO.setDataPath((new File("src/test/resources/data")).getAbsolutePath());
		stateDAO = new StateDAO();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stateDAO = null;
	}
	
	@Test
	public void createStationsShapeFile() throws Exception {
		final StationDAO stationDAO = new StationDAO(true);
		final Gson gson = new Gson();
		StationParser parser = new StationParser();
		StationHandler handler = new StationHandler() {			
			@Override
			public void station(StationDTO station) {
				try {
					StateDTO state = stateDAO.getState(station.getState());
					if (state != null) {
						NOAAForecastPage forecastPage = new NOAAForecastPage(station.getLatitude().doubleValue(), station.getLongitude().doubleValue());
						String wfo = forecastPage.getWFO();
						String radar = forecastPage.getRadar();
						station.setWFO(wfo);
						station.setRadar(radar);
						String json = gson.toJson(station);
						logger.debug(json);
						if ((wfo != null) && (wfo.length() > 0) && (radar != null) && (radar.length() > 0)) {
							stationDAO.add(station);
						} else {
							logger.debug("Skipping station!");
						}
					}
				} catch (Throwable t) {
					logger.warn("Exception", t);
				}
			}
		};
		parser.parse("src/test/resources/data/stations.xml", handler);
	}
}
