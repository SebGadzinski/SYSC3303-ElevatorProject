package project.utils.objects.elevator_objects;

import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;

public class ElevatorDoor {

    public ElevatorDoorStatus state;

    public ElevatorDoor(ElevatorDoorStatus state) {
        this.state = state;
    }

	public ElevatorDoorStatus getDoorState() {
		return state;
	}

	public void setDoorState(ElevatorDoorStatus doorState) {
		this.state = doorState;
	}
}
