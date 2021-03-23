package project.utils.datastructs;

import project.Config;

public class ElevatorTimerWorker implements Runnable {
	
	
	boolean timerRunning;
	boolean timeOut;
	
	public ElevatorTimerWorker() {
		this.timeOut = false;
		this.timerRunning = false;
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
			Thread.sleep(Config.ELEVATOR_TIMEOUT);
		} catch (InterruptedException e) {
			this.timerRunning = false;;
		}
		
		if (this.timerRunning) {
			System.out.println("TIMER EXPIRED!!!");
			this.timeOut = true;
			this.timerRunning = false;
		}
	}

}
