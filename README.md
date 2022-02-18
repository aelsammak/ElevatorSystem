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
- /elevatorsubsystem:
  - ArrivalSensor.java - arrival logic for elevator reaching a floor
  - Door.java - door class maintaining an elevator door's state
  - Elevator.java - overarching class maintaining all logic and functionality for an elevator
  - ElevatorButton.java - class for state of an individual elevator button
  - ElevatorLamp.java - class for state of an individual elevator lamps
  - ElevatorState.java - enum responsible for representing the different states of the Elevator's motor
  - Motor.java - class for motors operation and state for an elevator
  - ElevatorSubsystem.java - reponsible for parsing the file to generate elevator events
- /floorsubsystem:
  - Floor.java - base class for floor logic 
  - TopFloor.java - child class of floor for uppermost floor
  - MiddleFloor.java - child class of floor for middle floors
  - BottomFloor.java - child class of floor for bottommost floor
  - FloorButton.java - class for state of a specific floor's button 
  - FloorLamp.java - class for state of a specific floor's lamp
  - FloorSubsystem.java - responsible for generating floor events and creating the floors
- /scheduler:
  - Scheduler.java - class to handle floor and elevator events and schedule elevators/update floor information to handle requests
  - Event.java - super class for events that occur within the control system
    - FloorEvent.java - Handle a request/event made by simulated up/down button pressed on a specific floor
    - ElevatorEvent.java - Handle a request/event made by simulated floor button pressed from within a specific elevator
  - FloorEventComparator.java - class to rank floor events based on time left until event, giving higher priority to events with less time left
  - ElevatorEventComparator.java - class to rank elevator events based on time left until event, giving higher priority to events with more time left
  - SchedulerState.java - enum responsible for representing the different states of the Scheduler
- /test:
  - InitialTest.java - contains test cases for testing reading and parsing of data from csvs
  - ElevatorTest.java - contains test cases for elevator movement between floors for events
  - TestSuiteRunner.java - responsible for running all tests within the project
  - StateChangeTest.java - contains test cases specifically focused on testing state changes of elevators as they move up and down floors.
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
