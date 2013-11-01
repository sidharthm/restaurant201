package restaurant;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.test.mock.EventLog;
import restaurant.test.mock.LoggedEvent;

/**
 * Restaurant Cook Agent
 */
//A Cook is the agent who makes food in a restaurant
public class CashierAgent extends Agent implements Cashier{
	public List<Order> pendingOrders = Collections.synchronizedList(new ArrayList<Order>());
	public List<MarketPayment> payments = Collections.synchronizedList(new ArrayList<MarketPayment>());
	public Map<Customer, Double> owingCustomers = Collections.synchronizedMap(new HashMap<Customer,Double>());

	private String name;
	private double money;
	private PriceList myPrices;
	public EventLog log;
	
	Timer timer = new Timer();

	public CashierAgent(String name) {
		super();

		this.name = name;
		myPrices = new PriceList(15.99,10.99,5.99,8.99);
		money = (int)(100*4*Math.random()) + 50;
		log = new EventLog();
	}

	public String getName() {
		return name;
	}
	
	// Messages

	
	public void msgHereIsBill(Waiter w, Customer c, String choice, int tNum) {
		print("Received bill for table " + tNum);
		log.add(new LoggedEvent("Received bill for table " + tNum));
		pendingOrders.add(new Order(w, c, choice, tNum));
		stateChanged();
	}
	
	public void msgCustomerPaid(Customer c, double value){
		owingCustomers.remove(c);
		money += value;
		print("Got money from waiter, I now have " + money);
	}
	
	public void msgFoodDelivered(MarketAgent m, String it, int qty){
		payments.add(new MarketPayment(m, it, qty));
		print("Handling payment to market");
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if (!(pendingOrders.isEmpty())){
			CalculateBill(pendingOrders.get(0));
			pendingOrders.remove(0);
			return true;
		} else if (!(payments.isEmpty())){
			ComputePayment(payments.get(0));
			payments.remove(0);
			return true;
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void CalculateBill(Order o){
		double value = myPrices.compute(o.getMeal());
		if (owingCustomers.containsKey(o.getCustomer())){
			print("He owes from last time");
			value += owingCustomers.get(o.getCustomer());
		}
		owingCustomers.put(o.getCustomer(), value);
		o.getWaiter().msgHereIsTheBill(value, o.getTable());
	}
	private void ComputePayment(MarketPayment p){
		double value = myPrices.compute(p.getItem());
		value *= p.getQty();
		money -= value;
		p.getMarket().msgHereIsCash(p.getItem(), value);
	}
	//utilities
	
	public List<Order> getOrders(){
		return pendingOrders;
	}

	private class Order{
		private Waiter myW;
		private Customer myC;
		private int tNum;
		private String meal;
		
		public Order(Waiter w, Customer c, String m, int t){
			myW = w;
			meal = m;
			myC = c;
			tNum = t;
		}
		
		public String getMeal(){
			return meal;
		}
		
		public int getTable(){
			return tNum;
		}
		
		public Customer getCustomer(){
			return myC;
		}
		
		public Waiter getWaiter(){
			return myW;
		}
	}
	
	private class PriceList{
		private Map <String, Double> prices;
		public PriceList(double a, double b, double c, double d){
			prices = new HashMap<String,Double>();
			prices.put("Steak", a);
			prices.put("Chicken", b);
			prices.put("Salad", c);
			prices.put("Pizza", d);
		}
		public double compute(String o){
			return prices.get(o);
		}
		public void setPrice(String o, double p){
			prices.put(o, p);
		}
	}
	
	private class MarketPayment{
		private int qty;
		private String item;
		private MarketAgent market;
		
		public MarketPayment(MarketAgent agent, String i, int q){
			qty = q;
			item = i;
			market = agent;
		}
		
		public MarketAgent getMarket(){
			return market;
		}
		public String getItem(){
			return item;
		}
		public int getQty(){
			return qty;
		}
	}
}


