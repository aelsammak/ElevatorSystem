package elevatorsubsystem;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;

import common.Common;
import common.RPC;
import floorsubsystem.FileLoader;
import scheduler.Scheduler;

/**
 * The ElevatorSubsystem class is reponsible for parsing the file to generate elevator events 
 * 
 * @author Adi El-Sammak
 * @version 1.0
 *
 */
public class ElevatorSubsystem extends Thread {
	
	private Thread[] elevators;
	private int elevatorNumber = 0;
	private HashMap<Integer, LinkedList<byte[]>> elevatorMsgBuffer;
	private LinkedList<byte[]> schedulerMsgBuffer;
	
	/* PORTS */
	/* PLEASE NOTE: THIS PORT MIGHT NEED TO CHANGE DEPENDING IF YOUR LOCAL SYSTEM IS USING THIS PORT NUMBER */
	private final InetAddress SCHEDULER_ADDR;
	private final InetAddress ELEVATOR_ADDR;
	public static final int ELEVSUBSYSTEM_RECEIVE_PORT  = 10003;

	/* The Initial port number used for receiving from elevator
	*  1st elevator = 9000
	*  2nd elevator = 9001
	*  3rd elevator = 9002
	*  etc... */
	private static final int ELEVSUBSYSTEM_RECEIVE_FROM_ELEV_PORT = 9000;

	/* The Initial port number used for receiving by elevator
	 *  1st elevator = 10201
	 *  2nd elevator = 10202
	 *  3rd elevator = 10203
	 *  etc... */
	private static final int ELEV_RECEIVE_PORT = 10200;

	
	public ElevatorSubsystem(FileLoader fileLoader) throws Exception {
		
		if (Common.NUM_ELEVATORS <= 0) {
			throw new Exception("incompatible setting: numElev should be at least 1.");
		}
		
		// Init inet address
		SCHEDULER_ADDR = InetAddress.getLocalHost();
		ELEVATOR_ADDR = InetAddress.getLocalHost();

		// Init elevators
		elevators = new Thread[Common.NUM_ELEVATORS];
		for (int i = 0; i < Common.NUM_ELEVATORS; ++i){
			int elevatorNumber = i + 1;
			elevators[i] = new Elevator(elevatorNumber, 1, ELEVSUBSYSTEM_RECEIVE_FROM_ELEV_PORT + elevatorNumber, ELEV_RECEIVE_PORT + elevatorNumber, fileLoader);
		}

		// Init Buffers
		elevatorMsgBuffer = new HashMap<Integer, LinkedList<byte[]>>();
		schedulerMsgBuffer = new LinkedList<byte[]>();
	}
	
