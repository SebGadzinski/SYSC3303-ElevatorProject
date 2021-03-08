package project;

import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.systems.Scheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static project.Config.NUMBER_OF_ELEVATORS;
import static project.Config.NUMBER_OF_FLOORS;

/**
 * The driving class.
 *
 * @author Paul Roode
 * @version Iteration 3
 */
public class Runner {

    public static void main(String[] args) {

        // initialize thread-safe subsystem containers
        List<ElevatorSubsystem> elevatorSubsystems = Collections.synchronizedList(new ArrayList<>());
        List<FloorSubsystem> floorSubsystems = Collections.synchronizedList(new ArrayList<>());

        // populate the subsystem containers
        Collections.addAll(elevatorSubsystems,
                new ElevatorSubsystem(),
                new ElevatorSubsystem(),
                new ElevatorSubsystem()
        );
        Collections.addAll(floorSubsystems,
                new FloorSubsystem(),
                new FloorSubsystem(),
                new FloorSubsystem()
        );
        /*
         or:
         for (int i = 0; i < NUMBER_OF_ELEVATORS; ++i) elevatorSubsystems.add(new ElevatorSubsystem());
         for (int i = 0; i < NUMBER_OF_FLOORS; ++i) floorSubsystems.add(new FloorSubsystem());
        */

        // start subsystem threads
        for (ElevatorSubsystem elevatorSubsystem : elevatorSubsystems) (new Thread(elevatorSubsystem)).start();
        for (FloorSubsystem floorSubsystem : floorSubsystems) (new Thread(floorSubsystem)).start();
        (new Thread(new Scheduler(elevatorSubsystems, floorSubsystems))).start();

    }

}
