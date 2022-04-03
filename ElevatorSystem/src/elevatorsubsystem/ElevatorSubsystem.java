package elevatorsubsystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;

import common.Common;
import common.RPC;
import floorsubsystem.FileLoader;
import gui.GUI;
import scheduler.Scheduler;

/**
 * The ElevatorSubsystem class acts as the intermediate between the Elevators and the Scheduler. 
 * 
 * @author Adi El-Sammak
 * @version 3.0
 *
 */
public class ElevatorSubsystem extends Thread {
	
	private Thread[] elevators;
	private int elevatorNumber = 0;
	private HashMap<Integer, LinkedList<byte[]>> elevatorMsgBuffer;
	private LinkedList<byte[]> schedulerMsgBuffer;
	private final InetAddress SCHEDULER_ADDR;
	private final InetAddress ELEVATOR_ADDR;
	private FileLoader fileLoader;
	private RPC schedulerTransmitter, rpcGUI;
	
	/* PORTS */
	/* PLEASE NOTE: THIS PORT MIGHT NEED TO CHANGE DEPENDING IF YOUR LOCAL SYSTEM IS USING THIS PORT NUMBER */
	public static final int ELEVSUBSYSTEM_RECEIVE_PORT  = 10003;

	/* The starting port number used for receiving packets from the elevator */
	/* The elevatorNumber will be added to this port to offset this port number and give a unique port number */
	private static final int ELEVSUBSYSTEM_RECEIVE_FROM_ELEV_PORT = 9002;

	/* The starting port number used for receiving packets by the elevator */
	/* The elevatorNumber will be added to this port to offset this port number and give a unique port number */
	private static final int ELEV_RECEIVE_PORT = 10202;
	
	/* The starting port number used for sending packets from the elevator */
	public static final int ELEVSUBSYSTEM_TO_GUI_PORT = 9010;
	
	/**
	 * Constructor for the ElevatorSubSystem class.
	 * 
	 * @throws Exception - invalid setting
	 */
	public ElevatorSubsystem() throws Exception {
		
		if (Common.NUM_ELEVATORS <= 0) {
			throw new Exception("Invalid setting: NUM_ELEVATORS should be at least 1.");
		}
		
		/* Initialize InetAddresses */
		SCHEDULER_ADDR = InetAddress.getLocalHost();
		ELEVATOR_ADDR = InetAddress.getLocalHost();

//		/* Initialize Elevators */
//		fileLoader = new FileLoader();
//		elevators = new Thread[Common.NUM_ELEVATORS];
//		for (int i = 0; i < Common.NUM_ELEVATORS; ++i) {
//			int elevatorNumber = i + 1;
//			elevators[i] = new Elevator(elevatorNumber, 1, ELEVSUBSYSTEM_RECEIVE_FROM_ELEV_PORT + elevatorNumber, ELEV_RECEIVE_PORT + elevatorNumber, fileLoader);
//		}

		/* Initialize buffers */
		elevatorMsgBuffer = new HashMap<Integer, LinkedList<byte[]>>();
		schedulerMsgBuffer = new LinkedList<byte[]>();
		schedulerTransmitter = new RPC(SCHEDULER_ADDR, Scheduler.SCHEDULER_RECEIVE_PORT, ELEVSUBSYSTEM_RECEIVE_PORT);
		rpcGUI = new RPC(InetAddress.getLocalHost(), GUI.GUI_RECEIVE_FROM_ELEVSUBSYSTEM_PORT, ELEVSUBSYSTEM_TO_GUI_PORT);
	}
	
	/**
	 * This method is used to create schedulerCommunicator threads which will be used by the ElevatorSubSystem to communicate with the Schduler.
	 */
	private void schedulerCommunicator() {
		byte[] msg;

		while(true) {
			msg = schedulerTransmitter.receivePacket();

			if(Common.findType(msg) == Common.MESSAGETYPE.ACKNOWLEDGEMENT) {
				/* Received Ack */
				Common.ACKOWLEDGEMENT confirmationType = Common.findAcknowledgement(msg);
				if(confirmationType == Common.ACKOWLEDGEMENT.CHECK){
					/* Ack is of type CHECK */
					msg = getMsgFromSchedulerBuffer();
					if (msg == null){
						/* No msg for Scheduler, send an ACK instead */
						msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG);
					}

				} else {
					System.out.println("ERROR: Unexpected msg from Scheduler: " + confirmationType);
					msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
				}

			} else {
		    	try {
					FileWriter writer = new FileWriter(new File("Timings.txt"), true);
					writer.write("Time of ElevatorSubSystem Received Message From Scheduler : " + LocalTime.now() + "\n");
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				/* Msg received for Elevator */
				addToElevatorBuffer(msg);
				msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
				
			}
			/* Reply to Scheduler */
			schedulerTransmitter.sendPacket(msg);
		}
	}

