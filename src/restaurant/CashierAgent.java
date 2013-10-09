package restaurant;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Cook Agent
 */
//A Cook is the agent who makes food in a restaurant
public class CashierAgent extends Agent {
	public List<Order> pendingOrders = new ArrayList<Order>();

	private String name;
	private PriceList myPrices;
	Timer timer = new Timer();

	public CashierAgent(String name) {
		super();

		this.name = name;
		myPrices = new PriceList(15.99,10.99,5.99,8.99);
	}

	public String getName() {
		return name;
	}
	
	// Messages

	
	public void msgHereIsBill(WaiterAgent w, String choice, int tNum) {
		print("Received bill for table " + tNum);
		pendingOrders.add(new Order(w,choice, tNum));
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		if (!(pendingOrders.isEmpty())){
			CalculateBill(pendingOrders.get(0));
			pendingOrders.remove(0);
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
		o.getWaiter().msgHereIsTheBill(value, o.getTable());
	}
	//utilities

	private class Order{
		private WaiterAgent myW;
		private int tNum;
		private String meal;
		
		public Order(WaiterAgent w, String m, int t){
			myW = w;
			meal = m;
			tNum = t;
		}
		
		public String getMeal(){
			return meal;
		}
		
		public int getTable(){
			return tNum;
		}
		
		public WaiterAgent getWaiter(){
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
}


