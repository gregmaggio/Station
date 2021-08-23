/**
 * 
 */
package ca.datamagic.station.dao;

/**
 * @author Greg
 *
 */
public class BaseDAO {
	private static String dataPath = "C:/Dev/Applications/Station/src/main/resources/data";
	
	public static String getDataPath() {
		return dataPath;
	}
	
	public static void setDataPath(String newVal) {
		dataPath = newVal;
	}
}
