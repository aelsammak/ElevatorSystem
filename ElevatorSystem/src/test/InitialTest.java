package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import org.junit.Test;

import elevatorsubsystem.Elevator;
import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.Floor;
import floorsubsystem.FloorSubsystem;
import scheduler.ElevatorEvent;
import scheduler.FloorEvent;
import scheduler.Scheduler;
/**
 * File contains test cases specifically focused on testing reading and parsing of data from csv's to the floor and elevator subsystems. 
 * @author Ben Herriott
 * @version 1.0 
 */
public class InitialTest {
	private static final String FILE_NAME = "simulation.csv";
	
	/**
	 * Test case for reading floor events within the simulation from the csv and parsing the data to
	 * the relevant floors. Tests floor number, direction of elevator request and time of event 
	 */
	@Test
	public void test() {
		System.out.println("Starting floor event parse test");
		Scheduler scheduler = new Scheduler();
        try {
        	// reads the csv and creates floors
            FloorSubsystem.generateFloorsAndEvents(scheduler, FILE_NAME);
            List<Floor> f = scheduler.getFloors();
            
            // peek into the event queues of the floors that will be populated based on the csv
            PriorityQueue<FloorEvent> floor2 = f.get(1).getFloorEventQueue();
            PriorityQueue<FloorEvent> floor3 = f.get(2).getFloorEventQueue();
            PriorityQueue<FloorEvent> floor4 = f.get(3).getFloorEventQueue();
            
            // check the second floor has the correct events and then the remaining floors
            assertEquals(2, floor2.peek().getFloor().getFloorNumber()); 
            assertEquals(true, floor2.peek().isUpButton());
            assertEquals(5, floor2.peek().getTimeLeftTillEvent());
            
            assertEquals(3, floor3.peek().getFloor().getFloorNumber()); 
            assertEquals(true, floor3.peek().isUpButton());
            assertEquals(40, floor3.peek().getTimeLeftTillEvent());
            
            assertEquals(4, floor4.peek().getFloor().getFloorNumber()); 
            assertEquals(false, floor4.peek().isUpButton());
            assertEquals(20, floor4.peek().getTimeLeftTillEvent());
            System.out.println("Finish");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Tests elevator events (button pushes to a destination) can be read correctly from the provided csv.
	 * Tests the destination floor is read correctly and that the event time is read correctly and parsed into the elevator event queues.
	 */
	@Test
	public void test1() {
		System.out.println("Starting elevator event parse test");
		Scheduler scheduler = new Scheduler();
        try {
            FloorSubsystem.generateFloorsAndEvents(scheduler, FILE_NAME);
            Elevator elevator = new Elevator(1, scheduler); // this time create an elevator
            assertEquals(false, elevator.hasEvents()); // check the elevator event queue is empty
            
            //read the csv's contents into the elevator subsystem
            ElevatorSubsystem.generateElevatorEvents(scheduler, FILE_NAME, elevator);
            scheduler.addElevator(elevator);
            
            // fetch all events in the elevators priority queue and output them as sorted
            PriorityQueue<ElevatorEvent> pq = elevator.getElevatorEventQueue();
            ElevatorEvent[] events = elevator.getElevatorEventQueue().toArray(new ElevatorEvent[pq.size()]);
            Arrays.sort(events, pq.comparator());
            
            // Elevator events are sorted by time till event, check to see if destination is read correctly and the time
            // Check the destination floor has been parsed and read correctly and the same for the time till event
            assertEquals(3, events[0].getDestinationFloor().getFloorNumber()); 
            assertEquals(5, events[0].getTimeLeftTillEvent());
            
            assertEquals(1, events[1].getDestinationFloor().getFloorNumber()); 
            assertEquals(20, events[1].getTimeLeftTillEvent());
            
            assertEquals(5, events[2].getDestinationFloor().getFloorNumber()); 
            assertEquals(40, events[2].getTimeLeftTillEvent());
            System.out.println("Finish");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}
}
