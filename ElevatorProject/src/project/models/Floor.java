package project.models;

import project.utils.objects.floor_objects.*;
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
 * @author Chase Fridgen
 */

public class Floor implements Runnable {

<<<<<<< Updated upstream
    public FloorButton upButton;
    public FloorButton downButton;

    public FloorLamp upLamp;
    public FloorLamp downLamp;

    public DirectionLamp upDirectionLamp;
    public DirectionLamp downDirectionLamp;

    public Scheduler scheduler;

    /*
     * Constructors
     */

    public Floor(Scheduler scheduler, DirectionLamp upDirectionLamp, DirectionLamp downDirectionLamp) {
        setUpButtons();
        setUpLamps();
        setUpArrivalSensors();
        this.scheduler = scheduler;
        this.upDirectionLamp = upDirectionLamp;
        this.downDirectionLamp = downDirectionLamp;
    }

    /*
     * Functions
     */

    private void setUpButtons() {
        this.upButton = new FloorButton();
        this.downButton = new FloorButton();
    }

    private void setUpLamps() {
        this.upLamp = new FloorLamp();
        this.downLamp = new FloorLamp();
    }

    private void setUpArrivalSensors() {
        // code
    }

    private void requestElevator() {
        // code
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }
=======
	public FloorButton upButton;
	public FloorButton downButton;

	public FloorLamp upLamp;
	public FloorLamp downLamp;

	public DirectionLamp upDirectionLamp;
	public DirectionLamp downDirectionLamp;

	private BlockingQueue<ConcurrentMap<Request.Key, Object>> floorQueue; // input to the floor
	private BlockingQueue<ConcurrentMap<Request.Key, Object>> serverQueue; // output to the server

	public Floor(BlockingQueue<ConcurrentMap<Request.Key, Object>> serverQueue) {
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
	public void realTimeWait(ConcurrentMap<Request.Key, Object> packet) throws ParseException {
		String milTime = (String) packet.get(Request.Key.TIME);
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

		
		try {
			int timeToWait = arrTime - currentTime;
<<<<<<< Updated upstream
			Thread.sleep(timeToWait);
			if(timeToWait < 0) {
				throw new IllegalArgumentException("");
=======
						
			if(timeToWait < 0) {
				timeToWait += 8.64*Math.pow(10, 7); //wait 24 more hours for the next time
			}

			System.out.println(timeToWait);

			try {
				Thread.sleep(timeToWait);
			} catch (InterruptedException e) {
				e.printStackTrace();
>>>>>>> Stashed changes
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(-1);
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
	public void putRequest(ConcurrentMap<Request.Key, Object> item) {
		try {

			this.floorQueue.put(item);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
<<<<<<< Updated upstream
	}

	/**
	 * check in the queue to see if there is a packet in it
	 * 
	 * @return the received packet
	 */
	public ConcurrentMap<Key, Object> getRequest() {
		try {
			ConcurrentMap<Request.Key, Object> packet = this.floorQueue.take();
			System.out.println("Floor " + packet.get(Request.Key.ORIGIN_FLOOR) + " received a request");
			return packet;

=======
    }
    
    public Request getRequest() {
    	try {
    		Request packet = this.floorQueue.take();
    		
    		if (packet instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) packet;
                System.out.println("Floor " + fileRequest.getOrginFloor() + " received request from FloorSubsystem");
       			return fileRequest;
    		}
    		return packet;
>>>>>>> Stashed changes
		} catch (InterruptedException e) {
			System.out.println("Could not receive packet from FloorSubsystem");
			e.printStackTrace();
		}
		return null;
<<<<<<< Updated upstream
	}
=======
    }
    
    public void sendServer(Request packet) {
    	try {
    		if (packet instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) packet; 
                System.out.println("\nFloor " + fileRequest.getOrginFloor() + " sending packet to scheduler at time " + fileRequest.getTime());
    			this.serverQueue.put(fileRequest);
    		}
>>>>>>> Stashed changes

	/**
	 * send the packet to the scheduler
	 * 
	 * @param packet the packet to send
	 */
	public void sendServer(ConcurrentMap<Request.Key, Object> packet) {
		try {
			Thread.sleep((int) (Math.random() * (5000 - 500 + 1) + 500));
			System.out.println("\nFloor " + packet.get(Request.Key.ORIGIN_FLOOR) + " sending packet to scheduler");
			this.serverQueue.put(packet);
		} catch (InterruptedException e) {
			System.out.println("Could not send packet to server");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			ConcurrentMap<Key, Object> packet = this.getRequest();
			
//			try {
//				this.realTimeWait(packet);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
			this.sendServer(packet);
		}
	}
>>>>>>> Stashed changes

}
