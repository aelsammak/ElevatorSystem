# SYSC 3303A - Project (Elevator System)
__Lab Section:__ L1 (Group 10)\
__Members:__ Kareem El-Hajjar, Adi El-Sammak, Ben Herriott, Erica Morgan, Cam Sommerville\
__Author:__ Ben Herriott, Erica Morgan
__Revisions:__

__Purpose:__ Design and develop a multi-car elevator system and simulator utilizing concurrent programming. The system will use a scheduler to control elevator routing, an elevator subsystem to handle individual car operations, and a floor subsystem to handle elevator requests on each floor.

__Setup Instructions__: 
- Download JDK-15.0.2 (https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html)
- 

__Execution Instructions__: TBD\
__Testing Instructions__: TBD

__List of files:__ root of code files ('/ElevatorSystem/src'):
- /common: 
  - Common.java - base config
  - ElevatorState.java - state config/constants for elevators
- /elevator:
  - ArrivalSensor.java - arrival logic for elevator reaching a floor
  - Door.java - door class maintaining an elevator door's state
  - Elevator.java - overarching class maintaining all logic and functionality for an elevator
  - ElevatorButton.java - class for state of an individual elevator button
  - ElevatorLamp.java - class for state of an individual elevator lamps
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
- /main/main.java - class that is responsible for running simulation and starting all threads
- measurements - Recorded timings used to simulate elevator times within the program

__Responsibilities:__
- Elevator Subsystem: Adi El-Sammak
- Floor Subsystem: Kareem El-Hajjar
- Scheduler Subsystem: Erica Morgan & Cam Sommerville
- Elevator Timings: Erica Morgan
- UML: Cam Sommerville, Erica Morgan
- SEQUENCE DIAGRAM: Adi El-Sammak & Kareem
- TESTING: Ben Herriott
- README: Ben Herriott, Erica Morgan
