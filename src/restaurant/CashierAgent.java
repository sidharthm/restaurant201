package restaurant;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Cook Agent
 */
//A Cook is the agent who makes food in a restaurant
public class CookAgent extends Agent {
	public List<Order> pendingOrders = new ArrayList<Order>();
	public List<Order> completeOrders = new ArrayList<Order>();

	private String name;
	private Inventory myStock = new Inventory(1,1,1,1);
	private Semaphore cooking = new Semaphore(0,true);
	Timer timer = new Timer();
	
	private MarketAgent market;

	public CookAgent(String name) {
		super();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List getOrders() {
		return pendingOrders;
	}
	
	public Inventory getInventory(){
		return myStock;
	}
	
	public void addMarket(MarketAgent m){
		market = m;
	}
	// Messages

	
	public void msgHereIsAnOrder(WaiterAgent w, String choice, int tNum) {
		print("Received order for " + choice);
		pendingOrders.add(new Order(w,choice, tNum));
		stateChanged();
	}
	
	public void msgFoodDelivered(String o, int n){
		print("Received delivery of " + n + " " + o);
		int num = myStock.getStock(o);
		num += n;
		myStock.setStock(o,num);
	}
	
	public void msgNoDelivery(){
		print("Switching markets");
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		if (!(pendingOrders.isEmpty())){
			CookOrder(pendingOrders.get(0));
			pendingOrders.remove(0);
			return true;
		} else if (!completeOrders.isEmpty()){
			Order send = completeOrders.get(0);
			send.getWaiter().msgOrderReady(send.getMeal(),send.getTable());
			send = null;
			completeOrders.remove(0);
			return true;
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
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
}

