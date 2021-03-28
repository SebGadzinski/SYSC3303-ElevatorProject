package project.tests.unit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.utils.datastructs.ElevatorTimerWorker;
import project.utils.datastructs.SchedulerElevatorInfo;
import project.utils.datastructs.UDPInfo;

/**
 * Test the timeout thread for each elevator
 * @author Chase Badalato
 * @version 4
 *
 */
class TimerFaultTest_Two {
	
	ElevatorTimerWorker timer;
	Thread timerWorker;
	
	public TimerFaultTest_Two() {
		timer = new ElevatorTimerWorker(1000);
		this.timerWorker = new Thread(timer, "timer");
	}

    public synchronized void startTimer() {
    	if(this.timerWorker.getState() == Thread.State.NEW) {
        	this.timerWorker.start();
    	}
    	else if (this.timerWorker.getState() == Thread.State.TERMINATED) {
    		this.timer = new ElevatorTimerWorker(1000);
    		this.timerWorker = new Thread(this.timer, "timer");
    		this.timerWorker.start();
    	}
    	else {
    		this.stopTimer();
    	}
    }
    
    public synchronized void stopTimer() {
    	this.timerWorker.interrupt();
    	while(this.timerWorker.getState() != Thread.State.TERMINATED) {};
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
     * Start the timeout timer.  This would begin when the schedulder
     * receives a motor request.  Wait MORE than the timeout time and
     * check the see if the timeout expired variable it set to true.
     * This means that the elevator timed out and must be shutdown
     */
	@Test
	void timeoutTest() {
		this.startTimer();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
	void resetClockTest() {
		this.startTimer();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.stopTimer();

		assertFalse(this.getTimeOut());
	}

}
