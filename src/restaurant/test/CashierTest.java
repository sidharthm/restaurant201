package restaurant.test;

import restaurant.CashierAgent;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockMarket market, market2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		market = new MockMarket("mockmarket");
		market2 = new MockMarket("mockmarket2");
	}	
	/**
	 * This tests the cashier under very simple terms: one market is ready to pay the exact bill.
	 */
	public void testOneNormalMarketScenario()
	{
		//setUp() runs first before this test!
		
		double cash = cashier.getMoney();
		
		//check preconditions
		assertEquals("Cashier should have 0 payments in it. It doesn't.",cashier.payments.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		//msgFoodDelivered(Market, Item, Quantity)
		cashier.msgFoodDelivered(market, "Steak", 1);//send the message from the market

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.payments.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true (one bill to calculate from a market), but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.payments.size(),0);
		
		cash -= 15.99;
		
		assertEquals("Cashier's money should have decreased by 15.99. It didn't.", cashier.getMoney(),cash);
		
		assertTrue("MockMarket should have logged \"Received cash\", but it reads " + market.log.getLastLoggedEvent().toString(),market.log.containsString("Received cash"));
	}//end one normal customer scenario
	
	public void testTwoNormalMarketsScenario(){

		//setUp() runs first before this test!
		
		double cash = cashier.getMoney();
		
		//check preconditions
		assertEquals("Cashier should have 0 payments in it. It doesn't.",cashier.payments.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		//msgFoodDelivered(Market, Item, Quantity)
		cashier.msgFoodDelivered(market, "Steak", 1);//send the message from the market
		cashier.msgFoodDelivered(market2, "Chicken", 1);

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("First MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertEquals("Cashier should have 2 bills in it. It doesn't.", cashier.payments.size(), 2);
		
		assertTrue("Cashier's scheduler should have returned true (two bills to calculate from a market), but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("Cashier should have 1 bills in it. It doesn't.", cashier.payments.size(),1);
		
		cash -= 15.99;
		
		assertEquals("Cashier's money should have decreased by 15.99. It didn't.", cashier.getMoney(),cash);
		
		assertTrue("MockMarket should have logged \"Received cash\", but it reads " + market.log.getLastLoggedEvent().toString(),market.log.containsString("Received cash"));
		
		assertEquals("Second MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		
		assertTrue("Cashier's scheduler should have returned true (one bill to calculate from a market), but didn't.", cashier.pickAndExecuteAnAction());
		
		cash -= 10.99;
		
		assertEquals("Cashier's money should have decreased by 10.99. It didn't.", cashier.getMoney(),cash);
		
		assertTrue("MockMarket should have logged \"Received cash\", but it reads " + market2.log.getLastLoggedEvent().toString(),market2.log.containsString("Received cash"));

	}
	
}
