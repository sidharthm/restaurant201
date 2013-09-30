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
	private Semaphore cooking = new Semaphore(0,true);
	Timer timer = new Timer();

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
	// Messages

	
	public void msgHereIsAnOrder(WaiterAgent w, String choice, int tNum) {
		print("Received order for " + choice);
		pendingOrders.add(new Order(w,choice, tNum));
		stateChanged();
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
	private void CookOrder(Order o){
		final Order temp = o;
		int timeToRun = 0;
		switch (o.getMeal()){
			case "Steak":
				timeToRun = 3000;
				break;
			case "Chicken":
				timeToRun = 2000;
				break;
			case "Salad":
				timeToRun = 1000;
				break;
			case "Pizza":
				timeToRun = 4000;
				break;
		}
		timer.schedule(new TimerTask() {
			public void run() {
				print("Done cooking current meal");
				completeOrders.add(new Order(temp.getWaiter(), temp.getMeal(),temp.getTable()));
				stateChanged();
			}
		},
		timeToRun);
		//completeOrders.add(new Order(o.getWaiter(), o.getMeal(),o.getTable()));
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
}

