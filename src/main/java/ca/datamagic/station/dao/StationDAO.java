/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import ca.datamagic.station.dao.BaseDAO;
import ca.datamagic.station.dao.StationDAO;
import ca.datamagic.station.dto.StationDTO;
import ca.datamagic.station.inject.MemoryCache;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Greg
 *
 */
public class StationDAO extends BaseDAO {
	private static Logger logger = LogManager.getLogger(StationDAO.class);
	private String fileName = null;
	private String typeName = null; 
	private SimpleFeatureType featureType = null;  
	private GeometryFactory geometryFactory = null;
	private SimpleFeatureBuilder featureBuilder = null;
	private SimpleFeatureSource featureSource = null;
	
	public StationDAO() throws IOException, SchemaException {
		this(false);
	}
	
	public StationDAO(boolean newShapeFile) throws IOException, SchemaException {
		this.fileName = MessageFormat.format("{0}/stations_wfo/stations_wfo.shp", getDataPath());
		logger.debug("fileName: " + this.fileName);
		if (newShapeFile) {
			File file = new File(this.fileName);
			if (file.exists()) {
				file.delete();
			}
			SimpleFeatureType featureType = DataUtilities.createType("stations_wfo",
	                "location:Point:srid=4326," + // <- the geometry attribute: Point type
	                        "station_id:String," + // <- a String attribute
	                        "name:String," + // <- a String attribute
	                        "state:String," + // <- a String attribute
	                        "wfo:String," + // <- a String attribute
	                        "radar:String," + // <- a String attribute
	                        "latitude:Integer," + // a number attribute
	                        "longitude:Integer" // a number attribute
	        );
			HashMap<Object, Object> connect = new HashMap<Object, Object>();
			connect.put("url", file.toURI().toURL());
			connect.put("create spatial index", Boolean.TRUE);
			ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
			ShapefileDataStore dataStore = (ShapefileDataStore)dataStoreFactory.createNewDataStore(connect);
			dataStore.createSchema(featureType);
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(featureType.getTypeName());
			this.typeName = featureType.getTypeName();
			this.featureType = featureType;
			this.geometryFactory = geometryFactory;
			this.featureBuilder = featureBuilder;
			this.featureSource = featureSource;
		} else {
			HashMap<Object, Object> connect = new HashMap<Object, Object>();
			connect.put("url", "file://" + this.fileName);
			DataStore dataStore = DataStoreFinder.getDataStore(connect);
			String[] typeNames = dataStore.getTypeNames();			
			String typeName = typeNames[0];
			SimpleFeatureType featureType = dataStore.getSchema(typeName);
			SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
			for (int ii = 0; ii < featureSource.getSchema().getAttributeCount(); ii++) {
				logger.debug("attribute[" + ii + "].Name : " + featureSource.getSchema().getDescriptor(ii).getName().getLocalPart());
				logger.debug("attribute[" + ii + "].Type : " + featureSource.getSchema().getDescriptor(ii).getType().toString());
			}
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
			this.typeName = typeName;
			this.featureType = featureType;
			this.geometryFactory = geometryFactory;
			this.featureBuilder = featureBuilder;
			this.featureSource = featureSource;
		}
	}
	
	public void add(StationDTO station) throws IOException {
		Transaction transaction = new DefaultTransaction("create");
		
		Point point = this.geometryFactory.createPoint(new Coordinate(station.getLongitude(), station.getLatitude()));
		this.featureBuilder.add(point);
		this.featureBuilder.add(station.getStationId());
		this.featureBuilder.add(station.getStationName());
		this.featureBuilder.add(station.getState());
		this.featureBuilder.add(station.getWFO());
		this.featureBuilder.add(station.getRadar());
		this.featureBuilder.add(station.getLatitude());
		this.featureBuilder.add(station.getLongitude());
        SimpleFeature feature = this.featureBuilder.buildFeature(null);
        SimpleFeatureCollection collection = new ListFeatureCollection(this.featureType);
        collection.add(feature);
        SimpleFeatureStore featureStore = (SimpleFeatureStore)this.featureSource;        
        featureStore.setTransaction(transaction);
        try {
            featureStore.addFeatures(collection);
            transaction.commit();
        } catch (Throwable t) {
            logger.error("Exception", t);
            transaction.rollback();
        } finally {
            transaction.close();
        }
	}
	
	@MemoryCache
	public List<StationDTO> list() throws IOException, CQLException {
		return list(null, null);
	}
	
	@MemoryCache
	public List<StationDTO> list(String state, String wfo) throws IOException, CQLException {
		StringBuffer filter = new StringBuffer();
		if ((state != null) && (state.length() > 0)) {
			if (filter.length() > 0) {
				filter.append(" AND ");
			}
			filter.append(MessageFormat.format("state = {0}", "'" + state + "'"));
		}
		if ((wfo != null) && (wfo.length() > 0)) {
			if (filter.length() > 0) {
				filter.append(" AND ");
			}
			filter.append(MessageFormat.format("wfo = {0}", "'" + wfo + "'"));
		}
		SimpleFeatureCollection collection = null;
		if (filter.length() < 1) {
			collection = this.featureSource.getFeatures();
		} else {
			logger.debug("filter: " + filter.toString());
			Query query = new Query(typeName, CQL.toFilter(filter.toString()));
			collection = this.featureSource.getFeatures(query);
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
		logger.debug("filter: " + filter);
		Query query = new Query(typeName, CQL.toFilter(filter));
		SimpleFeatureCollection collection = this.featureSource.getFeatures(query);
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
		logger.debug("filter: " + filter);
		Query query = new Query(typeName, CQL.toFilter(filter));
		SimpleFeatureCollection collection = this.featureSource.getFeatures(query);
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
}
