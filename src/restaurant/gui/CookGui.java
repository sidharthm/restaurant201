package restaurant.gui;

import restaurant.CookAgent;

import java.awt.*;

public class CookGui implements Gui{

	private CookAgent agent = null;
	
	//RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private String order;

	public static final int initialX = 400;
	public static final int initialY = 20;
	public static final int platesX = 360;
	public static final int platesY = 20;
	public static final int grillX = 400;
	public static final int grillY = 100;
	public static final int grillSize = 10;
	public static final int cookSize = 20;

	public CookGui(CookAgent c){
		agent = c;
		xPos = initialX;
		yPos = initialY;
		xDestination = initialX;
		yDestination = initialY;
		order = "";
		//maitreD = m;
		//this.gui = gui;
	}

	public void updatePosition() {
		if (xPos < xDestination)
			xPos+=5;
		else if (xPos > xDestination)
			xPos-=5;

		if (yPos < yDestination)
			yPos+=5;
		else if (yPos > yDestination)
			yPos-=5;
		if (((xPos == platesX+20) && (yPos == platesY))||((xPos == grillX) && (yPos == grillY+20))){
			agent.msgDestReached();
		}
		if ((xPos == initialX) && yPos == initialY){
			order = "";
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.BLUE);
		g.fillRect(xPos, yPos, cookSize, cookSize);
		g.setColor(Color.gray);
		g.fillRect(grillX, grillY, grillSize*2, grillSize);
		g.setColor(Color.darkGray);
		g.fillOval(platesX, platesY, 20, 20);
		g.drawString(order,xPos,yPos);
	}
	
	public boolean isPresent(){
		return true;
	}

	public void DoCheckOrders() {
		xDestination = platesX+20;
		yDestination = platesY;
	}
	
	public void DoGoToGrill(){
		xDestination = grillX;
		yDestination = grillY+20;
	}
	
	public void DoReturnToStart(){
		xDestination = initialX;
		yDestination = initialY;
	}
	
	public void setOrder(String c,boolean received){
		if (c.length() > 2){
			order = c.substring(0,2);
			if (!received)
				order += "?";
		}else 
			order = "";
	}
}
