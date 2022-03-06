package floorsubsystem;

import java.net.InetAddress;
import java.time.LocalTime;
import java.util.LinkedList;

import common.Common;
import common.RPC;

/**
 * This class represents the FloorSubSystem.
 * 
 * @author Kareem El-Hajjar
 * @version 1.0
 * 
 */
public class FloorSubsystem extends Thread {
	
    private Floor[] floors;
    public FileLoader simulationFile;
    public RPC rpc;
    private LinkedList<byte[]> messageQueue;
    
    /* PORTS */
    public static final int FLOORSUBSYSTEM_DEST_PORT = 10002;
    public static final int FLOORSUBSYSTEM_RECV_PORT = 10001;
    
    public FloorSubsystem(FileLoader fileLoader) throws Exception{
        if (Common.NUM_FLOORS <= 1) {
            throw new Exception("incompatible setting: maxFloor should be at least 2.");
        }

        floors = new Floor[Common.NUM_FLOORS];
        createFloors(Common.NUM_FLOORS, floors);

        // Init instruction reader
        simulationFile = fileLoader;
        
        rpc = new RPC(InetAddress.getLocalHost(), FLOORSUBSYSTEM_DEST_PORT, FLOORSUBSYSTEM_RECV_PORT);
        rpc.setSocketTimeout(2000);
        messageQueue = new LinkedList<byte[]>();
    }
    
	/**
	 * Create the floors and fill the scheduler's floor list
	 * 
	 * @param maxFloorNumber, the top floor's number
	 * @param floors, the list of floors
	 */
	public void createFloors(int maxFloorNumber, Floor[] floors) {
        for(int floorNumber = 0; floorNumber < maxFloorNumber; floorNumber++) {
        	if(floorNumber == 0) {
        		floors[floorNumber] = new BottomFloor(floorNumber + 1);
        	} else if(floorNumber == maxFloorNumber - 1) {
        		floors[floorNumber] = new TopFloor(floorNumber + 1);
        	} else {
        		floors[floorNumber] = new MiddleFloor(floorNumber + 1);
        	}
        }
	}
	
    private void addToQueue(byte[] msg) {
    	messageQueue.add(msg);
    }
    
    private byte[] getMsgFromQueue() {
    	if(messageQueue.isEmpty()) {
    		return null;
    	}
    	return messageQueue.pop();
    }

    // Read next instruction from file
    public void nextInstruction() {
        try {
            simulationFile.nextLine();
        } catch (Exception e) {
            System.out.println("ERROR: Reading simulation file failed");
        }
    }

    // send method: send data to scheduler.
    public void readInstruction() {
        // turn on up/ down button correspondingly
        int departureFloor = simulationFile.departFloor();

        // Error check
        if(1 <= departureFloor && departureFloor <= Common.NUM_FLOORS){
            // Register corresponding button
        	Floor currentFloor = floors[departureFloor - 1];
        	if (currentFloor instanceof MiddleFloor) {
        		if (simulationFile.isUp()) {
        			((MiddleFloor) currentFloor).turnOnUpButton();
        			((MiddleFloor) currentFloor).turnOnUpLamp();
        			System.out.println("\nFLOOR: Floor #" + currentFloor.getFloorNumber() + " UP button pressed @ time = " + LocalTime.now());
        		} else {
        			((MiddleFloor) currentFloor).turnOnDownLamp();
        			((MiddleFloor) currentFloor).turnOnDownButton();
        			System.out.println("\nFLOOR: Floor #" + currentFloor.getFloorNumber() + " DOWN button pressed @ time = " + LocalTime.now());
        		}
    		} else if(currentFloor instanceof TopFloor) {
    			((TopFloor) currentFloor).turnOnDownLamp();
    			((TopFloor) currentFloor).turnOnDownButton();
    			System.out.println("\nFLOOR: Floor #" + currentFloor.getFloorNumber() + " DOWN button pressed @ time = " + LocalTime.now());
    		} else {
    			((BottomFloor) currentFloor).turnOnUpLamp();
    			((BottomFloor) currentFloor).turnOnUpButton();
    			System.out.println("\nFLOOR: Floor #" + currentFloor.getFloorNumber() + " UP button pressed @ time = " + LocalTime.now());
    		}
        } else {
            // Unexpected floor in instruction, ignore.
            System.out.println("ERROR: Departure Floor # " + departureFloor + " out of range");
            return;
        }

        // encode and send request to scheduler
        byte[] message = Common.encodeFloorMsgIntoBytes(departureFloor, simulationFile.isUp());

        addToQueue(message);
    }

