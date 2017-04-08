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
	private String _streetNumber = null;
	private String _streetName = null;
	private String _city = null;
	private String _stateCode = null;
	private String _stateName = null;
	private String _zip = null;
	private String _countryCode = null;
	private String _countryName = null;
	private Double _latitude = null;
	private Double _longitude = null;
	private Boolean _hasRadiosonde = null;

	public StationDTO() {
	}

	public StationDTO(SimpleFeature feature) {
		Point point = (Point)feature.getDefaultGeometry();
		Coordinate coordinate = point.getCoordinate();
		_stationId = (String)feature.getAttribute("station_id");
		_stationName = (String)feature.getAttribute("name");
		_streetNumber = (String)feature.getAttribute("street_no");
		_streetName = (String)feature.getAttribute("street");
		_city = (String)feature.getAttribute("city");
		_stateCode = (String)feature.getAttribute("state_cd");
		_stateName = (String)feature.getAttribute("state_nm");
		_zip = (String)feature.getAttribute("zip");
		_countryCode = (String)feature.getAttribute("country_cd");
		_countryName = (String)feature.getAttribute("country");
		_latitude = coordinate.y;
		_longitude = coordinate.x;
		_hasRadiosonde = ((String)feature.getAttribute("has_rad")).compareToIgnoreCase("Y") == 0;
	}
	
	public String getStationId() {
		return _stationId;
	}
	
	public String getStationName() {
		return _stationName;
	}
	
	public String getStreetNumber() {
		return _streetNumber;
	}
	
	public String getStreetName() {
		return _streetName;
	}
	
	public String getCity() {
		return _city;
	}
	
	public String getStateCode() {
		return _stateCode;
	}
	
	public String getStateName() {
		return _stateName;
	}
	
	public String getZip() {
		return _zip;
	}
	
	public String getCountryCode() {
		return _countryCode;
	}
	
	public String getCountryName() {
		return _countryName;
	}
	
	@JsonProperty(value = "latitude")
	public Double getLatitude() {
		return _latitude;
	}
	
	@JsonProperty(value = "longitude")
	public Double getLongitude() {
		return _longitude;
	}
	
	@JsonProperty(value = "hasRadiosonde")
	public Boolean hasRadiosonde() {
		return _hasRadiosonde;
	}
	
	public void setStationId(String newVal) {
		_stationId = newVal;
	}
	
	public void setStationName(String newVal) {
		_stationName = newVal;
	}
	
	public void setStreetNumber(String newVal) {
		_streetNumber = newVal;
	}
	
	public void setStreetName(String newVal) {
		_streetName = newVal;
	}
	
	public void setCity(String newVal) {
		_city = newVal;
	}
	
	public void setStateCode(String newVal) {
		_stateCode = newVal;
	}
	
	public void setStateName(String newVal) {
		_stateName = newVal;
	}
	
	public void setZip(String newVal) {
		_zip = newVal;
	}
	
	public void setCountryCode(String newVal) {
		_countryCode = newVal;
	}
	
	public void setCountryName(String newVal) {
		_countryName = newVal;
	}
	
	@JsonProperty(value = "latitude")
	public void setLatitude(Double newVal) {
		_latitude = newVal;
	}
	
	@JsonProperty(value = "longitude")
	public void setLongitude(Double newVal) {
		_longitude = newVal;
	}
	
	@JsonProperty(value = "hasRadiosonde")
	public void setHasRadiosonde(Boolean newVal) {
		_hasRadiosonde = newVal;
	}
}
