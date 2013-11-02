package restaurant.interfaces;

import restaurant.interfaces.Customer;

public interface Waiter {
	
	public abstract void msgHereIsTheBill(double value, int tNum);
	public abstract void msgHereIsCash(Customer c, double value);
	public abstract void msgCantPay(Customer c, double value);
	
}
