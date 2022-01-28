/**
 * 
 */
package main;

import java.io.FileNotFoundException;
import elevatorsubsystem.Elevator;
import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.Floor;
import floorsubsystem.FloorSubsystem;
import scheduler.Scheduler;

public class Main {
	
	private static final String FILE_NAME = "simulation.csv";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        try {
            FloorSubsystem.generateFloorsAndEvents(scheduler, FILE_NAME);
            Elevator elevator = new Elevator(1, scheduler);
            scheduler.addElevator(elevator);
            ElevatorSubsystem.generateElevatorEvents(scheduler, FILE_NAME, elevator);
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
