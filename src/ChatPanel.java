/**
 * ChatPanel
 * 
 * Created 2018-02-19
 */
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * Class for handling input from the user
 * @author Gustav
 *
 */
public class ChatPanel extends JPanel{
	private SendButton mySendButton;
	private SettingsButton mySettingsButton;
	private Color myColor;
	private SimpleAttributeSet keyWord;
	private String myName;
	private ColorChooser myColorChooser;
	private ChatObservable myObservable;
	private JTextArea myTextArea;
	
	/**
	 * Constructor
	 */
	public ChatPanel(){
		/* Fields */
		this.setVisible(true);
		myColor = Color.BLACK;
		myColorChooser = new ColorChooser(myColor);
		myName = "Anon";
		keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, myColor);
		myTextArea = new JTextArea();
		myObservable = new ChatObservable();
		mySendButton = new SendButton();
		mySettingsButton = new SettingsButton();
		
		/* Make UI */
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(myTextArea);
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new GridLayout(1, 2, 10, 5));
		bringThePane.add(mySettingsButton);
		bringThePane.add(mySendButton);
		this.add(bringThePane);
	}
	
	public SimpleAttributeSet getKeyWord() {
		return keyWord;
	}
	public String getName(){
		return this.myName;
	}
	public Color getColor(){
		return this.myColor;
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
	
	private class ColorChooser extends JPanel implements ChangeListener{
		private JColorChooser myJColorChooser;
		
		public ColorChooser(Color color) {
			this.setVisible(true);
			myJColorChooser = new JColorChooser(color);
			myJColorChooser.getSelectionModel().addChangeListener(this);
			this.add(myJColorChooser);
		}
		
		public void stateChanged(ChangeEvent e) {
			myColor = myJColorChooser.getColor();
			StyleConstants.setForeground(keyWord, myColor);
		}
	}
	
	private class SendButton extends JButton implements ActionListener{

		public SendButton(){
			this.setText("Send Message");
			this.addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e){
			String newText = myTextArea.getText();
			myObservable.sendUpdate(newText);
		}		
		
	}
	
	public class SettingsButton extends JButton implements ActionListener{

		public SettingsButton(){
			this.setText("Settings");
			this.addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e){
			JFrame tempFrame = new JFrame();
			tempFrame.setVisible(true);
			tempFrame.add(new SettingsSelector());
			tempFrame.pack();
		}
	}
	
	private class SettingsSelector extends JPanel{
		private JButton colorButton;
		private JButton nameButton;
		
		public SettingsSelector(){
			colorButton = new JButton();
			nameButton = new JButton();
			colorButton.setText("Color");
			nameButton.setText("Username");
			
			this.add(colorButton);
			this.add(nameButton);
			
			colorButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					JFrame tempFrame = new JFrame();
					tempFrame.setVisible(true);
					tempFrame.add(myColorChooser);
					tempFrame.pack();
				}
			});
			
			nameButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					myName = JOptionPane.showInputDialog("Enter username:");
				}
			});
		}
		
	}
}
