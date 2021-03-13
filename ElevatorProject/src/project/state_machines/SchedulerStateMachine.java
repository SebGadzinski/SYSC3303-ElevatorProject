package project.state_machines;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.utils.datastructs.*;
import project.utils.datastructs.SubsystemSource;
import project.utils.datastructs.SubsystemSource.Subsystem;
import project.utils.datastructs.ElevatorPassengerWaitRequest.WaitState;

/**
 * The Scheduler state machine.
 *
 * @author Paul Roode
 * @version Iteration 2
 */
public class SchedulerStateMachine {

    public enum SchedulerState {

        /**
         * Scheduler awaits a request.
         */
        AWAIT_REQUEST {
            @Override
            public SchedulerState advance(Request request) {

                // dispatch a request read in from a file
                if (request instanceof FileRequest) {
                    FileRequest fileRequest = (FileRequest) request;
                    SubsystemSource source = fileRequest.getSource();
                    Integer originFloor = fileRequest.getOriginFloor();
                    ElevatorDirection direction = fileRequest.getDirection();
                    Integer destinationFloor = fileRequest.getDestinationFloor();

                    // validate the given request
                    if (direction == ElevatorDirection.UP && (originFloor > destinationFloor || originFloor.equals(destinationFloor))) {
                        return INVALID_REQUEST;
                    } else if (direction == ElevatorDirection.DOWN && (originFloor < destinationFloor || originFloor.equals(destinationFloor))) {
                        return INVALID_REQUEST;
                    }

                    return DISPATCH_REQUEST_TO_SUBSYSTEM;

                }

                // dispatch a MotorRequest to an Elevator to move it in the direction of its destination
                else if (request instanceof ElevatorDestinationRequest) {
                    return DISPATCH_REQUEST_TO_SUBSYSTEM;
                }

                // dispatch an ElevatorDoorRequest to an Elevator to open its doors once it's reached its destination
                else if (request instanceof ElevatorDoorRequest) {
                    //IF im opening the doors send a request to the floor

                    return DISPATCH_REQUEST_TO_SUBSYSTEM;
                }

                // respond to an ElevatorPassengerWaitRequest from an Elevator
                else if (request instanceof ElevatorPassengerWaitRequest) {

                    WaitState waitState = ((ElevatorPassengerWaitRequest) request).getState();
                    return DISPATCH_REQUEST_TO_SUBSYSTEM;
                }

                else if (request instanceof ElevatorArrivalRequest) {
                    return DISPATCH_REQUEST_TO_SUBSYSTEM;

                }

                // the request type is not recognized
                return INVALID_REQUEST;

            }
        },

        /**
         * Scheduler dispatches a given FileRequest to an ElevatorSubsystem.
         */
        DISPATCH_REQUEST_TO_SUBSYSTEM {
            @Override
            public SchedulerState advance(Request request) {
                return AWAIT_REQUEST;
            }
        },

        /**
         * Scheduler receives an invalid request.
         */
        INVALID_REQUEST {
            @Override
            public SchedulerState advance(Request request) {
                return AWAIT_REQUEST;
            }
        };

        public abstract SchedulerState advance(Request request);

    }

}