package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import java.awt.*;

public class WaiterGUI implements Gui {

    private WaiterAgent agent = null;
    
    public static final int initialX = -20;
    public static final int initialY = -20;
    public static final int cookX = 450;
    public static final int cookY = 100;

    private int xPos = initialX, yPos = initialY;//default waiter position
    private int xDestination = initialX, yDestination = initialY;//default start position
    private int tableOffset = 0;

    public static final int xTable = 100;
    public static final int yTable = 125;
    public static final int hostSize = 20;
    public static final int tableSize = 50;

    public WaiterGUI(WaiterAgent agent) {
        this.agent = agent;
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;

        if (xPos == xDestination && yPos == yDestination){
        		if ((xDestination == xTable + tableOffset + tableSize) & (yDestination == yTable - tableSize)) {
        			agent.msgAtTable();
        		} else if (xDestination == cookX && yDestination == cookY){
        			agent.msgAtCook();
        		}
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, hostSize, hostSize);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer) {
    	tableOffset = ((customer.getTableNum()-1) * 100);
        xDestination = (xTable + tableOffset) + tableSize;
        yDestination = yTable - tableSize;
    }
    
    public void DoGoToCook(){
    	xDestination = cookX;
    	yDestination = cookY;
    }

    public void DoLeaveCustomer() {
        xDestination = initialX;
        yDestination = initialY;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
    public boolean atStart(){
    		if (xPos == initialX && yPos == initialY)
    			return true;
    		return false;
    }
    public boolean atCook(){
    	if (xPos == cookX && yPos == cookY)
    		return true;
    	return false;
    }
}
