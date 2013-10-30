package restaurant;

import agent.Agent;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant.gui.CookGui;

/**
 * Restaurant Cook Agent
 */
//A Cook is the agent who makes food in a restaurant
public class CookAgent extends Agent {
	public List<Order> pendingOrders = new ArrayList<Order>();
	public List<Order> completeOrders = new ArrayList<Order>();
	public List<MarketAgent> myMarkets = new ArrayList<MarketAgent>();

	private String name;
	private Inventory myStock = new Inventory(0,0,0,0);
	private Semaphore moving = new Semaphore(0,true);
	private CookGui gui;
	Timer timer = new Timer();
	
	//private MarketAgent market;

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
		myMarkets.add(m);
	}
	
	public void setGui(CookGui g){
		gui = g;
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
		print("Switching to " + myMarkets.get(1).getName());
		MarketAgent m = myMarkets.get(0);
		myMarkets.remove(0);
		myMarkets.add(m);
		stateChanged();
	}
	
	public void msgMarketOut(MarketAgent m){
		myMarkets.remove(m);
	}
	
	public void msgDestReached(){
		moving.release();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//Dealing with Orders
		if (!(pendingOrders.isEmpty())){
			CookOrder(pendingOrders.get(0));
			pendingOrders.remove(0);
			return true;
		} else if (!completeOrders.isEmpty()){
			sendOrder();
			return true;
		} else {
			//Inventory Check
			String [] items = {"Steak","Chicken","Pizza","Salad"};
			for (int i = 0; i < items.length; i++){
				int amount = myStock.getStock(items[i]);
				if (amount < 2){
					reStock(items[i]);
				}
			}
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void CookOrder(Order o){
		gui.DoCheckOrders();
		try{
			moving.acquire();
		}  catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gui.setOrder(o.getMeal(), false);
		gui.DoGoToGrill();
		try{
			moving.acquire();
		}  catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (myStock.useStock(o.getMeal())){
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

			final String temp2 = o.getMeal();
			timer.schedule(new TimerTask() {
				public void run() {
					print("Done cooking current meal");
					gui.setOrder(temp2, true);
					gui.DoCheckOrders();
					try{
						moving.acquire();
					}  catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gui.DoReturnToStart();
					completeOrders.add(new Order(temp.getWaiter(), temp.getMeal(),temp.getTable()));
					stateChanged();
				}
			},
			timeToRun);
		} else {
			print("We're out of " + o.getMeal());
			o.getWaiter().msgOutOfChoice(o.getMeal());
		}
	}
	
	private void sendOrder(){
		Order send = completeOrders.get(0);
		send.getWaiter().msgOrderReady(send.getMeal(),send.getTable());
		send = null;
		completeOrders.remove(0);
	}
	  
	private void reStock(String food){
		myMarkets.get(0).msgInventoryLow(this, food);
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
	public class Inventory{
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
	}
}

