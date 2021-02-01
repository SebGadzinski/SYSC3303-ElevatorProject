package project.models;

import project.systems.*;

/*
 * Info:
 * 	Communication between Floor and Elevator
 * 	This is a client to the Scheduler
 * Sending:
 * 	Reply to elevator when work is to be done
 * 	Send data from Elevator to Floor
 * Receiving:
 * 	Input from Floor
 * 	Calls from Elevator
 */

public class Scheduler extends Thread {

	public FloorSubSystem floorSubSystem;
	public ElevatorSubSystem elevatorSubSystem;

	/*
	 * Constructors
	 */

	public Scheduler(FloorSubSystem floorSubSystem, ElevatorSubSystem elevatorSubSystem) {
		this.floorSubSystem = floorSubSystem;
		this.elevatorSubSystem = elevatorSubSystem;
	}

	/*
	 * Functions
	 */

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
