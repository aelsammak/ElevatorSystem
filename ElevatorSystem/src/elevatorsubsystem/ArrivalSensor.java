package elevatorsubsystem;

public class ArrivalSensor extends Thread{
	
    private final Elevator elevator;

    public ArrivalSensor(Elevator elevator) {
        this.elevator = elevator;
    }

    public Elevator getElevator() {
        return elevator;
    }

    @Override
    public void run() {

    }

}
