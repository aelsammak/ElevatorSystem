package main;

import java.io.FileNotFoundException;
import java.io.IOException;

import common.Config;
import elevatorsubsystem.Elevator;
import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.Floor;
import floorsubsystem.FloorSubsystem;
import scheduler.Scheduler;

/**
 * The Main class is reponsible for running the simulation by starting all the threads
 * 
 * @version 1.0
 * 
 */
public class Main {

	/**
	 * The main method which intializes and starts the scheduler, floor and elevator threads
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Config config = new Config();
        Scheduler scheduler = new Scheduler();
        try {
            FloorSubsystem.generateFloorsAndEvents(scheduler, config.getProperty("csvFileName"));
            Elevator elevator = new Elevator(1, scheduler, config);
            ElevatorSubsystem.generateElevatorEvents(scheduler, config.getProperty("csvFileName"), elevator);
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
