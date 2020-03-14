/**
 * 
 */
package ca.datamagic.station.dao;

import java.io.IOException;
import java.text.MessageFormat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Greg
 *
 */
public class NOAAForecastPage {
	private static final String HREF_ATTRIBUTE = "href";
	private static final String WFO_DOMAIN = "forecast.weather.gov";
	private static final String WFO_ID = "site=";
	private static final String RADAR_DOMAIN = "radar.weather.gov";
	private static final String RADAR_ID = "rid=";
	private static final String AMP = "&";
	private Document document = null;
	private Elements anchors = null;
	
	public NOAAForecastPage(double latitude, double longitude) throws IOException {
		String uri = MessageFormat.format("https://forecast.weather.gov/MapClick.php?lat={0}&lon={1}", Double.toString(latitude), Double.toString(longitude));
		this.document = Jsoup.connect(uri).get();
		this.anchors = this.document.getElementsByTag("a");
	}
	
	public String getWFO() {
		for (int ii = 0; ii < this.anchors.size(); ii++) {
			Element element = this.anchors.get(ii);
			if (element.hasAttr(HREF_ATTRIBUTE)) {
				String href = element.attr(HREF_ATTRIBUTE).toLowerCase();
				if (href.contains(WFO_DOMAIN)) {
					int startIndex = href.indexOf(WFO_ID);
					if (startIndex > -1) {
						int endIndex = href.indexOf(AMP, startIndex);
						return href.substring(startIndex + 5, endIndex).toUpperCase();
					}
				}
			}
		}
		return null;
	}
	
	public String getRadar() {
		for (int ii = 0; ii < this.anchors.size(); ii++) {
			Element element = this.anchors.get(ii);
			if (element.hasAttr(HREF_ATTRIBUTE)) {
				String href = element.attr(HREF_ATTRIBUTE).toLowerCase();
				if (href.contains(RADAR_DOMAIN)) {
					int startIndex = href.indexOf(RADAR_ID);
					if (startIndex > -1) {
						int endIndex = href.indexOf(AMP, startIndex);
						return href.substring(startIndex + 4, endIndex).toUpperCase();
					}
				}
			}
		}
		return null;
	}
}
