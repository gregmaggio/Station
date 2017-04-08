/**
 * 
 */
package ca.datamagic.station.dto;

import java.util.List;

/**
 * @author Greg
 *
 */
public class CachedItemDTO {
	private String _key = null;
	private StationDTO _station = null;
	private List<StationDTO> _stations = null;
	private List<CityDTO> _cities = null;
	private List<ZipDTO> _zips = null;
	
	public CachedItemDTO() {
	}

	public String getKey() {
		return _key;
	}
	
	public void setKey(String newVal) {
		_key = newVal;
	}
	
	public StationDTO getStation() {
		return _station;
	}
	
	public void setStation(StationDTO newVal) {
		_station = newVal;
	}
	
	public List<StationDTO> getStations() {
		return _stations;
	}
	
	public void setStations(List<StationDTO> newVal) {
		_stations = newVal;
	}
	
	public List<CityDTO> getCities() {
		return _cities;
	}
	
	public void setCities(List<CityDTO> newVal) {
		_cities = newVal;
	}
	
	public List<ZipDTO> getZips() {
		return _zips;
	}
	
	public void setZips(List<ZipDTO> newVal) {
		_zips = newVal;
	}
}
