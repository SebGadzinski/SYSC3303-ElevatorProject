# Multithreaded Elevator System v4.0 (Iteration 4)
## SYSC 3303 L5, Winter 2021 – Real-Time Concurrent Systems
### Team 4 – Paul Roode (101056469), Chase Badalato (101072570), Sebastian Gadzinksi (101083974), Chase Fridgen (101077379)

---

***Welcome!***

We hope you enjoy the 4th iteration of our multithreaded elevator system!

This project aims to design a real-time elevator control system that quickly and efficiently transports passengers between floors.

---

***Authors and their contributions***

Author | Contributions
--- | ---
Paul Roode | Wrote TestRunner, which discovers and executes all JUnit-annotated tests and reports the results; wrote TestRealTimeElevatorArrival, which tests the real-time capability of the system; revamped Config to permit the spinning up of multiple test threads without interference with one another or those constituting the main application; reorganized the codebase and documentation; updated README; general de-smelling.
Chase Badalato | Created ElevatorTimerWorker class of which each ElevatorSubsystem has an instance. The timer starts when an elevator is on its way to a floor. If the timer runs out then the elevator is placed in a fatal state, and if the elevator arrives successfully the timer resets. Created an integrated test case (ITFaultsTimer) for this timer. Created a FloorSubsystemEmergencyRequest packet. Modified FloorSubsystem to receive these packets.
Sebastian Gadzinski | Created the UML diagrams. Created ElevatorSubsystemEmergencyPacket. Implemented the sending and receiving of these packets, and what to do once an emergency request is received.
Chase Fridgen | Implemented the fault detection within the Scheduler and ElevatorSubsystem. Created a unit test (TestElevatorFaults) which tests the emergency packet handling, and makes sure that emergency packets do not get randomly sent.

---

***Running the application***

In src/project/systems, first run (as a Java Application) ElevatorSubsystem, followed by Scheduler, followed by FloorSubsystem; and observe the terminal output, as well as the outputted ElevatorX.txt files and schedulerFile.txt in the root directory.

---

***Running the tests***

Navigate to src/project/tests, then run TestRunner as a Java Application and observe the terminal output. You may have to wait a few seconds for TestRunner to discover and execute all the tests and output the results.

---

***Main components***

Component | Description
--- | ---
FloorSubsystem | Acts as the client; sends floor requests to the scheduler for relaying to an elevator, and manages the sending and receiving of requests for all the floors.
ElevatorSubsystem | Receives relayed floor requests from the scheduler and sends them to the routed elevator node.
Scheduler | Acts as the system server and manages interactions between the elevator and floor systems; once a floor request is received, it is routed to the appropriate elevator and relays the request to the ElevatorSubsystem.
Floor | Node of the FloorSubsystem; handles its own floor requests, and has its own floor number, up/down buttons, and floor lamps.
Elevator | Node of the ElevatorSubsystem; handles its own movement and opening/closing of doors, keeps track of all pending floor requests and lamps, and updates the scheduler about its status.
