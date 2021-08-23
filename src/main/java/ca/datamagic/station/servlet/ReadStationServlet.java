/**
 * 
 */
package ca.datamagic.station.servlet;

import java.io.IOError;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import ca.datamagic.station.dto.StationDTO;

/**
 * @author Greg
 *
 */
public class ReadStationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(ReadStationServlet.class);
	private static final Pattern readNearestPattern = Pattern.compile("/(?<latitude>[+-]?([0-9]*[.])?[0-9]+)/(?<longitude>[+-]?([0-9]*[.])?[0-9]+)/nearest", Pattern.CASE_INSENSITIVE);
	private static final Pattern readPattern = Pattern.compile("/(?<identifier>\\w+)", Pattern.CASE_INSENSITIVE);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String pathInfo = request.getPathInfo();
			logger.debug("pathInfo: " + pathInfo);
			Matcher readNearestMatcher = readNearestPattern.matcher(pathInfo);
			if (readNearestMatcher.find()) {
				logger.debug("readNearest");
				String latitude = readNearestMatcher.group("latitude");
				logger.debug("latitude: " + latitude);
				String longitude = readNearestMatcher.group("longitude");
				logger.debug("longitude: " + longitude);
				double doubleLatitude = Double.parseDouble(latitude);
				double doubleLongitude = Double.parseDouble(longitude);
				StationDTO station = StationContextListener.getDAO().readNearest(doubleLatitude, doubleLongitude, 25, "statute miles");
				String json = (new Gson()).toJson(station);
				response.setContentType("application/json");
				response.getWriter().println(json);
				return;
			}
			
			Matcher readMatcher = readPattern.matcher(pathInfo);
			if (readMatcher.find()) {
				logger.debug("read");
				String identifier = readMatcher.group("identifier");
				logger.debug("identifier: " + identifier);
				StationDTO station = StationContextListener.getDAO().read(identifier);
				String json = (new Gson()).toJson(station);
				response.setContentType("application/json");
				response.getWriter().println(json);
				return;
			}
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Throwable t) {
			logger.error("Exception", t);
			throw new IOError(t);
		}
	}
}
