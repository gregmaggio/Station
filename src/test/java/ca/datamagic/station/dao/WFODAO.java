/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import ca.datamagic.station.dto.WFODTO;

/**
 * @author Greg
 *
 */
public class WFODAO extends BaseDAO {
	private static Logger _logger = LogManager.getLogger(WFODAO.class);
	private String _fileName = null;
	private String _typeName = null; 
	private SimpleFeatureSource _featureSource = null;
	
	public WFODAO() throws IOException {
		_fileName = MessageFormat.format("{0}/w_10nv15/w_10nv15.shp", getDataPath());
		HashMap<Object, Object> connect = new HashMap<Object, Object>();
		connect.put("url", "file://" + _fileName);
		DataStore dataStore = DataStoreFinder.getDataStore(connect);
		String[] typeNames = dataStore.getTypeNames();
		String typeName = typeNames[0];
		SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
		_typeName = typeName;
		_featureSource = featureSource;
	}
	
	public List<WFODTO> read(double latitude, double longitude) throws IOException, CQLException {
		String filter = MessageFormat.format("CONTAINS (the_geom, POINT({0} {1}))", Double.toString(longitude), Double.toString(latitude));
		_logger.debug("filter: " + filter);
		Query query = new Query(_typeName, CQL.toFilter(filter));
		SimpleFeatureCollection collection = _featureSource.getFeatures(query);
		SimpleFeatureIterator iterator = null;
		try {
			List<WFODTO> items = new ArrayList<WFODTO>();
			iterator = collection.features();
			while (iterator.hasNext()) {
				items.add(new WFODTO(iterator.next()));
			}
			return items;
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
	}
}
