package fi.utu.tech.weatherInfo;

/*
 * Class presenting current weather
 * Is returned by  weather service class
 */

public class WeatherData {

	/*
	 * What kind of data is needed? What are the variable types. Define class
	 * variables to hold the data
	 */

	/*
	 * Since this class is only a container for weather data we only need to set the
	 * data in the constructor.
	 */
	double lämpötila;
	double sade;
	public WeatherData(String s,String l) {
		lämpötila=Double.valueOf(l);
		if (s == "Nan"){
			sade = 0.0;
		}
		else {
			sade = Double.valueOf(s);
		}

	}

	//palauttaa onko pakkasta
	public boolean GetTemp(){
		if(lämpötila>0)
			return true;
		else
			return false;

	}

	//paulauttaa sataako
	public boolean GetRain(){
		if (sade>0)
			return true;
		else
			return false;
	}


}
