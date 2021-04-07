package project.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.utils.datastructs.SchedulerElevatorInfo;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ElevatorProjectGUI implements Runnable{

	private JFrame frame;
	private GridLayout grid;
	private ElevatorGUI elevatorGUIs[] = new ElevatorGUI[Config.NUMBER_OF_ELEVATORS];
	private GridLayout backgroundGridBagLayout = new GridLayout(0,2, 5, 0);
	private GridLayout elevatorGridLayout = new GridLayout(0,Config.NUMBER_OF_ELEVATORS,10, 0);
	private GridLayout floorGridLayout = new GridLayout(Config.NUMBER_OF_FLOORS,0);
	private Container [] elevatorContainers = new Container[Config.NUMBER_OF_ELEVATORS];
	private JPanel [] floorPanels = new JPanel[Config.NUMBER_OF_FLOORS];
	private ArrayList<JPanel[]> elevatorFloorPanels = new ArrayList<JPanel[]>();
	private ArrayList<LinkedHashMap<Integer, Integer>> destinationRequests = new ArrayList<LinkedHashMap<Integer, Integer>>();
	private Color tileColor1 = new Color(240, 240, 240);
	private Color tileColor2 = Color.LIGHT_GRAY;
	private int[] elevatorIdFloors = new int[Config.NUMBER_OF_ELEVATORS];
	private int[] elevatorCurrentFloors = new int[Config.NUMBER_OF_ELEVATORS];
	private boolean running = true;
	private Dimension elevatorLabelDimension;


	/**
	 * Create the application.
	 */
	public ElevatorProjectGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container container = frame.getContentPane();

		for(int i = 0; i < Config.NUMBER_OF_ELEVATORS; i++) {
			elevatorFloorPanels.add(new JPanel[Config.NUMBER_OF_FLOORS]);
		}
		
		container.setLayout(backgroundGridBagLayout);
		
		Container elevatorsFrame = new Container();
		elevatorsFrame.setLayout(elevatorGridLayout);
		for(int i = 0; i < elevatorContainers.length; i++) {
			elevatorContainers[i] = new Container();
			elevatorContainers[i].setLayout(new GridLayout(Config.NUMBER_OF_FLOORS,0));
			for(int y = 0; y < elevatorFloorPanels.get(i).length; y++) {
				elevatorFloorPanels.get(i)[y] = new JPanel();
				elevatorFloorPanels.get(i)[y].setBackground((y % 2 == 0 ? tileColor1 : tileColor2));
				elevatorContainers[i].add(elevatorFloorPanels.get(i)[y]);
			}
			elevatorsFrame.add(elevatorContainers[i]);
			elevatorCurrentFloors[i] = 0;
			elevatorIdFloors[i] = ElevatorGUI.AMOUNT_OF_ELEMENTS;
		}
		updateElevatorLabelDimension();
		
		//Elevator Panels are in reverse index form, put them in proper form
		for(int i = 0; i < elevatorFloorPanels.size(); i++) {
			Collections.reverse(Arrays.asList(elevatorFloorPanels.get(i)));
		}
		
		for(int i = 0; i < Config.NUMBER_OF_ELEVATORS; i++) {
			elevatorGUIs[i] = new ElevatorGUI(Integer.toString(i), new HashMap<Integer, Boolean>(), 0, ElevatorDoorStatus.CLOSED, ElevatorDirection.IDLE, -1);
			moveElevatorToFloor(i, 0);
		}
		
		container.add(elevatorsFrame);
		
		Container floorsFrame = new Container();
		floorsFrame.setLayout(floorGridLayout);
		for(int i = 0; i < floorPanels.length; i++) {
			floorPanels[i] = new JPanel();
			floorPanels[i].setBackground((i % 2 == 0 ? tileColor1 : tileColor2));
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(FlowLayout.LEFT);
			floorPanels[i].setLayout(flowLayout);
			JLabel floorNumberLabel = new JLabel("Floor " + floorIndex(i) + ": " + (floorIndex(i) < 10 ? " " : ""));
			floorNumberLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
			floorPanels[i].add(floorNumberLabel);
			floorsFrame.add(floorPanels[i]);
			destinationRequests.add(new LinkedHashMap<Integer, Integer>());
		}
		
		Collections.reverse(Arrays.asList(floorPanels));
		
		container.add(floorsFrame);
	}
	
	private synchronized void moveElevatorToFloor(int elevatorId, int floor) {
		updateElevatorLabelDimension();
		clearAllFloorsFromElevator(elevatorId);
		elevatorCurrentFloors[elevatorId] = floor;
		elevatorIdFloors[elevatorId] = (floor <= ElevatorGUI.AMOUNT_OF_ELEMENTS - 1 ? floor + ElevatorGUI.AMOUNT_OF_ELEMENTS : floor - 1);
		
		if(elevatorGUIs[elevatorId].isElevatorUnderRepair()) {
			for(int i = 0; i < ElevatorGUI.AMOUNT_OF_ELEMENTS; i++) {
				elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId] - i].setBackground(Color.yellow);
			}			
		}
		else if(elevatorGUIs[elevatorId].isElevatorShutDown()) {
			for(int i = 0; i < ElevatorGUI.AMOUNT_OF_ELEMENTS; i++) {
				elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId] - i].setBackground(Color.red);
			}			
		}
		else {
			//Set Up Id Label
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]].add(elevatorGUIs[elevatorId].getIdLabel(elevatorLabelDimension));
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]].setBackground(Color.white);
			//Set Up Destination Label
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-1].add(elevatorGUIs[elevatorId].getDestinationLabel(elevatorLabelDimension));
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-1].setBackground(Color.white);
			//Set Up Lamps Label
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-2].add(elevatorGUIs[elevatorId].getLampsLabel(elevatorLabelDimension));
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-2].setBackground(Color.white);
			//Set UP Passengers Label
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-3].add(elevatorGUIs[elevatorId].getPassengersLabel(elevatorLabelDimension));
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-3].setBackground(Color.white);
			//Set Up door state
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-4].add(elevatorGUIs[elevatorId].getDoorStateLabel(elevatorLabelDimension));
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-4].setBackground(Color.white);
			//Set Up direction
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-5].add(elevatorGUIs[elevatorId].getDirectionLabel(elevatorLabelDimension));
			elevatorFloorPanels.get(elevatorId)[elevatorIdFloors[elevatorId]-5].setBackground(Color.white);
			
			elevatorFloorPanels.get(elevatorId)[floor].setBackground(Color.black);
			JLabel currentFloor = new JLabel("Floor: " + floor);
			currentFloor.setSize(elevatorLabelDimension);
			currentFloor.setForeground(Color.white);;
			elevatorFloorPanels.get(elevatorId)[floor].add(currentFloor);
		}
		
	}
	
	//Update Elevator Function (ALL Params)
	public synchronized void updateElevator(SchedulerElevatorInfo elevator) {
		updateElevatorLabelDimension();
		int id = Integer.parseInt(elevator.getId());
		
		if(!elevator.isRepairing() && elevatorGUIs[id].isElevatorUnderRepair()) {
			elevatorGUIs[id].setElevatorUnderRepair(false);
		}
		
		if(elevator.isRepairing()) {
			elevatorGUIs[id].setElevatorUnderRepair(true);
			elevatorUnderRepair(id);		
		}
		else if(elevator.isShutDown()) {
			elevatorGUIs[id].setElevatorShutDown(true);
			elevatorShutDown(id);		
		}
		else {
			elevatorGUIs[id].setDestination(elevator.getCurrentDestinationFloor());
			elevatorGUIs[id].setLamps(elevator.getLamps());
			elevatorGUIs[id].setPassengers(elevator.getPassengers());
			elevatorGUIs[id].setDoorState(elevator.getDoorStatus());
			elevatorGUIs[id].setDirection(elevator.getDirection());
			moveElevatorToFloor(id, elevator.getCurrentFloor());
		}
	}
	
	//Update Destination
	public synchronized void updateElevatorLamps(int id, int destination) {
		updateElevatorLabelDimension();
		elevatorGUIs[id].setDestination(destination);;
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-1].removeAll();
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-1].add(elevatorGUIs[id].getLampsLabel(elevatorLabelDimension));
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-1].setBackground(Color.white);
	}
	
	//Update Lamp
	public synchronized void updateElevatorLamps(int id, HashMap<Integer, Boolean> lamps) {
		updateElevatorLabelDimension();
		elevatorGUIs[id].setLamps(lamps);
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-2].removeAll();
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-2].add(elevatorGUIs[id].getLampsLabel(elevatorLabelDimension));
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-2].setBackground(Color.white);
	}
	
	//Update Direction
	public synchronized void updateElevatorPassengers(int id, int passengers) {
		updateElevatorLabelDimension();
		elevatorGUIs[id].setPassengers(passengers);
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-3].removeAll();
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-3].add(elevatorGUIs[id].getPassengersLabel(elevatorLabelDimension));
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-3].setBackground(Color.white);
	}
	
	//Update Passengers
	public synchronized void updateElevatorDoors(int id, ElevatorDoorStatus doorState) {
		updateElevatorLabelDimension();
		elevatorGUIs[id].setDoorState(doorState);
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-4].removeAll();
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-4].add(elevatorGUIs[id].getDoorStateLabel(elevatorLabelDimension));
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-4].setBackground(Color.white);
	}
	
	//Update Door State
	public synchronized void updateElevatorDirection(int id, ElevatorDirection direction) {
		updateElevatorLabelDimension();
		elevatorGUIs[id].setDirection(direction);
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-5].removeAll();
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-5].add(elevatorGUIs[id].getDirectionLabel(elevatorLabelDimension));
		elevatorFloorPanels.get(id)[elevatorIdFloors[id]-5].setBackground(Color.white);
	}
	
	public synchronized void elevatorUnderRepair(int id) {
		clearAllFloorsFromElevator(id);
		elevatorFloorPanels.get(id)[elevatorCurrentFloors[id]].setBackground(Color.yellow);
		for (int i = 0 ; i < ElevatorGUI.AMOUNT_OF_ELEMENTS; i++) {
			elevatorFloorPanels.get(id)[elevatorIdFloors[id] - i].setBackground(Color.yellow);
		}
	}
	
	public synchronized void elevatorFinishedRepair(int id) {
		clearAllFloorsFromElevator(id);
		moveElevatorToFloor(id, elevatorCurrentFloors[id]);
	}
	
	public synchronized void elevatorShutDown(int id) {
		clearAllFloorsFromElevator(id);
		elevatorFloorPanels.get(id)[elevatorCurrentFloors[id]].setBackground(Color.red);
		for (int i = 0 ; i < ElevatorGUI.AMOUNT_OF_ELEMENTS; i++) {
			elevatorFloorPanels.get(id)[elevatorIdFloors[id] - i].setBackground(Color.red);
		}
	}
	
	public synchronized void addRequestToFloor(int requestId, int orginFloor, int destinationFloor) {
		destinationRequests.get(orginFloor).put(requestId, destinationFloor);
		updateFloor(orginFloor);
	}
	
	public synchronized void updateFloor(int orginFloor) {
		floorPanels[orginFloor].removeAll();
		
		JLabel floorNumberLabel = new JLabel("Floor " + orginFloor + ": " + (orginFloor < 10 ? " " : ""));
		floorNumberLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
		floorPanels[orginFloor].add(floorNumberLabel);
		
		Iterator it = destinationRequests.get(orginFloor).entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        floorPanels[orginFloor].add(new JLabel("<Dest:" + pair.getValue() + "> "));
	    }
	    
		floorPanels[orginFloor].updateUI();
	}

	//Take away from floor
	public synchronized void removeRequestFromFloor(int requestId, int orginFloor) {
		destinationRequests.get(orginFloor).remove(requestId);
		updateFloor(orginFloor);
	}
	
	private void clearAllFloorsFromElevator(int elevatorId) {
		for(int i = 0; i < Config.NUMBER_OF_FLOORS; i++) {
			elevatorFloorPanels.get(elevatorId)[i].removeAll();
			elevatorFloorPanels.get(elevatorId)[i].setBackground((i % 2 != 0 ? tileColor1: tileColor2));
		}
	}
	
	private void clearAllFloors() {
		for(int i = 0; i < Config.NUMBER_OF_FLOORS; i++) {
			floorPanels[i].removeAll();
			floorPanels[i].setBackground((i % 2 == 0 ? tileColor1: tileColor2));
		}
	}
	
	//Floors are in reverse order
	private int floorIndex(int floorIndex) {
		return Config.NUMBER_OF_FLOORS - 1 - floorIndex;
	}
	
	private void updateElevatorLabelDimension() {
		elevatorLabelDimension = elevatorFloorPanels.get(0)[0].getSize();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		frame.setVisible(true);
		while(running) {
			
		}
	}

}










