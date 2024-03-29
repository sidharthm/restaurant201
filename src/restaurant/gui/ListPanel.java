package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class ListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private JPanel addCustomers = new JPanel();
   
    private List<JButton> list = new ArrayList<JButton>();
    private JTextField PersonName = new JTextField("Name");
    private JButton addPersonB = new JButton("Add");
    private JCheckBox PersonState;
    private RestaurantPanel restPanel;
    private String type;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public ListPanel(RestaurantPanel rp, String type) {
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));
        addCustomers.setLayout(new FlowLayout(FlowLayout.LEFT));
        PersonName.addActionListener(this);
        PersonName.setColumns(10);
        addCustomers.add(PersonName);

        PersonState = new JCheckBox();
        PersonState.setVisible(true);
        PersonState.addActionListener(this);
        addCustomers.add(PersonState);
        PersonState.setText("Hungry?");
        PersonState.setEnabled(false);
     
        addPersonB.addActionListener(this);
        addCustomers.add(addPersonB);
        add(addCustomers);

        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
    	if (e.getSource() == PersonName){
    		String result = PersonName.getText();
    		if (result.equals("Name") || result.equals(null)){
    			PersonState.setEnabled(false);    		
    		} else 
    			PersonState.setEnabled(true);
    	}else if (e.getSource() == addPersonB) {
        	// Chapter 2.19 describes showInputDialog()
            //addPerson(JOptionPane.showInputDialog("Please enter a name:"));
    		String result = PersonName.getText();
    		if (!result.equals("Name"))
    			addPerson(PersonName.getText());
        }
        else {
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
        	for (JButton temp:list){
                if (e.getSource() == temp)
                    restPanel.showInfo(type, temp.getText());
            }
        }
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name) {
        if (name != null) {
            JButton button = new JButton(name);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
            list.add(button);
            view.add(button);
            restPanel.addPerson(type, name);//puts customer on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
            if (PersonState.isSelected()){
            	restPanel.makeHungry(name);
    		} 
        	PersonState.setEnabled(false);
        	PersonName.setText("Name");
        }
    }
}
