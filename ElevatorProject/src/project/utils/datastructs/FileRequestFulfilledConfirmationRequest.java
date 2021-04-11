package project.utils.datastructs;

public class FileRequestFulfilledConfirmationRequest extends Request {

	boolean isCompleted;
	
	public FileRequestFulfilledConfirmationRequest(SubsystemSource source, int fault, boolean isCompleted) {
		super(source, fault);
		this.isCompleted = isCompleted;
	}

}
