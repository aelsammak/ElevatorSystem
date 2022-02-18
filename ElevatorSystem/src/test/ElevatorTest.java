package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import common.Common;
import common.Config;
import elevatorsubsystem.Elevator;
import floorsubsystem.Floor;
import floorsubsystem.FloorSubsystem;
import scheduler.ElevatorEvent;
import scheduler.FloorEvent;
import scheduler.Scheduler;

/**
 * Test for elevator movements to both floor and elevator events. Floor event moving the elevator to someone's floor based on a floor button click.
 * Elevator event a person enters the elevator and clicks a floor number test whether the elevator moves to both floors correctly. 
 * 
 * @author Ben Herriott
 * @version 1.0
 */
public class ElevatorTest {
	
	private Scheduler scheduler;
	private ArrayList<Floor> f;
	private Elevator e;
	private FloorEvent floorEv1; 
	private ElevatorEvent elevatorEv1; 
	
	@Before
	public void initElevatorTest() {
		scheduler = new Scheduler();
        f = new ArrayList<Floor>(); 
		Common.setMaxNumFloors(3);
		Config config = null;
		try {
			config = new Config();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        FloorSubsystem.createFloors(4, scheduler, f);
		e = new Elevator(1, scheduler, config); 
		scheduler.addElevator(e);
		floorEv1 = new FloorEvent(f.get(1), true, 5);
		elevatorEv1 = new ElevatorEvent(e, f.get(2), 5);
	}
	
	@Test
	public void moveElevatorToFloorTest() throws InterruptedException, IOException {
		// create the scheduler and mimic elevator and floor setup
		System.out.println("Starting elevator movement test");
		// check there are no floor events
		assertEquals(0, f.get(1).getFloorEventQueue().size()); 
		
		// add a floor event
		f.get(1).addFloorEvent(floorEv1);
		assertEquals(1, f.get(1).getFloorEventQueue().size());
		
		// check the event queue for the elevator is empty 
		assertEquals(0, e.getElevatorEventQueue().size());
		
		// add an elevator event 
		e.addElevatorEvent(elevatorEv1);
		assertEquals(1, e.getElevatorEventQueue().size());
		
		// check the elevator is on the very bottom floor
		assertEquals(1, e.getFloorNumber()); 
		
		// mimic 5 seconds passing so it's time to move to the first event
		scheduler.setElapsedTime(5); 
		f.get(1).handlePriorityFloorEvent();
		
		// ensure that the events has been removed as they're being handled by the scheduler
		assertEquals(0, f.get(1).getFloorEventQueue().size());
		assertEquals(0, e.getElevatorEventQueue().size());
		assertTrue(scheduler.hasEvents()); 
		
		// mimic floor event
		e.moveToFloor(floorEv1.getFloor());
		assertEquals(2, e.getFloorNumber()); // check the elevator has moved to floor 2 from 1
		
		// elevator Event
		e.moveToFloor(elevatorEv1.getDestinationFloor());
		assertEquals(3, e.getFloorNumber()); // check the elevator has moved to floor 3 from floor 2
		System.out.println("Finish");
    }
}
