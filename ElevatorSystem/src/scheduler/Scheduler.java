package scheduler;

import java.net.InetAddress;
import java.time.LocalTime;
import java.util.*;

import common.Common;
import common.RPC;
import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.FloorSubsystem;

/**
 * Scheduler class is responsible for storing and dispatching elevators in response to passenger requests
 * 
 * @author Cam Sommerville
 * @author Erica Morgan
 * @version 3.0
 * 
 */
public class Scheduler extends Thread {
	
	public static final int SCHEDULER_RECEIVE_PORT = 10004;
	private final byte[] ackCheckMSG = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.CHECK);
	private SchedulerState schedulerState;
	private ElevatorState[] elevatorStates;
	private FloorState[] floorStates;
	private Queue<byte[]> msgsToElevatorSubSystem, msgsToFloorSubSystem;
	private RPC rpcFloor, rpcElevator;

	public Scheduler() throws Exception {
		schedulerState = SchedulerState.WAITING;
		this.elevatorStates = new ElevatorState[Common.NUM_ELEVATORS];
		this.floorStates = new FloorState[Common.NUM_FLOORS];
		this.msgsToElevatorSubSystem = new LinkedList<byte[]>();
		this.msgsToFloorSubSystem = new LinkedList<byte[]>();
		
		for (int elevNumber = 0; elevNumber < elevatorStates.length; elevNumber++) { elevatorStates[elevNumber] = new ElevatorState(elevNumber + 1); }
		for (int floorNumber = 0; floorNumber < floorStates.length; floorNumber++) { floorStates[floorNumber]= new FloorState(floorNumber + 1); }

		rpcElevator = new RPC(InetAddress.getLocalHost(), ElevatorSubsystem.ELEVSUBSYSTEM_RECEIVE_PORT, SCHEDULER_RECEIVE_PORT);
		rpcFloor = new RPC(InetAddress.getLocalHost(), FloorSubsystem.FLOORSUBSYSTEM_RECV_PORT, FloorSubsystem.FLOORSUBSYSTEM_DEST_PORT);
	}
	
	public SchedulerState getSchedulerState() {
		return schedulerState;
	}

	private void elevtSubAddMsg (byte[] msg) {
		int[] message = Common.decode(msg);
		int elevatorNumber = message[0];
		int currentFloorNumber = message[1];
		int motorDir = message[2];
		int targetFloor = message[3];

		// update elevatorStates
		elevatorStates[elevatorNumber - 1].setCurrentFloorNumber(currentFloorNumber);
		elevatorStates[elevatorNumber - 1].setMotorDir(motorDir);
		elevatorStates[elevatorNumber - 1].setTargetFloor(targetFloor);

		/* if the the elevator stops on a floor, turn off corresponding floor buttons & lamps */
		if (motorDir == 0) {

			if (targetFloor < currentFloorNumber) {
				byte[] oneMsgToFloorSub = Common.encodeSchedulerMsgIntoBytes(elevatorNumber, currentFloorNumber, false);
				msgsToFloorSubSystem.offer(oneMsgToFloorSub);
			}

			if (currentFloorNumber != 1 && (targetFloor < currentFloorNumber || targetFloor == currentFloorNumber)) {
				byte[] oneMsgToFloorSub = Common.encodeSchedulerMsgIntoBytes(elevatorNumber, currentFloorNumber, false);
				msgsToFloorSubSystem.offer(oneMsgToFloorSub);
			}
			if (currentFloorNumber != floorStates.length && (targetFloor > currentFloorNumber || targetFloor == currentFloorNumber)) {
				byte[] oneMsgToFloorSub = Common.encodeSchedulerMsgIntoBytes(elevatorNumber, currentFloorNumber, true);
				msgsToFloorSubSystem.offer(oneMsgToFloorSub);
			}

		}

        return;
    }

	/**
	 *
	 * @param msg The message sent by the floor subsystem.
	 */
	private void floorSubAddMsg (byte[] msg) {
		int[] message = Common.decode(msg);
		int floor = message[0];
		
		boolean isUp = false;  
		if (message[1] == 1) {
			isUp = true;
		}

		int closestElevt = findClosestElevator(floor, isUp);
		byte[] oneMsgToElevtSub = Common.encodeSchedulerMsgIntoBytes(closestElevt, floor, isUp);
		msgsToElevatorSubSystem.offer(oneMsgToElevtSub);
	}

	private int findClosestElevator(int floorNumber, boolean isFloorBtnUp) {
		int result = 0;
		int[] distances = new int[Common.NUM_ELEVATORS];
		// find distance for all elevators
		for (int i = 0; i < Common.NUM_ELEVATORS; i++) {
			int dis = findDistance(floorNumber, isFloorBtnUp, elevatorStates[i]);
			distances[i] = dis;
		}
	
		/* Find the Elevator with the smallest distance to the floor */
		int index = 0;
		int min = distances[0];
		for (int i = 0; i < distances.length; i++) {
			if (distances[i] < min) {
				min = distances[i];
				index = i;
			}
		}
		
		result = index + 1;
		return result;
	}


	private int findDistance(int floor, boolean isUp, ElevatorState elevatorState) {
		int distance = 0;
		int elevCurrPosition = elevatorState.getCurrentFloorNumber();
		int elevDir = elevatorState.getMotorDir();
		int elevDest = elevatorState.getTargetFloor();
	
		int floorDiff = elevCurrPosition - floor; /* positive means floor is below the elevator; negative means above */
		if (elevCurrPosition == elevDest) {
			distance = Math.abs(floorDiff);
		} else if ((floorDiff > 0 && !isUp && elevDir == -1) || (floorDiff < 0 && isUp && elevDir == 1)) {
			distance = Math.min(Math.abs(elevCurrPosition - floor), Math.abs(elevCurrPosition - floor));
		} else { /* elevator needs turn around */
			if (elevDir == -1) { /* elevator is going down */
				distance = elevCurrPosition + floor;
			} else { /* elevator is going up */
				distance = (Common.NUM_FLOORS - elevCurrPosition) + (Common.NUM_FLOORS - floor);
			}
		}
		return distance;
	}

	// communicate with FloorSub
	private void sendReceiveFloorSub() {
		// send to FloorSub
		schedulerState = SchedulerState.SENDING;
		byte[] msgSend = msgsToFloorSubSystem.poll();
		if (msgSend != null) {
			rpcFloor.sendPacket(msgSend);
			String sendMsg = Common.decodeSchedulerFloorMsgToString(Common.decode(msgSend));
			if (sendMsg != "") {
				System.out.println("SCHEDULER: Method: SEND | To: FloorSubSystem | Msg: " +  sendMsg + " @ time = " + LocalTime.now());
			}
		} else {
			rpcFloor.sendPacket(ackCheckMSG);
		}
		schedulerState = SchedulerState.WAITING;

		// receive from FloorSub
		schedulerState = SchedulerState.RECEIVING;
		byte[] msgReceive = rpcFloor.receivePacket();
		if (Common.findType(msgReceive) != Common.MESSAGETYPE.ACKNOWLEDGEMENT) {
			floorSubAddMsg(msgReceive);
			System.out.println("SCHEDULER: Method: RECEIVE | From: FloorSubSystem | Msg: " + Common.decodeSchedulerFromFloorMsgToString(Common.decode(msgReceive)) + " @ time = " + LocalTime.now());

		}
		schedulerState = SchedulerState.WAITING;
	}

	// communicate with ElevtSub
	private void sendReceiveElevtSub() {
		// check ElevtSub
		schedulerState = SchedulerState.SENDING;
		byte[] msgSend = msgsToElevatorSubSystem.poll();
		if ( msgSend != null) {
			rpcElevator.sendPacket(msgSend);
			System.out.println("SCHEDULER: Method: SEND | To: ElevatorSubSystem | Msg: " +  Common.decodeSchedulerElevMsgToString(Common.decode(msgSend)) + " @ time = " + LocalTime.now());

		} else{
			rpcElevator.sendPacket(ackCheckMSG);
		}
		schedulerState = SchedulerState.WAITING;

		// receive from ElevtSub
		schedulerState = SchedulerState.RECEIVING;
		byte[] msgReceive = rpcElevator.receivePacket();
		if (Common.findType(msgReceive) != Common.MESSAGETYPE.ACKNOWLEDGEMENT){
			elevtSubAddMsg(msgReceive);
			System.out.println("SCHEDULER: Method: RECEIVE | From: ElevatorSubSystem | Msg: " + Common.decodeSchedulerFromElevMsgToString(Common.decode(msgReceive))  + " @ time = " + LocalTime.now());

		}
		schedulerState = SchedulerState.WAITING;
	}

	@Override
	public void run() {
		while (true) {
			try {
				sendReceiveFloorSub();
				Thread.sleep(200);
				sendReceiveElevtSub();
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
