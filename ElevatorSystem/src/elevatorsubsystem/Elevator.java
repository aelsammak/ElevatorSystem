package elevatorsubsystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

import common.Common;
import common.RPC;
import common.Common.ELEV_ERROR;
import floorsubsystem.FileLoader;

/**
 * The Elevator class represents a single Elevator in the ElevatorSubSystem. 
 * 
 * @author Adi El-Sammak
 * @version 3.0
 *
 */
public class Elevator extends Thread {
	
	private int currentFloor;
	private ArrivalSensor arrivalSensor;
	private final int elevatorNumber;
	private final ElevatorButton[] elevatorButtons;
	private final ElevatorLamp[] elevatorLamps;
	private final Motor motor;
	private final Door door;
	private ArrayList<Integer> destinationFloors;
	private int targetFloor;
	private RPC transmitter;
	private InetAddress addr;
	private FileLoader fileLoader;
	private int nextButtonClick;
	private StuckSensor stuckSensor;
	private boolean isStuck;
	private int departureFloor;
	
	/**
	 * Constructor for the Elevator class. 
	 * 
	 * @param elevatorNumber - the elevatorNumber
	 * @param currentFloor - the currentFloor of the elevator
	 * @param destPort - the destination port for the elevator
	 * @param recPort - the receive port for the elevator
	 * @param fileLoader - the file loader
	 * @throws UnknownHostException - thrown if the host specified is unknown
	 */
	public Elevator(int elevatorNumber, int currentFloor, int destPort, int recPort, FileLoader fileLoader) throws UnknownHostException {
		this.fileLoader = fileLoader;
		this.elevatorNumber = elevatorNumber;
		motor = new Motor(elevatorNumber);
		door = new Door(elevatorNumber);
		arrivalSensor = new ArrivalSensor(this);
		stuckSensor = new StuckSensor(this);
		isStuck = false;
		destinationFloors = new ArrayList<Integer>();
		this.currentFloor = currentFloor;
		this.addr = InetAddress.getLocalHost();
		transmitter = new RPC(addr, destPort, recPort);
		targetFloor = currentFloor;
		
		/* init elevator buttons and lamps */
		elevatorButtons = new ElevatorButton[Common.NUM_FLOORS];
		elevatorLamps = new ElevatorLamp[Common.NUM_FLOORS];
		
		try {
			while(fileLoader.hasNextInstruction()) {
				fileLoader.nextLine();
			}
		} catch (Exception e) {
			
		}
		
        for (int i = 0; i < Common.NUM_FLOORS; i++) {
        	elevatorButtons[i] = new ElevatorButton(i);
        	elevatorLamps[i] = new ElevatorLamp(i);
        }
	}

	/**
	 * This method is responsible for moving the Elevator to the specified floor number being passed.
	 * 
	 * @param floorNumber - the floor the elevator should move to
	 */
	public void moveToFloor() {
		
		targetFloor = getFloor();

		/* if statements to checks the location of the destination floor relative to the current floor */
		if (targetFloor != -1 && getMotorState() == MotorState.IDLE) {
			if(currentFloor > targetFloor) {
				setMotorState(MotorState.MOVING_DOWN);
			} else if (currentFloor < targetFloor) {
				setMotorState(MotorState.MOVING_UP);
			} else {
				setMotorState(MotorState.IDLE);
				System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " | CurrentFloor: " + currentFloor + " == TargetFloor: " + targetFloor  + " | MotorState: " + getMotorState());
				notifyElevatorArrival();
				return;
			}
			
			System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " | CurrentFloor: " + currentFloor + " | MotorState: " + getMotorState() + " | TargetFloor: " + targetFloor);
			
	    	try {
	    		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
				writer.write("Time of Elevator Starting to move to Target Floor : " + LocalTime.now() + "\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			sendAndReceive();
			sendGUI(-1, true, targetFloor, getMotorState());
			
			arrivalSensor.simulateElevatorMovement(currentFloor, targetFloor);
		}
		
	}

