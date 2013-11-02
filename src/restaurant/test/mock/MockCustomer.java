package restaurant.test.mock;


import restaurant.WaiterAgent.Menu;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public Waiter waiter;
	public EventLog log;

	public MockCustomer(String name) {
		super(name);
		log = new EventLog();
	}

	@Override
	public void msgHereIsCheck(double total) {
		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ total));

		if(this.name.toLowerCase().contains("thief")){
			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
			waiter.msgCantPay(this, total);

		}else {
			//test the normative scenario
			waiter.msgHereIsCash(this, total);
		}
	}

	@Override
	public void msgHereIsChange(double total) {
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ total));
	}

}
