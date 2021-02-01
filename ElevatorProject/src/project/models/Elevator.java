package Project;

import java.util.ArrayList;

/*
 * Info:
 * 	This is a client to the Scheduler
 * Sending:
 * 	Send calls to Scheduler
 * 	Send data to Scheduler to be sent to the floor
 * Receiving:
 * 	Replies from Scheduler
 */
public class Elevator extends Thread {

	public ArrayList<ElevatorButton> buttons;
	public ArrayList<ElevatorLamp> lamps;
	public ArrayList<ArivalSensor> arrivalSensors;
	public Motor motor = new Motor();
	public Door door = new Door();
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
//		code
	}

	private void setUpLamps() {
//		code
	}

	private void setUpArrivalSensors() {
//		code
	}

	private void selectDestination() {
//		code
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
