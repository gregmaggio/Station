/**
 * 
 */
package ca.datamagic.station.servlet;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import ca.datamagic.station.dto.StationDTO;

/**
 * @author Greg
 *
 */
public class ListStationsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(ListStationsServlet.class);

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			String state = request.getParameter("state");
			List<StationDTO> stations = StationContextListener.getDAO().list(state);
			String json = (new Gson()).toJson(stations);
			response.setContentType("application/json");
			response.getWriter().println(json);
		} catch (Throwable t) {
			logger.error("Exception", t);
			throw new IOError(t);
		}
	}
}
