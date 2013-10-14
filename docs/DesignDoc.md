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

## Interaction Diagrams
###v2 Diagram
![alt text](Interaction Diagram v2.png "Interaction Diagram")
###Waiter Break Scenario
![alt text](waiter break.jpg "Waiter going on break")
###Customer Reordering Scenario
![alt text](customer reorder.jpg "Customer must reorder if cook is out of food")
###Cook-Market Scenario
![alt text](market cook.jpg "Cook orders food from market when out of stock")
###Customer Payment Scenarios 
![alt text](customer pays.jpg "Customer pays Cashier through Waiter for meal")
![alt text](customer nopay.jpg "Customer cannot pay, remains on Cashier's owed money list")

## Agent Summary 

###Host 

1. Data
  + `List<Customer> waitingCustomers` - queue of customers waiting to be seated
  + `List<Waiter> availableWaiters` - list of waiter available to help customers
  + `Collection<Table>` tables - all available tables
2. Messages
  + msgIWantFood(CustomerAgent cust){ waitingCustomers.add(cust); }
  + msgTableCleared(CustomerAgent cust, WaiterAgent wait){  
  
  >     If there exists a table in tables such that  
  >		table.customer matches cust  
  >				table.setUnoccupied()  
  >				availableWaiters.add(wait)  

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
	
		`class myCustomer{
			Customer c; int t; String choice; CustomerState s;
		}`

2. Messages 
	+ msgSitAtTable(CustomerAgent cust, int tNum){ myCustomers.add(new myCustomer(cust, tNum));}
	+ msgReadytoOrder(CustomerAgent cust){
	
	
  >  	  If there exists a myCustomer in myCustomers such that 
	   		myCustomer.c = cust
		then myCustomer.s = cust.s}  
  
	+ msgCustomerOrder(CustomerAgent cust, String choice){
	
	
  >		If there exists a myCustomer in myCustomers such that 
			myCustomer.c = cust
		then myCustomer.choice = cust.choice}  

	+ msgOrderReady(Order myOrder){
	
  	
    >		If there exists a Customer in myCustomers such that 
			myCustomer.c = myOrder.c
		then myCustomer.msgOrderReceived();}

	+ msgLeavingTable(CustomerAgent cust){

	
  >		If there exists a myCustomer in myCustomers such that 
			myCustomer.c = cust
		then {myCustomers.remove(myCustomer); host.msgTableCleared(cust,this);}
		
3. Scheduler


>   If there exists a myCustomer in myCustomers such that  
		 myCustomer.state = waiting  
	Then seatCustomer(myCustomer.c, ,myCustomer.t)  

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

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Ordering;
			OrderFood();
			return true;
		}
		
		if (state == AgentState.Ordering && event == AgentEvent.readyToOrder){
			state = AgentState.Eating;
			EatFood();
			return true;
		}

		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}
4. Actions
	+ goToRestaurant() - tells the Host to seat the customer
	+ SitDown() - tells the GUI to animate the sitting motion
	+ OrderFood() - has the customer make a choice and sends that choice to the Host 
	+ EatFood() - the customer eats the food
	+ leaveTable() - the customer is done eating, and the GUI can animate him leaving the restaurant

##Cook
1. Data 
	+ `List<Order> pendingOrders` - list of Orders that the cook must fill 
	
	> class Order{
	>Waiter w; String c; int t;
	>}
2. Messages
	+ msgHereIsAnOrder(Waiter wait, String choice, int tNum){
	
	> pendingOrders.add(new Order(wait, choice, tNum) }
	
3. Scheduler

>	If there exists an order in pendingOrders
>		CookOrder(order);

4. Actions
	+CookOrder(Order){
	
	>	Do("Cooking");
	>	timer.schedule(new TimerTask() {
	>		waiter.OrderisReady(Order)
	>	},1000);