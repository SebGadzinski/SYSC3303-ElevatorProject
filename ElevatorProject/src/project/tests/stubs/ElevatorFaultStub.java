package project.tests.stubs;

import java.util.HashMap;

import project.state_machines.ElevatorStateMachine;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.ElevatorStateMachine.ElevatorState;

public class ElevatorFaultStub {

    private final ElevatorStateMachine esm;

    public ElevatorFaultStub() {
        esm = new ElevatorStateMachine(
                ElevatorState.IDLE,
                ElevatorDoorStatus.CLOSED,
                ElevatorDirection.IDLE,
                0,
                new HashMap<>()
        );
    }

    public void MakeStateFault() {
        this.esm.doorFault();
    }

    public ElevatorState checkIfFault(int i) {
        if (i == 0) {
            this.MakeStateFault();
        }
        return this.esm.getState();
    }

}