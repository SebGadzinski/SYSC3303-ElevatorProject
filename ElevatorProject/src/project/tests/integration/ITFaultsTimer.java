package project.tests.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import project.utils.datastructs.ElevatorTimerWorker;

/**
 * Test the timeout thread for each elevator
 *
 * @author Chase Badalato
 * @version 4
 */
class ITFaultsTimer {

    ElevatorTimerWorker timer;
    Thread timerWorker;

    public ITFaultsTimer() {
        timer = new ElevatorTimerWorker(1000);
        this.timerWorker = new Thread(timer, "timer");
    }

    public synchronized void startTimer() {
        if (this.timerWorker.getState() == Thread.State.NEW) {
            this.timerWorker.start();
        } else if (this.timerWorker.getState() == Thread.State.TERMINATED) {
            this.timer = new ElevatorTimerWorker(1000);
            this.timerWorker = new Thread(this.timer, "timer");
            this.timerWorker.start();
        } else {
            this.stopTimer();
        }
    }

    public synchronized void stopTimer() {
        this.timerWorker.interrupt();
        while (this.timerWorker.getState() != Thread.State.TERMINATED) {
        }
    }

    public synchronized boolean isTimerRunning() {
        return this.timer.getTimerRunning();
    }

    public synchronized boolean getTimerRunning() {
        return this.timer.getTimerRunning();
    }

    public synchronized boolean getTimeOut() {
        return this.timer.getTimeOut();
    }

    /**
     * Start the timeout timer.  This would begin when the scheduler
     * receives a motor request.  Wait MORE than the timeout time and
     * check the see if the timeout expired variable it set to true.
     * This means that the elevator timed out and must be shutdown
     */
    @Test
    void testTimeout() {
        this.startTimer();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.stopTimer();

        assertTrue(this.getTimeOut());
    }

    /**
     * Test the see if the resetting of the clock functions.  If an interrupt occurs in the
     * timer thread before the timeout value is set then we know that a timeout didnt occur.
     * We can confirm this by checking the output of the timeOut variable
     */
    @Test
    void testResetClock() {
        this.startTimer();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.stopTimer();

        assertFalse(this.getTimeOut());
    }

}