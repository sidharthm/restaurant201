package restaurant;

import agent.Agent;
import restaurant.gui.WaiterGUI;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Waiter Agent
 */
//A Waiter is responsible for seating customers, taking their orders, and 
//bringing them their desired meal from the cook. 
public class WaiterAgent extends Agent {
	

	private String name;
	public enum CustState {Waiting,Seated, Ordering, WaitingForFood, Delivering, Done,Leaving};
	
	private int tableNum = 0;
	private ArrayList<myCustomer> customers = new ArrayList<myCustomer>();
	private Menu todaysMenu;
	
	private Semaphore atTable = new Semaphore(0,true);
	
//Other Agents
	private HostAgent host;
	private myCustomer customer;
	private CookAgent cook;
	private Order currentOrder;

	public WaiterGUI hostGui = null;

	public WaiterAgent(String name) {
		super();

		this.name = name;
		currentOrder = new Order("",0);
		todaysMenu = new Menu();
		
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	// Messages

	public void msgSitAtTable(CustomerAgent cust, int tNum){
		customers.add(0, new myCustomer(cust,tNum));
		customer = customers.get(0);
		tableNum = tNum;
		stateChanged();
		
	}
	
	public void msgReadytoOrder(CustomerAgent cust){
		print("Going to " + cust + " to get their order");
		stateChanged();
	}
	
	public void msgOrderReceived(CustomerAgent cust, String choice){
		Do("Okay, I'll bring your " + choice);
		customer.setState(CustState.Ordering);
		customer.makeOrder(choice, customer.getTable());
		stateChanged();
	}
	
	public void msgOrderReady(String choice, int tNum){
		customer.getOrder().setReady();
		customer.setState(CustState.Delivering);
		stateChanged();
	}
	
	public void msgImGood(){
		customer.setState(CustState.Leaving);
		stateChanged();
	}

	public void msgLeavingTable(CustomerAgent cust) {
			print(cust + " leaving table" + tableNum);
			if (customer.getCustomer() == cust){
					customers.remove(customer);
			}
			stateChanged();
	}

	public void msgAtTable() {//from animation
		atTable.release();// = true;
		stateChanged();
	}
	
	public void msgAtCook(){//from animation
		atTable.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for (myCustomer m:customers){
			customer = m;
			if (customer.getState() == CustState.Waiting){
				seatCustomer(customer.getCustomer(),customer.getTable());
				return true;
			} else if (customer.getState() == CustState.Seated){
				getOrder();
				return true;
			} else if (customer.getState() == CustState.Ordering){
				takeOrderToCook();
				return true;
			} else if (customer.getState() == CustState.Delivering){
				deliverFood();
				return true;
			} else if (customer.getState() == CustState.Leaving){
				host.msgTableCleared(this);
				LeaveTable();
				return true;
			}
		}
		/*if (currentOrder.getTable() != 0){
			if (currentOrder.getMeal() == ""){
				getOrder();
			}else{
				if (currentOrder.getReadiness())
					deliverFood();
				else 
					takeOrderToCook();
			}
			return true;
		} else if (!(customer == null) && !customer.isSeated()) {
			seatCustomer(customer, tableNum);
			return true;
		} else if (customer == null){
			host.msgTableCleared(this);
			tableNum = 0;
			LeaveTable();
			return true;
		}*/
	
		return false;
	}

	// Actions

	private void seatCustomer(CustomerAgent cust, int tNum) {
		if (hostGui.atStart()){
			cust.setTableNum(tNum);
			cust.msgSitAtTable(todaysMenu);
			DoSeatCustomer(cust, tNum);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			customer.setState(CustState.Seated);
		}
	}
	
	private void getOrder(){
		hostGui.DoBringToTable(customer.getCustomer());
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.getCustomer().msgWhatIsYourOrder();
	}
	
	private void takeOrderToCook(){
		print("Taking order to cook");
		hostGui.DoGoToCook();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cook.msgHereIsAnOrder(this,customer.getOrder().getMeal(), customer.getOrder().getTable());
		customer.setState(CustState.WaitingForFood);
		hostGui.DoLeaveCustomer();
	}
	
	private void deliverFood(){
			hostGui.DoGoToCook();
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hostGui.setPlate(customer.getOrder().getMeal());
			hostGui.DoBringToTable(customer.getCustomer());
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hostGui.setPlate("");
			customer.setState(CustState.Done);
			customer.getCustomer().msgOrderReceived();
			stateChanged();
	}
	
	private void LeaveTable(){
		if (!hostGui.atStart())
			hostGui.DoLeaveCustomer();
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent cust, int tNum) {
		print("Seating " + cust + " at table " + tNum);
		hostGui.DoBringToTable(cust);

	}

	//utilities

	public void setGui(WaiterGUI gui) {
		hostGui = gui;
	}

	public WaiterGUI getGui() {
		return hostGui;
	}
	
	public int getTableNum(){
		return tableNum;
	}
	
	public void setCook(CookAgent c){

		cook = c;
	}
	
	public void setHost(HostAgent h){
		host = h;
	}
	
	public int getNumTotalTables(){
		return host.getNumTables();
	}
	
	private class myCustomer{
		CustomerAgent c;
		Order o;
		int t;
		private CustState s = CustState.Waiting;
		public myCustomer(CustomerAgent cust, int table){
			c = cust;
			t = table;
		}
		public CustomerAgent getCustomer(){
			return c;
		}
		public int getTable(){
			return t;
		}
		public Order getOrder(){
			return o;
		}
		public CustState getState(){
			return s;
		}
		public void setTable(int nt){
			t = nt;
		}
		public void setState(CustState ns){
			s = ns;
		}
		public void makeOrder(String c, int t){
			o = new Order(c,t);
		}
	}
	
	public class Menu{
		private ArrayList<String> choices;
		public Menu(){
			choices = new ArrayList<String>();
			choices.add("Steak");
			choices.add("Chicken");
			choices.add("Salad");
			choices.add("Pizza");
		}
		public String getChoice(int n){
			if (n < choices.size()){
				String c = choices.get(n);
				return c;
			} else 
				return "Chicken";
		}
	}
	
	private class Order{

		private int tNum;
		private String meal;
		private boolean ready;
		
		public Order(String m, int t){
			meal = m;
			tNum = t;
			ready = false;
		}
		
		public String getMeal(){
			return meal;
		}
		
		public int getTable(){
			return tNum;
		}
		
		public void setMeal(String c){
			meal = c;
		}
		
		public void setTable(int t){
			tNum = t;
		}
		public boolean getReadiness(){
			return ready;
		}
		public void setReady(){
			ready = true;
		}
		public void resetReady(){
			ready = false;
		}
	}
}
	
