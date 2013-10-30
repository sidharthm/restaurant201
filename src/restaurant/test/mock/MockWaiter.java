package restaurant.test.mock;

import restaurant.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter{
	
	public EventLog log;
	public MockWaiter(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		log = new EventLog();
	}
	
	@Override
	public void msgHereIsTheBill(double value, int tNum) {
		log.add(new LoggedEvent("Received bill for "+value+" from table "+tNum));
	}

}
