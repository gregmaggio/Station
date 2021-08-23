/**
 * 
 */
package ca.datamagic.station.servlet;

import java.text.MessageFormat;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ca.datamagic.station.dao.BaseDAO;
import ca.datamagic.station.dao.StationDAO;
import ca.datamagic.station.inject.DAOModule;

/**
 * @author Greg
 *
 */
public class StationContextListener implements ServletContextListener {
	private static Logger logger = LogManager.getLogger(StationContextListener.class);
	private static Injector injector = null;
	private static StationDAO dao = null;
	
	public static StationDAO getDAO() {
		return dao;
	}
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String realPath = sce.getServletContext().getRealPath("/");
		String dataPath = MessageFormat.format("{0}/WEB-INF/classes/data", realPath);
		BaseDAO.setDataPath(dataPath);
		injector = Guice.createInjector(new DAOModule());
		dao = injector.getInstance(StationDAO.class);
		logger.debug("contextInitialized");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.debug("contextDestroyed");
	}
}
