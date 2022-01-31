/**
 * 
 */
package main;

import java.io.FileNotFoundException;
import elevatorsubsystem.Elevator;
import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.Floor;
import floorsubsystem.FloorSubsystem;
import scheduler.ElevatorEvent;
import scheduler.Event;
import scheduler.FloorEvent;
import scheduler.Scheduler;

/**
 * The Main class is reponsible for running the simulation by starting all the threads
 * 
 * @version 1.0
 * 
 */
public class Main {
	
	private static final String FILE_NAME = "simulation.csv";

	/**
	 * The main method which intializes and starts the scheduler, floor and elevator threads
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        try {
            FloorSubsystem.generateFloorsAndEvents(scheduler, FILE_NAME);
            Elevator elevator = new Elevator(1, scheduler);
            ElevatorSubsystem.generateElevatorEvents(scheduler, FILE_NAME, elevator);
            scheduler.addElevator(elevator);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        scheduler.start();
        
        for(Floor floor : scheduler.getFloors()) {
        	floor.start();
        }
        
        for(Elevator elevator : scheduler.getElevators()) {
        	elevator.start();
        }
        
	}

}
