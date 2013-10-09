package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentEvent;
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
	private boolean breakable = false;
	private boolean onBreak = false;
	public enum CustState {Waiting,Seated, Ordering, WaitingForFood, Delivering, WaitingForBill, WaitingForCashier, Paying, Done,Leaving, OrderAgain, None};
	
	private int tableNum = 0;
	private ArrayList<myCustomer> customers = new ArrayList<myCustomer>();
	private Menu todaysMenu;
	private Timer breakTimer = new Timer();
	
	private Semaphore atTable = new Semaphore(0,true);
	
//Other Agents
	private HostAgent host;
	private myCustomer customer;
	private CookAgent cook;
	private CashierAgent cashier;
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
		customers.add(new myCustomer(cust,tNum));
		if (customers.size() == 1)
				customer = customers.get(0);
		if (name.equals("OnB"))
			WantToBreak();
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
	
	public void msgOutOfChoice(String i){
		todaysMenu.removeChoice(i);
		for (myCustomer m:customers){
			if (m.getOrder().getMeal().equals(i)){
				print (m.getCustomer() + " needs to order again");
				m.setState(CustState.OrderAgain);
			}
		}
		stateChanged();
	}
	
	public void msgOrderReady(String choice, int tNum){
		if (tNum == customer.getTable()){
			customer.getOrder().setReady();
			customer.setState(CustState.Delivering);
		} else {
			for (myCustomer m :customers){
				if (m.getTable() == tNum){
					m.getOrder().setReady();
					m.setState(CustState.Delivering);
				}
			}
		}
		stateChanged();
	}
	
	public void msgIWantCheck(CustomerAgent c){
		for (myCustomer m: customers){
			if (m.getCustomer() == c){
				m.setState(CustState.WaitingForBill);
				stateChanged();
			}
		}
	}
	
	public void msgHereIsTheBill(double value, int tNum){
		print("Got bill for table " + tNum);
		if (tNum == customer.getTable()){
			customer.chargeBill(value);
			customer.setState(CustState.Paying);
			stateChanged();
		} else { 
			for (myCustomer m: customers){
				if (m.getTable() == tNum){
					m.chargeBill(value);
					m.setState(CustState.Paying);
					stateChanged();
				}
			}
		}
	}
	
	public void msgHereIsCash(CustomerAgent c, double value){
		print("Got " + value + " from " + c);
		for (myCustomer m: customers){
			if (m.getCustomer() == c){
				m.payBill(value);
				m.getCustomer().msgHereIsChange(0.01);
				m.setState(CustState.Done);
			}
		}
	}
	
	public void msgImGood(){
		customer.setState(CustState.Done); //Implemented later, in case we need to return to the customer
		stateChanged();
	}
	

	public void msgLeavingTable(CustomerAgent cust) {
		if (customer.getCustomer() == cust){
			print(cust + " leaving table " + cust.getTableNum());
			customer.setState(CustState.Leaving);
		} else {
			for (myCustomer m: customers){
				if (m.getCustomer() == cust)
					m.setState(CustState.Leaving);
			}
		}
		stateChanged();
	}
	
	public void msgGoOnBreak(){
		breakable = true;
		stateChanged();
	}
	
	public void WantToBreak(){//from GUI
		print("Can I take a break?");
		host.msgIWantABreak(this);
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
			if (onBreak)
				return true;
			if (customer.getState() == CustState.Waiting){
				seatCustomer(customer.getCustomer(),customer.getTable());
				return true;
			} else if (customer.getState() == CustState.Seated || customer.getState() == CustState.OrderAgain){
				getOrder();
				return true;
			} else if (customer.getState() == CustState.Ordering){
				takeOrderToCook();
				return true;
			} else if (customer.getState() == CustState.Delivering){
				deliverFood();
				return true;
			} else if (customer.getState() == CustState.WaitingForBill){
				getBill();
				return true;
			} else if (customer.getState() == CustState.Paying){
				takeBill();
				return true;
			} else if (customer.getState() == CustState.Done){
				LeaveTable();
			} else if (customer.getState() == CustState.Leaving){
				host.msgTableCleared(this,customer.getTable(),customers.size());
				customers.remove(customer);
				if (customers.isEmpty())
					customer = null;
				LeaveTable();
				return true;
			}
			customer = m;
		}
		if (breakable){
			goOnBreak();
			return true;
		}
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
		if (customer.getState() == CustState.OrderAgain){
			customer.getCustomer().msgPickAgain();
		}
		customer.getCustomer().msgWhatIsYourOrder(todaysMenu);
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
			customer.setState(CustState.None);
			customer.getCustomer().msgOrderReceived();
			stateChanged();
	}
	
	private void getBill(){
		if (!hostGui.atStart()){
			hostGui.DoLeaveCustomer();
		} else{
			cashier.msgHereIsBill(this, customer.getOrder().getMeal(), customer.getTable());
			customer.setState(CustState.WaitingForCashier);
		}
	}
	
	private void takeBill(){
		customer.setState(CustState.WaitingForCashier);
		hostGui.DoBringToTable(customer.getCustomer());
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.getCustomer().msgHereIsCheck(customer.getMoneyOwed());
	}
	
	private void LeaveTable(){
		if (!hostGui.atStart())
			hostGui.DoLeaveCustomer();
	}
	
	private void goOnBreak(){
		hostGui.DoLeaveCustomer();
		onBreak = true;
		print("I'm on break");
		breakTimer.schedule(new TimerTask() {
			public void run() {
				print("I'm done with my break");
				onBreak = false;
				breakable  = false;
				stateChanged();
				host.msgImBack(WaiterAgent.this);
			}
		},
		10000);
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
	
	public void setCashier(CashierAgent c){
		
		cashier = c;
	}
	
	public int getNumTotalTables(){
		return host.getNumTables();
	}
	
	private class myCustomer{
		CustomerAgent c;
		Order o;
		private double moneyOwed;
		int t;
		private CustState s = CustState.Waiting;
		public myCustomer(CustomerAgent cust, int table){
			c = cust;
			t = table;
			moneyOwed = 0;
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
		public void chargeBill(double v){
			moneyOwed += v;
		}
		public void payBill(double v){
			moneyOwed -= v;
		}
		public double getMoneyOwed(){
			return moneyOwed;
		}
	}
	
	public class Menu{
		private ArrayList<String> choices;
		public Menu(){
			choices = new ArrayList<String>();
			choices.add("Steak");//0
			choices.add("Chicken");//1
			choices.add("Salad");//2
			choices.add("Pizza");//3
		}
		public String getChoice(int n){
			if (n < choices.size()){
				String c = choices.get(n);
				return c;
			} else 
				return "Out";
		}
		public void removeChoice(String c){
			int n = choices.indexOf(c);
			choices.set(n, "Out");
		}
		public void addChoice(String c){
			for (String s: choices){
				if (s.equals(c)){
					int n = choices.indexOf(c);
					String item = "Out"; 
					switch (n){
						case 0: item = "Steak"; break;
						case 1: item = "Chicken"; break;
						case 2: item = "Salad"; break;
						case 3: item = "Pizza"; break;
					}
					choices.set(n, item);
				}
			}
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
	
