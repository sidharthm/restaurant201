package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;

import java.awt.*;

public class HostGui implements Gui {

    private HostAgent agent = null;
    
    public static final int initialX = -20;
    public static final int initialY = -20;

    private int xPos = initialX, yPos = initialY;//default waiter position
    private int xDestination = initialX, yDestination = initialY;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;
    public static final int hostSize = 20;
    public static final int tableSize = 50;

    public HostGui(HostAgent agent) {
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

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + tableSize) & (yDestination == yTable - tableSize)) {
           agent.msgAtTable();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, hostSize, hostSize);
        for (int i = 0; i < agent.getTables().size(); i++){
        	g.setColor(Color.ORANGE);
        	g.fillRect(xTable + (100*i), yTable, tableSize, tableSize);
        }
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer) {
        xDestination = xTable + tableSize;
        yDestination = yTable - tableSize;
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
}
