package fi.utu.tech.ringersClock.entities;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

/*
 * Entity class presenting a WakeUpGroup. The class is not complete.
 * You need to add some variables.
 */

public class WakeUpGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private Integer ID;
	public Integer commandID;

	private int hour;
	private int minutes;
	private boolean norain;
	private boolean temp;
	private Instant time;
	public ArrayList<Integer> members = new ArrayList<Integer>();

	public WakeUpGroup(Integer id, String name) {
		super();
		this.ID = id;
		this.name = name;
	}
	public WakeUpGroup(String name, Integer hour, Integer minutes, boolean norain, boolean temp){
		this.minutes=minutes;
		this.hour=hour;
		this.name=name;
		this.norain=norain;
		this.temp=temp;
		aika(hour,minutes);
		System.out.println("Ryhmä luotu aika "+time);
	}

	public Instant getTime() {
		return time;
	}

	public void removeMember(Integer member){
		members.remove(member);
	}

	public void setCommandID(Integer commandID) {
		this.commandID = commandID;
	}

	public Integer getCommandID() {
		return commandID;
	}

	public ArrayList<Integer> getMembers() {
		return members;
	}

	public String getName() {
		return this.name;
	}
	public Integer getHour(){
		return this.hour;
	}
	public Integer getMinutes(){
		return this.minutes;
	}
	public Integer getID() {
		return this.ID;
	}
	public boolean getnoRain(){
		return this.norain;
	}
	public boolean getTemp(){
		return this.temp;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setID(Integer ID) {
		this.ID = ID;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public void aika(int hour, int minutes){

		// get the current instant
		Instant now = Instant.now();

		// create a LocalDateTime object representing the current date and time
		LocalDateTime localDateTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

		// add one day to the LocalDateTime object
		if(localDateTime.getHour()>=hour && localDateTime.getMinute()>=minutes) {
			localDateTime = localDateTime.plusDays(1);
		}

		// set the hour and minute of the LocalDateTime object
		localDateTime = localDateTime.withHour(hour);
		localDateTime = localDateTime.withMinute(minutes);
		localDateTime = localDateTime.withSecond(0);
		localDateTime = localDateTime.withNano(0);
		// convert the LocalDateTime object back to an Instant object
		Instant dayInstant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		System.out.println("hour + min "+ hour+ "   "+minutes+" localtime " +dayInstant);
		time = dayInstant;
	}

}
