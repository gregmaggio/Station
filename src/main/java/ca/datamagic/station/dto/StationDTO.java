/**
 * 
 */
package ca.datamagic.station.dto;

import org.opengis.feature.simple.SimpleFeature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

/**
 * @author Greg
 *
 */
public class StationDTO {
	private String _stationId = null;
	private String _stationName = null;
	private String _state = null;
	private String _wfo = null;
	private String _radar = null;
	private Double _latitude = null;
	private Double _longitude = null;

	public StationDTO() {
	}

	public StationDTO(SimpleFeature feature) {
		Point point = (Point)feature.getDefaultGeometry();
		Coordinate coordinate = point.getCoordinate();
		_stationId = (String)feature.getAttribute("station_id");
		_stationName = (String)feature.getAttribute("name");
		_state = (String)feature.getAttribute("state");
		_wfo = (String)feature.getAttribute("wfo");
		_radar = (String)feature.getAttribute("radar");
		_latitude = coordinate.y;
		_longitude = coordinate.x;
	}
	
	@JsonProperty(value = "stationId")
	public String getStationId() {
		return _stationId;
	}
	
	@JsonProperty(value = "stationName")
	public String getStationName() {
		return _stationName;
	}
	
	@JsonProperty(value = "state")
	public String getState() {
		return _state;
	}
	
	@JsonProperty(value = "wfo")
	public String getWFO() {
		return _wfo;
	}
	
	@JsonProperty(value = "radar")
	public String getRadar() {
		return _radar;
	}
	
	@JsonProperty(value = "latitude")
	public Double getLatitude() {
		return _latitude;
	}
	
	@JsonProperty(value = "longitude")
	public Double getLongitude() {
		return _longitude;
	}
	
	@JsonProperty(value = "stationId")
	public void setStationId(String newVal) {
		_stationId = newVal;
	}
	
	@JsonProperty(value = "stationName")
	public void setStationName(String newVal) {
		_stationName = newVal;
	}
	
	@JsonProperty(value = "state")
	public void setState(String newVal) {
		_state = newVal;
	}
	
	@JsonProperty(value = "wfo")
	public void setWFO(String newVal) {
		_wfo = newVal;
	}
	
	@JsonProperty(value = "radar")
	public void setRadar(String newVal) {
		_radar = newVal;
	}
	
	@JsonProperty(value = "latitude")
	public void setLatitude(Double newVal) {
		_latitude = newVal;
	}
	
	@JsonProperty(value = "longitude")
	public void setLongitude(Double newVal) {
		_longitude = newVal;
	}
}
