/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.File;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.datamagic.station.dao.BaseDAO;
import ca.datamagic.station.dao.StationDAO;
import ca.datamagic.station.dto.StationDTO;

import com.google.gson.Gson;

/**
 * @author Greg
 *
 */
public class StationDAOTester {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DOMConfigurator.configure("src/test/resources/log4j.cfg.xml");
		BaseDAO.setDataPath((new File("src/test/resources/data")).getAbsolutePath());
	}

	@Test
	public void test1() throws Exception {
		StationDAO dao = new StationDAO();
		List<StationDTO> items = dao.list();
		for (StationDTO item : items) {
			System.out.println("Station: " + item.getStationId());
		}
		Gson gson = new Gson();
		String json = gson.toJson(items);
		System.out.println("json: " + json);
	}
	
	@Test
	public void test2() throws Exception {
		StationDAO dao = new StationDAO();
		StationDTO dto = dao.read("KIAD");
		System.out.println("Station: " + dto.getStationId());
		Gson gson = new Gson();
		String json = gson.toJson(dto);
		System.out.println("json: " + json);
	}
	
	@Test
	public void test3() throws Exception {
		StationDAO dao = new StationDAO();
		//45.7820729/-108.7041157
		double latitude = 45.7820729;
	    double longitude = -108.7041157;
	    double distance = 25;
	    String units = "statute miles";
	    StationDTO dto = dao.readNearest(latitude, longitude, distance, units);
	    Gson gson = new Gson();
		String json = gson.toJson(dto);
		System.out.println("json: " + json);
	}
}
