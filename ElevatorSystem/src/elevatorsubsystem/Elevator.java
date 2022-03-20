package elevatorsubsystem;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.ArrayList;

import common.Common;
import common.RPC;
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
		destinationFloors = new ArrayList<Integer>();
		this.currentFloor = currentFloor;
		this.addr = InetAddress.getLocalHost();
		transmitter = new RPC(addr, destPort, recPort);
		
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
	public void moveToFloor(int floorNumber) {
		
		targetFloor = floorNumber;
		destinationFloors.add((Integer)floorNumber);

		/* if statements to checks the location of the destination floor relative to the current floor */
		if(currentFloor > floorNumber) {
			setMotorState(MotorState.MOVING_DOWN);
		} else if (currentFloor < floorNumber) {
			setMotorState(MotorState.MOVING_UP);
		} else {
			setMotorState(MotorState.IDLE);
			System.out.println("ELEVATOR: Elevator #" + elevatorNumber + " | CurrentFloor: " + currentFloor + " == TargetFloor: " + targetFloor  + " | MotorState: " + getMotorState() + " @ time = " + LocalTime.now());
			removeDestinationFloor(floorNumber);
			return;
		}
		
		System.out.println("ELEVATOR: Elevator #" + elevatorNumber + " | CurrentFloor: " + currentFloor + " | MotorState: " + getMotorState() + " | TargetFloor: " + targetFloor + " @ time = " + LocalTime.now());
		
		sendAndReceive();
		
		arrivalSensor.simulateElevatorMovement(currentFloor, targetFloor);
	}

	/**
	 * This method is used to notify the ElevatorSubSystem who will then notify the Scheduler of its arrival.
	 * This method will also service the passenger IF they need to get to a destination floor.
	 */
	public void notifyElevatorArrival() {
		openDoors();
		setMotorState(MotorState.IDLE);
		System.out.println("\nELEVATOR: Elevator #" + elevatorNumber + " | ARRIVED at Floor #" + targetFloor + " | MotorState: " + getMotorState() + " @ time = " + LocalTime.now());
		removeDestinationFloor(targetFloor);
		
		if (elevatorButtons[currentFloor - 1].isPressed()) {
			elevatorButtons[currentFloor - 1].turnOff();
	        elevatorLamps[currentFloor - 1].turnOff();
	        System.out.println("ELEVATOR: Turning OFF Elevator #" + elevatorNumber + " car button " + currentFloor + " @ time = " + LocalTime.now());
		}
		
		sendAndReceive();
		closeDoors();
		servePassengerToDestFloor();
	}
	
	/**
	 * This method is used to serve the passenger to their destination floor if they are currently in the elevator 
	 */
	private void servePassengerToDestFloor() {		
		
		if (nextButtonClick != -1) {
			int nextDest = nextButtonClick;
			nextButtonClick = -1;
			System.out.println("\nELEVATOR: Elevator #" + elevatorNumber + " car button " + nextDest + " has been pressed @ time = " + LocalTime.now());
			elevatorButtons[nextDest - 1].turnOn();
		    elevatorLamps[nextDest - 1].turnOn();
			moveToFloor(nextDest);
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
	}
	
	/**
	 * This method is used to remove a floor from the Elevator's list of destination floors
	 * 
	 * @param floorNumber - the floor number to be removed
	 */
	public void removeDestinationFloor(int floorNumber) {
		destinationFloors.remove((Integer)floorNumber);
	}
	
	/**
	 * This method is resonsible for opening the Elevator doors
	 */
	public void openDoors() {
		this.door.open();
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
	public void closeDoors() {
        this.door.close();
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
			int received[] = Common.decode(receiveMsg);
			if(fileLoader.getDestinations().containsKey((Integer)received[1])) {
				nextButtonClick = fileLoader.getDestinations().get((Integer)received[1]).get(0);
				fileLoader.getDestinations().get((Integer)received[1]).remove(0);
			} else {
				nextButtonClick = -1;
			}
			
			moveToFloor(received[1]);
		}
	}

	/**
	 * The run() method which will be invoked once start() has been called on the Elevator thread.  
	 */
	@Override
	public void run() {
		while (true) {
			receive();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
