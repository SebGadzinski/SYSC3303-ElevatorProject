package project.utils.datastructs;

public class ElevatorFaultRequest extends Request {

	ElevatorFault fault;

	public ElevatorFaultRequest(Source source, ElevatorFault fault) {
		super(source);
		this.fault = fault;
	}
	
	public enum ElevatorFault{
		DOOR_FAULT, MOTOR_FAULT;
	}

	public ElevatorFault getFault() {
		return fault;
	}

	@Override
	public String toString() {
		return "ElevatorFaultRequest: " + "\n"
		+ "Source: " + this.getSource() + "\n" 
		+ "Fault: " + fault;
	}

}
