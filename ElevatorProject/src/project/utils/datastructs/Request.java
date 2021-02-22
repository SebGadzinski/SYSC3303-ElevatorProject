package project.utils.datastructs;

public class Request {
	
    private String source;
	
    public synchronized String getSource() {
        return source;
    }
	
}
