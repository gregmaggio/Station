/**
 * 
 */
package ca.datamagic.station.dao;

/**
 * @author Greg
 *
 */
public class BaseDAO {
	private static String _dataPath = "C:/Dev/Applications/Station/src/main/resources/data";
	
	public static String getDataPath() {
		return _dataPath;
	}
	
	public static void setDataPath(String newVal) {
		_dataPath = newVal;
	}
}
