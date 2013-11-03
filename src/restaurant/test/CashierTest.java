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
	MockCustomer customer, thief;
	MockMarket market, market2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");
		thief = new MockCustomer("thief");
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
		
		//step 2 of the test
		assertTrue("Cashier's scheduler should have returned true (one bill to calculate from a market), but didn't.", cashier.pickAndExecuteAnAction());
		
		//check post-conditions of step 2
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.payments.size(),0);
		
		cash -= 15.99;
		
		assertEquals("Cashier's money should have decreased by 15.99. It didn't.", cashier.getMoney(),cash);
		
		assertTrue("MockMarket should have logged \"Received cash\", but it reads " + market.log.getLastLoggedEvent().toString(),market.log.containsString("Received cash"));
	}//end one normal market scenario
	
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
		
		//step 2 of the test 
		assertTrue("Cashier's scheduler should have returned true (two bills to calculate from a market), but didn't.", cashier.pickAndExecuteAnAction());
		
		//check post-conditions of step 2
		assertEquals("Cashier should have 1 bills in it. It doesn't.", cashier.payments.size(),1);
		
		cash -= 15.99;
		
		assertEquals("Cashier's money should have decreased by 15.99. It didn't.", cashier.getMoney(),cash);
		
		assertTrue("MockMarket should have logged \"Received cash\", but it reads " + market.log.getLastLoggedEvent().toString(),market.log.containsString("Received cash"));
		
		//check pre-conditions for step 3
		assertEquals("Second MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
				+ market2.log.toString(), 0, market2.log.size());
		
		//step 3 of the test
		assertTrue("Cashier's scheduler should have returned true (one bill to calculate from a market), but didn't.", cashier.pickAndExecuteAnAction());
		
		//check post-conditions for step 3
		cash -= 10.99;
		
		assertEquals("Cashier's money should have decreased by 10.99. It didn't.", cashier.getMoney(),cash);
		
		assertTrue("MockMarket should have logged \"Received cash\", but it reads " + market2.log.getLastLoggedEvent().toString(),market2.log.containsString("Received cash"));

	}//end two normal market scenarios
	
	public void testSingleCustomerScenario(){
		//This test has the cashier deal with a single customer paying for his meal in full.
		//setUp() runs first before this test!
		
		double cash = cashier.getMoney();
		customer.waiter = waiter;
		customer.cashier = cashier;
		
		waiter.customer = customer;
		waiter.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.pendingOrders.size(),0);
		assertEquals("Cashier should have 0 owingCustomers in it. It doesn't.",cashier.owingCustomers.size(),0);
		assertEquals("Cashier should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		//step 1 of the test 
		//msgHereIsBill(waiter,customer,choice,table)
		cashier.msgHereIsBill(waiter, customer, "Steak", 0);
		
		//check post-conditions of step 1, pre-conditions of step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
				+ customer.log.toString(), customer.log.size(), 0);
		
		assertTrue("Cashier should have \"Received bill for table 0\" in it, but it doesn't. Instead, the log reads: " + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("Received bill for table 0"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.pendingOrders.size(),1);
		
		//step 2 of the test 
		assertTrue("Cashier's scheduler should have returned true (1 order to calculate), but didn't.", cashier.pickAndExecuteAnAction());
		
		//check post-conditions of step 2
		assertTrue("MockWaiter should have \"Received bill\" in its log, but it doesn't. Instead, the log reads: " + waiter.log.getLastLoggedEvent(), waiter.log.containsString("Received bill"));
		assertTrue("MockWaiter should have \"Got money from customer\" in its log, but it doesn't. Instead, the log reads: " + waiter.log.getLastLoggedEvent(), waiter.log.containsString("Got money from customer"));
		
		assertTrue("MockCustomer should have \"Received HereIsYourTotal from cashier. Total = 15.99\" in its log, but it doesn't. Instead, the log reads: " + customer.log.getLastLoggedEvent(), customer.log.containsString("Received HereIsYourTotal from cashier. Total = 15.99"));
		assertTrue("MockCustomer should have \"Received HereIsYourChange from cashier. Change = 0.01\" in its log, but it doesn't. Instead, the log reads: " + customer.log.getLastLoggedEvent(), customer.log.containsString("Received HereIsYourChange from cashier. Change = 0.01"));
		
		assertEquals("Cashier should have no more pending orders, but it does.", cashier.pendingOrders.size(), 0);
		assertTrue("Cashier should have \"Got money from waiter\" in its log, but it doesn't. Instead, it reads: " + cashier.log.getLastLoggedEvent(), cashier.log.containsString("Got money from waiter"));
		assertEquals("Cashier should have no more owingCustomers, but it doesn't.", cashier.owingCustomers.size(), 0);
		cash += 15.99;
		assertEquals("Cashier should have 15.99 more in its money, but it doesn't.", cashier.getMoney(), cash);
		
	}
	
	public void testThiefScenario(){
		//This test has the cashier deal with a single customer that can't pay for his meal in full. Then resolves it upon a second visit. 
		//setUp() runs first before this test!
		
		double cash = cashier.getMoney();
		thief.waiter = waiter;
		thief.cashier = cashier;
		
		waiter.customer = thief;
		waiter.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.pendingOrders.size(),0);
		assertEquals("Cashier should have 0 owingCustomers in it. It doesn't.",cashier.owingCustomers.size(),0);
		assertEquals("Cashier should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		//step 1 of the test 
		//msgHereIsBill(waiter,customer,choice,table)
		cashier.msgHereIsBill(waiter, thief, "Steak", 0);
		
		//check post-conditions of step 1, pre-conditions of step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
				+ thief.log.toString(), thief.log.size(), 0);
		
		assertTrue("Cashier should have \"Received bill for table 0\" in it, but it doesn't. Instead, the log reads: " + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("Received bill for table 0"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.pendingOrders.size(),1);
		
		//step 2 of the test 
		assertTrue("Cashier's scheduler should have returned true (1 order to calculate), but didn't.", cashier.pickAndExecuteAnAction());
		
		//check post-conditions of step 2, pre-conditions of step 3
		assertTrue("MockWaiter should have \"Received bill\" in its log, but it doesn't. Instead, the log reads: " + waiter.log.getLastLoggedEvent(), waiter.log.containsString("Received bill"));
		assertTrue("MockWaiter should have \"Customer can't pay\" in its log, but it doesn't. Instead, the log reads: " + waiter.log.getLastLoggedEvent(), waiter.log.containsString("Customer can't pay"));
		
		assertTrue("MockCustomer should have \"Received HereIsYourTotal from cashier. Total = 15.99\" in its log, but it doesn't. Instead, the log reads: " + thief.log.getLastLoggedEvent(), thief.log.containsString("Received HereIsYourTotal from cashier. Total = 15.99"));
		
		assertEquals("Cashier should have no more pending orders, but it does.", cashier.pendingOrders.size(), 0);
		assertEquals("Cashier should have 1 owingCustomer, but it doesn't.", cashier.owingCustomers.size(), 1);
		assertEquals("Cashier's money should not have changed, but it did.", cashier.getMoney(), cash);
		
		//step 3 of the test 
		cashier.msgHereIsBill(waiter, thief, "Chicken", 0);
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.pendingOrders.size(),1);
		
		//step 4 of the test 
		assertTrue("Cashier's scheduler should have returned true (1 order to calculate), but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("Cashier should have \"He owes us from last time\" in his long, but doesn't. Instead, the log reads: " + cashier.log.getLastLoggedEvent(), cashier.log.containsString("He owes us from last time"));
		
		assertTrue("MockWaiter should have \"Received bill\" in its log, but it doesn't. Instead, the log reads: " + waiter.log.getLastLoggedEvent(), waiter.log.containsString("Received bill"));
		
		assertTrue("MockCustomer should have \"Received HereIsYourTotal from cashier. Total = 26.98\" in its log, but it doesn't. Instead, the log reads: " + thief.log.getLastLoggedEvent(), thief.log.containsString("Received HereIsYourTotal from cashier. Total = 26.98"));
		
		assertEquals("Cashier should have no more pending orders, but it does.", cashier.pendingOrders.size(), 0);
		assertEquals("Cashier should have 1 owingCustomer, but it doesn't.", cashier.owingCustomers.size(), 1);
		assertEquals("Cashier's money should not have changed, but it did.", cashier.getMoney(), cash);
		
		//step 5 of the test
		cashier.msgCustomerPaid(thief, 26.98);
		
		assertEquals("Cashier should have no owingCustomers, but it doesn't.", cashier.owingCustomers.size(), 0);
		cash += 26.98;
		assertEquals("Cashier's money should have increased by 26.98, but it didn't.", cashier.getMoney(),cash);

	}
	
	public void testOneMarketOneCustomerScenario(){
		double cash = cashier.getMoney();
		customer.waiter = waiter;
		customer.cashier = cashier;
		
		waiter.customer = customer;
		waiter.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.pendingOrders.size(),0);
		assertEquals("Cashier should have 0 owingCustomers in it. It doesn't.",cashier.owingCustomers.size(),0);
		assertEquals("Cashier should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		//msgFoodDelivered(Market, Item, Quantity)
		cashier.msgFoodDelivered(market, "Steak", 1);//send the message from the market

		//check postconditions for step 1 and preconditions for step 3
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.payments.size(), 1);
		
		//step 2 of the test 
		//msgHereIsBill(waiter,customer,choice,table)
		cashier.msgHereIsBill(waiter, customer, "Steak", 0);
		
		//check post-conditions of step 2, pre-conditions of step 3
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the MockCustomer's event log reads: "
				+ customer.log.toString(), customer.log.size(), 0);
		
		assertTrue("Cashier should have \"Received bill for table 0\" in it, but it doesn't. Instead, the log reads: " + cashier.log.getLastLoggedEvent().toString(),cashier.log.containsString("Received bill for table 0"));
		
		assertEquals("Cashier should have 1 order in it. It doesn't.", cashier.pendingOrders.size(),1);
		
		//step 3 of the test 
		assertTrue("Cashier's scheduler should have returned true (1 order to calculate), but didn't.", cashier.pickAndExecuteAnAction());
		
		//check post-conditions of step 3, pre-conditions of step 4
		assertTrue("MockWaiter should have \"Received bill\" in its log, but it doesn't. Instead, the log reads: " + waiter.log.getLastLoggedEvent(), waiter.log.containsString("Received bill"));
		assertTrue("MockWaiter should have \"Got money from customer\" in its log, but it doesn't. Instead, the log reads: " + waiter.log.getLastLoggedEvent(), waiter.log.containsString("Got money from customer"));
		
		assertTrue("MockCustomer should have \"Received HereIsYourTotal from cashier. Total = 15.99\" in its log, but it doesn't. Instead, the log reads: " + customer.log.getLastLoggedEvent(), customer.log.containsString("Received HereIsYourTotal from cashier. Total = 15.99"));
		assertTrue("MockCustomer should have \"Received HereIsYourChange from cashier. Change = 0.01\" in its log, but it doesn't. Instead, the log reads: " + customer.log.getLastLoggedEvent(), customer.log.containsString("Received HereIsYourChange from cashier. Change = 0.01"));
		
		assertEquals("Cashier should have no more pending orders, but it does.", cashier.pendingOrders.size(), 0);
		assertTrue("Cashier should have \"Got money from waiter\" in its log, but it doesn't. Instead, it reads: " + cashier.log.getLastLoggedEvent(), cashier.log.containsString("Got money from waiter"));
		assertEquals("Cashier should have no more owingCustomers, but it doesn't.", cashier.owingCustomers.size(), 0);
		cash += 15.99;
		assertEquals("Cashier should have 15.99 more in its money, but it doesn't.", cashier.getMoney(), cash);
		
		//step 4 of the test
		assertTrue("Cashier's scheduler should have returned true (1 bill to calculate), but didn't.", cashier.pickAndExecuteAnAction());
		
		//check post-conditions of step 2
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.payments.size(),0);
		
		cash -= 15.99;
		
		assertEquals("Cashier's money should have decreased by 15.99. It didn't.", cashier.getMoney(),cash);
		
		assertTrue("MockMarket should have logged \"Received cash\", but it reads " + market.log.getLastLoggedEvent().toString(),market.log.containsString("Received cash"));
	}
	
}
