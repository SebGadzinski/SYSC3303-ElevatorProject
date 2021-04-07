package project.gui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;

public class ElevatorGUI {
	public static final int AMOUNT_OF_ELEMENTS = 6;
	String id, topLabelString;
	JLabel idLabel, destinationLabel, lampsLabel, passengersLabel, doorStateLabel, directionLabel;
	private HashMap<Integer, Boolean> lamps;
	private int passengers, sizeOfElevator, destination;
	private ElevatorDoorStatus doorState;
	private ElevatorDirection direction;
	private boolean elevatorUnderRepair, elevatorShutDown;
	
	/*
	 * Initiates all attributes of elevator GUI
	 */
	public ElevatorGUI(String id, HashMap<Integer, Boolean> lamps, int passengers, ElevatorDoorStatus doorState,
			ElevatorDirection direction, int destination ) {
		this.id = id;
		this.idLabel = new JLabel("id: " + id);
		this.lamps = lamps;
		lampsLabel = new JLabel("lamps on: ");
		updateLamps();
		this.passengers = passengers;
		passengersLabel = new JLabel("passengers: ");
		this.doorState = doorState;
		doorStateLabel = new JLabel("doors: ");
		this.direction = direction;
		directionLabel = new JLabel("direction: ");
		this.destination = destination;
		destinationLabel = new JLabel("dest: ");
	}

	/*
	 * Updates the lamps label with current lamps that are on
	 */
	public void updateLamps() {
		Iterator it = lamps.entrySet().iterator();
		String lampsOn = "lamps on: ";
	    while (it.hasNext()) {
	        Map.Entry<Integer, Boolean> pair = (Map.Entry<Integer, Boolean>)it.next();
	        if(pair.getValue()) {
	        	lampsOn += Integer.toString((Integer) pair.getKey()) + ",";
	        }
	    }
		this.lampsLabel.setText(lampsOn);
		lampsLabel.updateUI();
	}
	
	/*
	 * Updates the passengers label with current passengers
	 */
	public void updatePassengers(){
		passengersLabel.setText("passengers: " + Integer.toString(passengers));
		passengersLabel.updateUI();
	}
	
	/*
	 * Updates the door label with the current door state
	 */
	public void updateDoorState(){
		doorStateLabel.setText("doors: " + doorState);
		doorStateLabel.updateUI();
	}
	
	/*
	 * Updates the direction label with the current direction
	 */
	public void updateDirection(){
		directionLabel.setText("direction: " + direction);
		directionLabel.updateUI();
	}
	
	/*
	 * Updates the destination label with the current destination
	 */
	public void updateDestination(){
		destinationLabel.setText("dest: " + destination);
		destinationLabel.updateUI();
	}
	
	public HashMap<Integer, Boolean> getLamps() {
		return lamps;
	}
	
	public void setLamp(int lampNumber, boolean isOn){
		lamps.put(lampNumber, isOn);
		updateLamps();
	}
	
	public void setLamps(HashMap<Integer, Boolean> lamps){
		this.lamps = lamps;
		updateLamps();
	}

	public int getPassengers() {
		return passengers;
	}
	
	public void setPassengers(int passengers) {
		this.passengers = passengers;
		updatePassengers();
	}

	public void addPassenger() {
		this.passengers++;
		updatePassengers();
	}
	
	public void subtractPassenger() {
		this.passengers++;
		updatePassengers();
	}

	public ElevatorDoorStatus getDoorState() {
		return doorState;
	}

	public void setDoorState(ElevatorDoorStatus doorState) {
		this.doorState = doorState;
		updateDoorState();
	}

	public ElevatorDirection getDirection() {
		return direction;
	}

	public void setDirection(ElevatorDirection direction) {
		this.direction = direction;
		updateDirection();
	}

	public JLabel getIdLabel(Dimension dimension) {
		idLabel.setSize(dimension);
		return idLabel;
	}
	
	public JLabel getDestinationLabel(Dimension dimension) {
		updateDestination();
		destinationLabel.setSize(dimension);
		return destinationLabel;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
		updateDestination();
	}

	public JLabel getLampsLabel(Dimension dimension) {
		updateLamps();
		lampsLabel.setSize(dimension);
		return lampsLabel;
	}

	public JLabel getPassengersLabel(Dimension dimension) {
		updatePassengers();
		passengersLabel.setSize(dimension);
		return passengersLabel;
	}

	public JLabel getDoorStateLabel(Dimension dimension) {
		updateDoorState();
		doorStateLabel.setSize(dimension);
		return doorStateLabel;
	}

	public JLabel getDirectionLabel(Dimension dimension) {
		updateDirection();
		directionLabel.setSize(dimension);
		return directionLabel;
	}

	public boolean isElevatorUnderRepair() {
		return elevatorUnderRepair;
	}

	public void setElevatorUnderRepair(boolean elevatorUnderRepair) {
		this.elevatorUnderRepair = elevatorUnderRepair;
	}

	public boolean isElevatorShutDown() {
		return elevatorShutDown;
	}

	public void setElevatorShutDown(boolean elevatorShutDown) {
		this.elevatorShutDown = elevatorShutDown;
	}
	
	
	
}
