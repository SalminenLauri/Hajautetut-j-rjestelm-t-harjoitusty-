package fi.utu.tech.ringersClockServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

import fi.utu.tech.ringersClock.entities.WakeUpGroup;

public class ClientHandler extends Thread {

    private Socket clientSocket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;
    private WakeUpService wup;

    public ClientHandler(Socket clientSocket, WakeUpService wup) {
        this.clientSocket = clientSocket;
        this.wup=wup;
    }

    public void run() {
        System.out.println("Client handler käynnissä");

        try {
            outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            inStream = new ObjectInputStream(clientSocket.getInputStream());
            try {
            	sendGroups(wup.wugList);
                while(true) {
                    WakeUpGroup message = (WakeUpGroup) inStream.readObject();

                    switch(message.getCommandID()) {       
                        case(1):
                            System.out.println("Case 1/luoryhmä aktivoituu Clienthandlerissa");
                            wup.luoRyhmä(message,this);
                            sendGroups(wup.wugList);
                            break;
                        case(2):
                            System.out.println("Case 2/liityryhmään aktivoituu Clienthandlerissa");
                            wup.liityRyhmään(message,this);
                            sendGroups(wup.wugList);
                            break;
                        case(3):
                            System.out.println("Case 3/poisturyhmästä aktivoituu Clienthandlerissa");
							wup.poistuRyhmästä(this);
                            sendGroups(wup.wugList);
                            break;
                        case(22): //Tätä ei käytetä tällä hetkellä, koska herätyksen peruttaessa ryhmä nollataan kokonaan
                            System.out.println("Case 22/poistaherätys aktivoituu Clienthandlerissa");
                            wup.poistaHerätys(message, clientSocket.getPort());
                            sendGroups(wup.wugList);
                            break;
                        case(5):
                            System.out.println("Case 5/herätä aktivoituu Clienthandlerissa");
                            wup.herätä(message);
                            sendGroups(wup.wugList);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception en) {en.printStackTrace();}
    }

    public void sendMessage(WakeUpGroup message) {
        try {
            //System.out.println("Sendmessage aktivoituu ClientHandlerissa");
            outStream.reset();
            outStream.writeObject(message);
            outStream.flush();
            System.out.println("Viesti lähti ClientHandlerista " + message);
        } catch (IOException ioe){System.out.println("Send message ClientHandler ei toimi ");}
    }
    public void sendGroups(List<WakeUpGroup> message) {
        try {
            ArrayList<WakeUpGroup> msg = new ArrayList<>(message);
            System.out.println("Lähetetään kaikille uudet ryhmät");
            outStream.reset();
            outStream.writeObject(msg);
            outStream.flush();
            //System.out.println("Ryhmät lähti ClockClientista " + message);
        } catch (IOException ioe){System.out.println("Send groups ClientHandler ei toimi ");}
    }
    public Socket getClientSocket() {
        return clientSocket;
    }
}
