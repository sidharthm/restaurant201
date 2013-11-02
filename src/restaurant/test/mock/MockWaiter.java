package restaurant.test.mock;

import restaurant.CashierAgent;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter{
	
	public EventLog log;
	public CashierAgent cashier;
	public Customer customer;
	
	public MockWaiter(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		log = new EventLog();
	}
	
	@Override
	public void msgHereIsTheBill(double value, int tNum) {
		log.add(new LoggedEvent("Received bill"));
		customer.msgHereIsCheck(value);
	}
	
	public void msgHereIsCash(Customer c, double value){
		log.add(new LoggedEvent("Got money from customer"));
		cashier.msgCustomerPaid(c, value);
		c.msgHereIsChange(0.01);
	}
	
	public void msgCantPay(Customer c, double value){
		log.add(new LoggedEvent("Customer can't pay"));
	}

}
