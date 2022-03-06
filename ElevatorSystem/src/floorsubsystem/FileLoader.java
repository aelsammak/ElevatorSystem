package floorsubsystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.constant.Constable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import common.Common;

public class FileLoader {
	
	private File simulationFile = new File(Common.config.getProperty("csvFileName"));
    private Scanner scanner;
    private boolean reachedEndOFile;
    private String currLine;
    private String[] csvSimInfo;
    private static String SIMULATION_START_TIME = "8:00:00"; 
    private LocalTime localTime;
    private HashMap<Integer, ArrayList<Integer>> destinations; // Saves destination floors for each departure floor

    public FileLoader() throws Exception {
    	localTime = LocalTime.now();
    	reachedEndOFile = false;
        try {
            scanner = new Scanner(simulationFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            reachedEndOFile = true;
        }

        // Initialize destinations
        destinations = new HashMap<Integer, ArrayList<Integer>>();

        // Call nextLine() to load the first instruction
        this.nextLine();
    }
    
    public HashMap<Integer, ArrayList<Integer>> getDestinations() {
    	return destinations;
    }

    // Add curLine instruction into destinations,
    // should only be called by nextLine()
    private void pushDestinations() {
        if (!destinations.containsKey(departFloor())){
            // create new arraylist on demand
            destinations.put(departFloor(), new ArrayList<Integer>());
        }
        // add to arraylist
        destinations.get(departFloor()).add(destFloor());
    }
    
    // Public method to get departure floor of current instruction
    public int departFloor() {
        int result = 0;
        try{
            result = Integer.parseInt(csvSimInfo[1]);
        } catch(Exception e) {
            System.out.println("ERROR: Parsing the simulation file failed");
        }
        return result;
    }
    
    // Public method to get destination floor of current instruction
    private int destFloor() {
        int result = 0;
        try {
            result = Integer.parseInt(csvSimInfo[3]);
        } catch(Exception e) {
            System.out.println("ERROR: Parsing the simulation file failed");
        }
        return result;
    }
    
    public boolean hasNextInstruction() {
        return !reachedEndOFile && scanner.hasNextLine();
    }

    // Returns a new line of the instruction file
    private String readLine() {
        return reachedEndOFile ? "" : scanner.nextLine();
    }

    // Public method to switch to next instruction
    public boolean nextLine() throws Exception {
        if (hasNextInstruction()) {
            currLine = readLine();
            if(!currLine.equals("")) {
                // Split line into 4 segments: Time, Departure floor, Direction, Destination floor
                csvSimInfo = currLine.split(",");
                if (csvSimInfo.length == 0) {
                    throw new Exception("ERROR: Parsing the simulation file format is not supported");
                }
                // Update destinations
                pushDestinations();
                return true;
            }
        }
        return false;
    }

    // Public method to get time of current instruction
    @SuppressWarnings("deprecation")
	public LocalTime getTime() {
    	int[] offsets = findOffsets();
    	
    	Date currentDate = null;
		try {
			currentDate = Common.TIMESTAMP_FORMAT.parse(csvSimInfo[0]);
			currentDate.setHours(localTime.getHour() + offsets[0]);
			currentDate.setMinutes(localTime.getMinute() + offsets[1]);
			currentDate.setSeconds(localTime.getSecond() + offsets[2]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return LocalDateTime.ofInstant(currentDate.toInstant(),
    		    ZoneId.systemDefault()).toLocalTime();
    }
    
    private int[] findOffsets() {
    	int[] offsets = new int[3];
    	String[] simStart = SIMULATION_START_TIME.split(":");
    	//int offsetHours = 0, offsetMinutes = 0, offsetSeconds = 0;
    	for (int i = 0; i < simStart.length; i++) {
    		if (i == 0) {
    			int hours = Integer.parseInt(simStart[i]);
    			int diff;
    			if (Integer.parseInt(csvSimInfo[0].split(":")[0]) < hours) {
    				diff = hours + Integer.parseInt(csvSimInfo[0].split(":")[0]);
    			} else {
    				diff = Integer.parseInt(csvSimInfo[0].split(":")[0]) - hours;
    			}
    			offsets[0] = diff;
    		} else if (i == 1) {
    			int minutes = Integer.parseInt(simStart[i]);
    			int diff;
    			if (Integer.parseInt(csvSimInfo[0].split(":")[1]) < minutes) {
    				diff = minutes + Integer.parseInt(csvSimInfo[0].split(":")[1]);
    			} else {
    				diff =  Integer.parseInt(csvSimInfo[0].split(":")[1]) - minutes;
    			}
    			offsets[1] = diff;
    		} else {
    			int seconds = Integer.parseInt(simStart[i]);
    			int diff;
    			if (Integer.parseInt(csvSimInfo[0].split(":")[2]) < seconds) {
    				diff = seconds + Integer.parseInt(csvSimInfo[0].split(":")[2]);
    			} else {
    				diff = Integer.parseInt(csvSimInfo[0].split(":")[2]) - seconds;
    			}
    			offsets[2] = diff;
    		}
    	}
    	return offsets;
    }

    // Public method to get request direction of current instruction
    public boolean isUp() {
    	return csvSimInfo[2].strip().toUpperCase().equals("UP");
    }
    
}
