package floorsubsystem;

import scheduler.FloorEvent;
import scheduler.FloorEventComparator;
import scheduler.Scheduler;

import java.util.PriorityQueue;

/**
 * This class represents the floor and is responsible for sending FloorEvents to the scheduler class.
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 * 
 */
public class Floor extends Thread {
	
	private final int floorNumber;
	private final Scheduler scheduler;
	private final PriorityQueue<FloorEvent> floorEventQueue;
	
	/**
	 * Parameterized constructor
	 * 
	 * @param floorNumber, the floor's number
	 * @param scheduler, the scheduler associated with the floor
	 */
	public Floor(int floorNumber, Scheduler scheduler) {
		this.floorNumber = floorNumber;
		this.scheduler = scheduler;
		this.floorEventQueue = new PriorityQueue<>(new FloorEventComparator());
	}
		
	/**
	 * Run method (called upon thread start) responsible for sending FloorEvents to scheduler
	 */
	@Override
	public void run() {
		while(scheduler.hasEvents() && scheduler.isAlive()) {
			handlePriorityFloorEvent();
        }
		System.out.println("FLOOR THREAD #" + this.getFloorNumber() + " IS DONE");
	}
	
	/**
	 * Returns true if the there are events in the eventQueue
	 * 
	 * @return boolean - false if empty, true otherwise
	 */
	public boolean hasEvents() {
		return !floorEventQueue.isEmpty();
	}

	/**
	 * Getter for floorNumber attribute
	 * 
	 * @return int - the floorNumber attribute
	 */
	public int getFloorNumber() {
		return floorNumber;
	}
	
	/**
	 * Checks if it is time for the FloorEvent at the top of the queue to be sent to scheduler
	 */
	private synchronized void handlePriorityFloorEvent() {
		if (floorEventQueue.peek() != null) {
			if (floorEventQueue.peek().getTimeLeftTillEvent() <= scheduler.getElapsedTime()) {
				FloorEvent currentFloorEvent = floorEventQueue.poll();
            	
                if(currentFloorEvent.getFloor() instanceof MiddleFloor) {
                	if(currentFloorEvent.isUpButton()) {
                		((MiddleFloor)(currentFloorEvent.getFloor())).turnOnUpButton();
                	} else {
                		((MiddleFloor)(currentFloorEvent.getFloor())).turnOnDownButton();
                	}
                } else if(currentFloorEvent.getFloor() instanceof TopFloor){
                	((TopFloor)(currentFloorEvent.getFloor())).turnOnDownButton();
                } else {
                	((BottomFloor)(currentFloorEvent.getFloor())).turnOnUpButton();
                }
                
                scheduler.addEvents(currentFloorEvent);
			}
		}
	}
	
	/**
	 * Adds a FloorEvent to the event queue
	 * 
	 * @param floorEvent, the FloorEvent to be added to the queue
	 */
	public void addFloorEvent(FloorEvent floorEvent) {
		this.floorEventQueue.add(floorEvent);
	}
	
}