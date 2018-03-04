package project;
/**
 * ChatPanel
 * 
 * Created 2018-02-19
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
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
	private ChatObservable myChatObs;
	private FileObservable myFileObs;
	private EncryptObservable myEncryptObservable;
	private JTextArea myTextArea;
	private FileButton myFileButton;
	private File myFile;
	private byte[] myKey;
	private boolean encrypted = false;
	private String encryptType;
	
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
		myChatObs = new ChatObservable();
		myFileObs = new FileObservable();
		myEncryptObservable = new EncryptObservable();
		mySendButton = new SendButton();
		mySettingsButton = new SettingsButton();
		myFileButton = new FileButton();
		
		/* Make UI */
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(myTextArea);
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new GridLayout(1, 2, 10, 5));
		bringThePane.add(mySettingsButton);
		bringThePane.add(myFileButton);
		bringThePane.add(mySendButton);
		this.add(bringThePane);
	}
	
	/**
	 * Returns the users chosen color as a SimpleAttributeSet
	 * @return
	 */
	public SimpleAttributeSet getKeyWord() {
		return keyWord;
	}
	/**
	 * Returns the users chosen file
	 * @return
	 */
	public File getFile() {
		return myFile;
	}
	/**
	 * Returns the username
	 */
	public String getName(){
		return this.myName;
	}
	/**
	 * Returns chosen color as a Color object
	 * @return
	 */
	public Color getColor(){
		return this.myColor;
	}
	/**
	 * Returns color as a hex string
	 * @return
	 */
	public String getHexColor(){
		Color aColor = this.myColor;
		return "#"+Integer.toHexString(aColor.getRGB()).substring(2).
				toUpperCase();
	}
	/**
	 * Method for returning the chat observable object
	 * @return
	 */
	public ChatObservable getChatObservable(){
		return myChatObs;
	}
	/**
	 * Returns the encrypt observable object
	 * @return
	 */
	public EncryptObservable getEncryptObservable(){
		return myEncryptObservable;
	}	
	
	/**
	 * Method for returning the dile observable object
	 * @return
	 */
	public FileObservable getFileObservable(){
		return myFileObs;
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
	
	/**
	 * Update on ecrypted type and Key.
	 * @author Emanuel
	 *
	 */
	class EncryptObservable extends Observable{
		public void sendUpdate(String myCrypt){
			setChanged();
			notifyObservers(myCrypt);
		}
	}	
	
	/**
	 * Class for updating observers to send file input by the user
	 * @author Gustav
	 *
	 */
	class FileObservable extends Observable{
		
		/**
		 * Update observers with string to send
		 * @param myString
		 */
		public void sendUpdate(String myString){
			setChanged();
			notifyObservers(myString);
		}
	}
	
	/**
	 * Class for selecting color
	 * @author Gustav
	 *
	 */
	private class ColorChooser extends JPanel implements ChangeListener{
		private JColorChooser myJColorChooser;
		
		/**
		 * Constructs the color chooser, pre selects the specified color
		 * @param color
		 */
		public ColorChooser(Color color) {
			this.setVisible(true);
			myJColorChooser = new JColorChooser(color);
			myJColorChooser.getSelectionModel().addChangeListener(this);
			this.add(myJColorChooser);
		}
		
		/**
		 * Updates color and SimpleAttributeSet
		 */
		public void stateChanged(ChangeEvent e) {
			myColor = myJColorChooser.getColor();
			StyleConstants.setForeground(keyWord, myColor);
		}
	}
	
	/**
	 * Class for sending typed messages
	 * @author Gustav
	 *
	 */
	private class SendButton extends JButton implements ActionListener{
		
		/**
		 * Constructor
		 */
		public SendButton(){
			this.setText("Send Message");
			this.addActionListener(this);
		}
		
		/**
		 * If the button is pressed send the written text (from textArea)
		 */
		public void actionPerformed(ActionEvent e){
			String tempString = myTextArea.getText();
			Scanner tempScanner = new Scanner(tempString);
			while(tempScanner.hasNextLine()) {
				myChatObs.sendUpdate(tempScanner.nextLine());
			}
			tempScanner.close();
			myTextArea.setText(null);
		}			
	}
	
	/**
	 * Class for sending files
	 * @author Gustav
	 *
	 */
	private class FileButton extends JButton implements ActionListener{
		private JFileChooser myFileChooser;
		/**
		 * Constructor
		 */
		public FileButton() {
			this.setText("Send File");
			this.addActionListener(this);
			myFileChooser = new JFileChooser();
		}
		/**
		 * Sends selected file
		 */
		public void actionPerformed(ActionEvent e) {
			int returnValue = myFileChooser.showOpenDialog(null);
			String input;
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				myFile = myFileChooser.getSelectedFile();
				input = JOptionPane.showInputDialog("Send message with file: ");
				myFileObs.sendUpdate(input);
			}
		}
	}
	
	/**
	 * Class for opening settings
	 * @author Gustav
	 *
	 */
	private class SettingsButton extends JButton implements ActionListener{
		
		/**
		 * Constructor
		 */
		public SettingsButton(){
			this.setText("Settings");
			this.addActionListener(this);
		}
		
		/**
		 * Opens settings
		 */
		public void actionPerformed(ActionEvent e){
			JFrame tempFrame = new JFrame();
			tempFrame.setVisible(true);
			tempFrame.add(new SettingsSelector());
			tempFrame.pack();
		}
	}
	
	/**
	 * Class for selecting setting to change and changing it
	 * @author Gustav
	 *
	 */
	private class SettingsSelector extends JPanel{
		private JButton colorButton;
		private JButton nameButton;
		private JButton myEncryptButton;
		
		/**
		 * Constructor
		 */
		public SettingsSelector(){
			colorButton = new JButton();
			nameButton = new JButton();
			myEncryptButton = new JButton();
			colorButton.setText("Color");
			nameButton.setText("Username");
			myEncryptButton.setText("Encrypt");
			
			this.add(colorButton);
			this.add(nameButton);
			this.add(myEncryptButton);
			
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
					myName = myName.replaceAll("\\s","");
				}
			});
			
			//Encryption choices
			myEncryptButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int n;
					Object[] options = {"Caesar","AES","None","Cancel"};
					n = JOptionPane.showOptionDialog(new JFrame(),
						    "What type of encryption would you want?",
						    "Encryption selection",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[3]);
					if(n==0){
						System.out.print("This");
						String keyCode = JOptionPane.showInputDialog("Enter "
								+ "integer key");
						try {
							myKey = keyCode.getBytes("UTF8");
						} catch (UnsupportedEncodingException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						encrypted = true;
						encryptType = "Caesar";
					}
					else if(n==1){
						KeyGenerator AESgen = null;
						try {
							AESgen = KeyGenerator.getInstance("AES");
						} catch (NoSuchAlgorithmException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						AESgen.init(128);
						SecretKeySpec AESkey = (SecretKeySpec)AESgen.
								generateKey();
						myKey = AESkey.getEncoded();
						encryptType = "AES";
						encrypted = true;
					}
					else if(n==2){
						encrypted = false;
					}
				}
			});
		}
	}
	/**
	 * Returns encryption key as bytearray
	 * @return
	 */
	public byte[] getKey(){
		return myKey;
	}
	/**
	 * Set encryption type
	 * @param inString
	 */
	public void setEncryptType(String inString){
		if(inString.equals("AES"))
			encryptType = inString;
		else if(inString.equals("Caesar")){
			encryptType = inString;
		}
	}
	/**
	 * Return encryption type
	 * @return
	 */
	public String getEncryptType(){
		return encryptType;
	}
	/**
	 * Return true if encryption is used
	 * @return
	 */
	public boolean isEncrypted(){
		return encrypted;
	}
	
}