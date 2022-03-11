# SYSC 3303A - Project (Elevator System)
__Lab Section:__ L1 (Group 10)\
__Members:__ Kareem El-Hajjar, Adi El-Sammak, Ben Herriott, Erica Morgan, Cam Sommerville\
__Authors:__ Ben Herriott, Erica Morgan

__Purpose:__ Design and develop a multi-car elevator system and simulator utilizing concurrent programming. The system will use a scheduler to control elevator routing, an elevator subsystem to handle individual car operations, and a floor subsystem to handle elevator requests on each floor.

__Requirements__: 
- Integrated Development Environment (IDE): Eclipse Java IDE
- Java Development Kit: JDK-15.0.2 
- Plugins: Egit
- Credentials: GitHub username and personal access token (PAC)

__Setup Instructions__: 

- Clone Repository
  - Clone repository via Eclipse
      - Go to File -> Import -> Git -> Projects from git -> Next -> Clone URI
        - Enter URI: https://github.com/aelsammak/ElevatorSystem.git
        - Enter username and password and click Next- username is GitHub username, password is GitHub personal access token
      - Ensure all branches are checked and click Next
      - Choose directory to store project and click Next
      - Select Working Tree and click Next
      - Click Finish
  - Cloning repository via terminal
     -  Go to https://github.com/aelsammak/ElevatorSystem/
     -  Above the list of files click "Code"
     -  Under "Clone with HTTPS" click clipboard
     -  Open Terminal
     -  Change the current working directory to the location where you want the cloned directory
     -  Type git clone, and then paste the URL you copied earlier
     -  `$ git clone https://github.com/aelsammak/ElevatorSystem.git`
     -  Press Enter to create your local clone
     -  Open Eclipse and Go to File -> Import -> General -> Existing Projects into Workspace
     -  Select root directory where project is kept
     -  Click Finish

__Execution Instructions__:
- In Eclipse
  - Navigate to the /ElevatorSystem/src/main/Main.java
  - Right-click on Main.java then click Run as -> Java Application

__Testing Instructions__:
- In Eclipse
  - Navigate to the /ElevatorSystem/src/test/TestSuiteRunner.java
  - Right-click on TestSuiteRunner.java then click Run as -> Java Application
  - Note: this will run all test cases contained within the project

__List of files:__ root of code files ('/ElevatorSystem/src'):
- /common: 
  - Common.java - class responsible for storing common attributes for other classes within the system to access
  - Config.java - class responsible for loading all the properties necessary for the simulation from a given config file.
  - RPC.java - class used as helper class for RPC communication.
- /elevatorsubsystem:
  - ArrivalSensor.java - class represents the Elevator's ArrivalSensor
  - Door.java - class represents the Elevator's doors
  - Elevator.java - class represents a single Elevator in the ElevatorSubSystem
  - ElevatorButton.java - class represents the Elevator's buttons
  - ElevatorLamp.java - class represents the Elevator's lamps
  - ElevatorSubsystem.java - class acts as the intermediate between the Elevators and the Scheduler
  - Motor.java - class represents the Elevator's motor
  - MotorState.java - enum responsible for representing the different states of the Elevator's motor
- /floorsubsystem:
  - BottomFloor.java - Bottom floor variant in the Floor class
  - FileLoader.java - responsible for reading the instructions from the simulation.csv file
  - Floor.java - class represents a single Floor in the FloorSubSystem. 
  - FloorButton.java - class represents the floor button and all its functionality
  - FloorLamp.java - class represents the floor lamp and all its functionality
  - FloorSubsystem.java - class represents the FloorSubSystem
  - MiddleFloor.java - Middle floor variant of the Floor class
  - TopFloor.java - Top floor variant of the Floor class
- /scheduler:
  - ElevatorState.java - Keeps track of elevator state
  - FloorState.java - Keeps track of floor state
  - Scheduler.java - class responsible for storing and dispatching elevators in response to passenger requests
  - SchedulerState.java - enum responsible for representing the different states of the Scheduler
- /test:
  - ElevatorTest.java - tests the implemented elevator selection algorithm to see based on a floor request which elevator will be selected to best facilitate the request
  - ElevatorTestHelper.java - helper class to mimic the work of the schedule on a series of elevators, code taken from the Scheduler class
  - InitialTest.java - test cases specifically focused on testing reading and parsing of data from csv's to the floor and elevator subsystems.
  - RPCTest.java - Tests all mechanisms of RPC within the application, from encoding to decoding to a simple send/recieve test
  - TestSuiteRunner.java - runs all test cases created within the application
- /main:
  - Main.java - class that is responsible for running simulation and starting all threads
- /resources:
  - config.cfg - config file used to configure the parameters and initial settings for the program
- measurements - Recorded timings used to simulate elevator times within the program

__Responsibilities:__
- Elevator Subsystem: Adi El-Sammak
- Floor Subsystem: Kareem El-Hajjar
- Scheduler Subsystem: Erica Morgan & Cam Sommerville
- Elevator Timings: Erica Morgan
- UML Class Diagram: Cam Sommerville, Erica Morgan
- Sequence Diagram: Adi El-Sammak & Kareem
- State Machine Diagrams: Adi El-Sammak, Cam Sommerville & Kareem El-Hajjar
- Testing: Ben Herriott
- README: Ben Herriott, Erica Morgan
