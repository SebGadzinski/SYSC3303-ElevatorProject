# Multithreaded Elevator System v2.0
## SYSC 3303 L5, Winter 2021 – Real-Time Concurrent Systems
### Team 4 – Paul Roode (101056469), Chase Badalato (101072570), Sebastian Gadzinksi (101083974), Oluwaseyi Sehinde-Ibini (101092822), Chase Fridgen (101077379)

---

***Welcome!***

We hope you enjoy the 2nd iteration of our multithreaded elevator system!

This project aims to design a real-time elevator control system that will quickly and efficiently transport passengers between floors.

---

***Authors and Their Contributions***

Paul Roode
> Fully implemented the scheduler state machine (i.e., SchedulerStateMachine.java) and retrofitted Scheduler.java accordingly. Formatted this README.

<br>

Chase Badalato
> Constructed the UML sequence diagram, and implemented ElevatorSubsystem and helped establish its connection to Scheduler. Implemented JUnit tests for verification. 

<br>

Sebastian Gadzinksi
> Set up the initial project hierarchy and GitHub repo, and implemented Scheduler and helped establish its connection to ElevatorSubsystem and FloorSubsystem. Helped implement JUnit tests for verification.

<br>

Oluwaseyi Sehinde-Ibini
> Worked through and understood what the project classes and subsystems were doing. Used this knowledge to write the README file content that explains the aim of the project and what the classes within the project are doing (i.e., Main components below). Worked on tests for this iteration.

<br>

Chase Fridgen
> Created the two UML diagrams (one for subsystems and one for elevators and floors), implemented methods for Floor.java and Elevator.java, and implemented the JUnit tests for verifying the FloorSubsystem.

---

***Running the Application***

Runner.java is the driver.

---

***Main Components***

- FloorSubsystem: acts as the client; sends floor requests to the scheduler for relaying to an elevator, and manages the sending and receiving of requests for all the floors
- ElevatorSubsystem: receives relayed floor requests from the scheduler and sends them to the routed elevator node
- Scheduler: acts as the system server and manages interactions between the elevator and floor systems; once a floor request is received, it is routed to the appropriate elevator and relays the request to the ElevatorSubsystem
- Floor: node of the FloorSubsystem; handles its own floor requests, and has its own floor number, up/down buttons, and floor lamps
- Elevator: node of the ElevatorSubsystem; handles its own movement and opening/closing of doors, keeps track of all pending floor requests and lamps, and updates the scheduler about its status
