package floorsubsystem;

import scheduler.FloorEvent;
import scheduler.Scheduler;

import java.util.PriorityQueue;

public class Floor extends Thread {
	
	private final int floorNumber;
	private final Scheduler scheduler;
	private final PriorityQueue<FloorEvent> floorEventQueue;
	
	public Floor(int floorNumber, Scheduler scheduler, PriorityQueue<FloorEvent> floorEventQueue) {
		this.floorNumber = floorNumber;
		this.scheduler = scheduler;
		this.floorEventQueue = floorEventQueue;
	}
		
	@Override
	public void run() {
		while(scheduler.hasEvents()) {
            if(this.isPriorityFloorEvent())
            {
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
                
                //TODO Fix handleFloorEvent in scheduler
                scheduler.handleFloorEvent(currentFloorEvent);
                floorEventQueue.poll();
                
            }
        }
	}

	public int getFloorNumber() {
		return floorNumber;
	}
	
	private boolean isPriorityFloorEvent() {
		return (!floorEventQueue.isEmpty() && floorEventQueue.peek().getTimeLeftTillEvent() <= scheduler.getElapsedTime() && scheduler.getEventQueue().peek().equals(floorEventQueue.peek()));
	}
	


}