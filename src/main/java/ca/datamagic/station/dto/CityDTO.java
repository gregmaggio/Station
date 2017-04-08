/**
 * 
 */
package ca.datamagic.station.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Greg
 *
 */
public class CityDTO implements Comparable<CityDTO> {
	private String _city = null;
	private String _state = null;
	
	public CityDTO() {
		
	}
	
	public CityDTO(String city, String state) {
		_city = city;
		_state = state;
	}

	public String getCity() {
		return _city;
	}
	
	public String getState() {
		return _state;
	}

	@JsonIgnore
	public String getKey() {
		StringBuffer buffer = new StringBuffer();
		if (((_city != null) && (_city.length() > 0)) &&
			((_state != null) && (_state.length() > 0))) {
			buffer.append(_city);
			buffer.append(", ");
			buffer.append(_state);
		}
		return buffer.toString();
	}
	
	public void setCity(String newVal) {
		_city = newVal;
	}
	
	public void setState(String newVal) {
		_state = newVal;
	}

	@Override
	public int compareTo(CityDTO city) {
		String key1 = getKey();
		String key2 = city.getKey();
		return key1.compareToIgnoreCase(key2);
	}
}
