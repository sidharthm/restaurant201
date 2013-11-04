##Restaurant Project Repository

###Student Information
  + Name: Sidharth Menon
  + USC Email: sidhartm@usc.edu
  + USC ID: 4473274021


###Compilation/Running Instructions
  + The program runs normally without any extra conditions through Eclipse on my Win 7 64 bit PC.
  + Making customers hungry upon creation requires taht you hit "Enter" before you can check the box
  + Adding waiters is identical to adding waiters from v1, just type in a name and hit add. 
  + The timers have been programmed such that some tasks will take a large amount of time [such as eating salad or pizza].
    + There is a 1 sec pause between the Customer being seated and placing an order, the program is not hanging if that happens.
    + New customers are not seated until the waiter returns to his starting position. This can take time if the waiter is returning from the cook. 
  
###Issues
  + My attempts to format with Markdown for the design document resulted in some weird issues with reading the doc. All the content should be correct though. 
  
###Other Information
  + Cook Animation Upgrades- the plating area is a circle, the grill is the gray square, and the fridge is the black rectangle
  + I did both extra credit opportunities 
    + If the cashier does not have enough money for the market, the market will not fulfill any further orders from the market (besides the current wave of deliveries)
    + There's an extra test for the Cashier in CashierTest.java, and there's a test for the Market in SimpleTest.java
	+ Animation changes the '?' behind the order to a '!' to signal that ingredients have been gathered. 

###Resources
  + [Restaurant v1](http://www-scf.usc.edu/~csci201/readings/restaurant-v1.html)
  + [Agent Roadmap](http://www-scf.usc.edu/~csci201/readings/agent-roadmap.html)
