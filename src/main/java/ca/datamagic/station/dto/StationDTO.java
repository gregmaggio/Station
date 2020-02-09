/**
 * 
 */
package ca.datamagic.station.dto;

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
	private transient SimpleFeature feature = null;
	
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
		this.feature = feature;
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
	
	public SimpleFeature getFeature() {
		return this.feature;
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
}
