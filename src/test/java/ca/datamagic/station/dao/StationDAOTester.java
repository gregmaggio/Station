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
import ca.datamagic.station.dto.CityDTO;
import ca.datamagic.station.dto.StationDTO;
import ca.datamagic.station.dto.ZipDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Greg
 *
 */
public class StationDAOTester {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DOMConfigurator.configure("src/test/resources/META-INF/log4j.cfg.xml");
		BaseDAO.setDataPath((new File("src/test/resources/data")).getAbsolutePath());
	}

	@Test
	public void test1() throws Exception {
		StationDAO dao = new StationDAO();
		List<StationDTO> items = dao.list();
		for (StationDTO item : items) {
			System.out.println("Station: " + item.getStationId());
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(items);
		System.out.println("json: " + json);
	}
	
	@Test
	public void test2() throws Exception {
		StationDAO dao = new StationDAO();
		StationDTO dto = dao.read("KIAD");
		System.out.println("Station: " + dto.getStationId());
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(dto);
		System.out.println("json: " + json);
	}
	
	@Test
	public void test3() throws Exception {
		StationDAO dao = new StationDAO();
		double latitude = 38.9967;
	    double longitude = -76.9275;
	    double distance = 25;
	    String units = "statute miles";
	    StationDTO dto = dao.readNearest(latitude, longitude, distance, units);
	    System.out.println("Station: " + dto.getStationId());
	}
	
	@Test
	public void test4() throws Exception {
		StationDAO dao = new StationDAO();
		double latitude = 38.9967;
	    double longitude = -76.9275;
	    double distance = 25;
	    String units = "statute miles";
	    StationDTO dto = dao.readNearest(latitude, longitude, distance, units, true);
	    System.out.println("Station: " + dto.getStationId());
	}
	
	@Test
	public void test5() throws Exception {
		StationDAO dao = new StationDAO();
	    List<StationDTO> list = dao.list(true);
	    for (StationDTO dto : list) {
	    	System.out.println("Station: " + dto.getStationId());
	    }
	}
	
	@Test
	public void test6() throws Exception {
		StationDAO dao = new StationDAO();
	    List<StationDTO> list = dao.list(false);
	    for (StationDTO dto : list) {
	    	System.out.println("Station: " + dto.getStationId());
	    }
	}
	
	@Test
	public void test7() throws Exception {
		StationDAO dao = new StationDAO();
	    List<StationDTO> list = dao.list("college", null, null);
	    for (StationDTO dto : list) {
	    	System.out.println("Station: " + dto.getStationId());
	    }
	}
	
	@Test
	public void test8() throws Exception {
		StationDAO dao = new StationDAO();
	    List<StationDTO> list = dao.list(null, null, "20740");
	    for (StationDTO dto : list) {
	    	System.out.println("Station: " + dto.getStationId());
	    }
	}
	
	@Test
	public void test9() throws Exception {
		StationDAO dao = new StationDAO();
	    List<StationDTO> list = dao.list("Google Inc, 1600 Amphitheatre Parkway, Mountain View, CA 94043");
	    for (StationDTO dto : list) {
	    	System.out.println("Station: " + dto.getStationId());
	    }
	}
	
	@Test
	public void test10() throws Exception {
		StationDAO dao = new StationDAO();
	    List<StationDTO> list = dao.list("Mountain View, CA 94043");
	    for (StationDTO dto : list) {
	    	System.out.println("Station: " + dto.getStationId());
	    }
	}
	
	@Test
	public void test11() throws Exception {
		StationDAO dao = new StationDAO();
	    List<StationDTO> list = dao.list("Mountain View, CA");
	    for (StationDTO dto : list) {
	    	System.out.println("Station: " + dto.getStationId());
	    }
	}
	
	@Test
	public void test12() throws Exception {
		StationDAO dao = new StationDAO();
	    List<CityDTO> cities = dao.cities();
	    for (CityDTO dto : cities) {
	    	System.out.println("City: " + dto.getKey());
	    }
	}
	
	@Test
	public void test13() throws Exception {
		StationDAO dao = new StationDAO();
	    List<ZipDTO> zips = dao.zips();
	    for (ZipDTO dto : zips) {
	    	System.out.println("Zip: " + dto.getKey());
	    }
	}
}
