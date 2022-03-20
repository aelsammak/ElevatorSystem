package main;

import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.FloorSubsystem;
import scheduler.Scheduler;

/**
 * The Main class is responsible for running the simulation by starting all the threads
 * 
 * @version 1.0
 * 
 */
public class Main {

	/**
	 * The main method which initializes and starts the scheduler, floor and elevator threads
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Scheduler scheduler = new Scheduler();
		FloorSubsystem floorSubsystem = new FloorSubsystem();
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
		floorSubsystem.start();
		elevatorSubsystem.start();
		scheduler.start();
	}

}
