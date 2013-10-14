package restaurant;

import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantGui;
import agent.Agent;
import restaurant.WaiterAgent.Menu;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	private String name;
	private String choice;
	private int hungerLevel = (int)(Math.random()*4);        // determines choice and length of meal
	private int tableNum = 1;
	private double money = 20; // = Math.random() * 10 + 10;
	private double owed = 0;
	Timer timer = new Timer();
	private CustomerGui customerGui;

	// agent correspondents
	private HostAgent host;
	private WaiterAgent wait;
	private Menu myChoices;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, Eating, DoneEating, Billing, Paying, Leaving, Choosing, Ordering};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, seated, doneEating, choosing, doneChoosing, doneBilling, donePaying, doneLeaving, gotFood, pickAgain, cantPay};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name){
		super();
		this.name = name;
		
		if (name.equals("Rich")){
			money = 100;
		}
		if (name.equals("Poor")){
			money = 10;
		}
		if (name.equals("Poorer")){
			money = 8;
		}
		if (name.equals("Poorest")){
			money = 0;
		}
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(HostAgent host) {
		this.host = host;
	}
	public void setWaiter(WaiterAgent wait){
		this.wait = wait;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages

	public void gotHungry() {//from animation
		print("I'm hungry [" + hungerLevel + "]");
		if (!name.equals("Poorest") && !name.equals("Poorer") && !name.equals("Poor")){
			double income = Math.random()*10 + 5;
			money += income;	
		}
		event = AgentEvent.gotHungry;
		stateChanged();
		
	}

	public void msgSitAtTable(Menu m) {
		print("Received msgSitAtTable");
		myChoices = m;
		event = AgentEvent.followHost;
		stateChanged();
	}
	
	public void msgWhatIsYourOrder(Menu m){
		myChoices = m;
		stateChanged();
	}
	
	public void msgPickAgain(){
		event = AgentEvent.pickAgain;
		stateChanged();
	}
	
	public void msgOrderReceived(){
		event = AgentEvent.gotFood;
		stateChanged();
	}
	
	public void msgHereIsCheck(double amount){
		print ("Got check for " + amount);
		owed += amount;
		event = AgentEvent.doneBilling;
		stateChanged();
	}
	
	public void msgHereIsChange(double amount){
		print ("Got " + amount + " in change");
		money += amount;
		event = AgentEvent.donePaying;
		stateChanged();
	}
	
	public void msgjustLeave(){
		event = AgentEvent.donePaying;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine

		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
			
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Choosing;
			ChooseFood();
			return true;
		}
		if (state == AgentState.Choosing && event == AgentEvent.doneChoosing){
			state = AgentState.Ordering;
			OrderFood();
			return true;
		}
		
		if (state == AgentState.Ordering && event == AgentEvent.pickAgain){
			state = AgentState.Choosing;
			ChooseFood();
			return true;
		}
		
		if (state == AgentState.Ordering && event == AgentEvent.gotFood){
		    state = AgentState.Eating;
		    EatFood();
		    return true;
		}

		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.Billing;
			requestBill();
			return true;
		}

		if (state == AgentState.Billing && event == AgentEvent.doneBilling){
			state = AgentState.Paying;
			payBill();
			return true;
		}
		
		if ((state == AgentState.Paying || state == AgentState.Choosing) && event == AgentEvent.donePaying){
			state = AgentState.Leaving;
			leaveTable();
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
		timer.schedule(new TimerTask() {
			public void run() {
				if(wait == null){
					state = AgentState.DoingNothing;
					print("This is taking too long, I'm leaving");
					host.msgCancelSeating(CustomerAgent.this);
					customerGui.DoExitRestaurant();
				}
			}
		},
		5000);//how long to wait before running task
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tableNum);//hack; only one table
	}

	private void EatFood() {
		Do("Eating Food");
		customerGui.setOrder(choice,true);
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				customerGui.setOrder("",true);
				//isHungry = false;
				stateChanged();
			}
		},
		(getHungerLevel() + 1) * 3000);//how long to wait before running task
	}
	
	private void ChooseFood(){
		print("Choosing food");
		timer.schedule(new TimerTask() {
			public void run() {
				if (money > 5.99){
					if (money < 8.99){
						hungerLevel = 2;
						choice = myChoices.getChoice(hungerLevel);
						if (choice.equals("Out")){
							print("Can't afford cheapest item");
							event = AgentEvent.donePaying;
						} else {
							print("Ordering cheapest item");
							event = AgentEvent.doneChoosing;
							customerGui.setOrder(choice,false);
							wait.msgReadytoOrder(CustomerAgent.this);
						}
					} else {
						do {
							choice = myChoices.getChoice(hungerLevel);
							if (choice.equals("Out"))
								hungerLevel = (int)(Math.random() * 4);
						} while (choice.equals("Out"));
						event = AgentEvent.doneChoosing;
						customerGui.setOrder(choice,false);
						wait.msgReadytoOrder(CustomerAgent.this);
					}
					stateChanged();
				} 
				else {
					int choose = (int)(Math.random()*2);
					if (choose != 0){
						print("Can't afford anything");
						event = AgentEvent.donePaying;
					} else {
						do {
							choice = myChoices.getChoice(hungerLevel);
							if (choice.equals("Out"))
								hungerLevel = (int)(Math.random() * 4);
						} while (choice.equals("Out"));
						event = AgentEvent.doneChoosing;
						customerGui.setOrder(choice,false);
						wait.msgReadytoOrder(CustomerAgent.this);
					}
					stateChanged();
				}
			}
		},
		1000);
	}
	
	private void OrderFood(){
		print("I would like " + choice);
		wait.msgOrderReceived(this, choice);
	}
	
	private void requestBill(){
		print ("Check please!");
		wait.msgIWantCheck(this);
	}
	
	private void payBill(){
		if (money-owed >= 0){
			wait.msgHereIsCash(this,owed);
			money -= owed;
			owed = 0;
		} else {
			wait.msgCantPay(this,owed);
		}
	}

	private void leaveTable() {
		Do("Leaving.");
		wait.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
	}

	// Accessors, etc.

	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	public void setTableNum(int n){
		tableNum = n;
	}
	
	public int getTableNum(){
		return tableNum;
	}
	
	public boolean isSeated(){
		if (event == AgentEvent.seated || event == AgentEvent.gotFood)
			return true;
		return false;
	}
}