	// This thread communicates with scheduler
	private void schedulerCommunicator() {
		// initialize vars
		RPC schedulerTransmitter = new RPC(SCHEDULER_ADDR, Scheduler.SCHEDULER_RECEIVE_PORT, ELEVSUBSYSTEM_RECEIVE_PORT);
		byte[] msg;

		while(true) {
			// receive message from scheduler:
			// 1. scheduler ready to receive a new message
			// 2. scheduler has a message for one of the elevators
			msg = schedulerTransmitter.receivePacket();

			if(Common.findType(msg) == Common.MESSAGETYPE.ACKNOWLEDGEMENT) {
				// Received confirmation
				Common.ACKOWLEDGEMENT confirmationType = Common.findAcknowledgement(msg);
				if(confirmationType == Common.ACKOWLEDGEMENT.CHECK){
					// Scheduler wants to check message
					// send anything needs to be sent to scheduler
					msg = getMsgFromSchedulerBuffer();
					if (msg == null){
						// no message for scheduler, send a confirmation instead
						msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG);
					}

				} else {
					System.out.println("ERROR: Unexpected msg from Scheduler: " + confirmationType);
					msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
				}

			} else {
				// Received message for elevator
				// make received message available for elevator
				addToElevatorBuffer(msg);
				msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
			}
			// Reply to Scheduler
			schedulerTransmitter.sendPacket(msg);
		}
	}

	// This thread communicates with a single elevator
	private void elevatorCommunicator() {
		// serialNumber = elevator number
		int elevatorNumber = incrementElevatorNumber();
		// Enable buffer for current elevator
		inititializeElevBuffers(elevatorNumber);
		// initialize vars
		RPC elevatorTransmitter = new RPC(ELEVATOR_ADDR, ELEV_RECEIVE_PORT + elevatorNumber, ELEVSUBSYSTEM_RECEIVE_FROM_ELEV_PORT + elevatorNumber);
		byte[] msg;

		while(true){
			// receive message from elevator:
			// 1. elevator ready to receive a new message
			// 2. elevator have a message for scheduler
			msg = elevatorTransmitter.receivePacket();

			if(Common.findType(msg) == Common.MESSAGETYPE.ACKNOWLEDGEMENT){
				// Received confirmation
				Common.ACKOWLEDGEMENT confirmationType = Common.findAcknowledgement(msg);
				if(confirmationType == Common.ACKOWLEDGEMENT.CHECK) {
					// Elevator wants to check message
					// send anything needs to be sent to elevator
					msg = getMsgFromElevatorBuffer(elevatorNumber);
					if (msg == null) {
						// no message for elevator, send a confirmation instead
						msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG);
					}

				} else {
					System.out.println("Unexpected msg from Elevator " + elevatorNumber + ": " + confirmationType);
					msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
				}

			} else {
				// Elevator wants to send message
				// make received message available for scheduler
				addToSchedulerBuffer(msg);
				msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);

			}
			// Reply to Elevator
			elevatorTransmitter.sendPacket(msg);
		}
	}

	// ONLY used when starting new elevator communicators
	private synchronized int incrementElevatorNumber() {
		return ++elevatorNumber;
	}

	// Initialize buffer for elevator with serialNumber
	// ONLY used when starting new elevator communicators
	private synchronized void inititializeElevBuffers(Integer elevatorNumber) {
		elevatorMsgBuffer.put(elevatorNumber, new LinkedList<byte[]>());
	}

	// Add msg to scheduler queue
	private synchronized void addToSchedulerBuffer(byte[] msg) {
		schedulerMsgBuffer.add(msg);
	}
	
	// Get msg for scheduler
	private synchronized byte[] getMsgFromSchedulerBuffer() {
		if(schedulerMsgBuffer.isEmpty()) {
			return null;
		}
		return schedulerMsgBuffer.pop();
	}

	// Add msg to elevator queue
	private synchronized void addToElevatorBuffer(byte[] msg) {
		// System.out.println("ElevSub holding msg for Elevator...");
		// message should be a scheduler msg
		Common.MESSAGETYPE messageType = Common.findType(msg);
		if(messageType == Common.MESSAGETYPE.SCHEDULER){
			// decodeScheduler: index 0 corresponds to elev number
			Integer elevatorNum = Common.decode(msg)[0];
			// add msg to elevator's queue
			elevatorMsgBuffer.get(elevatorNum).add(msg);
		} else {
			System.out.println("ERROR: Unexpected msg from Scheduler: " + messageType);
		}
	}
	
	// Get msg for elevator (providing elevator number)
	private synchronized byte[] getMsgFromElevatorBuffer(Integer elevatorNumber) {
		if(elevatorMsgBuffer.get(elevatorNumber).isEmpty()) {
			return null;
		}
		return elevatorMsgBuffer.get(elevatorNumber).pop();
	}
	
	public void run() {
		// init & start scheduler communicator
		Thread schedulerCommunicator = new Thread(this::schedulerCommunicator);
		schedulerCommunicator.start();
	
		// init & start elevator communicator(s)
		Thread[] elevatorCommunicators = new Thread[Common.NUM_ELEVATORS];
		for(int i = 0; i < Common.NUM_ELEVATORS; ++i){
			elevatorCommunicators[i] = new Thread(this::elevatorCommunicator);
			elevatorCommunicators[i].start();
			// start elevator thread
			System.out.println("Starting elevator " + (i + 1) + " thread.");
			elevators[i].start();
		}
	}

}
