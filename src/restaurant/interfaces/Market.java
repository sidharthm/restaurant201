package restaurant.interfaces;

import restaurant.CookAgent;

public interface Market {
	public abstract void msgInventoryLow(CookAgent c, String o);
	public abstract void msgHereIsCash(String s, double val);
	public abstract void msgNoCash(String s, double val);
}
