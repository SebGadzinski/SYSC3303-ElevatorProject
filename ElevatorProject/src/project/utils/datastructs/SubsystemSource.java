package project.utils.datastructs;

import java.io.Serializable;

public class SubsystemSource implements Serializable {

    private Subsystem subsystem;
    private String id;

    public SubsystemSource(Subsystem subsystem, String id) {
        this.subsystem = subsystem;
        this.id = id;
    }

    public Subsystem getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(Subsystem subsystem) {
        this.subsystem = subsystem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public enum Subsystem {
        ELEVATOR_SUBSYSTEM, FLOOR_SUBSYSTEM, SCHEDULER
    }

    @Override
    public String toString() {
    	if(subsystem == Subsystem.ELEVATOR_SUBSYSTEM) return "Source : Elevator # " + id;
    	else if(subsystem == Subsystem.FLOOR_SUBSYSTEM) return "Source : Floor # " + id;
    	else return "Source : Scheduler";
    }

}
