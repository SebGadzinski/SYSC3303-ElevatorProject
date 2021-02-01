package project.models;

import project.utils.objects.floor_objects.*;
import project.utils.objects.general.DirectionLamp;

/*
 * Info:
 * 	UML diagram of Elevator and Floor explains what variables are inside, along bold text above it
 * 	This is a client to the Scheduler
 * 	Floor subsystem is to read in events using the format shown above: Time, floor or elevator number, and button.
 * Sending:
 * 	Each line of input is sent to the scheduler
 * Receiving:
 * 	Should be able to read commands from floorSubsystem class
 * 	information from the scheduler
 */

public class Floor extends Thread {

	public FloorButton upButton;
	public FloorButton downButton;

	public FloorLamp upLamp;
	public FloorLamp downLamp;

	public DirectionLamp upDirectionLamp;
	public DirectionLamp downDirectionLamp;

	public Scheduler scheduler;

	/*
	 * Constructors
	 */

	public Floor(Scheduler scheduler, DirectionLamp upDirectionLamp, DirectionLamp downDirectionLamp) {
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
		// code
	}

	private void setUpLamps() {
		// code
	}

	private void setUpArrivalSensors() {
		// code
	}

	private void requestElevator() {
		// code
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
