package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalTime;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import common.Common;
import common.RPC;
import elevatorsubsystem.ElevatorSubsystem;
import floorsubsystem.FloorSubsystem;

/**
 * This class is used to represent the Elevator Simulation in the form of a GUI
 * 
 * @author Adi El-Sammak
 *
 */
public class GUI extends JFrame {
	
	/* The port number used for receiving packets from the ElevatorSubSystem */
	public static final int GUI_RECEIVE_FROM_ELEVSUBSYSTEM_PORT = 9030;
	
	/* The port number used for receiving packets from the FloorSubSystem */
	public static final int GUI_RECEIVE_FROM_FLOORSUBSYSTEM_PORT = 9031;
	
	private RPC floorToGUI, elevatorToGUI;
	public static JTextArea textPanel;
	
	/**
	 * Default constructor for the GUI
	 */
	public GUI() {
		super();
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(4, 1));
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		
		JLabel mainLabel = new JLabel("ELEVATOR SIMULATION");
		mainLabel.setFont(new Font("COMIC SANS", Font.BOLD, 25));
		
		topPanel.add(mainLabel);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 7));
		
		JPanel[] elevPanel = new JPanel[Common.NUM_ELEVATORS];
		JPanel[] elevatorNumberPanels = new JPanel[Common.NUM_ELEVATORS];
		JLabel[] elevatorNumberLabels = new JLabel[Common.NUM_ELEVATORS];
		JPanel[] elevatorInfoPanels = new JPanel[Common.NUM_ELEVATORS];
		JLabel[] elevStateLabels = new JLabel[Common.NUM_ELEVATORS];
		JLabel[] floorNumberLabels = new JLabel[Common.NUM_ELEVATORS];
		JButton[] elevStateBtns = new JButton[Common.NUM_ELEVATORS];
		JButton[] floorNumberBtns = new JButton[Common.NUM_ELEVATORS];
		
		for (int j = 0; j < Common.NUM_ELEVATORS; j++) {
			elevPanel[j] = new JPanel();
			elevatorNumberPanels[j] = new JPanel();
			elevatorNumberLabels[j] = new JLabel();
			elevatorInfoPanels[j] = new JPanel();
			elevStateLabels[j] = new JLabel();
			floorNumberLabels[j] = new JLabel();
			elevStateBtns[j] = new JButton();
			floorNumberBtns[j] = new JButton();
		}
		
		for(int i = 0; i < Common.NUM_ELEVATORS; i++) {
			elevPanel[i].setLayout(new GridLayout(2, 1));
			elevatorNumberPanels[i].setLayout(new GridBagLayout());
			elevatorNumberLabels[i].setText("ELEVATOR #" + (i+1));
			elevatorNumberLabels[i].setFont(new Font("COMIC SANS", Font.BOLD, 18));
			
			elevatorInfoPanels[i].setLayout(new GridLayout(2, 2));
			
			elevStateLabels[i].setText("STATE");
			elevStateLabels[i].setFont(new Font("COMIC SANS", Font.PLAIN, 14));
			elevStateLabels[i].setHorizontalAlignment(JLabel.CENTER);
			
			floorNumberLabels[i].setText("FLOOR #");
			floorNumberLabels[i].setFont(new Font("COMIC SANS", Font.PLAIN, 14));
			floorNumberLabels[i].setHorizontalAlignment(JLabel.CENTER);
			elevStateBtns[i].setText("IDLE");
			floorNumberBtns[i].setText("1");
			elevatorInfoPanels[i].add(elevStateLabels[i]);
			elevatorInfoPanels[i].add(floorNumberLabels[i]);
			elevatorInfoPanels[i].add(elevStateBtns[i]);
			elevatorInfoPanels[i].add(floorNumberBtns[i]);
			
			elevatorNumberPanels[i].add(elevatorNumberLabels[i]);
			
			elevPanel[i].add(elevatorNumberPanels[i]);
			elevPanel[i].add(elevatorInfoPanels[i]);
			if (i == 0) {
				bottomPanel.add(elevPanel[i]);
			} else {
				bottomPanel.add(new JPanel());
				bottomPanel.add(elevPanel[i]);
			}
		}
		
		mainPanel.add(topPanel);
		mainPanel.add(bottomPanel);
		mainPanel.add(new JPanel());
		mainPanel.add(textPanel());
		
		// get the screen size as a java dimension
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// get 2/3 of the height, and 2/3 of the width
		int height = screenSize.height * 2 / 3;
		int width = (screenSize.width * 5 / 6);
		
		this.setPreferredSize(new Dimension(width, height));
		
		this.add(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Elevator System");
		this.pack();
		this.setVisible(true);
		
		try {
			floorToGUI = new RPC(InetAddress.getLocalHost(), FloorSubsystem.FLOORSUBSYSTEM_TO_GUI_PORT, GUI_RECEIVE_FROM_FLOORSUBSYSTEM_PORT);
			elevatorToGUI = new RPC(InetAddress.getLocalHost(), ElevatorSubsystem.ELEVSUBSYSTEM_TO_GUI_PORT, GUI_RECEIVE_FROM_ELEVSUBSYSTEM_PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        Thread elevatorReceiveThread = new Thread() {
        	public void run() {
        		while(true) {
	       			byte[] msg = elevatorToGUI.receivePacket();
	       			int[] decodedMsg = Common.decode(msg);
	       			if (Common.findType(msg) == Common.MESSAGETYPE.ELEVATOR_ERROR_TO_GUI) {
	       				if (decodedMsg[1] == 1) {
	       					// change button to red for fault
	       					TextManager.print("Elevator #" + decodedMsg[0] + " IS STUCK");
	       					elevStateBtns[decodedMsg[0] - 1].setBackground(Color.RED);
		       				floorNumberBtns[decodedMsg[0] - 1].setBackground(Color.RED);
	       				} else {
	       					// Undo the red button -> make it green
	       					TextManager.print("Elevator #" + decodedMsg[0] + " IS UNSTUCK");
	       					elevStateBtns[decodedMsg[0] - 1].setBackground(null);
		       				floorNumberBtns[decodedMsg[0] - 1].setBackground(null);
	       				}
	       			} else if (Common.findType(msg) == Common.MESSAGETYPE.ELEVATOR_TO_GUI) {
	       				int elevatorNumber = decodedMsg[0];
	       				int currentElevatorFloor = decodedMsg[1];
	       				int motorState = decodedMsg[2];
	       				if (decodedMsg[3] != -1) {
	       					// car button pressed @ decodedMsg[3]
	       					TextManager.print("Elevator #" + elevatorNumber + " car button " + decodedMsg[3] + " has been pressed");
	       				} else if (decodedMsg[5] == 1) {
	       					TextManager.print("Elevator #" + elevatorNumber + " " + (motorState == 0 ? "arrived at " : (motorState == 1 ? "MOVING UP to " : "MOVING DOWN to ")) + "Floor #" + decodedMsg[4]);
	       				}
	       				elevStateBtns[elevatorNumber - 1].setText((motorState == 0 ? "IDLE" : (motorState == 1 ? "UP" : "DOWN")));
	       				floorNumberBtns[elevatorNumber - 1].setText("" + currentElevatorFloor);
	       			}
        		}
        	}
        };
        elevatorReceiveThread.start();
        
        Thread floorReceiveThread = new Thread() {
        	public void run() {
        		while(true) {
	       			byte[] msg = floorToGUI.receivePacket();
	       			int[] decodedMsg = Common.decode(msg);
	       			if (Common.findType(msg) == Common.MESSAGETYPE.FLOOR_TO_GUI) {
	       				int floorNumber = decodedMsg[0];
	       				int isPressed = decodedMsg[2];
	       				String dirBtn = "DOWN"; 
	       				if (decodedMsg[1] == 1) {
	       					dirBtn = "UP";
	       				}
	       				
	       				if (isPressed == 1) {
	       					TextManager.print("Floor #" + floorNumber + " " + dirBtn + " button pressed");
	       				} else {
	       					TextManager.print("Turning OFF Floor #" + floorNumber + " "+ dirBtn + " button & lamp");
	       				}
	       				
	       			}
        		}
        	}
        };
        floorReceiveThread.start();
	}
	
	/**
	 * create a scrollable text panel to write important logs of the simlation too
	 * @return JPanel
	 */
	private JPanel textPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5 , 5));
		panel.setLayout(new BorderLayout(0, 0));
		
		textPanel = new JTextArea(10, 10);
		TextManager.print("Logging will appear here\nSimulation starting...\n");
		panel.add(textPanel, BorderLayout.CENTER);
		
		JScrollPane sp = new JScrollPane(textPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		panel.add(sp);
		
		return panel;
	}
	
	/**
	 * Main method to run the GUI
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new GUI();
	}

}
