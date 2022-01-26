package floorsubsystem;

import scheduler.FloorEvent;
import scheduler.Scheduler;

import java.util.PriorityQueue;

public class Floor extends Thread {
	
	private final int floorNumber;
	private final Scheduler scheduler;
	private final PriorityQueue<FloorEvent> eventQueue;
	
	public Floor(int floorNumber, Scheduler scheduler, PriorityQueue<FloorEvent> eventQueue) {
		this.floorNumber = floorNumber;
		this.scheduler = scheduler;
		this.eventQueue = eventQueue;
	}
		
	@Override
	public void run() {
		while(scheduler.hasEvents()) {
            if(this.isPriorityFloor())
            {
            	FloorEvent currentFloorEvent = eventQueue.peek();
            	
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
                
                //TODO Fix handleFloorEvent in scheduler
                scheduler.handleFloorEvent(eventQueue.peek());
                eventQueue.poll();
                
            }
        }
	}

	public int getFloorNumber() {
		return floorNumber;
	}
	
	private boolean isPriorityFloor() {
		return (!eventQueue.isEmpty() && eventQueue.peek().getTimeLeftTillEvent() <= scheduler.getElapsedTime() && scheduler.getEventQueue().peek().equals(eventQueue.peek()));
	}
	


}