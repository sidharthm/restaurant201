# Design Document for Agents in Restaurant v2

## Purpose/Overview
> To use agent-based design to simulate the interactions involved in the running of a restaurant. 


## Requirements
> This program has 6 major required upgrades on the v1 Restaurant

> 1. Add a Waiter class that is responsible for seating Customers, taking their orders, relaying those orders to the cook, and delivering the order to the customer's table.
> 2. Add a Cook class that is responsible for receiving orders and cooking the appropriate meal for each customer.
> 3. Allow the user to add multiple functioning Waiters to the restaurant through the GUI
> 4. Allow customers to choose different meals, and differentiate these meals in the GUI
> 5. Consolidate the GUI and the Animation panel into a single window
> 6. Implement a Pause button from the GUI that pauses the entire simulation.

## Interaction Diagram 
![alt text](Interaction Diagram v2.png "Interaction Diagram")

## Agent Summary 

###Host 

1. Data
  + `List<Customer> waitingCustomers` - queue of customers waiting to be seated
  + `List<Waiter> availableWaiters` - list of waiter available to help customers
  + `Collection<Table>` tables - all available tables
2. Messages
  + msgIWantFood(CustomerAgent cust){ waitingCustomers.add(cust); }
  + msgTableCleared(CustomerAgent cust, WaiterAgent wait){  
       If there exists a table in tables such that  
		table.customer matches cust  
				table.setUnoccupied()  
				availableWaiters.add(wait)  

3. Scheduler 
>
>		If waitingCustomers is not empty and
>			If there exists a table in tables such that
>				table.isUnoccupied() and
>					If availableWaiters is not empty
>					 then availableWaiters.first().msgSitAtTable(waitingCustomers.first, table.getNumber());
>
4. Actions   
	None

### Waiter

1. Data
	+ `List<myCustomer> myCustomers` - list of myCustomer objects that the waiter must serve
	
>		class myCustomer{
>			Customer c; int t; String choice; CustomerState s;
>		}
>
2. Messages 
	+ msgSitAtTable(CustomerAgent cust, int tNum){ myCustomers.add(new myCustomer(cust, tNum));}
	+ msgReadytoOrder(CustomerAgent cust){
	>
	>	If there exists a myCustomer in myCustomers such that 
	>		myCustomer.c = cust
	>	then myCustomer.s = cust.s}
	>
	+ msgCustomerOrder(CustomerAgent cust, String choice){
	>
	>	If there exists a myCustomer in myCustomers such that 
	>		myCustomer.c = cust
	>	then myCustomer.choice = cust.choice}
	>
	+ msgOrderReady(Order myOrder){
	>
	>	If there exists a Customer in myCustomers such that 
	>		myCustomer.c = myOrder.c
	>	then myCustomer.msgOrderReceived();}
	>
	+ msgLeavingTable(CustomerAgent cust){
	>
	>	If there exists a myCustomer in myCustomers such that 
	>		myCustomer.c = cust
	>	then {myCustomers.remove(myCustomer); host.msgTableCleared(cust,this);}
	>
3. Scheduler
>
>   If there exists a myCustomer in myCustomers such that  
>		 myCustomer.state = waiting  
>	Then seatCustomer(myCustomer.c, ,myCustomer.t)  
>
4. Actions 
	+ seatCustomer(CustomerAgent customer, int tNum){
	>	customer.setTableNum(tNum);
	>	customer.msgSitAtTable();
	>	}
	
###Customer 

1. Data
2. Messages
	+ gotHungry() - from GUI, sets event to gotHungry
	+ msgSitAtTable(){  
	>  Change event to followHost}
	+ msgWhatisYourOrder(){
	>  Change event to readyToOrder}
	+ msgOrderReceived(){
	>  Change event to Eating}
3. Scheduler
>
>
	
## Class Definitions
> The Agent implementation requires 5 classes 

> 1. Agent - The abstract class from which all other agents inherit their definition. 
>  	+ Variables(private)
>		+ agentThread - an instance of the AgentThread class unique to each Agent. 
>		+ stateChange - a Semaphore that is responsible for getting the Agent's thread started in the event that something changes
>	+ Functions(protected)
>		+ stateChanged() - releases the Semaphore when the Agent is prompted to act
>		+ pickAndExecuteAnAction() - the Agent's scheduler 
>		+ getName() - returns the Agent's name as a String 
>		+ print(String) - Outputs a String to the console
>	+ Functions (public)
>		+ Constructor - null constructor since this is an abstract class
>		+ startThread() - starts the agentThread object, causing its run method to execute
>		+ stopThread() - ends the agentThread object 
> 2. AgentThread - thread within the Agent class that is responsible for allowing the Agent to respond to changes
>	+ Variables(private)
>		+ goOn - boolean that determines whether the thread should continue executing 
>	+ Functions(private)
>		+ Constructor - assumes the name of the parent Agent
>		+ stopAgent() - sets goOn to false, and interrupts the thread
>	+ Functions(public)
>		+ run - responds to a state change, allowing the Agent to execute until the Agent has nothing to do
> 2. CustomerAgent - An Agent that acts like the customer in a restaurant- getting hungry, going to the restaurant, being seated, ordering, eating, and leaving
>	+ Variables(private)
>		+ name - the name of the Agent, as provided by the user from the GUI
>		+ hungerLevel - an integer that determines the length of the meal 
>		+ tableNum - an integer denoting the table at which the customer will be seated
>		+ host - an instance of HostAgent that denotes the host that will arrange for the customer's seating
>		+ customerGui - an instance of the CustomerGui class that coordinates the Agent's actions with the appropriate animation 
>		+ state - an enum that describes the current state of the customer: DoingNothing, WaitingInRestaurant, BeingSeated, Seated, Eating, DoneEating, Leaving
>		+ event - an enum that describes what the customer is experiencing: none, gotHungry, followHost, seated, doneEating, doneLeaving
>	+ Functions(public)
>		+ Constructor - establishes the CustomerAgent with the name provided from the GUI
>		+ setHost(HostAgent host)- assigns a value to host, associating the CustomerAgent with a HostAgent
>		+ getCustomerName() - returns the CustomerAgent's name as a String
>		+ gotHungry() - triggered from the GUI, this message sets the CustomerAgent's event to gotHungry
>		+ msgSitAtTable() - received from the host, this message changes the CustomerAgent's event to followHost
>		+ msgAnimationFinishedGoToSeat() - received from GUI, this message changes the CustomerAgent's event to seated
>		+ msgAnimationFinishedLeaveRestaurant() - received from GUI, this message changes the CustomerAgent's event to doneLeaving
>		+ getName() - returns the name of the Customer
>		+ getHungerLevel() - returns the customer's hunger level
>		+ setHungerLevel(int hungerLevel) - sets the customer's hunger level
>		+ toString() - allows the customer to be directly printed as "customer " + name
>		+ setTableNum(int) - sets the table number to which the customer should proceed
>	+ Functions(protected)
>		+ pickAndExecuteAnAction() - the Customer's scheduler. See the Agent Description to see details
>	+ Functions(private)
>		+ goToRestaurant() - sends a message to the Host that the Customer is going to the Restaurant
>		+ SitDown() - tells the CustomerGui to animate the process of being seated
>		+ EatFood() - based on the hunger level, spends an amount of time for the Customer to eat the meal he has ordered 
>		+ LeaveTable() - sends a message to the Host that the Customer is leaving, signals to CustomerGui to animate the customer leaving
> 3. HostAgent - the Agent responsible for managing tables at the restaurant
>	+ Variables(public)
>		+ waitingCustomers - an ArrayList that tracks the number of customers waiting to be seated
>		+ tables - a Collection of Table objects for customers to be seated at
>	+ Variables(private)
>		+ name - names of the Host 
>		+ atTable - a Semaphore used by the Host to manage the 
>	+ Functions(public)
>		+ Constructor - initializes the Host with its name and the number of tables in the restaurant
>		+ getMaitreDName() - returns the Host's name
>		+ getName() - returns the Host's name
>		+ getWaitingCustomers() - returns the waitingCustomers ArrayList
>		+ getTables() - returns the tables Collection
>		+ msgIWantFood(CustomerAgent cust) - message received from customer that adds the sender to the waitingCustomers list
>		+ msgLeavingTable(CustomerAgent cust) - message received from customer that marks a table unoccupied 
>	+ Functions(protected)
>		+ pickAndExecuteAnAction() - Scheduler for the Host. See the Agent Description to see details
>	+ Functions(private)
>		+ seatCustomer(CustomerAgent customer, Table table) - assigns a seat to the customer, removes it from waitingCustomers, and allows the Waiter to seat the customer ?????
> 4. WaiterAgent
>	+ Variables (public)
>		+ hostGui - an instance of the hostGui class to link the Agent's behavior with the animation
>	+ Variables (private)
>		+ name - the name of the Waiter, provided by the user from the GUI
>		+ tableNum - an integer referring to the table at which the customer must be seated
>		+ atTable - Semaphore responsible for seating of customers
>		+ host - an instance of the HostAgent class to be used for messaging
>		+ cust - an instance of the CustomerAgent class to be used for messaging
>	+ Functions(public)
>		+ Constructor - generates a new instance of the class with the assigned name
>		+ getMaitreDNAme() - returns the name of the WaiterAgent
>		+ getName() - returns the name of the WaiterAgent
>		+ msgSitAtTable(CustomerAgent cust, int tNum) - a message sent by the host that instructs the Waiter to lead the assigned customer to the appropriate table
>		+ msgLeavingTable(CustomerAgent cust) - a message sent by the customer that instructs the Waiter to inform the host of the vacant table
>		+ msgAtTable() - a message sent from the GUI to alert the Waiter to release the Semaphore
>		+ setGui(HostGui gui) - links the WaiterAgent with a HostGui class in order to allow animation 
>		+ getGui() - returns the associated HostGui class
>	+ Functions (protected)
>		+ pickAndExecuteAnAction() - Scheduler for the Waiter Agent. See Agent Summary for details
>	+ Functions (private)
>		+ seatCustomer(CustomerAgent customer, int tNum)- responsible for performing the action of seating the customer
>		+ DoSeatCustomer(CustomerAgent customer, int tNum) - handles the animation of seating the customer
> 5. CookAgent

