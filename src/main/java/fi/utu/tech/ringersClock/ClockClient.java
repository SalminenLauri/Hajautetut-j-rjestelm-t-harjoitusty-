package fi.utu.tech.ringersClock;

/*
 * A class for handling network related stuff
 */

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClockClient extends Thread {

	private String host;
	private int port;
	private Gui_IO gio;
	private ObjectOutputStream outStream;
	private ObjectInputStream inStream;
	private WakeUpGroup wakeUpGroup;
	Socket clientSocket = null;


	public ClockClient(String host, int port, Gui_IO gio) {
		this.host = host;
		this.port = port;
		this.gio = gio;
	}

	public void run() {
		System.out.println("Host name: " + host + " Port: " + port + " Gui_IO:" + gio.toString());

		try {
			Socket clientSocket = new Socket(host, port);
			System.out.println("Soketti luotu");
			gio.setClient(this);
			outStream = new ObjectOutputStream(clientSocket.getOutputStream());
			inStream = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Streamit luotu");

			//lähetä client portti, että saadaan lista tulemaan?
			
			
			//hae ryhmälista
			do {
				Object viesti = inStream.readObject();
				try{
					gio.fillGroups((ArrayList<WakeUpGroup>) viesti);
				}catch(Exception e) {
					WakeUpGroup wug = (WakeUpGroup) viesti;
					//System.out.println(((WakeUpGroup) viesti).commandID + " commandid");
					switch (wug.commandID) {
						case 1:
							gio.appendToStatus("Joined group " + wug);
							gio.setAlarmTime(wug.getTime());
							break;
						case 200:
							gio.appendToStatus("Resigned from group " + wug);
							gio.clearAlarmTime();
							break;
						case 20:
							System.out.println("case 20");
							gio.confirmAlarm(wug);
							break;
						case 9:
							System.out.println("case 9");
							gio.alarm();
							break;
						case 22:
							System.out.println("case 22");
							gio.CancelAlarm(wug);
							break;
					}
				}
			} while (true);
		}catch (Exception excpetion) {System.out.println("Joku häikkä clockclient");}
	}
	public void sendMessage(WakeUpGroup message) {
		try {
			//System.out.println("Sendmessage aktivoituu ClockClientissa");
			outStream.reset();
			outStream.writeObject(message);
			outStream.flush();
			System.out.println("Viesti lähti ClockClientista " + message);
		} catch (IOException ioe){System.out.println("Send message clockclient ei toimi 1");}
	}


}
