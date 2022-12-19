package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;
import fi.utu.tech.weatherInfo.FMIWeatherService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WakeUpService extends Thread {


	public List<WakeUpGroup> wugList = new CopyOnWriteArrayList<>();
	private FMIWeatherService sää = new FMIWeatherService();;
	private ServerSocketListener ss;

	public WakeUpService() {

	}

	public void run() {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

		Runnable task = () -> {
			try {
				Instant now = Instant.now();
				System.out.println("tarkistetaan herätyksiä ");
				for (WakeUpGroup ryhmä : wugList) {
					if (ryhmä.getTime().isBefore(now)==false) {
						System.out.println("Ei ole herätysaika");
						continue;
					}
					System.out.println("On herätysaika");
					boolean herätys = false;
					if (!(ryhmä.getnoRain() && sää.getWeather().GetRain()) && !(ryhmä.getTemp() && sää.getWeather().GetTemp())) { //Muistutus meille
						herätys = true;
						System.out.println("Herätysehdot täyttyvät");
					}
					if (herätys) {
						//aloita herätys
						System.out.println("Aloitetaan herätys lähettämälläjohtajalle viesti");
						herätetäänkö(ryhmä);
					} else {
						//Selvitetään ryhmän johtajan clientin indeksi clienttilistassa
						int johtaja = 0;
								for (int i =0; i<ss.getListenerMembers().size(); i++){
									if (ss.getListenerMembers().get(i).getClientSocket().getPort()==ryhmä.getMembers().get(0)){
										johtaja = i;
									}
								}
						//Poistetaan johtaja ja koko ryhmä, koska herätyksen ehdot eivät täyty
						poistuRyhmästä(ss.getListenerMembers().get(johtaja)); //Poistaa ryhmän johtajan ryhmästä
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);
	}

	public void herätetäänkö(WakeUpGroup ryhmä){
		//tossa omistaja laitetaan GUI käsky confirmalarm()
		//putki clockclientiiin
		ryhmä.setCommandID(20);
		ss.lähetäviesti(ryhmä, ryhmä.getMembers().get(0));
	}
	public void herätä(WakeUpGroup ryhmä){
		ryhmä.setCommandID(9);
		for(Integer member:ryhmä.getMembers()) {
			ss.lähetäviesti(ryhmä, member);
		}
		
		//Selvitetään ryhmän johtajan clientin indeksi clienttilistassa
		int johtaja = 0;
				for (int i =0; i<ss.getListenerMembers().size(); i++){
					if (ss.getListenerMembers().get(i).getClientSocket().getPort()==ryhmä.getMembers().get(0)){
						johtaja = i;
					}
				}
		System.out.println("Johtajan indeksi " + johtaja);
		
		
		poistuRyhmästä(ss.getListenerMembers().get(johtaja)); //Poistaa ryhmän johtajan ryhmästä
	}

	public void poistaHerätys(WakeUpGroup ryhmä, Integer member){
		ryhmä.setCommandID(200);

		ss.lähetäviesti(ryhmä, member);
	}
	public void päivitäryhmät(WakeUpGroup ryhmä, Integer member){
		ryhmä.setCommandID(22);
		ss.lähetäviesti(ryhmä, member);
	}

	public void poistuRyhmästä(ClientHandler client){

		try {
			for (WakeUpGroup ryhmä : wugList) {
				//delete group if host leave
				if (ryhmä.getMembers().get(0).equals(client.getClientSocket().getPort())) {
					for (Integer member : ryhmä.getMembers()) {
						poistaHerätys(ryhmä, member);
					}
					wugList.remove(ryhmä);
				} else {
					//lähetä  viesti et on poistettu ryhmästä ja nollaa kellon
					poistaHerätys(ryhmä, client.getClientSocket().getPort());
					ryhmä.removeMember(client.getClientSocket().getPort());
				}
			}
		}catch (Exception e){e.printStackTrace();}
	}
	//luo ryhmä
	public void luoRyhmä(WakeUpGroup ryhmä, ClientHandler client){
		System.out.println("Yrittää luoda ryhmän");
		//System.out.println(client.getClientSocket().getPort() + " portti");
		//System.out.println("Wug add yritys");

		boolean onJoRyhmässä = false;
		for (WakeUpGroup wug:wugList) {
			if(wug.getMembers().contains(client.getClientSocket().getPort())){
				onJoRyhmässä = true;
			}
		}
		if(onJoRyhmässä){
			//tarkista ettei käyttäjä ole jo ryhmässä
			System.out.println("This is a duplicate");
		}else {
			wugList.add(ryhmä);
			liityRyhmään(ryhmä, client);
		}
		ss.lähetäryhmät(wugList);
	}


	public void liityRyhmään(WakeUpGroup ryhmä,ClientHandler client){

		for (WakeUpGroup group:wugList) {
			System.out.println("Liitytään ryhmään ryhmä "+ryhmä.getName());
			//System.out.println("Liitytään ryhmään wugi "+group.getName());
			if (group.getName().equals(ryhmä.getName())){
				group.members.add(client.getClientSocket().getPort());
				ryhmä.setCommandID(1); //set to 1 for gio.appendToStatus("Joined group " + wug);
				ss.lähetäviesti(ryhmä,client.getClientSocket().getPort());
			}
		}
	}


	public List<WakeUpGroup> getWugList() {
		return wugList;
	}

	public void setSs(ServerSocketListener ss) {
		this.ss = ss;
	}
}
