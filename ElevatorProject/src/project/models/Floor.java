package project.models;

import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.objects.floor_objects.FloorButton;
import project.utils.objects.floor_objects.FloorLamp;
import project.utils.objects.general.DirectionLamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static project.Config.REQUEST_QUEUE_CAPACITY;

/*
 * Info:
 * 	UML diagram of Elevator and Floor explains what variables are inside, along bold text above it.
 * 	This is a client to the Scheduler.
 * 	Floor subsystem is to read in events using the format shown above: time, floor or elevator number, and button.
 * Sending:
 * 	Each line of input is sent to the Scheduler.
 * Receiving:
 * 	Should be able to read commands from FloorSubsystem class.
 * 	Information from the Scheduler.
 *
 */

/**
 * @author Chase Fridgen (iter 2 and 1), Chase Badalato (iter 2)
 * @version Iteration 2
 */
public class Floor implements Runnable {

    public FloorButton upButton;
    public FloorButton downButton;

    public FloorLamp upLamp;
    public FloorLamp downLamp;

    public DirectionLamp upDirectionLamp;
    public DirectionLamp downDirectionLamp;

    private final BlockingQueue<Request> floorQueue; // input to the floor
    private final BlockingQueue<Request> serverQueue; // output to the server

    public Floor() {
        floorQueue = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        serverQueue = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
    }

    /**
     * Each thread waits for a given amount of time in real time before it sends a
     * request off the the scheduler
     *
     * @param request the received request from the floor subsystem
     */
    public void realtimeWait(Request request) {

        if (request instanceof FileRequest) {

            FileRequest fileRequest = (FileRequest) request;

            String milTime = fileRequest.getTime();
            String[] arrMilTime = milTime.split(":");
            String[] arrSec = arrMilTime[2].split("[.]");

            Date dt = new Date();
            SimpleDateFormat dateFormat;
            dateFormat = new SimpleDateFormat("kk:mm:ss");
            String currDate = dateFormat.format(dt);
            String[] currTime = currDate.split(":");

            int arrTime = toMilliSeconds(arrMilTime[0], arrMilTime[1], arrSec[0]);
            int currentTime = toMilliSeconds(currTime[0], currTime[1], currTime[2]);
            int timeToWait = arrTime - currentTime;
            if (timeToWait < 0) {
                timeToWait += 8.64 * Math.pow(10, 7); // wait 24 more hours for the next time
            }

            try {
                Thread.sleep(timeToWait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public int toMilliSeconds(String hour, String min, String sec) {

        int intHour = Integer.parseInt(hour);
        int intMin = Integer.parseInt(min);
        int intSec = Integer.parseInt(sec);

        int milliH = intHour * 3600000;
        int milliM = intMin * 60000;
        int milliS = intSec * 1000;

        return milliH + milliM + milliS;
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
                System.out.println("\nFloor " + fileRequest.getOriginFloor() + " received a packet from "
                        + fileRequest.getSource());

                return fileRequest;
            }
            return packet;
        } catch (InterruptedException e) {
            System.out.println("Interrupted when waiting for packet from FloorSubsystem");
            e.printStackTrace();
        }
        return null;
    }

    public void sendServer(Request packet) {
        try {
            if (packet instanceof FileRequest) {
                FileRequest fileRequest = (FileRequest) packet;
                System.out.println("\nFloor " + fileRequest.getOriginFloor() + " sending packet to scheduler at time "
                        + fileRequest.getTime());
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
            int randVal = (int) (10 * Math.random() + 1);
            try {
                Thread.sleep(randVal * 1000L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
//			try {
//				this.realTimeWait(packet);
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
            this.sendServer(packet);
        }
    }

}
