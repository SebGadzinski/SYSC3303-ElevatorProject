package project.tests.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import project.models.Floor;

/**
 * Tests Floor.
 *
 * @author Chase Badalato
 * @author Paul Roode
 * @version Iteration 4
 */
public class TestFloor extends Floor {

    private static final int TIME_FOR_ELEVATOR_ARRIVAL = 2; // seconds

    /**
     * Simulates and tests Floor::realTimeWait() and its inherited auxiliaries.
     */
    @Test
    public void testRealTimeWait() {
        TestFloor floor = new TestFloor();
        Instant arrivalTime = Instant.now().plus(TIME_FOR_ELEVATOR_ARRIVAL, ChronoUnit.SECONDS);
        floor.realTimeWait(arrivalTime);
        assertEquals(Instant.now().truncatedTo(ChronoUnit.SECONDS), arrivalTime.truncatedTo(ChronoUnit.SECONDS));
    }

    /**
     * Simulates Floor::realTimeWait() and invokes its inherited auxiliaries for testing purposes.
     *
     * @param arrivalTime The time at which the elevator shall arrive.
     */
    private void realTimeWait(Instant arrivalTime) {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
        String arrivalTimeStr = formatter.format(Date.from(arrivalTime));

        String[] arrMilTime = arrivalTimeStr.split(":");
        String[] arrSec = arrMilTime[2].split("[.]");

        Date dt = new Date();
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("kk:mm:ss");
        String currDate = dateFormat.format(dt);
        String[] currTime = currDate.split(":");

        int arrTime = toMilliSeconds(arrMilTime[0], arrMilTime[1], arrSec[0]);
        int currentTime = toMilliSeconds(currTime[0], currTime[1], currTime[2]);
        int timeToWait = arrTime - currentTime;

        try {
            TimeUnit.MILLISECONDS.sleep(Duration.ofMillis(timeToWait).toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}