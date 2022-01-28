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
=======
package floorsubsystem;

import scheduler.FloorEvent;
import scheduler.Scheduler;

import java.util.LinkedList;
import java.util.Queue;

public class Floor extends Thread {
	
	private final int floorNumber;
	private final Scheduler scheduler;
	private final Queue<FloorEvent> floorEventQueue;
	
	public Floor(int floorNumber, Scheduler scheduler) {
		this.floorNumber = floorNumber;
		this.scheduler = scheduler;
		this.floorEventQueue = new LinkedList<FloorEvent>();
	}
		
	@Override
	public void run() {
		while(scheduler.hasEvents()) {
            if(this.isPriorityFloorEvent()) {
            	FloorEvent currentFloorEvent = floorEventQueue.peek();
            	
                if(currentFloorEvent.getFloor() instanceof MiddleFloor) {
                	if(currentFloorEvent.isUpButton()) {
                		((MiddleFloor)(currentFloorEvent.getFloor())).pressUpButton();
                	} else {
                		((MiddleFloor)(currentFloorEvent.getFloor())).pressDownButton();
                	}
                } else if(currentFloorEvent.getFloor() instanceof TopFloor){
                	((TopFloor)(currentFloorEvent.getFloor())).pressDownButton();
                } else {
                	((BottomFloor)(currentFloorEvent.getFloor())).pressUpButton();
                }
                
                scheduler.addEvents(currentFloorEvent);
                floorEventQueue.poll();
            }
        }
	}
	
	public boolean hasEvents() {
		return !floorEventQueue.isEmpty();
	}

	public int getFloorNumber() {
		return floorNumber;
	}
	
	private boolean isPriorityFloorEvent() {
		return (!floorEventQueue.isEmpty() && floorEventQueue.peek().getTimeLeftTillEvent() <= scheduler.getElapsedTime());
	}
	
	public void addFloorEvent(FloorEvent floorEvent) {
		this.floorEventQueue.add(floorEvent);
	}
	
}
