package project.utils.data_structures;

import java.util.ArrayList;

/**
 * Contains all of the important
 * elevator request data after
 * parsing the input file
 * 
 * @author Chase
 *
 */
public class InfoPacket {

	enum Direction {
		UP, 
		DOWN
	}

	private String time;
	private int reqFloor;
	private int currFloor;
	private Direction direction;
	
	public InfoPacket(String time, int currFloor, int reqFloor, Direction direction) {
		this.time = time;
		this.reqFloor = reqFloor;
		this.currFloor = currFloor;
		this.direction = direction;
	}
}
