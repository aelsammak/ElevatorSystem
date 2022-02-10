package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import org.junit.Test;

import common.Config;
import elevatorsubsystem.Elevator;
import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.Floor;
import floorsubsystem.FloorSubsystem;
import scheduler.ElevatorEvent;
import scheduler.FloorEvent;
import scheduler.Scheduler;
/**
 * File contains test cases specifically focused on testing reading and parsing of data from csv's to the floor and elevator subsystems.
 *  
 * @author Ben Herriott
 * @version 1.0 
 */
public class InitialTest {
	
	/**
	 * Test case for reading floor events within the simulation from the csv and parsing the data to
	 * the relevant floors. Tests floor number, direction of elevator request and time of event 
	 * @throws IOException 
	 */
	@Test
	public void test() throws IOException {
		System.out.println("Starting floor event parse test");
		Scheduler scheduler = new Scheduler();
		Config config = new Config();
        try {
        	// reads the csv and creates floors
            FloorSubsystem.generateFloorsAndEvents(scheduler, config.getProperty("csvFileName"));
            List<Floor> f = scheduler.getFloors();
            
            // peek into the event queues of the floors that will be populated based on the csv
            PriorityQueue<FloorEvent> floor2 = f.get(1).getFloorEventQueue();
            PriorityQueue<FloorEvent> floor3 = f.get(2).getFloorEventQueue();
            PriorityQueue<FloorEvent> floor4 = f.get(3).getFloorEventQueue();
            
            // check the second floor has the correct events and then the remaining floors
            assertEquals(2, floor2.peek().getFloor().getFloorNumber()); 
            assertEquals(true, floor2.peek().isUpButton());
            // the first event occurs at the start of the simulation 
            assertEquals(0, floor2.peek().getTimeLeftTillEvent());
            
            assertEquals(4, floor4.peek().getFloor().getFloorNumber()); 
            assertEquals(false, floor4.peek().isUpButton());
            assertEquals(15, floor4.peek().getTimeLeftTillEvent());
            
            assertEquals(3, floor3.peek().getFloor().getFloorNumber()); 
            assertEquals(true, floor3.peek().isUpButton());
            assertEquals(35, floor3.peek().getTimeLeftTillEvent());
            
            System.out.println("Finish");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Tests elevator events (button pushes to a destination) can be read correctly from the provided csv.
	 * Tests the destination floor is read correctly and that the event time is read correctly and parsed into the elevator event queues.
	 * @throws IOException 
	 */
	@Test
	public void test1() throws IOException {
		System.out.println("Starting elevator event parse test");
		Scheduler scheduler = new Scheduler();
		Config config = new Config();
        try {
            FloorSubsystem.generateFloorsAndEvents(scheduler, config.getProperty("csvFileName"));
            Elevator elevator = new Elevator(1, scheduler, config); // this time create an elevator
            assertEquals(false, elevator.hasEvents()); // check the elevator event queue is empty
            
            //read the csv's contents into the elevator subsystem
            ElevatorSubsystem.generateElevatorEvents(scheduler, config.getProperty("csvFileName"), elevator);
            scheduler.addElevator(elevator);
            
            // fetch all events in the elevators priority queue and output them as sorted
            PriorityQueue<ElevatorEvent> pq = elevator.getElevatorEventQueue();
            ElevatorEvent[] events = elevator.getElevatorEventQueue().toArray(new ElevatorEvent[pq.size()]);
            Arrays.sort(events, pq.comparator());
            
            // Elevator events are sorted by time till event, check to see if destination is read correctly and the time
            // Check the destination floor has been parsed and read correctly and the same for the time till event
            assertEquals(3, events[0].getDestinationFloor().getFloorNumber()); 
            // the first event occurs at the start of the simulation
            assertEquals(0, events[0].getTimeLeftTillEvent());
            
            assertEquals(1, events[1].getDestinationFloor().getFloorNumber()); 
            assertEquals(15, events[1].getTimeLeftTillEvent());
            
            assertEquals(5, events[2].getDestinationFloor().getFloorNumber()); 
            assertEquals(35, events[2].getTimeLeftTillEvent());
            System.out.println("Finish");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
	}
}
