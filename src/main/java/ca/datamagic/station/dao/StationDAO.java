/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sourceforge.jgeocoder.AddressComponent;
import net.sourceforge.jgeocoder.us.AddressParser;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.feature.simple.SimpleFeature;

import ca.datamagic.station.dao.BaseDAO;
import ca.datamagic.station.dao.StationDAO;
import ca.datamagic.station.dto.CityDTO;
import ca.datamagic.station.dto.StationDTO;
import ca.datamagic.station.dto.ZipDTO;
import ca.datamagic.station.inject.MemoryCache;

import com.vividsolutions.jts.geom.Point;

/**
 * @author Greg
 *
 */
public class StationDAO extends BaseDAO {
	private static Logger _logger = LogManager.getLogger(StationDAO.class);
	private String _fileName = null;
	private String _typeName = null; 
	private SimpleFeatureSource _featureSource = null;
	
	public StationDAO() throws IOException {
		_fileName = MessageFormat.format("{0}/stations/stations.shp", getDataPath());
		HashMap<Object, Object> connect = new HashMap<Object, Object>();
		connect.put("url", "file://" + _fileName);
		DataStore dataStore = DataStoreFinder.getDataStore(connect);
		String[] typeNames = dataStore.getTypeNames();
		String typeName = typeNames[0];
		SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
		_typeName = typeName;
		_featureSource = featureSource;
	}

