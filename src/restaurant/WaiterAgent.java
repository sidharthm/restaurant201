package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Waiter Agent
 */
//A Waiter is responsible for seating customers, taking their orders, and 
//bringing them their desired meal from the cook. 
public class WaiterAgent extends Agent {
	

	private String name;
	private int tableNum = 0;
	
	
	private Semaphore atTable = new Semaphore(0,true);
	
//Other Agents
	private HostAgent host;
	private CustomerAgent customer;
	private CookAgent cook;
	private Order currentOrder;

	public HostGui hostGui = null;

	public WaiterAgent(String name) {
		super();

		this.name = name;
		currentOrder = new Order("",0);
		
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	// Messages

	public void msgSitAtTable(CustomerAgent cust, int tNum){
		customer = cust;
		tableNum = tNum;
		stateChanged();
		
	}
	
	public void msgReadytoOrder(CustomerAgent cust){
		currentOrder.setTable(cust.getTableNum());
		stateChanged();
	}
	
	public void msgOrderReceived(CustomerAgent cust, String choice){
		Do("Okay, I'll bring your " + choice);
		currentOrder.setMeal(choice);
		stateChanged();
	}
	
	public void msgOrderReady(String choice, int tNum){
		currentOrder.setMeal(choice);
		currentOrder.setTable(tNum);
		currentOrder.setReady();
		stateChanged();
	}
	
	public void msgImGood(){
		customer = null;
		stateChanged();
	}

	public void msgLeavingTable(CustomerAgent cust) {
			print(cust + " leaving table" + tableNum);
			host.msgTableCleared(cust,WaiterAgent.this);
			customer = null;
			tableNum = 0;
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
		if (currentOrder.getTable() != 0){
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
			LeaveTable();
			return true;
		}
	
		return false;
	}

	// Actions

	private void seatCustomer(CustomerAgent customer, int tNum) {
		if (hostGui.atStart()){
			customer.setTableNum(tNum);
			customer.msgSitAtTable();
			DoSeatCustomer(customer, tNum);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hostGui.DoLeaveCustomer();
		}
	}
	
	private void getOrder(){
		if(currentOrder.getMeal() == ""){
			hostGui.DoBringToTable(customer);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			customer.msgWhatIsYourOrder();
		}
	}
	
	private void takeOrderToCook(){
		if (currentOrder.getMeal() != ""){
			hostGui.DoGoToCook(customer);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cook.msgHereIsAnOrder(this,currentOrder.getMeal(), currentOrder.getTable());
			currentOrder.setMeal("");
			currentOrder.setTable(0);
			hostGui.DoLeaveCustomer();
		}
	}
	
	private void deliverFood(){
			hostGui.DoBringToTable(customer);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			customer.msgOrderReceived();
			currentOrder.setTable(0);
			currentOrder.setMeal("");
			currentOrder.resetReady();
			print(currentOrder.getMeal() + " " + currentOrder.getTable());
			stateChanged();
	}
	
	private void LeaveTable(){
		if (!hostGui.atStart())
			hostGui.DoLeaveCustomer();
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, int tNum) {
		print("Seating " + customer + " at table " + tNum);
		hostGui.DoBringToTable(customer);

	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
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
