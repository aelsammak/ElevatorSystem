# SYSC 3303A - Project (Elevator System)
__Lab Section:__ L1 (Group 10)\
__Members:__ Kareem El-Hajjar, Adi El-Sammak, Ben Herriott, Erica Morgan, Cam Sommerville
__Author:__ Ben Herriott
__Revisions:__

__Purpose:__ Design and develop a multi-car elevator system and simulator utilizing concurrent programming. The system will use a scheduler to control elevator routing, an elevator subsystem to handle individual car operations, and a floor subsystem to handle elevator requests on each floor.

__Setup Instructions__: TBD\
__Execution Instructions__: TBD\
__Testing Instructions__: TBD

__List of files:__ root of code files ('/ElevatorSystem/src'):
- /common: Common.java (base config), ElevatorState.java (state config/constants for elevators)
- /elevator:
  - ArrivalSensor.java - arrival logic for elevator reaching a floor
  - Door.java - door class maintaining an elevator door's state
  - Elevator.java - overarching class maintaining all logic and functionality for an elevator
  - ElevatorButton.java (class for state of an individual elevator button), ElevatorLamp.java (class for state of an individual elevator lamps), Motor.java (class for motors operation and state for an elevator)
  - ElevatorSubsystem.java - reponsible for parsing the file to generate elevator events
- /floorsubsystem:
  - Floor.java - base class for floor logic (child classes TopFloor.java, MiddleFloor.java, BottomFloor.java)
  - FloorButton.java (class for state of a specific floor's button), FloorLamp.java (class for state of a specific floor's lamps)
  - FloorSubsystem.java - responsible for generating floor events and creating the floors
- /scheduler:
  - Scheduler.java - class to handle floor and elevator events and schedule elevators/update floor information to handle requests
  - Event.java - super class for events that occur within the control system
    - FloorEvent.java - Handle a request/event made by simulated on a specific floor
    - ElevatorEvent.java - Handle requests made from by a specific elevator
- /Main/main.java - class that is responsible running simulation and starting all threads
- measurements - Recorded timings used to simulate elevator times within the program

__Responsibilities:__
- Elevator Subsystem: Adi El-Sammak
- Floor Subsystem: Kareem El-Hajjar
- Scheduler Subsystem: Erica Morgan & Cam Sommerville
- Elevator Timings: Erica Morgan
- UML: Cam Sommerville, Erica Morgan
- SEQUENCE DIAGRAM: Floor - Adi El-Sammak & Kareem, Scheduler - Erica Morgan & Cam Sommerville
- TESTING: Ben Herriott
- README: Ben Herriott
