package project.tests;

import static project.Config.SCHEDULER_UDP_INFO;

import java.net.InetAddress;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.AbstractSubsystem;
import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.systems.Scheduler;
import project.utils.datastructs.FileRequest;

public class SchedulerStub extends AbstractSubsystem {

	protected SchedulerStub(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
		super(inetAddress, inSocketPort, outSocketPort);
	}

	public static void main(String[] args) {
		FloorSubsystem floor = new FloorSubsystem(Config.FLOORS_UDP_INFO[0].getInetAddress(), 100, 101, 0);

		FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, floor.getSource());
		SchedulerStub test = new SchedulerStub(Config.FLOORS_UDP_INFO[0].getInetAddress(), 6969, 7070);

		while (true) {
			test.sendRequest(fileRequest, Config.SCHEDULER_UDP_INFO.getInetAddress(), Config.SCHEDULER_UDP_INFO.getInSocketPort());
			
			System.out.println("Scheduler Stub sending a fileRequest");

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
