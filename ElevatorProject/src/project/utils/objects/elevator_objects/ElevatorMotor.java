package project.utils.objects.elevator_objects;

public class ElevatorMotor {

    private ElevatorMotorStatus state;

    public ElevatorMotor(ElevatorMotorStatus state) {
        this.state = state;
    }

    public ElevatorMotorStatus getState() {
        return state;
    }

    public void setState(ElevatorMotorStatus state) {
        this.state = state;
    }

    public enum ElevatorMotorStatus{
        ON, OFF;
    }
}
