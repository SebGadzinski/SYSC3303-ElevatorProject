package project.utils.datastructs;

import project.Config;

/**
 * This timer is meant to be created per elevator.  
 * If this thread is not interrupted before the timeout
 * event, this means that there has been a catastrophic failure
 * and the elevator needs to permanently shut down.
 *
 * @author Chase Badalato
 * @version Iteration 4
 */
public class ElevatorTimerWorker implements Runnable {
	
	
	boolean timerRunning;
	boolean timeOut;
	int timeOutTime;
	
	public ElevatorTimerWorker(int timeout) {
		this.timeOut = false;
		this.timerRunning = false;
		this.timeOutTime = timeout;
	}
	
	public synchronized boolean getTimerRunning () {
		return this.timerRunning;
	}
	
	public synchronized boolean getTimeOut() {
		return this.timeOut;
	}
	
	@Override
	public void run() {
		this.timerRunning = true;
		try {
			//Try to sleep for the timer timeout length
			Thread.sleep(timeOutTime);
		} catch (InterruptedException e) {
			//If the Scheduler receives an arrived request in time then the
			//timer is interrupted
			this.timerRunning = false;;
		}
		
		if (this.timerRunning) {
			//send emergencyRequest containing state SHUTDOWN
			System.out.println("WE SETTING IT OFF IN THE GOOOOD");
			this.timeOut = true;
			this.timerRunning = false;
		}
	}

}
