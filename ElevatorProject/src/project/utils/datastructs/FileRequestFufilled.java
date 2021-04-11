package project.utils.datastructs;

public class FileRequestFufilled extends Request {

	boolean isCompleted;
	
	public FileRequestFufilled(SubsystemSource source, int fault, boolean isCompleted) {
		super(source, fault);
		this.isCompleted = isCompleted;
	}

}
