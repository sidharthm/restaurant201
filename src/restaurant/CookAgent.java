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

	private String name;
	private Semaphore cooking = new Semaphore(0,true);


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

	
	public void msgHereIsAnOrder(WaiterAgent wait, String choice, int tNum) {
		pendingOrders.add(new Order(wait, choice, tNum));
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void CookOrder(Order o){
		//STUB
	}
	//utilities

	private class Order{
		WaiterAgent orderer;
		String dishOrdered;
		int tableNumber;
		//STUB
		public Order(WaiterAgent w, String d, int t){
			//IMPLEMENT ME 
		}
	}
}

