package floorsubsystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalTime;
import java.util.LinkedList;

import common.Common;
import common.RPC;
import gui.GUI;

/**
 * This class represents the FloorSubSystem.
 * 
 * @author Kareem El-Hajjar
 * @version 3.0
 * 
 */
public class FloorSubsystem extends Thread {
	
    private Floor[] floors;
    private LinkedList<byte[]> messageQueue;
    
    public FileLoader simulationFile;
    public RPC rpc, rpcGUI;
    
    public static final int FLOORSUBSYSTEM_DEST_PORT = 10002;
    public static final int FLOORSUBSYSTEM_RECV_PORT = 10001;
    
	/* The starting port number used for sending packets from the FloorSubSystem to the GUI */
	public static final int FLOORSUBSYSTEM_TO_GUI_PORT = 9011;
    
    /**
     * Parameterized constructor
     * 
     * @throws Exception - Invalid setting
     */
    public FloorSubsystem() throws Exception{
        if (Common.NUM_FLOORS <= 1) {
            throw new Exception("Invalid setting: NUM_FLOORS should be at least 2.");
        }

        floors = new Floor[Common.NUM_FLOORS];
        createFloors(Common.NUM_FLOORS, floors);
        
        rpc = new RPC(InetAddress.getLocalHost(), FLOORSUBSYSTEM_DEST_PORT, FLOORSUBSYSTEM_RECV_PORT);
        rpc.setSocketTimeout(2000);
        
        rpcGUI = new RPC(InetAddress.getLocalHost(), GUI.GUI_RECEIVE_FROM_FLOORSUBSYSTEM_PORT, FLOORSUBSYSTEM_TO_GUI_PORT);
        
        messageQueue = new LinkedList<byte[]>();
    }
    
	/**
	 * Create the floors and fill the scheduler's floor list
	 * 
	 * @param maxFloorNumber - the top floor's number
	 * @param floors - the list of floors
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
	
	/**
	 * Add a message to the message queue
	 * 
	 * @param msg - the message to add to the queue in the form of a byte array
	 */
    private void addToQueue(byte[] msg) {
    	messageQueue.add(msg);
    }
    
    /**
     * Get the message currently in the message queue
     * 
     * @return byte[] - the message as byte array
     */
    private byte[] getMsgFromQueue() {
    	if(messageQueue.isEmpty()) {
    		return null;
    	}
    	return messageQueue.pop();
    }

    /**
     * Read the next instruction form the file
     */
    public void nextInstruction() {
        try {
            simulationFile.nextLine();
        } catch (Exception e) {
            System.out.println("ERROR: Reading simulation file failed");
        }
    }

