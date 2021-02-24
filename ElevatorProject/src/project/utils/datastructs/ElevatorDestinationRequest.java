package project.utils.datastructs;

public class ElevatorDestinationRequest extends Request{

	private int requestedDestinationFloor;

	public ElevatorDestinationRequest(Source source, int requestedDestinationFloor) {
		super(source);
		this.requestedDestinationFloor = requestedDestinationFloor;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Source: " + this.getSource()
		+ "Set Destination to floor: " + this.requestedDestinationFloor + "\n";
	}

	public int getRequestedDestinationFloor() {
		return requestedDestinationFloor;
	}

	public void setRequestedDestinationFloor(int requestedDestinationFloor) {
		this.requestedDestinationFloor = requestedDestinationFloor;
	}
	
}
