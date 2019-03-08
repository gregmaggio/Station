/**
 * 
 */
package ca.datamagic.station.dto;

import java.util.List;

//import org.json.JSONArray;
//import org.json.JSONObject;

/**
 * @author Greg
 *
 */
public class CachedItemDTO {
	private String key = null;
	private StationDTO station = null;
	private List<StationDTO> stations = null;
	
	public CachedItemDTO() {
	}

	public String getKey() {
		return this.key;
	}
	
	public void setKey(String newVal) {
		this.key = newVal;
	}
	
	public StationDTO getStation() {
		return this.station;
	}
	
	public void setStation(StationDTO newVal) {
		this.station = newVal;
	}
	
	public List<StationDTO> getStations() {
		return this.stations;
	}
	
	public void setStations(List<StationDTO> newVal) {
		this.stations = newVal;
	}
	/*
	public JSONObject toJSON() {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("key", this.key);
		if (this.station != null) {
			jsonObj.put("station", this.station.toJSON());
		}
		if (this.stations != null) {
			jsonObj.put("stations", StationDTO.toJSON(this.stations));
		}
		return jsonObj;
	}
	
	public static JSONArray toJSON(List<CachedItemDTO> cachedItems) {
		JSONArray jsonArray = new JSONArray();
		for (int ii = 0; ii < cachedItems.size(); ii++) {
			jsonArray.put(cachedItems.get(ii).toJSON());
		}
		return jsonArray;
	}
	*/
}
