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
> Fully implemented the scheduler state machine (i.e., SchedulerStateMachine.java) and retrofitted Scheduler.java accordingly. Made the scheduler state machine diagram. Formatted this README.

<br>

Chase Badalato
> Reworked floorSubsystem class to send FileRequest packets to the respective floors. Reworked the Floor class to in parse, wait, and then forward the FileRequest packet to the scheduler. Created UML Sequence Diagram. 

<br>

Sebastian Gadzinksi
> Updated Request.java, allowing for different requests to be made. Also worked on the elevator subsystem and elevator state machine class and diagram. I also worked on the UML sequence diagram.

<br>

Oluwaseyi Sehinde-Ibini
> Worked on tests for the system to ensure it worked properly.

<br>

Chase Fridgen
> Coded methods realTimeWait() and toMilliSeconds() in the Floor class to make it wait for the proper time, as well as helped with floorSubSystem class. Made the UML class diagram.

---

***Running the Application***

project/Runner.java is the driver – run it and observe the terminal output.

---

***Main Components***

- FloorSubsystem: acts as the client; sends floor requests to the scheduler for relaying to an elevator, and manages the sending and receiving of requests for all the floors
- ElevatorSubsystem: receives relayed floor requests from the scheduler and sends them to the routed elevator node
- Scheduler: acts as the system server and manages interactions between the elevator and floor systems; once a floor request is received, it is routed to the appropriate elevator and relays the request to the ElevatorSubsystem
- Floor: node of the FloorSubsystem; handles its own floor requests, and has its own floor number, up/down buttons, and floor lamps
- Elevator: node of the ElevatorSubsystem; handles its own movement and opening/closing of doors, keeps track of all pending floor requests and lamps, and updates the scheduler about its status
