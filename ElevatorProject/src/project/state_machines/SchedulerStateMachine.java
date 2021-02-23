package project.state_machines;

import project.state_machines.ElevatorState.ElevatorDirection;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.Request.Source;

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

                // the request type is not recognized
                return INVALID_REQUEST;

            }
        },

        /**
         * Scheduler dispatches a given FileRequest to the ElevatorSubsystem.
         */
        DISPATCH_FILE_REQUEST_TO_ELEVATOR {
            @Override
            public SchedulerState advance(Request request) {
                return AWAIT_REQUEST;
            }
        },

        /**
         * Scheduler dispatches a given FileRequest to the FloorSubsystem.
         */
        DISPATCH_FILE_REQUEST_TO_FLOOR {
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