	/**
	 * This method is used to notify the ElevatorSubSystem who will then notify the Scheduler of its arrival.
	 * This method will also service the passenger IF they need to get to a destination floor.
	 */
	public void notifyElevatorArrival() {
		openDoors();
		setMotorState(MotorState.IDLE);
		System.out.println("\nTime: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " | ARRIVED at Floor #" + targetFloor + " | MotorState: " + getMotorState());
		removeDestinationFloor(targetFloor);
		
		sendGUI(-1, true, targetFloor, getMotorState());
		sendAndReceive();
		closeDoors();
		if (elevatorButtons[currentFloor - 1].isPressed()) {
			elevatorButtons[currentFloor - 1].turnOff();
	        elevatorLamps[currentFloor - 1].turnOff();
	        System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Turning OFF Elevator #" + elevatorNumber + " car button " + currentFloor);
		}
		servePassengerToDestFloor();
	}
	
	/**
	 * This method is responsible for removing a destination floor from the list of destination floors
	 * @param targetFloor2
	 */
	private void removeDestinationFloor(int targetFloor2) {
		destinationFloors.remove((Integer)targetFloor2);		
	}

	/**
	 * This method is responsible for notifying that the Elevator is unstuck
	 */
	public void notifyElevatorUnStuck() {
		isStuck = false;
		setMotorState(MotorState.IDLE);
		System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " is UNSTUCK");
		sendErrorToGUI(false);
		sendAndReceive();
	}
	
