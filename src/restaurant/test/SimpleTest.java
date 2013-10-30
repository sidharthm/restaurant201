package restaurant.test;

import restaurant.CashierAgent;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockWaiter;
import junit.framework.*;

public class SimpleTest extends TestCase{

	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	
	public void setUp() throws Exception{
		super.setUp();
		cashier = new CashierAgent("cashier");
		waiter= new MockWaiter("waiter");
		customer = new MockCustomer("customer");
		
	}
	
	public void testsimpleT(){
		//Preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.getOrders().size(), 0);
		
		//Step 1 
		cashier.msgHereIsBill(waiter, customer, "Steak", 1);
		
		//Check
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.getOrders().size(),1);
		
		//Step 2 
		assertTrue("Cashier's scheduler should respond true. It doesn't", cashier.pickAndExecuteAnAction());
		
		//Check
		assertTrue("MockWaiter should have gotten a message",waiter.log.containsString("Received bill for 15.99 from table 1"));
		
	}
}