	/**
	 * This method is used to create elevatorCommunicator threads which will be used by the ElevatorSubSystem to communicate with an Elevator.
	 */
	private void elevatorCommunicator() {
		int elevatorNumber = incrementElevatorNumber();
		inititializeElevBuffers(elevatorNumber);
		
		RPC elevatorTransmitter = new RPC(ELEVATOR_ADDR, ELEV_RECEIVE_PORT + elevatorNumber, ELEVSUBSYSTEM_RECEIVE_FROM_ELEV_PORT + elevatorNumber);
		byte[] msg;

		while(true) {
			msg = elevatorTransmitter.receivePacket();

			if(Common.findType(msg) == Common.MESSAGETYPE.ACKNOWLEDGEMENT) {
				/* Received Ack */
				Common.ACKOWLEDGEMENT confirmationType = Common.findAcknowledgement(msg);
				if(confirmationType == Common.ACKOWLEDGEMENT.CHECK) {
					/* Ack is of type CHECK */
					msg = getMsgFromElevatorBuffer(elevatorNumber);
					if (msg == null) {
						/* No msg for Elevator, send an ACK instead */
						msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG);
					} else {
				    	try {
				    		FileWriter writer = new FileWriter(new File("Timings.txt"), true);
							writer.write("Time of ElevatorSubSystem Sending Message To Elevator : " + LocalTime.now() + "\n");
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				} else {
					System.out.println("Unexpected msg from Elevator " + elevatorNumber + ": " + confirmationType);
					msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
				}

			} else if (Common.findType(msg) == Common.MESSAGETYPE.ELEVATOR_TO_GUI || Common.findType(msg) == Common.MESSAGETYPE.ELEVATOR_ERROR_TO_GUI) {
				rpcGUI.sendPacket(msg);
				msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG);
			} else {
				/* Msg received for Scheduler */
				addToSchedulerBuffer(msg);
				msg = Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.RECEIVED);
			}
			/* Reply to Scheduler */
			elevatorTransmitter.sendPacket(msg);
		}
	}

	/**
	 * This method is used to increment the elevator number to be used as an offset when initializing elevator communcator threads
	 * 
	 * @return int - the elevator number
	 */
	private synchronized int incrementElevatorNumber() {
		return ++elevatorNumber;
	}

	/**
	 * This method is used to initialize the elevator buffers using the elevatorNumber when initializing elevator communcator threads
	 * 
	 * @param elevatorNumber - the elevator number
	 */
	private synchronized void inititializeElevBuffers(Integer elevatorNumber) {
		elevatorMsgBuffer.put(elevatorNumber, new LinkedList<byte[]>());
	}

	/**
	 * This method is used add a msg to the scheduler's buffer
	 * 
	 * @param msg - the msg to add
	 */
	private synchronized void addToSchedulerBuffer(byte[] msg) {
		schedulerMsgBuffer.add(msg);
	}
	
	/**
	 * This method is used to get a msg from the scheduler's buffer
	 * 
	 * @return byte[] - the msg
	 */
	private synchronized byte[] getMsgFromSchedulerBuffer() {
		if(schedulerMsgBuffer.isEmpty()) {
			return null;
		}
		return schedulerMsgBuffer.pop();
	}

	/**
	 * This method is used to add a msg to the Elevator's buffer
	 * 
	 * @param msg - the msg to add
	 */
	private synchronized void addToElevatorBuffer(byte[] msg) {
		Common.MESSAGETYPE messageType = Common.findType(msg);
		if(messageType == Common.MESSAGETYPE.SCHEDULER){
			Integer elevatorNum = Common.decode(msg)[0];
			elevatorMsgBuffer.get(elevatorNum).add(msg);
		} else {
			System.out.println("ERROR: Unexpected msg from Scheduler: " + messageType);
		}
	}
	
	/**
	 * This method is used to a get a msg from the Elevator's buffer
	 * 
	 * @param elevatorNumber - the elevator number
	 * @return byte[] - the msg
	 */
	private synchronized byte[] getMsgFromElevatorBuffer(Integer elevatorNumber) {
		if(elevatorMsgBuffer.get(elevatorNumber).isEmpty()) {
			return null;
		}
		return elevatorMsgBuffer.get(elevatorNumber).pop();
	}
	
	private void waitToStartSim() {
    	byte[] message;
    	while (true) {
    		try {
    			message = schedulerTransmitter.receivePacket();
    		} catch (Exception e) {
				continue;
			}
    		if (message != null) {
        		if (Common.findType(message) == Common.MESSAGETYPE.START_SIM) {
        			schedulerTransmitter.sendPacket(Common.encodeAckMsgIntoBytes(Common.ACKOWLEDGEMENT.NO_MSG));
        			break;
                }
    		}
    	}
	}
	
	
	/**
	 * Creates scheduler and elevator communicator threads which send and receive.
	 */
	public void run() {
		waitToStartSim();
		
		/* Initialize Elevators */
		try {
			fileLoader = new FileLoader();
			elevators = new Thread[Common.NUM_ELEVATORS];
			for (int i = 0; i < Common.NUM_ELEVATORS; ++i) {
				int elevatorNumber = i + 1;
				elevators[i] = new Elevator(elevatorNumber, 1, ELEVSUBSYSTEM_RECEIVE_FROM_ELEV_PORT + elevatorNumber, ELEV_RECEIVE_PORT + elevatorNumber, fileLoader);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* Initialize and start the scheduler communicator threads */
		Thread schedulerCommunicator = new Thread(this::schedulerCommunicator);
		schedulerCommunicator.start();
	
		/* Initialize and start the elevator communicator threads */
		Thread[] elevatorCommunicators = new Thread[Common.NUM_ELEVATORS];
		for(int i = 0; i < Common.NUM_ELEVATORS; ++i){
			elevatorCommunicators[i] = new Thread(this::elevatorCommunicator);
			elevatorCommunicators[i].start();
			System.out.println("Starting elevator " + (i + 1) + " thread.");
			elevators[i].start();
		}
	}

	public static void main(String[] args) {
        ElevatorSubsystem elevatorSubsystem;
		try {
			elevatorSubsystem = new ElevatorSubsystem();
			elevatorSubsystem.start();
		} catch (Exception e) {

			e.printStackTrace();
		}
        
    }
}

