package fi.utu.tech.ringersClockServer;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketListener extends Thread {

	private String host;
	private int port;
	private WakeUpService wup;
	private ArrayList<ClientHandler> members;

	public ServerSocketListener(String host, int port, WakeUpService wup) {
		this.host = host;
		this.port = port;
		this.wup = wup;
		members = new ArrayList<>();

	}

	public void run() {
		System.out.println("Listener pyörii");
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server soketti luotu");
			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Kuuli kutsun ja luo handlerin");
				ClientHandler handler = new ClientHandler(clientSocket, wup);
				handler.start();
				members.add(handler);
			}
		} catch (IOException serverException) {
			System.out.println("Joku ei toimi listenerissä");
			serverException.printStackTrace();
		}
	}

	public void lähetäviesti(WakeUpGroup wakeUpGroup, Integer port){
		//System.out.println("lähetäviesti aktibvoitu");
		System.out.println(members);
		for (ClientHandler c: members) {
			if(c.getClientSocket().getPort()==port){
				c.sendMessage(wakeUpGroup);
			}
		}
	}
	public void lähetäryhmät(List<WakeUpGroup> message) {
		for (ClientHandler client:members) {
			client.sendGroups(message);
		}
	}
}
