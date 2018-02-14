import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
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
		JTextArea writeText = new JTextArea();
		writeText.setPreferredSize(new Dimension(400,100));
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new GridLayout(2,0,5,5));
		this.setLayout(new GridLayout(1,2,10,10));
		this.add(writeText);
		bringThePane.add(mySendButton);
		bringThePane.add(mySettingsButton);
		this.add(bringThePane);
//		this.add(mySendButton);
//		this.add(mySettingsButton);
	}
//	public ChatPanel getInstance(){
//	
//	}
	class myObservable extends Observable{
		public myObservable(){
			
		}
	}
}
