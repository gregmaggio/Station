/**
 * 
 */
package ca.datamagic.station.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;

import ca.datamagic.station.controller.IndexController;
import ca.datamagic.station.dao.BaseDAO;
import ca.datamagic.station.dao.StationDAO;
import ca.datamagic.station.dto.CachedItemDTO;
import ca.datamagic.station.dto.CityDTO;
import ca.datamagic.station.dto.SwaggerConfigurationDTO;
import ca.datamagic.station.dto.SwaggerResourceDTO;
import ca.datamagic.station.dto.StationDTO;
import ca.datamagic.station.dto.ZipDTO;
import ca.datamagic.station.inject.DAOModule;
import ca.datamagic.station.inject.MemoryCacheInterceptor;

/**
 * @author Greg
 *
 */
@Controller
@RequestMapping("")
public class IndexController {
	private static Logger _logger = LogManager.getLogger(IndexController.class);
	private static Injector _injector = null;
	private static StationDAO _dao = null;
	private static SwaggerConfigurationDTO _swaggerConfiguration = null;
	private static SwaggerResourceDTO[] _swaggerResources = null;
	private static String _swagger = null;
	
	static {
		FileInputStream swaggerStream = null;
		try {
			DefaultResourceLoader loader = new DefaultResourceLoader();       
		    Resource dataResource = loader.getResource("classpath:data");
		    Resource metaInfResource = loader.getResource("META-INF");
		    String dataPath = dataResource.getFile().getAbsolutePath();
		    String metaInfPath = metaInfResource.getFile().getAbsolutePath();
		    _logger.debug("dataPath: " + dataPath);
		    _logger.debug("metaInfPath: " + metaInfPath);
		    
		    String swaggerFileName = MessageFormat.format("{0}/swagger.json", metaInfPath);
		    swaggerStream = new FileInputStream(swaggerFileName);
		    _swagger = IOUtils.toString(swaggerStream, "UTF-8");
		    
		    BaseDAO.setDataPath(dataPath);
		    _injector = Guice.createInjector(new DAOModule());
			_dao = _injector.getInstance(StationDAO.class);
			_swaggerConfiguration = new SwaggerConfigurationDTO();
			_swaggerResources = new SwaggerResourceDTO[] { new SwaggerResourceDTO() };
		} catch (Throwable t) {
			_logger.error("Exception", t);
		}
		if (swaggerStream != null) {
			IOUtils.closeQuietly(swaggerStream);
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET, value="/api/cache", produces="application/json")
	@ResponseBody
	public List<CachedItemDTO> getCachedItems() {
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
					List<CityDTO> cities = null;
					List<ZipDTO> zips = null;
					try {
						stations = (List<StationDTO>)value;
					} catch (Throwable t) {
					}
					try {
						cities = (List<CityDTO>)value;
					} catch (Throwable t) {
					}
					try {
						zips = (List<ZipDTO>)value;
					} catch (Throwable t) {
					}
					CachedItemDTO cachedItem = new CachedItemDTO();
					cachedItem.setKey(key);
					cachedItem.setStations(stations);
					cachedItem.setCities(cities);
					cachedItem.setZips(zips);
					items.add(cachedItem);
				}				
			}
		}
		return items;
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/api/cache")
	public void clearCachedItems() {
		MemoryCacheInterceptor.clearCache();
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/api/cities", produces="application/json")
	@ResponseBody
    public List<CityDTO> cities(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			return _dao.cities();
		} catch (Throwable t) {
			_logger.error("Exception", t);
			throw new Exception(t);
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/api/zips", produces="application/json")
	@ResponseBody
    public List<ZipDTO> zips(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			return _dao.zips();
		} catch (Throwable t) {
			_logger.error("Exception", t);
			throw new Exception(t);
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/api/list", produces="application/json")
	@ResponseBody
    public List<StationDTO> list(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			StringBuffer requestURL = request.getRequestURL();
		    String queryString = request.getQueryString();
		    if ((queryString != null) && (queryString.length() > 0)) {
		        requestURL.append("?");
		        requestURL.append(queryString);
		    }
			String requestURI = requestURL.toString();
			_logger.debug("requestURI: " + requestURI);			
			MultiValueMap<String, String> queryParameters = UriComponentsBuilder.fromUri(new URI(requestURI)).build().getQueryParams();
			String address = null;
			String city = null;
			String state = null;
			String zip = null;
			boolean hasRadisonde = false;
			Set<String> queryKeys = queryParameters.keySet();
			for (String key : queryKeys) {
				_logger.debug("key: " + key);
				String value = null;
				List<String> values = queryParameters.get(key);
				if ((values != null) && (values.size() > 0)) {
					value = URLDecoder.decode(values.get(0), "UTF-8");
				}
				if ((value != null) && (value.length() > 0)) {
					_logger.debug("value: " + value);
					if (key.compareToIgnoreCase("address") == 0) {
						address = value;
					} else if (key.compareToIgnoreCase("city") == 0) {
						city = value;
					} else if (key.compareToIgnoreCase("state") == 0) {
						state = value;
					} else if (key.compareToIgnoreCase("zip") == 0) {
						zip = value;
					} else if (key.compareToIgnoreCase("hasRadisonde") == 0) {
						hasRadisonde = Boolean.parseBoolean(value);
					}
				}
			}
			List<StationDTO> stations = null;
			if ((address != null) && (address.length() > 0)) {
				stations = _dao.list(address, hasRadisonde);
			} else {
				stations = _dao.list(city, state, zip, hasRadisonde);
			}
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(stations);
			_logger.debug(json);
			return stations;
		} catch (Throwable t) {
			_logger.error("Exception", t);
			throw new Exception(t);
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/api/{id}", produces="application/json")
	@ResponseBody
    public StationDTO readById(@PathVariable String id, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			return _dao.read(id);
		} catch (Throwable t) {
			_logger.error("Exception", t);
			throw new Exception(t);
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/api/{latitude}/{longitude}/nearest", produces="application/json")
	@ResponseBody
    public StationDTO readNearest(@PathVariable String latitude, @PathVariable String longitude, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			return _dao.readNearest(Double.parseDouble(latitude), Double.parseDouble(longitude), 25, "statute miles");
		} catch (Throwable t) {
			_logger.error("Exception", t);
			throw new Exception(t);
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/api/{latitude}/{longitude}/nearestWithRadiosonde", produces="application/json")
	@ResponseBody
    public StationDTO readNearestWithRadiosonde(@PathVariable String latitude, @PathVariable String longitude, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			return _dao.readNearest(Double.parseDouble(latitude), Double.parseDouble(longitude), 25, "statute miles", true);
		} catch (Throwable t) {
			_logger.error("Exception", t);
			throw new Exception(t);
		}
	}
	
	@RequestMapping(value="/swagger-resources/configuration/ui", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public SwaggerConfigurationDTO getSwaggerConfigurationUI() {
		return _swaggerConfiguration;
	}
	
	@RequestMapping(value="/swagger-resources", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public SwaggerResourceDTO[] getSwaggerResources() {
		return _swaggerResources;
	}
	
	@RequestMapping(value="/v2/api-docs", method=RequestMethod.GET, produces="application/json")
	public void getSwagger(Writer responseWriter) throws IOException {
		responseWriter.write(_swagger);
	}
}
