package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import java.io.IOException;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
	//JFrame animationFrame = new JFrame("Restaurant Animation");
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JCheckBox stateCB;//part of infoLabel
    private JButton pauseButton;

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    private JPanel myPanel;
    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 450;
        int WINDOWY = 700;
        int BOUND = 50; 
    	
    	setBounds(BOUND, BOUND, WINDOWX, WINDOWY);

    	setLayout(new BoxLayout((Container) getContentPane(), BoxLayout.Y_AXIS));
    	//setLayout(new BoxLayout());

        Dimension restDim = new Dimension((int) (WINDOWX), (int) (WINDOWY*.25));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension((int) (WINDOWX), (int) (WINDOWY*.1));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);

        infoPanel.setLayout(new GridLayout(1, 2, 30, 0));
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
        infoPanel.add(infoLabel);
        infoPanel.add(stateCB);
        add(infoPanel);
        
        Dimension animDim = new Dimension((int)(WINDOWX), (int)(WINDOWY*0.45));
        animationPanel.setPreferredSize(animDim);
        animationPanel.setMinimumSize(animDim);
        animationPanel.setMaximumSize(animDim);
        add(animationPanel);
        
        //setup the user's panel
        Dimension myDim = new Dimension((int) (WINDOWX),(int) (WINDOWY*0.2));
        myPanel = new JPanel();
        myPanel.setPreferredSize(myDim);
        myPanel.setMinimumSize(myDim);
        myPanel.setMaximumSize(myDim);
        myPanel.setBorder(BorderFactory.createTitledBorder("Sidharth Menon"));
        
        
        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(this);
        BufferedImage myPic = null;
        try {
        	myPic = ImageIO.read(new File ("..\\restaurant_sidhartm\\src\\restaurant\\gui\\myimage.jpg"));
        } catch (IOException e){
        }
        ImageIcon myIcon = new ImageIcon(myPic);
        JLabel myLabel = new JLabel(myIcon);
        myPanel.add(myLabel);
        myPanel.add(pauseButton);
        add(myPanel);
        
        
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {
        stateCB.setVisible(true);
        currentPerson = person;

        if (person instanceof CustomerAgent) {
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        } else if (person instanceof WaiterAgent){
        	WaiterAgent waiter = (WaiterAgent) person;
        	stateCB.setText("Break?");
        	stateCB.setSelected(false);
        	stateCB.setEnabled(true);
        	infoLabel.setText(
               "<html><pre>     Name: " + waiter.getName() + " </pre></html>");
        }
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry();
                stateCB.setEnabled(false);
            } else if (currentPerson instanceof WaiterAgent){
            	WaiterAgent w = (WaiterAgent) currentPerson;
            	w.getGui().WantBreak();
            	stateCB.setEnabled(false);
            }
        } else if (e.getSource() == pauseButton){
        	if (restPanel.getRunning()){
        		restPanel.pauseRestaurant();
        	}else{ 
        		restPanel.resumeRestaurant();
        	}
        }
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerAgent c) {
        if (currentPerson instanceof CustomerAgent) {
            CustomerAgent cust = (CustomerAgent) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("csci201 Restaurant");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
