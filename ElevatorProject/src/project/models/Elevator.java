package project.models;

import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.utils.objects.elevator_objects.*;
import project.utils.objects.elevator_objects.ElevatorMotor.ElevatorMotorStatus;

import java.util.HashMap;

/**
 * @author Sebastian Gadzinski (iter 2), Chase Fridgen (iter 1)
 * @version Iteration 2
 */
public class Elevator {

    // buttons are represented by the lamps
    private final HashMap<Integer, Boolean> buttonLamps;
    private boolean upDirectionLamp;
    private boolean downDirectionLamp;
    private final ElevatorMotor motor;
    private final ElevatorDoor door;

    public Elevator(HashMap<Integer, Boolean> buttonLamps,
                    Boolean upDirectionLamp,
                    Boolean downDirectionLamp,
                    ElevatorMotor motor,
                    ElevatorDoor door) {

        this.buttonLamps = buttonLamps;
        this.upDirectionLamp = upDirectionLamp;
        this.downDirectionLamp = downDirectionLamp;
        this.motor = motor;
        this.door = door;

    }

    public void turnOnLamp(int floor) {
        buttonLamps.put(floor, true);
    }

    public void turnOffLamp(int floor) {
        buttonLamps.put(floor, false);
    }

    public void turnOnMotor() {
        motor.setState(ElevatorMotorStatus.ON);
    }

    public void turnOffMotor() {
        motor.setState(ElevatorMotorStatus.OFF);
    }

    public void openDoor() {
        door.setDoorState(ElevatorDoorStatus.OPENED);
    }

    public void closeDoor() {
        door.setDoorState(ElevatorDoorStatus.CLOSED);
    }

    public void turnOnUpDirectionLamp() {
        upDirectionLamp = true;
    }

    public void turnOffUpDirectionLamp() {
        upDirectionLamp = false;
    }

    public void turnODownDirectionLamp() {
        downDirectionLamp = true;
    }

    public void turnOffDownDirectionLamp() {
        downDirectionLamp = false;
    }

}
