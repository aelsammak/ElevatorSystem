package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
	
	public static int NUM_FLOORS;
	public static final SimpleDateFormat CSV_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static Date SIMULATION_START_DATE;
    
    static {
    	try {
			SIMULATION_START_DATE = CSV_DATE_FORMAT.parse("01-01-2022 8:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }
    
    public static void setMaxNumFloors(int maxFloors) {
    	NUM_FLOORS = maxFloors;
    }

}
