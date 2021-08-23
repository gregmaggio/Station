/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.File;
import java.util.Enumeration;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.datamagic.station.dao.BaseDAO;
import ca.datamagic.station.dao.StationDAO;
import ca.datamagic.station.dto.StationDTO;
import ca.datamagic.station.inject.DAOModule;
import ca.datamagic.station.inject.MemoryCacheInterceptor;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Greg
 *
 */
public class StationDAOTester {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
		double latitude = 32.8481029;
	    double longitude = -96.853395;
	    double distance = 25;
	    String units = "statute miles";
	    StationDTO dto = dao.readNearest(latitude, longitude, distance, units);
	    Gson gson = new Gson();
		String json = gson.toJson(dto);
		System.out.println("json: " + json);
	}
	
	@Test
	public void test4() throws Exception {
		StationDAO dao = new StationDAO();
		StationDTO dto = dao.read("KDTO");
		System.out.println("Station: " + dto.getStationId());
		Gson gson = new Gson();
		String json = gson.toJson(dto);
		System.out.println("json: " + json);
	}
	
	@Test
	public void test5() throws Exception {
		Injector injector = Guice.createInjector(new DAOModule());
		StationDAO dao = injector.getInstance(StationDAO.class);
		StationDTO dto1 = dao.read("KDTO");
		System.out.println("Station1: " + dto1.getStationId());
		
		StationDTO dto2 = dao.read("KDTO");
		System.out.println("Station2: " + dto2.getStationId());
		
		Enumeration<String> keys = MemoryCacheInterceptor.getKeys();
		while (keys.hasMoreElements()) {
			System.out.println("CacheKey: " + keys.nextElement());
		}		
	}
}
