/**
 * ChatPanel
 * 
 * Created 2018-02-19
 */
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import Buttons.*;

/**
 * Class for handling input from the user
 * @author Gustav
 *
 */
public class ChatPanel extends JPanel{
	private SendButton mySendButton;
	private SettingsButton mySettingsButton;
	private JOptionPane myOptionPane;
	private Color color;
	private String name;
	private ChatObservable myObservable;
	private JTextArea myTextArea;
	
	/**
	 * Constructor
	 */
	public ChatPanel(){
		/* Fields */
		this.setVisible(true);
		myTextArea = new JTextArea();
		myObservable = new ChatObservable();
		mySendButton = new SendButton();
		mySettingsButton = new SettingsButton();	
		
		/* Add listeners */
		mySendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String newText = myTextArea.getText();
				myObservable.sendUpdate(newText);
			}
		});	
		
		/* Make UI */
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(myTextArea);
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new GridLayout(1, 2, 10, 5));
		bringThePane.add(mySettingsButton);
		bringThePane.add(mySendButton);
		this.add(bringThePane);
	}
	
	/**
	 * Method for returning the observable object
	 * @return
	 */
	public ChatObservable getObservable(){
		return myObservable;
	}
	
	/**
	 * Class for updating observers to send message input by the user
	 * @author Gustav
	 *
	 */
	class ChatObservable extends Observable{
		
		/**
		 * Update observers with string to send
		 * @param myString
		 */
		public void sendUpdate(String myString){
			setChanged();
			notifyObservers(myString);
		}

	}
}
