# Multithreaded Elevator System v5.0 (Iteration 5)
## SYSC 3303 L5, Winter 2021 – Real-Time Concurrent Systems
### Team 4 – Paul Roode (101056469), Chase Badalato (101072570), Sebastian Gadzinksi (101083974), Chase Fridgen (101077379)

---

***Welcome!***

We hope you enjoy the 5th iteration of our multithreaded elevator system!

This project aims to design a real-time elevator control system that quickly and efficiently transports passengers between floors.

---

***Authors and their contributions***

Author | Contributions
--- | ---
Paul Roode | Revamped Config and refactored the subsystems to eliminate hardcoded interdependencies, thereby enabling the spinning up of as many subsystem threads as desired from anywhere in the codebase with no worry of the threads interfering with each other (e.g., the application and test suite can both spin up as many subsystem threads as desired and be safely run concurrently) while keeping multihost-friendly, and updated the JUnit test suite accordingly.  Wrote ITAllSubsystems.java, which comprises functional integration JUnit tests that leverage said refactor to spin up subsystem threads and verify that all of the subsystems can coordinate on optimally servicing all inputted requests, including all of the different kinds of faults; e.g., validates the execution time of the whole system, verifies the number of confirmed destination arrivals following the servicing of all requests, etc.
Chase Badalato | Created a test case that tested the functionality of the FloorSubsystem, confirming all send requests are properly executed.  Added real times for the movement of elevators, opening and closing of doors, and the loading of passengers through various functions.
Sebastian Gadzinski | Created the GUI and worked on creating fault printing, which if on, only outputs information on faults allowing the user to see how faults are taken care of clearly. Also worked on integrating the GUI into the subsystems.
Chase Fridgen | Implemented the Scheduler performance measurement of the system. Created the timing diagram for the system.

---

***Running the application***

Navigate to the src/project/systems directory and run the subsystems therein as Java Applications in the following order:
1. ElevatorSubsystem or Scheduler;
2. ElevatorSubsystem or Scheduler, whichever you did not choose in step 1;
3. FloorSubsystem.
The FloorSubsystem thread must be started last as it consumes the request input file and initiates the servicing of requests.  Starting Scheduler will cause the GUI to load, so do not be alarmed when it pops up after you start running Scheduler.  Do not close the GUI window while the program executes, as doing so will terminate the Scheduler thread (but feel free to minimize or resize the GUI).
After spinning up subsystem threads in the order specified above, observe the GUI and terminal while the subsystems coordinate on fulfilling requests, as well as the outputted ElevatorX.txt files and schedulerFile.txt file in the project root directory.

---

***Running the tests***

Navigate to the src/project/tests directory and run TestRunner as a Java Application.  TestRunner discovers and executes all tests (based on JUnit annotations) and outputs the results to the terminal once all tests have finished.
Because a couple of the integration tests spin up subsystem threads of their own, one or more GUIs may pop up as the application is functionally tested.  Do not close these test GUIs, as doing so will cause the tests to terminate.  The ITAllSubsystems class (“IT” being short for “integration test”) in particular functionally tests the full execution of the application, and so you will have to wait for the same duration as if you were running the application for its tests to complete, ~110 s.  You may then observe the test results outputted to the terminal by TestRunner, i.e., the number of tests discovered, the number of tests passed, and detailed descriptions of any failures.
Note that it is safe to run TestRunner and the application (as specified in the previous section, Application) concurrently—the subsystems have been architected and configured such that subsystem threads can be spun up from anywhere in the codebase with no possibility of interference with other threads.

---

***Main components***

Component | Description
--- | ---
FloorSubsystem | Acts as the client; sends floor requests to the scheduler for relaying to an elevator, and manages the sending and receiving of requests for all the floors.
ElevatorSubsystem | Receives relayed floor requests from the scheduler and sends them to the routed elevator node.
Scheduler | Acts as the system server and manages interactions between the elevator and floor systems; once a floor request is received, it is routed to the appropriate elevator and relays the request to the ElevatorSubsystem.
Floor | Node of the FloorSubsystem; handles its own floor requests, and has its own floor number, up/down buttons, and floor lamps.
Elevator | Node of the ElevatorSubsystem; handles its own movement and opening/closing of doors, keeps track of all pending floor requests and lamps, and updates the scheduler about its status.
