package restaurant;

import agent.Agent;

import java.util.*;

import restaurant.CookAgent.Inventory;

/**
 * Restaurant Market Agent
 */
//A Market is the agent who provides food to the cook in a restaurant
public class MarketAgent extends Agent {
	private String name;
	private Inventory myStock;
	
	private ArrayList<String> delivery;
	Timer timer = new Timer();
	
	private CookAgent cook;

	public MarketAgent(String name) {
		super();

		this.name = name;
		int a = (int)(Math.random()*10);
		myStock = new Inventory(a,a,a,a);
		delivery = new ArrayList<String>();
		print(a + " of all items available");
	}

	public String getName() {
		return name;
	}

	// Messages
	public void msgInventoryLow(CookAgent c, String o){
		cook = c;
		print("Received order to restock " + o);
		delivery.add(o);
		stateChanged();
	}
	
  
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		if (!delivery.isEmpty()){
			sendFood();
			return true;
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void sendFood(){
			final String tempO = delivery.get(0);
			delivery.remove(0);
			timer.schedule(new TimerTask() {
				public void run() {
					int count = 0;
					 while (myStock.useStock(tempO) && count < 5){
						count++;
					}
					if (count > 0)
						cook.msgFoodDelivered(tempO, count);
					else 
						cook.msgNoDelivery();
				}	
			},2000);
	}
	//utilities
	private class Inventory{
		private Map <String,Integer> stock;
		
		public Inventory(Integer s, Integer c, Integer sa, Integer p){
			stock = new HashMap <String,Integer>();
			stock.put("Steak",s);
			stock.put("Chicken",c);
			stock.put("Salad", sa);
			stock.put("Pizza", p);
		}
		
		public void setStock(String item, Integer num){
			if (stock.containsKey(item))
				stock.put(item,num);
		}
		
		public int getStock(String item){
			return stock.get(item);
		}
		
		public boolean useStock(String item){
			if (stock.get(item) > 0){
				stock.put(item, stock.get(item)-1);
				return true;
			}
			return false;
		}
		
		public boolean isEmpty(){
			boolean emp = true;
			for (Map.Entry<String, Integer> m : stock.entrySet()){
				if (m.getValue() > 0)
					emp = false;
			}
			return emp;
		}
	}
}

