package project.models;

import static project.Config.REQUEST_QUEUE_CAPACITY;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.objects.floor_objects.FloorButton;
import project.utils.objects.floor_objects.FloorLamp;
import project.utils.objects.general.DirectionLamp;

/*
 * Info:
 * 	UML diagram of Elevator and Floor explains what variables are inside, along bold text above it
 * 	This is a client to the Scheduler
 * 	Floor subsystem is to read in events using the format shown above: Time, floor or elevator number, and button.
 * Sending:
 * 	Each line of input is sent to the scheduler
 * Receiving:
 * 	Should be able to read commands from floorSubsystem class
 * 	information from the scheduler
 *
 */

/**
 * @author Chase Fridgen (Iteration One)
 * @author Chase Badalato (Iteration Two)
 */
public class Floor implements Runnable {

    public FloorButton upButton;
    public FloorButton downButton;

    public FloorLamp upLamp;
    public FloorLamp downLamp;

    public DirectionLamp upDirectionLamp;
    public DirectionLamp downDirectionLamp;
    
    private BlockingQueue<Request> floorQueue; //input to the floor
    private BlockingQueue<Request> serverQueue; //output to the server

    public Floor(BlockingQueue<Request> serverQueue) {
    	this.serverQueue = serverQueue;
    	this.floorQueue = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
    }

    	/**
	 * Each thread waits for a given amount of time in real time before it sends a
	 * packet off the the scheduler
	 * 
	 * @param packet the received packet from the floor subsystem
	 * @throws ParseException
	 */
	public void realTimeWait(Request packet) throws ParseException {

		if (packet instanceof FileRequest) {
			FileRequest fileRequest = (FileRequest) packet; 

			String milTime = fileRequest.getTime();
			String[] arrMilTime = milTime.split(":");
			// System.out.println("when elevator should arrive : " + arrMilTime[0] +
			// arrMilTime[1]);

			Date dt = new Date();
			SimpleDateFormat dateFormat;
			dateFormat = new SimpleDateFormat("kk:mm");
			String currDate = dateFormat.format(dt);
			String[] currTime = currDate.split(":");
			// System.out.println("current time : " + currTime[0] + currTime[1] + "\n");

			int arrTime = toMilliSeconds(arrMilTime[0], arrMilTime[1]);
			int currentTime = toMilliSeconds(currTime[0], currTime[1]);

			int timeToWait = arrTime - currentTime;
			
			if(timeToWait < 0) {
				timeToWait += 8.64*Math.pow(10, 7); //wait 24 more hours for the next time
			}

			System.out.println(timeToWait);

			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int toMilliSeconds(String hour, String min) {
		int intHour = Integer.parseInt(hour);
		int intMin = Integer.parseInt(min);

		int milliH = intHour * 3600000;
		int milliM = intMin * 60000;

		int total = milliH + milliM;
		return total;
	}

	/**
	 * This is a method for the FloorSubsystem to access. I doesn't need to be
	 * synchronized because there is only 1 floor subsystem working on this floor
	 * AND the floor queue that it is using is thread safe (it has its own wait()
	 * etc. already implemented
	 * 
	 * @param item the packet to place in the Floor's queue
	 */
    
    public void putRequest(Request item) {
    	try {
			this.floorQueue.put(item);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public Request getRequest() {
    	try {
    		Request packet = this.floorQueue.take();
    		
    		if (packet instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) packet;
                System.out.println("\nFloor " + fileRequest.getOrginFloor() + " received a packet");
       			return fileRequest;
    		}
    		return packet;
		} catch (InterruptedException e) {
			System.out.println("Could not receive packet from FloorSubsystem");
			e.printStackTrace();
		}
		return null;
    }
    
    public void sendServer(Request packet) {
    	try {
    		if (packet instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) packet; 
                System.out.println("\nFloor " + fileRequest.getOrginFloor() + " sending packet to scheduler at time " + fileRequest.getTime());
    			this.serverQueue.put(fileRequest);
    		}

		} catch (InterruptedException e) {
			System.out.println("Could not send packet to server");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			Request packet = this.getRequest();
			try {
				this.realTimeWait(packet);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.sendServer(packet);
		}
	}

}