    /**
     * Read the instruction from the file
     */
    public void readInstruction() {
    	try {
    		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
			writer.write("Time of Floor Button Pressed : " + LocalTime.now() + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        int departureFloor = simulationFile.departFloor();

        if(1 <= departureFloor && departureFloor <= Common.NUM_FLOORS){
        	Floor currentFloor = floors[departureFloor - 1];
        	if (currentFloor instanceof MiddleFloor) {
        		if (simulationFile.isUp()) {
        			((MiddleFloor) currentFloor).turnOnUpButton();
        			((MiddleFloor) currentFloor).turnOnUpLamp();
        			System.out.println("Time: " + LocalTime.now() + " | FLOOR: Floor #" + currentFloor.getFloorNumber() + " UP button pressed \n");
        			rpcGUI.sendPacket(Common.encodeFloorToGUIMsgIntoBytes(currentFloor.getFloorNumber(), true, true));
        		} else {
        			((MiddleFloor) currentFloor).turnOnDownLamp();
        			((MiddleFloor) currentFloor).turnOnDownButton();
        			System.out.println("Time: " + LocalTime.now() + " | FLOOR: Floor #" + currentFloor.getFloorNumber() + " DOWN button pressed \n");
        			rpcGUI.sendPacket(Common.encodeFloorToGUIMsgIntoBytes(currentFloor.getFloorNumber(), false, true));
        		}
    		} else if(currentFloor instanceof TopFloor) {
    			((TopFloor) currentFloor).turnOnDownLamp();
    			((TopFloor) currentFloor).turnOnDownButton();
    			System.out.println("Time: " + LocalTime.now() + " | FLOOR: Floor #" + currentFloor.getFloorNumber() + " DOWN button pressed \n");
    			rpcGUI.sendPacket(Common.encodeFloorToGUIMsgIntoBytes(currentFloor.getFloorNumber(), false, true));
    		} else {
    			((BottomFloor) currentFloor).turnOnUpLamp();
    			((BottomFloor) currentFloor).turnOnUpButton();
    			System.out.println("Time: " + LocalTime.now() + " | FLOOR: Floor #" + currentFloor.getFloorNumber() + " UP button pressed \n");
    			rpcGUI.sendPacket(Common.encodeFloorToGUIMsgIntoBytes(currentFloor.getFloorNumber(), true, true));
    		}
        } else {
            System.out.println("Time: " + LocalTime.now() + " | FLOOR: ERROR Departure Floor #" + departureFloor + " out of range");
            return;
        }

        // Send message after encoding it
        byte[] message = Common.encodeFloorMsgIntoBytes(departureFloor, simulationFile.isUp());
        
        // Add to message queue
        addToQueue(message);
    }

    /**
     * Method to receive message form the scheduler
     */
    public void receive() {
        byte[] message = rpc.receivePacket();

        if (message == null){
            return;
        }
        
        byte[] msg;
        if (Common.findType(message) == Common.MESSAGETYPE.ACKNOWLEDGEMENT) { 
        	msg = getMsgFromQueue();
        	if(msg == null) {
        		msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG);
        	} else {
            	try {
            		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
        			writer.write("Time of Floor Sent Button Pressed To Scheduler : " + LocalTime.now() + "\n");
        			writer.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
        } else {
	        int[] decodeMsg = Common.decode(message);
	
	        int arrivalFloor = decodeMsg[1];
	        boolean isUpBtn = decodeMsg[2] != 0;
	
	        msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
	        if(1 <= arrivalFloor && arrivalFloor <= Common.NUM_FLOORS) {
	        	Floor currentFloor = floors[arrivalFloor - 1];
            	try {
            		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
        			writer.write("Time of Floor Received Turn Off Button Pressed From Scheduler : " + LocalTime.now() + "\n");
        			writer.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
	        	if (currentFloor instanceof MiddleFloor) {
	        		if (isUpBtn && ((MiddleFloor) currentFloor).getUpLamp().isTurnedOn()) {
	        			System.out.println("Time: " + LocalTime.now() + " | FLOOR: Turning OFF Floor #" + arrivalFloor + " "+ (isUpBtn ? "UP" : "DOWN") + " button & lamp \n" );
	        			((MiddleFloor) currentFloor).turnOffUpButton();
	        			((MiddleFloor) currentFloor).turnOffUpLamp();
	        			rpcGUI.sendPacket(Common.encodeFloorToGUIMsgIntoBytes(arrivalFloor, isUpBtn, false));
	        		} else if (((MiddleFloor) currentFloor).getDownLamp().isTurnedOn()) {
	        			System.out.println("Time: " + LocalTime.now() + " | FLOOR: Turning OFF Floor #" + arrivalFloor + " "+ (isUpBtn ? "UP" : "DOWN") + " button & lamp \n");
	        			((MiddleFloor) currentFloor).turnOffDownLamp();
	        			((MiddleFloor) currentFloor).turnOffDownButton();
	        			rpcGUI.sendPacket(Common.encodeFloorToGUIMsgIntoBytes(arrivalFloor, isUpBtn, false));
	        		}
	    		}
	    		else if(currentFloor instanceof TopFloor) {
	    			if (((TopFloor) currentFloor).getDownLamp().isTurnedOn()) {
		    			System.out.println("Time: " + LocalTime.now() + " | FLOOR: Turning OFF Floor # " + arrivalFloor + " "+ (isUpBtn ? "UP" : "DOWN") + " button & lamp \n");
		    			((TopFloor) currentFloor).turnOffDownLamp();
		    			((TopFloor) currentFloor).turnOffDownButton();
		    			rpcGUI.sendPacket(Common.encodeFloorToGUIMsgIntoBytes(arrivalFloor, isUpBtn, false));
	    			}
	    		}
	    		else {
	    			if (((BottomFloor) currentFloor).getUpLamp().isTurnedOn()) {
		    			System.out.println("Time: " + LocalTime.now() + " | FLOOR: Turning OFF Floor # " + arrivalFloor + " "+ (isUpBtn ? "UP" : "DOWN") + " button & lamp \n");
		    			((BottomFloor) currentFloor).turnOffUpLamp();
		    			((BottomFloor) currentFloor).turnOffUpButton();
		    			rpcGUI.sendPacket(Common.encodeFloorToGUIMsgIntoBytes(arrivalFloor, isUpBtn, false));
	    			}
	    		}
	        } else {
	            System.out.println("Time: " + LocalTime.now() + " | FLOOR: ERROR Arrival Floor #" + arrivalFloor + " out of range");
	            return;
	        }
        }
        rpc.sendPacket(msg);
    }
    
    private void waitToStartSim() {
    	byte[] message;
    	while (true) {
    		try {
    			message = rpc.receivePacket();
    		} catch (Exception e) {
				continue;
			}
    		if (message != null) {
        		if (Common.findType(message) == Common.MESSAGETYPE.START_SIM) {
        			rpc.sendPacket(Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG));
        			break;
                }
    		}
    	}
    }
    
    /**
     * Constantly receive and send messages upon thread start.
     */
    @Override
	public void run() {
    	waitToStartSim();
    	try {
			simulationFile =  new FileLoader();
		} catch (Exception e) {
			e.printStackTrace();
		}
        boolean instructionSent = false;
        while (true) {        	
            if (instructionSent) {
                instructionSent = !simulationFile.hasNextInstruction();
                nextInstruction();                
            } else {            	
                if (LocalTime.now().isAfter(simulationFile.getTime())) {
                    readInstruction();
                    instructionSent = true;
                }
            }
            receive();
        }
    }
    
    public static void main(String[] args) {
        FloorSubsystem floorSubsystem;
		try {
			floorSubsystem = new FloorSubsystem();
			floorSubsystem.start();
		} catch (Exception e) {

			e.printStackTrace();
		}
        
    }

}
