package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostAgent extends Agent {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<CustomerAgent> waitingCustomers
	= new ArrayList<CustomerAgent>();
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	public List<WaiterAgent> availableWaiters = new ArrayList<WaiterAgent>();

	private String name;
	//private Semaphore atTable = new Semaphore(0,true);

	public HostGui hostGui = null;

	public HostAgent(String name) {
		super();

		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}
	
	public int getNumTables(){
		return NTABLES;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	
	public List getAvailableWaiters() {
		return availableWaiters;
	}
	// Messages

	
	public void msgIWantFood(CustomerAgent cust) {
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgTableCleared(CustomerAgent cust, WaiterAgent wait) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print("clearing " + cust);
				print(cust + " leaving " + table);
				table.setUnoccupied();
				availableWaiters.add(wait);
				print(availableWaiters.size() + " waiters ready");
				stateChanged();
			}
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer, AND waiter
            so that table is unoccupied and customer is waiting. AND the waiter is not at a table
            If so seat him at the table.
		 */		
		for (Table table : tables) {
			if (!table.isOccupied()) {
				if (!waitingCustomers.isEmpty()) {
					if (!availableWaiters.isEmpty()){
						print("Found waiter for " + waitingCustomers.get(0));
						table.setOccupant(waitingCustomers.get(0));
						waitingCustomers.get(0).setWaiter(availableWaiters.get(0));
						availableWaiters.get(0).msgSitAtTable(waitingCustomers.get(0), table.getNumber());//action
						availableWaiters.remove(0);
						waitingCustomers.remove(0);
						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
				}
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	
	public void addWaiter(WaiterAgent w){
		w.setHost(this);
		w.startThread();
		availableWaiters.add(w);
	}

	private class Table {
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
}

