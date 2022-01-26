package floorsubsystem;

import scheduler.Event;
import scheduler.Scheduler;

import java.util.List;
import java.util.PriorityQueue;

import common.Person;

public class Floor extends Thread {
	
	private final int floorNumber;
	private final Scheduler scheduler;
	private final PriorityQueue<Event> eventQueue;
	
	public Floor(int floorNumber, Scheduler scheduler, PriorityQueue<Event> eventQueue) {
		this.floorNumber = floorNumber;
		this.scheduler = scheduler;
		this.eventQueue = eventQueue;
	}
		
	@Override
	public void run() {
		while(scheduler.hasEvents()) {
            if(!eventQueue.isEmpty() && eventQueue.peek().getTimeLeftTillEvent() <= scheduler.getElapsedTime() && scheduler.getEventQueue().peek().equals(eventQueue.peek()))
            {
                //TODO implement 
            }

        }
	}

	public int getFloorNumber() {
		return floorNumber;
	}

}