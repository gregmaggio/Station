/**
 * 
 */
package ca.datamagic.station.dto;

//import java.util.List;

//import org.json.JSONArray;
//import org.json.JSONObject;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Greg
 *
 */
public class StationDTO {
	private String stationId = null;
	private String stationName = null;
	private String state = null;
	private String wfo = null;
	private String radar = null;
	private Double latitude = null;
	private Double longitude = null;

	public StationDTO() {
	}

	public StationDTO(SimpleFeature feature) {
		Point point = (Point)feature.getDefaultGeometry();
		Coordinate coordinate = point.getCoordinate();
		this.stationId = (String)feature.getAttribute("station_id");
		this.stationName = (String)feature.getAttribute("name");
		this.state = (String)feature.getAttribute("state");
		this.wfo = (String)feature.getAttribute("wfo");
		this.radar = (String)feature.getAttribute("radar");
		this.latitude = coordinate.y;
		this.longitude = coordinate.x;
	}
	
	public String getStationId() {
		return this.stationId;
	}
	
	public String getStationName() {
		return this.stationName;
	}
	
	public String getState() {
		return this.state;
	}
	
	public String getWFO() {
		return this.wfo;
	}
	
	public String getRadar() {
		return this.radar;
	}
	
	public Double getLatitude() {
		return this.latitude;
	}
	
	public Double getLongitude() {
		return this.longitude;
	}
	
	public void setStationId(String newVal) {
		this.stationId = newVal;
	}
	
	public void setStationName(String newVal) {
		this.stationName = newVal;
	}
	
	public void setState(String newVal) {
		this.state = newVal;
	}
	
	public void setWFO(String newVal) {
		this.wfo = newVal;
	}
	
	public void setRadar(String newVal) {
		this.radar = newVal;
	}
	
	public void setLatitude(Double newVal) {
		this.latitude = newVal;
	}
	
	public void setLongitude(Double newVal) {
		this.longitude = newVal;
	}
	/*
	public JSONObject toJSON() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("stationId", this.stationId);
		jsonObj.put("stationName", this.stationName);
		jsonObj.put("state", this.state);
		jsonObj.put("wfo", this.wfo);
		jsonObj.put("radar", this.radar);
		jsonObj.put("latitude", this.latitude);
		jsonObj.put("longitude", this.longitude);
		return jsonObj;
	}
	
	public static JSONArray toJSON(List<StationDTO> stations) {
		JSONArray jsonArray = new JSONArray();
		for (int ii = 0; ii < stations.size(); ii++) {
			jsonArray.put(stations.get(ii).toJSON());
		}
		return jsonArray;
	}
	*/
}
