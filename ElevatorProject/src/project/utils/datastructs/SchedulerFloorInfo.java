package project.utils.datastructs;

import project.utils.datastructs.SubsystemSource.Subsystem;

public class SchedulerFloorInfo extends SchedulerSubsystemInfo {

    public SchedulerFloorInfo(String id, UDPInfo udpInfo) {
        super(id, udpInfo);
    }

    /**
     * Replicates this Elevators Subsystem identification
     *
     * @return Elevator subsystems identification
     */
    public SubsystemSource getSource() {
        return new SubsystemSource(Subsystem.FLOOR_SUBSYSTEM, getId());
    }

    @Override
    public String toString() {
        return "Floor: " + "\n"
                + "id: " + getId() + "\n"
                + getUdpInfo() + "\n";
    }

}
