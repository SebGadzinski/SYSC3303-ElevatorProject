# SYSC 3303 Project: L5 GROUP 4
## Group Members: Paul Roode (101056469), Chase Badalato (101072570), Sebastian Gadzinksi (101083974), Oluwaseyi Sehinde-Ibini (101092822), Chase Fridgen (101077379)

This project aims to design a real time elevator control system that will quickly and efficiently transport passengers between floors.

## Classes:

### FloorSubsystem
* Acts as the client, sends floor requests to the scheduler for relaying to an elevator, manages the sending and receiving requests for all the floors

#### Floor
* Node of the FloorSubsystem; handles its respective floor requests, has its own floor number and up/down buttons, and floor lamps

### Scheduler
* Acts as the system server and manages interactions between the elevator and floor systems. Once a floor request is received, it is routed to the appropriate elevator and relays the request to the elevator subsystem.

### ElevatorSubsystem 
* Receives relayed floor requests from the scheduler and sends it to the routed elevator node

#### Elevator
* Node of the ElevatorSubsystem; handles it's own movement, opening/closing of doors, keeps track of all pending floor requests, lamps, and updates the scheduler about its status
