import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.*;
import Buttons.*;
public class ChatPanel extends JPanel{
	private SendButton mySendButton;
	private SettingsButton mySettingsButton;
	private JOptionPane myOptionPane;
	private Color color;
	private String name;
	private ChatObservable myObservable;
	private JTextArea myTextArea;
	public ChatPanel(){
		super();
		
		myTextArea = new JTextArea();
		myTextArea.setPreferredSize(new Dimension(400,100));
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new GridLayout(2,0,5,5));
		myObservable = new ChatObservable();
		mySendButton = new SendButton();
		mySettingsButton = new SettingsButton();
		this.setVisible(true);
		mySendButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String newText = myTextArea.getText();
				myObservable.sendUpdate(newText);
			}
		});
		this.setLayout(new GridLayout(1,2,10,10));
		this.add(myTextArea);
		bringThePane.add(mySendButton);
		bringThePane.add(mySettingsButton);
		this.add(bringThePane);
	}
	public ChatObservable getObservable(){
		return myObservable;
	}
	class ChatObservable extends Observable{
		public ChatObservable(){
			
		}
		public void sendUpdate(String myString){
			setChanged();
			notifyObservers(myString);
		}

	}
}
