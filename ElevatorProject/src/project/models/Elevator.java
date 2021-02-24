package project.models;

import project.state_machines.ElevatorState.ElevatorDoorStatus;
import project.utils.objects.elevator_objects.*;
import project.utils.objects.elevator_objects.ElevatorMotor.ElevatorMotorStatus;

import java.util.HashMap;

/*
 * Info:
 * Currently Unfinished
 *
 */

/**
 * @author Iter1 (Chase Fridgen), Iter2 (Sebastian Gadzinski)
 */
public class Elevator {
    //Buttons are represented by the lamps 
    private HashMap<Integer, Boolean> buttonLamps;
    private Boolean upDirectionLamp;
    private Boolean downDirectionLamp;
    private ElevatorMotor motor;
    private ElevatorDoor door;

     public Elevator(HashMap<Integer, Boolean> buttonLamps, Boolean upDirectionLamp, Boolean downDirectionLamp,
            ElevatorMotor motor, ElevatorDoor door) {
        this.buttonLamps = buttonLamps;
        this.upDirectionLamp = upDirectionLamp;
        this.downDirectionLamp = downDirectionLamp;
        this.motor = motor;
        this.door = door;
    }

    public void turnOnLamp(int floor){
        buttonLamps.put(floor, true);
    }

    public void turnOffLamp(int floor){
        buttonLamps.put(floor, false);
    }

    public void turnOnMotor(){
        motor.setState(ElevatorMotorStatus.ON);
    }

    public void turnOffMotor(){
        motor.setState(ElevatorMotorStatus.OFF);
    }

    public void openDoor(){
        door.setDoorState(ElevatorDoorStatus.OPENED);
    }

    public void closeDoor(){
        door.setDoorState(ElevatorDoorStatus.CLOSED);
    }

    public void turnOnUpDirectionLamp(){
        upDirectionLamp = true;
    }

    public void turnOffUpDirectionLamp(){
        upDirectionLamp = false;
    }

    public void turnODownDirectionLamp(){
        downDirectionLamp = true;
    }

    public void turnOffDownDirectionLamp(){
        downDirectionLamp = false;
    }

}
