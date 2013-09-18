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
	private int tableNum = 1;
	
	private Semaphore atTable = new Semaphore(0,true);
	
//Other Agents
	private HostAgent host;
	private CustomerAgent customer;

	public HostGui hostGui = null;

	public WaiterAgent(String name) {
		super();

		this.name = name;
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
		
	}
	
	public void msgCustomerOrder(CustomerAgent cust, String choice){
		
	}
	
	public void msgOrderReady(/*Order is ready*/){
	}

	public void msgLeavingTable(CustomerAgent cust) {
		print(cust + " leaving table" + tableNum);
		customer = null;
		host.msgTableCleared(cust,this);
		stateChanged();
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */		
		if (!(customer == null)) {
			seatCustomer(customer, tableNum);//the action
			return true;//return true to the abstract agent to reinvoke the scheduler.
		}
	
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
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

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, int tNum) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at table" + tNum);
		hostGui.DoBringToTable(customer);

	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	
	private class Order{
	
	}
}

	/*private class Table {
		CustomerAgent occupiedBy;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
		
		public int getNumber(){
			return tableNumber;
		}
	}
}*/

