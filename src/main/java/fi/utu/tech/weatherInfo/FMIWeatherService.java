
package fi.utu.tech.weatherInfo;

		import org.w3c.dom.*;
		import org.xml.sax.*;
		import javax.xml.parsers.*;
		import javax.xml.xpath.*;
		import java.net.URL;


public class FMIWeatherService {

	private final String CapURL = "https://opendata.fmi.fi/wfs?request=GetCapabilities";
	private final String FeaURL = "https://opendata.fmi.fi/wfs?request=GetFeature";
	private final String ValURL = "https://opendata.fmi.fi/wfs?request=GetPropertyValue";
	private final String DataURL = "http://opendata.fmi.fi/wfs?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::forecast::hirlam::surface::point::multipointcoverage&place=turku&";
	/*
	 * In this method your are required to fetch weather data from The Finnish
	 * Meteorological Institute. The data is received in XML-format.
	 */


	public static WeatherData getWeather() {

		/*
		haetaan data url osotteesta <gml:doubleOrNilReasonTupleList> ja sitten parsitaan lista " "
		otetaan indeksi 1 (lämpötila) ja 17 indeksi (sademäärä) jostain syystä tulee aluksi tyhiä nii sade asettuu indeksiin 17
		 */
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL("http://opendata.fmi.fi/wfs/fin?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::multipointcoverage&place=turku&").openStream());
			doc.getDocumentElement().normalize();
			NodeList tupleListElements = doc.getElementsByTagName("gml:doubleOrNilReasonTupleList");

			// get the first and seventh gml:doubleOrNilReasonTupleList element
			Node firstTupleListElement = tupleListElements.item(0).getFirstChild();
			String name = tupleListElements.item(0).getFirstChild().getTextContent();
			name.replaceAll(" ", "");
			// get the string value of the first and seventh gml:doubleOrNilReasonTupleList element
			String lämpötila = name.substring(17,20);
			//System.out.println("testi"+ lämpötila + " lämpörtila");
			//seittemäs muuttuja ja jokases 3 characterii
			String sade= name.substring(3*7+23,3*7+26);
			//System.out.println(sade+ " sade");
			WeatherData weatherData = new WeatherData(sade, lämpötila);

			return weatherData;


		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