	@MemoryCache
	public List<CityDTO> cities() throws IOException, CQLException {
		SimpleFeatureCollection collection = _featureSource.getFeatures();
		SimpleFeatureIterator iterator = null;
		try {
			Hashtable<String, CityDTO> items = new Hashtable<String, CityDTO>();
			iterator = collection.features();
			while (iterator.hasNext()) {
				StationDTO station = new StationDTO(iterator.next());
				CityDTO city = new CityDTO(station.getCity(), station.getStateCode());
				String key = city.getKey();
				if ((key != null) && (key.length() > 0)) {
					key = key.toUpperCase();
					if (!items.containsKey(key)) {
						items.put(key, city);
					}
				}
			}
			List<CityDTO> cities = new ArrayList<CityDTO>();
			Enumeration<CityDTO> citiesEnum = items.elements();
			while (citiesEnum.hasMoreElements()) {
				cities.add(citiesEnum.nextElement());
			}
			Collections.sort(cities);
			return cities;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
	
	@MemoryCache
	public List<ZipDTO> zips() throws IOException, CQLException {
		SimpleFeatureCollection collection = _featureSource.getFeatures();
		SimpleFeatureIterator iterator = null;
		try {
			Hashtable<String, ZipDTO> items = new Hashtable<String, ZipDTO>();
			iterator = collection.features();
			while (iterator.hasNext()) {
				StationDTO station = new StationDTO(iterator.next());
				ZipDTO zip = new ZipDTO(station.getZip(), station.getCity(), station.getStateCode());
				String key = zip.getKey();
				if ((key != null) && (key.length() > 0)) {
					key = key.toUpperCase();
					if (!items.containsKey(key)) {
						items.put(key, zip);
					}
				}
			}
			List<ZipDTO> zips = new ArrayList<ZipDTO>();
			Enumeration<ZipDTO> zipsEnum = items.elements();
			while (zipsEnum.hasMoreElements()) {
				zips.add(zipsEnum.nextElement());
			}
			Collections.sort(zips);
			return zips;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
	
	@MemoryCache
	public List<StationDTO> list() throws IOException, CQLException {
		return list(false);
	}
	
	@MemoryCache
	public List<StationDTO> list(boolean hasRadisonde) throws IOException, CQLException {
		return list(null, null, null, false);
	}
	
	@MemoryCache
	public List<StationDTO> list(String city, String state, String zip) throws IOException, CQLException {
		return list(city, state, zip, false);
	}
	
	@MemoryCache
	public List<StationDTO> list(String city, String state, String zip, boolean hasRadisonde) throws IOException, CQLException {
		StringBuffer filter = new StringBuffer();
		if ((city != null) && (city.length() > 0)) {
			if (filter.length() > 0) {
				filter.append(" AND ");
			}
			filter.append(MessageFormat.format("city like {0}", "'" + city + "%'"));
		}
		if ((state != null) && (state.length() > 0)) {
			if (filter.length() > 0) {
				filter.append(" AND ");
			}
			filter.append(MessageFormat.format("state_cd like {0}", "'" + state + "%'"));
		}
		if ((zip != null) && (zip.length() > 0)) {
			if (filter.length() > 0) {
				filter.append(" AND ");
			}
			filter.append(MessageFormat.format("zip like {0}", "'" + zip + "%'"));
		}
		if (hasRadisonde) {
			if (filter.length() > 0) {
				filter.append(" AND ");
			}
			filter.append("has_rad = 'Y'");
		}
		SimpleFeatureCollection collection = null;
		if (filter.length() < 1) {
			collection = _featureSource.getFeatures();
		} else {
			_logger.debug("filter: " + filter.toString());
			Query query = new Query(_typeName, CQL.toFilter(filter.toString()));
			collection = _featureSource.getFeatures(query);
		}
		SimpleFeatureIterator iterator = null;
		try {
			List<StationDTO> items = new ArrayList<StationDTO>();
			iterator = collection.features();
			while (iterator.hasNext()) {
				items.add(new StationDTO(iterator.next()));
			}
			return items;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
	
	@MemoryCache
	public List<StationDTO> list(String address) throws IOException, CQLException {
		return list(address, false);
	}
	
	@MemoryCache
	public List<StationDTO> list(String address, boolean hasRadisonde) throws IOException, CQLException {
		StringBuffer filter = new StringBuffer();
		if ((address != null) && (address.length() > 0)) {
			Map<AddressComponent, String> addressComponents = AddressParser.parseAddress(address, true);
			if (addressComponents.containsKey(AddressComponent.NUMBER)) {
				String component = addressComponents.get(AddressComponent.NUMBER);
				if ((component != null) && (component.length() > 0)) {
					if (filter.length() > 0) {
						filter.append(" AND ");
					}
					filter.append(MessageFormat.format("street_no = {0}", "'" + component + "'"));
				}
			}
			if (addressComponents.containsKey(AddressComponent.STREET)) {
				String component = addressComponents.get(AddressComponent.STREET);
				if ((component != null) && (component.length() > 0)) {
					if (filter.length() > 0) {
						filter.append(" AND ");
					}
					filter.append(MessageFormat.format("street = {0}", "'" + component + "'"));
				}
			}
			if (addressComponents.containsKey(AddressComponent.CITY)) {
				String component = addressComponents.get(AddressComponent.CITY);
				if ((component != null) && (component.length() > 0)) {
					if (filter.length() > 0) {
						filter.append(" AND ");
					}
					filter.append(MessageFormat.format("city = {0}", "'" + component + "'"));
				}
			}
			if (addressComponents.containsKey(AddressComponent.STATE)) {
				String component = addressComponents.get(AddressComponent.STATE);
				if ((component != null) && (component.length() > 0)) {
					if (filter.length() > 0) {
						filter.append(" AND ");
					}
					filter.append(MessageFormat.format("state_cd = {0}", "'" + component + "'"));
				}
			}
			if (addressComponents.containsKey(AddressComponent.ZIP)) {
				String component = addressComponents.get(AddressComponent.ZIP);
				if ((component != null) && (component.length() > 0)) {
					if (filter.length() > 0) {
						filter.append(" AND ");
					}
					filter.append(MessageFormat.format("zip = {0}", "'" + component + "'"));
				}
			}
		}
		if (hasRadisonde) {
			if (filter.length() > 0) {
				filter.append(" AND ");
			}
			filter.append("has_rad = 'Y'");
		}
		SimpleFeatureCollection collection = null;
		if (filter.length() < 1) {
			collection = _featureSource.getFeatures();
		} else {
			_logger.debug("filter: " + filter.toString());
			Query query = new Query(_typeName, CQL.toFilter(filter.toString()));
			collection = _featureSource.getFeatures(query);
		}
		SimpleFeatureIterator iterator = null;
		try {
			List<StationDTO> items = new ArrayList<StationDTO>();
			iterator = collection.features();
			while (iterator.hasNext()) {
				items.add(new StationDTO(iterator.next()));
			}
			return items;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
	
	@MemoryCache
	public StationDTO read(String id) throws IOException, CQLException {
		String filter = MessageFormat.format("station_id = {0}", "'" + id.toUpperCase() + "'");
		_logger.debug("filter: " + filter);
		Query query = new Query(_typeName, CQL.toFilter(filter));
		SimpleFeatureCollection collection = _featureSource.getFeatures(query);
		SimpleFeatureIterator iterator = null;
		try {
			iterator = collection.features();
			if (iterator.hasNext()) {
				return new StationDTO(iterator.next());
			}
			return null;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
	
	@MemoryCache
	public StationDTO readNearest(double latitude, double longitude, double distance, String units) throws IOException, CQLException {
		String filter = MessageFormat.format("DWITHIN(the_geom, POINT({0} {1}), {2}, {3})", Double.toString(longitude), Double.toString(latitude), Double.toString(distance), units);
		_logger.debug("filter: " + filter);
		Query query = new Query(_typeName, CQL.toFilter(filter));
		SimpleFeatureCollection collection = _featureSource.getFeatures(query);
		SimpleFeatureIterator iterator = null;
		GeodeticCalculator calculator = new GeodeticCalculator();
		calculator.setStartingGeographicPoint(longitude, latitude);
		SimpleFeature nearestFeature = null;
		double minDistanceToPoint = 0.0;
		try {
			iterator = collection.features();
			while (iterator.hasNext()) {		
				SimpleFeature feature = iterator.next();
				Point point = (Point)feature.getAttribute(0);
				calculator.setDestinationGeographicPoint(point.getX(), point.getY());
				double distanceToPoint = calculator.getOrthodromicDistance();
				if (nearestFeature == null) {
					nearestFeature = feature;
					minDistanceToPoint = distanceToPoint;
				} else {
					if (distanceToPoint < minDistanceToPoint) {
						nearestFeature = feature;
						minDistanceToPoint = distanceToPoint;
					}
				}
			}
			if (nearestFeature != null) {
				return new StationDTO(nearestFeature);
			}
			return null;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
	
	@MemoryCache
	public StationDTO readNearest(double latitude, double longitude, double distance, String units, boolean hasRadisonde) throws IOException, CQLException {
		String filter = MessageFormat.format("DWITHIN(the_geom, POINT({0} {1}), {2}, {3})", Double.toString(longitude), Double.toString(latitude), Double.toString(distance), units);
		_logger.debug("filter: " + filter);
		Query query = new Query(_typeName, CQL.toFilter(filter));
		SimpleFeatureCollection collection = _featureSource.getFeatures(query);
		SimpleFeatureIterator iterator = null;
		GeodeticCalculator calculator = new GeodeticCalculator();
		calculator.setStartingGeographicPoint(longitude, latitude);
		SimpleFeature nearestFeature = null;
		double minDistanceToPoint = 0.0;
		try {
			iterator = collection.features();
			while (iterator.hasNext()) {		
				SimpleFeature feature = iterator.next();
				if (hasRadisonde) {
					if (feature.getAttribute("has_rad").toString().compareToIgnoreCase("Y") != 0) {
						continue;
					}
				} else {
					if (feature.getAttribute("has_rad").toString().compareToIgnoreCase("N") != 0) {
						continue;
					}
				}
				Point point = (Point)feature.getAttribute(0);
				calculator.setDestinationGeographicPoint(point.getX(), point.getY());
				double distanceToPoint = calculator.getOrthodromicDistance();
				if (nearestFeature == null) {
					nearestFeature = feature;
					minDistanceToPoint = distanceToPoint;
				} else {
					if (distanceToPoint < minDistanceToPoint) {
						nearestFeature = feature;
						minDistanceToPoint = distanceToPoint;
					}
				}
			}
			if (nearestFeature != null) {
				return new StationDTO(nearestFeature);
			}
			return null;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
}
