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

public class ElevatorSubsystem {
	
	// TODO - ADD ELEVATOR TO SCHEDULER LIST OF ELEVATORS
    public static void generateElevatorEvents(Scheduler scheduler, String filename, Elevator elevator) throws FileNotFoundException {
    	List<Floor> floors = scheduler.getFloors();
    	List<Integer> destinationFloors = new ArrayList<>();
		List<Long> timeList = new ArrayList<>();
    	
    	Scanner scanner = new Scanner(new File(filename));
        
        while (scanner.hasNext()) {
            String[] line = scanner.nextLine().split(",");
            
            int destinationFloor = Integer.parseInt(line[3]);
            
            // Fill destinationFloors
            destinationFloors.add(destinationFloor);
            
            // Fill timeList
            Date currentDate = null;
			try {
				currentDate = Common.CSV_DATE_FORMAT.parse("01-01-2022 "+ line[0]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            timeList.add(abs(Common.SIMULATION_START_DATE.getTime() - currentDate.getTime())/1000);
        }
        
        for (int destinationFloor : destinationFloors) {
        	int tempIndex = 0;
        	elevator.addElevatorEvent(new ElevatorEvent(elevator, floors.get(destinationFloor - 1), timeList.get(tempIndex)));
        	tempIndex++;
        }

    }

}
