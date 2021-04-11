package project.tests.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static project.state_machines.ElevatorStateMachine.ElevatorDirection.UP;
import static project.utils.datastructs.SubsystemSource.Subsystem.FLOOR_SUBSYSTEM;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import org.junit.jupiter.api.Test;

import project.models.Floor;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.SubsystemSource;

/**
 * Tests the real-time capability of the system.
 *
 * @author Paul Roode
 * @version Iteration 5
 */
public class TestRealTimeElevatorArrival {

    private static final int TIME_FOR_ELEVATOR_ARRIVAL = 3;
    private static final TemporalUnit TIME_FOR_ELEVATOR_ARRIVAL_UNITS = ChronoUnit.SECONDS;
    private static final TemporalUnit REALTIME_PRECISION = ChronoUnit.SECONDS;

    /**
     * Tests Floor::realtimeWait() and its auxiliaries, thereby verifying the real-time capability of the system.
     */
    @Test
    public void testRealtimeWait() {

        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss.SSS");

        Instant arrivalTime = Instant.now().plus(TIME_FOR_ELEVATOR_ARRIVAL, TIME_FOR_ELEVATOR_ARRIVAL_UNITS);
        String arrivalTimeStr = timeFormatter.format(Date.from(arrivalTime));

        FileRequest fileRequest = new FileRequest(arrivalTimeStr, 2, UP, 5, new SubsystemSource(FLOOR_SUBSYSTEM, "0"));

        new Floor().realtimeWait(fileRequest);

        assertEquals(Instant.now().truncatedTo(REALTIME_PRECISION), arrivalTime.truncatedTo(REALTIME_PRECISION));

    }

}