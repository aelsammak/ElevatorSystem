package elevatorsubsystem;


import static java.lang.Math.abs;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import common.Common;
import floorsubsystem.Floor;
import scheduler.ElevatorEvent;
import scheduler.Scheduler;

/**
 * The ElevatorSubsystem class is reponsible for parsing the file to generate elevator events 
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class ElevatorSubsystem {
	
	
	/**
	 * This method is responsible for generating the elevator events from the file being passed in
	 * 
	 * @param scheduler - the schduler of the system
	 * @param filename - the file to be parsed containing the Elevator Events
	 * @param elevator - the elevator to add the elevator event to 
	 * @throws FileNotFoundException
	 */
    public static void generateElevatorEvents(Scheduler scheduler, String filename, Elevator elevator) throws FileNotFoundException {
    	List<Floor> floors = scheduler.getFloors();
    	List<Integer> destinationFloors = new ArrayList<>();
		List<Long> timeList = new ArrayList<>();
    	
    	Scanner scanner = new Scanner(new File(filename));
        
    	boolean isFirstDate = true;
        while (scanner.hasNext()) {
            String[] line = scanner.nextLine().split(",");
            
            if (line.length != 0) {
	            
	            int destinationFloor = Integer.parseInt(line[3]);
	            
	            // Fill destinationFloors
	            destinationFloors.add(destinationFloor);
	            
	            // Fill timeList
	            Date currentDate = null;
				try {
					currentDate = Common.CSV_DATE_FORMAT.parse("01-01-2022 "+ line[0]);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				/* Set the SIMULATION_START_DATE to the first date */
				if (isFirstDate) {
					Common.SIMULATION_START_DATE = currentDate;
					isFirstDate = false;
				}
				
	            timeList.add(abs(Common.SIMULATION_START_DATE.getTime() - currentDate.getTime())/1000);
            } else {
            	break;
            }
        }
        
        int tempIndex = 0;
        for (int destinationFloor : destinationFloors) {
        	elevator.addElevatorEvent(new ElevatorEvent(elevator, floors.get(destinationFloor - 1), timeList.get(tempIndex)));
        	tempIndex++;
        }
    }

}
