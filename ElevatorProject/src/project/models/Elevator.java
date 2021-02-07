package project.models;

import project.Config;
import project.utils.objects.elevator_objects.*;
import project.utils.objects.general.DirectionLamp;
import project.utils.objects.sensors.*;

import java.util.ArrayList;

/*
 * Info:
 * 	This is a client to the Scheduler
 * Sending:
 * 	Send calls to Scheduler
 * 	Send data to Scheduler to be sent to the floor
 * Receiving:
 * 	Replies from Scheduler
 *
 */

/**
 * @author Chase Fridgen
 */
public class Elevator implements Runnable {

    public ArrayList<ElevatorButton> buttons;
    public ArrayList<ElevatorLamp> lamps;
    public ArrayList<ArrivalSensor> arrivalSensors;
    public ElevatorMotor motor = new ElevatorMotor();
    public ElevatorDoor door = new ElevatorDoor();
    // Shared
    public DirectionLamp upDirectionLamp;
    public DirectionLamp downDirectionLamp;
    public Scheduler scheduler;
    /*
     * Constructors
     */

    public Elevator(Scheduler scheduler, DirectionLamp upDirectionLamp, DirectionLamp downDirectionLamp) {
        setUpButtons();
        setUpLamps();
        setUpArrivalSensors();
        this.scheduler = scheduler;
        this.upDirectionLamp = upDirectionLamp;
        this.downDirectionLamp = downDirectionLamp;
    }

    /*
     * Functions
     */

    private void setUpButtons() {
        this.buttons = new ArrayList<>();
        for (int i = 0; i < Config.NUMBER_OF_FLOORS; i++) {
            this.buttons.add(new ElevatorButton());
        }
    }

    private void setUpLamps() {
        this.lamps = new ArrayList<>();
        for (int i = 0; i < Config.NUMBER_OF_FLOORS; i++) {
            this.lamps.add(new ElevatorLamp());
        }
    }

    private void setUpArrivalSensors() {
        this.arrivalSensors = new ArrayList<>();
        for (int i = 0; i < Config.NUMBER_OF_FLOORS; i++) {
            this.arrivalSensors.add(new ArrivalSensor());
        }
    }

    private void selectDestination() {

    }

    @Override
    public void run() {

    }

}
