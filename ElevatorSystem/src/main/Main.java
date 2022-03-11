package main;

import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.FileLoader;
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
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		FileLoader fileLoader = new FileLoader();
		Scheduler scheduler = new Scheduler();
		FloorSubsystem floorSubsystem = new FloorSubsystem(fileLoader);
		ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(fileLoader);
		floorSubsystem.start();
		elevatorSubsystem.start();
		scheduler.start();
	}

}
