/**
 * 
 */
package ca.datamagic.station.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Greg
 *
 */
public class ZipDTO implements Comparable<ZipDTO> {
	private String _zip = null;
	private String _city = null;
	private String _state = null;
	
	public ZipDTO() {
		
	}
	
	public ZipDTO(String zip, String city, String state) {
		_zip = zip;
		_city = city;
		_state = state;
	}

	public String getZip() {
		return _zip;
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
		if (((_zip != null) && (_zip.length() > 0)) &&
			((_city != null) && (_city.length() > 0)) &&
			((_state != null) && (_state.length() > 0))) {
			buffer.append(_zip);
			buffer.append(", ");
			buffer.append(_city);
			buffer.append(", ");
			buffer.append(_state);
		}
		return buffer.toString();
	}
	
	public void setZip(String newVal) {
		_zip = newVal;
	}
	
	public void setCity(String newVal) {
		_city = newVal;
	}
	
	public void setState(String newVal) {
		_state = newVal;
	}

	@Override
	public int compareTo(ZipDTO zip) {
		String key1 = getKey();
		String key2 = zip.getKey();
		return key1.compareToIgnoreCase(key2);
	}
}
