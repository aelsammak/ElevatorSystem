package floorsubsystem;

import java.util.List;
import common.Person;

public class Floor extends Thread {
	
	private final int floorNumber;
	//private final Scheduler scheduler;
	private List<Person> peopleWaiting;
	
	
	public Floor(int floorNumber, List<Person> peopleWaiting) {
		this.floorNumber = floorNumber;
		this.peopleWaiting = peopleWaiting;
		//this.scheduler = scheduler ---------add as parameter 
	}
		
	@Override
	public void run() {
		
	}

	public int getFloorNumber() {
		return floorNumber;
	}

	public List<Person> getPeopleWaiting() {
		return peopleWaiting;
	}
}
