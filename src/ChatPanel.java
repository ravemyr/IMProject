import java.awt.Color;
import java.awt.TextField;
import java.util.Observable;

import javax.swing.*;
import Buttons.*;
public class ChatPanel extends JPanel{
private SendButton mySendButton;
private SettingsButton mySettingsButton;
private JOptionPane myOptionPane;
private Color color;
private String name;
	public ChatPanel(){
		super();
		mySendButton = new SendButton();
		mySettingsButton = new SettingsButton();
		this.setVisible(true);
		this.add(new JTextField());
		this.add(mySendButton);
		this.add(mySettingsButton);
	}
	public ChatPanel getInstance(){
	
	}
	class myObservable extends Observable{
		public myObservable(){
			
		}
	}
}
