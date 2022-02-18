package test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import org.junit.Before;
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
	
	private Scheduler scheduler;
	private Elevator elevator;
	
	// Initializes the scheduler, floor and elevator for following test cases
	@Before
	public void initFloorAndElevatorEvent() {
		scheduler = new Scheduler();
		try {
			Config config = new Config();
			FloorSubsystem.generateFloorsAndEvents(scheduler, config.getProperty("csvFileName"));
            elevator = new Elevator(1, scheduler, config); // this time create an elevator
            
            //read the csv's contents into the elevator subsystem
            ElevatorSubsystem.generateElevatorEvents(scheduler, config.getProperty("csvFileName"), elevator);
            scheduler.addElevator(elevator);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Test to see if the floor number is parsed from the csv correctly to the relevant floor event queues.
	 * 
	 * @throws IOException
	 */
	@Test
	public void floorEventTestParsedNumber() throws IOException {
		System.out.println("Starting floor event parse test");
    	// reads the csv and creates floors
        List<Floor> f = scheduler.getFloors();
        
        // peek into the event queues of the floors that will be populated based on the csv
        PriorityQueue<FloorEvent> floor2 = f.get(1).getFloorEventQueue();
        PriorityQueue<FloorEvent> floor3 = f.get(2).getFloorEventQueue();
        PriorityQueue<FloorEvent> floor4 = f.get(3).getFloorEventQueue();
        
        // check the second floor has the correct events and then the remaining floors
        assertEquals(2, floor2.peek().getFloor().getFloorNumber()); 
        assertEquals(4, floor4.peek().getFloor().getFloorNumber()); 
        assertEquals(3, floor3.peek().getFloor().getFloorNumber());  
        
        System.out.println("Finish");
	}
	
	/**
	 * Test to see if the requested direction is parsed from the csv correctly to the relevant floor event queues.
	 * Tests this through the buttons click on that floor level to see what is turned on.
	 * 
	 * @throws IOException
	 */
	@Test
	public void floorEventTestParsedDirection() throws IOException {
		System.out.println("Starting floor event parse test");
    	// reads the csv and creates floors
        List<Floor> f = scheduler.getFloors();
        
        // peek into the event queues of the floors that will be populated based on the csv
        PriorityQueue<FloorEvent> floor2 = f.get(1).getFloorEventQueue();
        PriorityQueue<FloorEvent> floor3 = f.get(2).getFloorEventQueue();
        PriorityQueue<FloorEvent> floor4 = f.get(3).getFloorEventQueue();
        
        // check the second floor has the correct events and then the remaining floors
        assertEquals(true, floor2.peek().isUpButton());
        assertEquals(false, floor4.peek().isUpButton()); 
        assertEquals(true, floor3.peek().isUpButton());        
        System.out.println("Finish");
	}
	
	/**
	 * Tests to see if the time of events is parsed correctly from the CSV to the floor event queues.
	 * 
	 * @throws IOException
	 */
	@Test
	public void floorEventTestParsedTimeToEvent() throws IOException {
		System.out.println("Starting floor event parse test");
    	// reads the csv and creates floors
        List<Floor> f = scheduler.getFloors();
        
        // peek into the event queues of the floors that will be populated based on the csv
        PriorityQueue<FloorEvent> floor2 = f.get(1).getFloorEventQueue();
        PriorityQueue<FloorEvent> floor3 = f.get(2).getFloorEventQueue();
        PriorityQueue<FloorEvent> floor4 = f.get(3).getFloorEventQueue();

        assertEquals(0, floor2.peek().getTimeLeftTillEvent());
        assertEquals(15, floor4.peek().getTimeLeftTillEvent());
        assertEquals(35, floor3.peek().getTimeLeftTillEvent());
        
        System.out.println("Finish");
	}
	
	/**
	 * Tests the destination floor is read correctly and that the event time is read correctly and parsed into the elevator event queues.
	 * 
	 * @throws IOException 
	 */
	@Test
	public void elevatorEventParseDestinationFloor() throws IOException {
		System.out.println("Starting elevator destination parse test");
            
        // fetch all events in the elevators priority queue and output them as sorted
        PriorityQueue<ElevatorEvent> pq = elevator.getElevatorEventQueue();
        ElevatorEvent[] events = elevator.getElevatorEventQueue().toArray(new ElevatorEvent[pq.size()]);
        Arrays.sort(events, pq.comparator());
        
        // Elevator events are sorted by time till event, check to see if destination is read correctly and the time
        // Check the destination floor has been parsed and read correctly and the same for the time till event
        assertEquals(3, events[0].getDestinationFloor().getFloorNumber()); 
        assertEquals(1, events[1].getDestinationFloor().getFloorNumber());                       
        assertEquals(5, events[2].getDestinationFloor().getFloorNumber());
	}
    
	/**
	 * Tests elevator events (button pushes to a destination) can be read correctly from the provided csv.
	 * Tests to see the parsed time till event is read correctly.
	 * 
	 * @throws IOException
	 */
	@Test
    public void elevatorEventParseTimeUntilEvent() throws IOException {
		System.out.println("Starting elevator time until event parse test");
        // fetch all events in the elevators priority queue and output them as sorted
        PriorityQueue<ElevatorEvent> pq = elevator.getElevatorEventQueue();
        ElevatorEvent[] events = elevator.getElevatorEventQueue().toArray(new ElevatorEvent[pq.size()]);
        Arrays.sort(events, pq.comparator());
        
        assertEquals(0, events[0].getTimeLeftTillEvent());
        assertEquals(15, events[1].getTimeLeftTillEvent());
        assertEquals(35, events[2].getTimeLeftTillEvent());
        System.out.println("Finish");
	}
}
