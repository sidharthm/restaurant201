package restaurant.interfaces;


public interface Cashier {
	
	public abstract void msgHereIsBill(Waiter w, Customer c, String choice, int tNum);
	public abstract void msgCustomerPaid(Customer c, double value);
	public abstract void msgFoodDelivered(Market m, String it, int qty);
}
