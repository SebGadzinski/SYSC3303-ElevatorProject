package project.state_machines;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.utils.datastructs.*;
import project.utils.datastructs.Request.Source;
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
                    Source source = fileRequest.getSource();
                    Integer originFloor = fileRequest.getOriginFloor();
                    ElevatorDirection direction = fileRequest.getDirection();
                    Integer destinationFloor = fileRequest.getDestinationFloor();

                    // validate the given request
                    if (direction == ElevatorDirection.UP && (originFloor > destinationFloor || originFloor.equals(destinationFloor))) {
                        return INVALID_REQUEST;
                    } else if (direction == ElevatorDirection.DOWN && (originFloor < destinationFloor || originFloor.equals(destinationFloor))) {
                        return INVALID_REQUEST;
                    }

                    // dispatch the validated request
                    if (source == Source.FLOOR_SUBSYSTEM) {
                        return DISPATCH_FILE_REQUEST_TO_ELEVATOR;
                    } else if (source == Source.ELEVATOR_SUBSYSTEM) {
                        return DISPATCH_FILE_REQUEST_TO_FLOOR;
                    }

                }

                // dispatch a MotorRequest to an ElevatorSubsystem
                else if (request instanceof ElevatorDestinationRequest) {
                    return DISPATCH_MOTOR_REQUEST_TO_ELEVATOR;
                }

                // dispatch an ElevatorDoorRequest to an ElevatorSubsystem
                else if (request instanceof ElevatorDoorRequest) {
                    return DISPATCH_ELEVATOR_DOOR_REQUEST_TO_ELEVATOR;
                }

                // respond to an ElevatorPassengerWaitRequest from an ElevatorSubsystem
                else if (request instanceof ElevatorPassengerWaitRequest) {
                    WaitState waitState = ((ElevatorPassengerWaitRequest) request).getState();
                    if (waitState == WaitState.WAITING) {
                        return DISPATCH_ELEVATOR_PASSENGER_WAIT_REQUEST_TO_ELEVATOR;
                    } else if (waitState == WaitState.FINISHED) {
                        return DISPATCH_ELEVATOR_DOOR_REQUEST_TO_ELEVATOR;
                    }
                }

                // consume an ElevatorArrivalRequest
                else if (request instanceof ElevatorArrivalRequest) {
                    ElevatorArrivalRequest elevatorArrivalRequest = (ElevatorArrivalRequest) request;
                    int floorArrivedAt = elevatorArrivalRequest.getFloorArrivedAt();
                    int destinationFloor = elevatorArrivalRequest.getDestinationFloor();
                    return floorArrivedAt == destinationFloor ? DISPATCH_MOTOR_REQUEST_TO_ELEVATOR : CONSUME_ELEVATOR_ARRIVAL_REQUEST;
                }

                // the request type is not recognized
                return INVALID_REQUEST;

            }
        },

        /**
         * Scheduler dispatches a given FileRequest to an ElevatorSubsystem.
         */
        DISPATCH_FILE_REQUEST_TO_ELEVATOR {
            @Override
            public SchedulerState advance(Request request) {
                return AWAIT_REQUEST;
            }
        },

        /**
         * Scheduler dispatches a given FileRequest to a FloorSubsystem.
         */
        DISPATCH_FILE_REQUEST_TO_FLOOR {
            @Override
            public SchedulerState advance(Request request) {
                return AWAIT_REQUEST;
            }
        },

        /**
         * Scheduler dispatches a new MotorRequest to an ElevatorSubsystem.
         */
        DISPATCH_MOTOR_REQUEST_TO_ELEVATOR {
            @Override
            public SchedulerState advance(Request request) {
                return AWAIT_REQUEST;
            }
        },

        /**
         * Scheduler dispatches an ElevatorDoorRequest to an ElevatorSubsystem.
         */
        DISPATCH_ELEVATOR_DOOR_REQUEST_TO_ELEVATOR {
            @Override
            public SchedulerState advance(Request request) {
                return AWAIT_REQUEST;
            }
        },

        /**
         * Scheduler dispatches an ElevatorPassengerWaitRequest to an ElevatorSubsystem.
         */
        DISPATCH_ELEVATOR_PASSENGER_WAIT_REQUEST_TO_ELEVATOR {
            @Override
            public SchedulerState advance(Request request) {
                return AWAIT_REQUEST;
            }
        },

        /**
         * Scheduler consumes an ElevatorArrivalRequest.
         */
        CONSUME_ELEVATOR_ARRIVAL_REQUEST {
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