    // receive method: save message from scheduler.
    public void receive() {
        // process message from scheduler
        byte[] message = rpc.receivePacket();

        // terminate if no message
        if (message == null){
            return;
        }
        
        byte[] msg;
        if (Common.findType(message) == Common.MESSAGETYPE.ACKNOWLEDGEMENT) { 
        	msg = getMsgFromQueue();
        	if(msg == null) {
        		msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG);
        	}
        } else {
	        int[] decodeMsg = Common.decode(message);
	
	        int arrivalFloor = decodeMsg[1];
	        boolean isUpBtn = decodeMsg[2] != 0;
	
	        msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
	        if(1 <= arrivalFloor && arrivalFloor <= Common.NUM_FLOORS) {
	            // Elevator reached requested floor
	        	Floor currentFloor = floors[arrivalFloor - 1];
	        	if (currentFloor instanceof MiddleFloor) {
	        		if (isUpBtn && ((MiddleFloor) currentFloor).getUpLamp().isTurnedOn()) {
	        			System.out.println("FLOOR: Turning OFF Floor #" + arrivalFloor + " "+ (isUpBtn ? "UP" : "DOWN") + " button & lamp @ time = " + LocalTime.now());
	        			((MiddleFloor) currentFloor).turnOffUpButton();
	        			((MiddleFloor) currentFloor).turnOffUpLamp();
	        		} else if (((MiddleFloor) currentFloor).getDownLamp().isTurnedOn()) {
	        			System.out.println("FLOOR: Turning OFF Floor #" + arrivalFloor + " "+ (isUpBtn ? "UP" : "DOWN") + " button & lamp @ time = " + LocalTime.now());
	        			((MiddleFloor) currentFloor).turnOffDownLamp();
	        			((MiddleFloor) currentFloor).turnOffDownButton();
	        		}
	    		}
	    		else if(currentFloor instanceof TopFloor) {
	    			if (((TopFloor) currentFloor).getDownLamp().isTurnedOn()) {
		    			System.out.println("FLOOR: Turning OFF Floor # " + arrivalFloor + " "+ (isUpBtn ? "UP" : "DOWN") + " button & lamp @ time = " + LocalTime.now());
		    			((TopFloor) currentFloor).turnOffDownLamp();
		    			((TopFloor) currentFloor).turnOffDownButton();
	    			}
	    		}
	    		else {
	    			if (((BottomFloor) currentFloor).getUpLamp().isTurnedOn()) {
		    			System.out.println("FLOOR: Turning OFF Floor # " + arrivalFloor + " "+ (isUpBtn ? "UP" : "DOWN") + " button & lamp @ time = " + LocalTime.now());
		    			((BottomFloor) currentFloor).turnOffUpLamp();
		    			((BottomFloor) currentFloor).turnOffUpButton();
	    			}
	    			
	    		}
	        } else {
	            // Unexpected floor received, ignore.
	            System.out.println("ERROR: Arrival Floor #" + arrivalFloor + " out of range");
	            return;
	        }
        }
        rpc.sendPacket(msg);
    }
    
	public void run() {
        boolean instructionSent = false;
        while (true) {
            // send instruction if needed
            if (instructionSent) {
                instructionSent = !simulationFile.hasNextInstruction();
                // read instruction
                nextInstruction();
            } else {
                // compare time stamp
                if (LocalTime.now().isAfter(simulationFile.getTime())) {
                    // read instruction now
                    readInstruction();
                    instructionSent = true;
                }
            }
            receive();
        }
    }

}