	/**
	 * This method is used to serve the passenger to their destination floor if they are currently in the elevator 
	 */
	private void servePassengerToDestFloor() {		
		if (nextButtonClick != -1) {
			int nextDest = nextButtonClick;
			nextButtonClick = -1;
			System.out.println("\nTime: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " car button " + nextDest + " has been pressed");
			elevatorButtons[nextDest - 1].turnOn();
		    elevatorLamps[nextDest - 1].turnOn();
		    destinationFloors.add((Integer)nextDest);
		    MotorState motorState;
		    if(currentFloor > targetFloor) {
		    	motorState = MotorState.MOVING_DOWN;
			} else if (currentFloor < targetFloor) {
				motorState = MotorState.MOVING_UP;
			} else {
				motorState = MotorState.IDLE;
			}
		    sendGUI(nextDest, true, nextDest, motorState);
		    moveToFloor();
		} else if (departureFloor != -1) {
			if (fileLoader.getDestinations().containsKey((Integer)departureFloor) && fileLoader.getDestinations().get((Integer)departureFloor).size() > 0) {
				nextButtonClick = fileLoader.getDestinations().get((Integer)departureFloor).get(0);
				fileLoader.getDestinations().get((Integer)departureFloor).remove(0);
				int nextDest = nextButtonClick;
				nextButtonClick = -1;
				System.out.println("\nTime: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " car button " + nextDest + " has been pressed");
				elevatorButtons[nextDest - 1].turnOn();
			    elevatorLamps[nextDest - 1].turnOn();
			    destinationFloors.add((Integer)nextDest);
			    MotorState motorState;
			    if(currentFloor > targetFloor) {
			    	motorState = MotorState.MOVING_DOWN;
				} else if (currentFloor < targetFloor) {
					motorState = MotorState.MOVING_UP;
				} else {
					motorState = MotorState.IDLE;
				}
			    sendGUI(nextDest, true, nextDest, motorState);
			    moveToFloor();
			} else {
				departureFloor = -1;
			}
		}
	}
	
	/**
	 * This method is used to either increment or decrement the elevators current floor based on motorState. 
	 * This method is called to change the elevator's position with respect to time.
	 */
	public void updatePosition() {
		if (getMotorState() == MotorState.MOVING_UP) {
			currentFloor++;
		} else if (getMotorState() == MotorState.MOVING_DOWN) {
			currentFloor--;
		}
		sendAndReceive();
		sendGUI(-1, false, targetFloor, getMotorState());
	}
	
	/**
	 * This method is responsible for the handling the error (transient or hard fault)
	 * @return true - if a fault/error occurred, else false
	 */
	public boolean handleError() {
		// check if fault has not occurred, if it hasnt then return false, else coninue till end and return true
		if (fileLoader.getFaults().containsKey(elevatorNumber)) {
			if (LocalTime.now().isAfter(fileLoader.getFaults().get(elevatorNumber))) {
				sendErrorToGUI(true);
				boolean isHardFault = false;
				isStuck = true;
				fileLoader.getFaults().remove(elevatorNumber);
				if (getFloor() != -1 && !this.door.isOpen()) {
					isHardFault = true;
				}
				Common.ELEV_ERROR errorType;
				MotorState motorState;
				if (isHardFault) {
					errorType = ELEV_ERROR.STUCK_BETWEEN;
					motorState = MotorState.HARD_FAULT;
					System.out.println("/************HARD FAULT************\\");
					System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber 
							+ " is stuck between Floor #" + currentFloor + " and Floor #" 
							+ (getMotorState() == MotorState.MOVING_UP ? currentFloor + 1 : currentFloor - 1));
					System.out.println("/**********************************\\");
				} else {
					errorType = ELEV_ERROR.DOOR_CLOSE;
					motorState = MotorState.TRANSIENT_FAULT;
					System.out.println("/************TRANSIENT FAULT************\\");
					System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber 
							+ " is stuck door " + (this.door.isOpen() ?  "open" : "closed") + " at Floor #" + currentFloor);
					System.out.println("/**********************************\\");
				}
				
				// while loop through all destinations and send to scheduler until empty
				byte[] msg;
				System.out.println("GETFLOOR(): " + getFloor());
				if (this.door.isOpen() && !elevatorButtons[currentFloor - 1].isPressed()) {
					fileLoader.getDestinations().get(targetFloor).add(nextButtonClick);
					System.out.println("Removing floors...");
					msg = Common.encodeElevError(errorType, elevatorNumber, currentFloor, targetFloor, (getMotorState() == MotorState.MOVING_UP ? true : false));
					transmitter.sendPacket(msg);
					nextButtonClick = -1;
				} else if (getFloor() == -1) {
					System.out.println("No Floors to reassign...");
					msg = Common.encodeElevError(errorType, elevatorNumber, currentFloor, -1, (getMotorState() == MotorState.MOVING_UP ? true : false));
					transmitter.sendPacket(msg);
				}
				
				while(getFloor() != -1) {
					if (elevatorButtons[getFloor() - 1].isPressed()) {
						removeDestinationFloor(getFloor());
						elevatorButtons[currentFloor - 1].turnOff();
				        elevatorLamps[currentFloor - 1].turnOff();
				        msg = Common.encodeElevError(errorType, elevatorNumber, currentFloor, -1, (getMotorState() == MotorState.MOVING_UP ? true : false));
						transmitter.sendPacket(msg);
						continue;
					}
					fileLoader.getDestinations().get(getFloor()).add(nextButtonClick);
					System.out.println("Removing floors...");
					msg = Common.encodeElevError(errorType, elevatorNumber, currentFloor, getFloor(), (getMotorState() == MotorState.MOVING_UP ? true : false));
					transmitter.sendPacket(msg);
					removeDestinationFloor(getFloor());
				}
				
				setMotorState(motorState);
				
				if (isHardFault) {
					System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " is has SHUTDOWN");
					Thread.currentThread().stop();
				}
				
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * This method is responsible for finding the closest floor out of the elevators destinations based on motorstate
	 * @return Integer - the closest floor to the elevator
	 */
	public Integer getFloor() {
		int target = -1;
		try {
			if(getMotorState() == MotorState.MOVING_UP) {
				target = Collections.min(destinationFloors);
			} else {
				target = Collections.max(destinationFloors);
			}
			return target;
		} catch (NoSuchElementException e) {
			return -1;
		}
	}
	
	/**
	 * This method is resonsible for opening the Elevator doors
	 */
	public void openDoors() {
		System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " opening doors");
		this.door.open();
    	try {
    		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
			writer.write("Time of Elevator Door Opened : " + LocalTime.now() + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns Floor number at which the Elevator is on
	 * 
	 * @return int - the floor number
	 */
	public int getCurrentFloorNumber() {
		return currentFloor;
	}
	
	/**
	 * Sets the current floor number of the elevator to the floor number being passed in.
	 * 
	 * @param floorNumber - the new current floor number
	 */
	public void setCurrentFloorNumber(int floorNumber) {
		currentFloor = floorNumber;
	}
	
	/**
	 * This method is responsible for closing the Elevator doors while taking load/unload times into account
	 */
	public synchronized void closeDoors() {
		Long loadTime = Common.config.getLongProperty("LOAD_UNLOAD_TIME");
		
		float loadTimeFloat = ((float) loadTime / 10);
		loadTime = ((Float) loadTimeFloat).longValue();
		
		System.out.println("LOAD/UNLOAD TIME: " + loadTime);
    	
        try {
        	for (int i = 0; i < 10; i++) {
        		wait(loadTime);
        		handleError();
        		if (isStuck) {
        			stuckSensor.simulateStuck();
        		}
        	}
        } catch (InterruptedException e) {
        	System.out.println("In method closeDoors()");
            e.printStackTrace();
        }
        
        System.out.println("Time: " + LocalTime.now() + " | ELEVATOR: Elevator #" + elevatorNumber + " closing doors");
        this.door.close();
    	try {
    		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
			writer.write("Time of Elevator Door Closed : " + LocalTime.now() + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

	
	/**
	 * Getter for the Motor's ElevatorState attribute
	 * 
	 * @return ElevatorState - the state of the Elevator
	 */
	public MotorState getMotorState() {
		return motor.getState();
	}
	
	/**
	 * Setter for the motor's Elevator state
	 * 
	 * @param state - the elevator state
	 */
	public void setMotorState(MotorState state) {
		motor.setState(state);
	}
	
	/**
	 * Getter for elevatorNumber attribute
	 * 
	 * @return int - the elevatorNumber
	 */
	public int getElevatorNumber() {
		return elevatorNumber;
	}

	/**
	 * Getter for array of elevatorButtons
	 * 
	 * @return ElevatorButton[] - the elevatorButtons
	 */
	public ElevatorButton[] getElevatorButtons() {
		return elevatorButtons;
	}

	/**
	 * Getter for the array of elevatorLamps
	 * 
	 * @return ElevatorLamp[] - the elevatorLamps
	 */
	public ElevatorLamp[] getElevatorLamps() {
		return elevatorLamps;
	}
	
	/**
	 * This method is used to send and receive a packet to the Scheduler.
	 */
	public void sendAndReceive() {
		byte[] msg = Common.encodeElevMsgIntoBytes(elevatorNumber, currentFloor, getMotorState(), targetFloor);
		transmitter.sendPacket(msg);
		msg = transmitter.receivePacket();
	}
	
	/**
	 * This method is used to send an elevator update to the GUI
	 */
	public void sendGUI(int carButtonClicked, boolean printToTextField, int realTargetFloor, MotorState motorState) {
		byte[] msg = Common.encodeElevToGUIMsgIntoBytes(elevatorNumber, currentFloor, motorState, carButtonClicked, realTargetFloor, printToTextField);
		transmitter.sendPacket(msg);
		msg = transmitter.receivePacket();
	}
	
	/**
	 * This method is used to send an elevator error to the GUI
	 */
	public void sendErrorToGUI(boolean isStuck) {
		byte[] msg = Common.encodeElevErrorToGUI(elevatorNumber, isStuck);
		transmitter.sendPacket(msg);
		msg = transmitter.receivePacket();
	}

	/**
	 * This method first sends an acknowledgement check msg to the ElevatorSubSystem and then receives a msg with the instructions from Scheduler.
	 */
	public void receive() {
		byte[] ackcheckMsg;
		byte[] receiveMsg;
		
		ackcheckMsg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.CHECK);
		transmitter.sendPacket(ackcheckMsg);

		receiveMsg = transmitter.receivePacket();
		
		if (receiveMsg == null) {
			return;
		}
		
		if (Common.findType(receiveMsg) != Common.MESSAGETYPE.ACKNOWLEDGEMENT) {
	    	try {
	    		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
				writer.write("Time of Elevator Receive Message From ElevatorSubSystem : " + LocalTime.now() + "\n");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			int received[] = Common.decode(receiveMsg);
			if (!isStuck) {
				if (!handleError()) {
					if (fileLoader.getDestinations().containsKey((Integer)received[1]) && fileLoader.getDestinations().get((Integer)received[1]).size() > 0) {
						departureFloor = received[1];
						nextButtonClick = fileLoader.getDestinations().get((Integer)received[1]).get(0);
						fileLoader.getDestinations().get((Integer)received[1]).remove(0);
					} else {
						nextButtonClick = -1;
						departureFloor = -1;
					}
					destinationFloors.add((Integer) received[1]);
					moveToFloor();
				}	
			} else {
				stuckSensor.simulateStuck();
			}
		}
	}

	/**
	 * The run() method which will be invoked once start() has been called on the Elevator thread.  
	 */
	@Override
	public void run() {
		while (getMotorState() != MotorState.HARD_FAULT) {
			receive();
			handleError();
			if (isStuck) {
				stuckSensor.simulateStuck();
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
		}
	}

}
