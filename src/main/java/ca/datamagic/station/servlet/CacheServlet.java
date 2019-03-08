/**
 * 
 */
package ca.datamagic.station.servlet;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import ca.datamagic.station.dto.CachedItemDTO;
import ca.datamagic.station.dto.StationDTO;
import ca.datamagic.station.inject.MemoryCacheInterceptor;

/**
 * @author Greg
 *
 */
public class CacheServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(CacheServlet.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			List<CachedItemDTO> items = new ArrayList<CachedItemDTO>();
			Enumeration<String> keys = MemoryCacheInterceptor.getKeys();
			if (keys != null) {
				while (keys.hasMoreElements()) {
					String key = keys.nextElement();
					Object value = MemoryCacheInterceptor.getValue(key);
					if (value instanceof StationDTO) {
						CachedItemDTO cachedItem = new CachedItemDTO();
						cachedItem.setKey(key);
						cachedItem.setStation((StationDTO)value);
						items.add(cachedItem);
					} else if (value instanceof List<?>) {
						List<StationDTO> stations = null;
						try {
							stations = (List<StationDTO>)value;
						} catch (Throwable t) {
						}
						CachedItemDTO cachedItem = new CachedItemDTO();
						cachedItem.setKey(key);
						cachedItem.setStations(stations);
						items.add(cachedItem);
					}				
				}
			}
			String json = (new Gson()).toJson(items);
			response.setContentType("application/json");
			response.getWriter().println(json);
		} catch (Throwable t) {
			logger.error("Exception", t);
			throw new IOError(t);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			MemoryCacheInterceptor.clearCache();
		} catch (Throwable t) {
			logger.error("Exception", t);
			throw new IOError(t);
		}
	}
}
