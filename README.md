# Multithreaded Elevator System v4.0 (Iteration 4)
## SYSC 3303 L5, Winter 2021 – Real-Time Concurrent Systems
### Team 4 – Paul Roode (101056469), Chase Badalato (101072570), Sebastian Gadzinksi (101083974), Chase Fridgen (101077379)

---

***Welcome!***

We hope you enjoy the 4th iteration of our multithreaded elevator system!

This project aims to design a real-time elevator control system that will quickly and efficiently transport passengers between floors.

---

***Authors and their contributions***

Author | Contributions
--- | ---
Paul Roode | Added an abstraction layer by abstracting the UDP mechanism and request serialization into an AbstractSubsystem class, from which all subsystems (ElevatorSubsystem, FloorSubsystem, and Scheduler) inherit; and updated the concrete subsystem classes accordingly. Refactored UDP configuration to accommodate multiple hosts. De-smelled and formatted all src files.
Chase Badalato | Converted the FloorSubsystem to use UDP, and wrote test cases for all subsystems.
Sebastian Gadzinski | Worked on the Scheduler and ElevatorSubsystem classes.
Chase Fridgen | Made the CreateFile class and updated ElevatorSubsystem and Scheduler to output to .txt files. Helped with the FloorSubsystemStub test class. Made the UML class and sequence UML diagrams.

---

***Running the application***

In src/project/systems, first run (as a Java Application) ElevatorSubsystem, followed by Scheduler, followed by FloorSubsystem; and observe the terminal output, as well as the outputted ElevatorX.txt files and schedulerFile.txt in the root directory.

---

***Running the tests***

Navigate to src/project/tests; run TestRunner as a Java Application and observe the terminal output.

---

***Main components***

Component | Description
--- | ---
FloorSubsystem | Acts as the client; sends floor requests to the scheduler for relaying to an elevator, and manages the sending and receiving of requests for all the floors.
ElevatorSubsystem | Receives relayed floor requests from the scheduler and sends them to the routed elevator node.
Scheduler | Acts as the system server and manages interactions between the elevator and floor systems; once a floor request is received, it is routed to the appropriate elevator and relays the request to the ElevatorSubsystem.
Floor | Node of the FloorSubsystem; handles its own floor requests, and has its own floor number, up/down buttons, and floor lamps.
Elevator | Node of the ElevatorSubsystem; handles its own movement and opening/closing of doors, keeps track of all pending floor requests and lamps, and updates the scheduler about its status.
