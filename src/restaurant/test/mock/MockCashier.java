package restaurant.test.mock;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;

public class MockCashier extends Mock implements Cashier{
	public EventLog log;
	private boolean paid = false;
	
	public MockCashier(String name){
		super(name);
		log = new EventLog();
	}

	@Override
	public void msgHereIsBill(Waiter w, Customer c, String choice, int tNum) {
		log.add(new LoggedEvent("Got a bill"));
		
	}

	@Override
	public void msgCustomerPaid(Customer c, double value) {
		log.add(new LoggedEvent("Customer paid"));
	}

	@Override
	public void msgFoodDelivered(Market m, String it, int qty) {
		log.add(new LoggedEvent("Got receipt from market"));
		if ((this.name.toLowerCase().contains("thief"))){
			if (!paid){
				m.msgNoCash(it, 15.99);
				paid = true;
			} else{
				m.msgHereIsCash(it, 15.99);
			}
		} else
			m.msgHereIsCash(it, 15.99);
	}

}
