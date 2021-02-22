package project.models;

import static project.Config.REQUEST_QUEUE_CAPACITY;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import project.utils.datastructs.Request;
import project.utils.datastructs.Request.Key;
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
    
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> floorQueue; //input to the floor
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> serverQueue; //output to the server

    public Floor(BlockingQueue<ConcurrentMap<Request.Key, Object>> serverQueue) {
    	this.serverQueue = serverQueue;
    	this.floorQueue = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
    }

    /**
     * Each thread waits for a given amount of time in real time before
     * it sends a packet off the the scheduler
     * 
     * @param packet the received packet from the floor subsystem
     */
    public void realtimeWait(ConcurrentMap<Request.Key, Object> packet) {
    	
    }
    
    /**
     * This is a method for the FloorSubsystem to access.  I doesn't need to be synchronized because
     * there is only 1 floor subsystem working on this floor AND the floor queue that it is using is 
     * thread safe (it has its own wait() etc. already implemented
     * 
     * @param item the packet to place in the Floor's queue
     */
    public void putRequest(ConcurrentMap<Request.Key, Object> item) {
    	try {
    		
			this.floorQueue.put(item);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
			
		} catch (InterruptedException e) {
			System.out.println("Could not receive packet from FloorSubsystem");
			e.printStackTrace();
		}
    	return null;
    }
    
    /**
     * send the packet to the scheduler
     * 
     * @param packet the packet to send
     */
    public void sendServer(ConcurrentMap<Request.Key, Object> packet) {
    	try {
    		Thread.sleep((int)(Math.random() * (5000 - 500 + 1) + 500));
    		System.out.println("\nFloor " + packet.get(Request.Key.ORIGIN_FLOOR) + " sending packet to scheduler");
			this.serverQueue.put(packet);
		} catch (InterruptedException e) {
			System.out.println("Could not send packet to server");
			e.printStackTrace();
		}
    }
    

    @Override
    public void run() {
    	while(true) {
    		ConcurrentMap<Key, Object> packet = this.getRequest();
    		//this.realTimeWait(packet);
    		this.sendServer(packet);
    	}
    }

}
