/**
 * ChatPanel
 * 
 * Created 2018-02-19
 */
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
	private ChatObservable myObservable;
	private EncryptObservable myEncryptObservable;
	private JTextArea myTextArea;
	private FileButton myFileButton;
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
		myObservable = new ChatObservable();
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
	
	public SimpleAttributeSet getKeyWord() {
		return keyWord;
	}
	public String getName(){
		return this.myName;
	}
	public Color getColor(){
		return this.myColor;
	}
	public String getHexColor(){
		Color aColor = this.myColor;
		return "#"+Integer.toHexString(aColor.getRGB()).substring(2).toUpperCase();
		
	}
	
	/**
	 * Method for returning the observable object
	 * @return
	 */
	public ChatObservable getObservable(){
		return myObservable;
	}
	public EncryptObservable getEncryptObservable(){
		return myEncryptObservable;
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
	class EncryptObservable extends Observable{
		public void sendUpdate(String myCrypt){
			setChanged();
			notifyObservers(myCrypt);
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
			myTextArea.setText(null);
		}			
	}
	
	private class FileButton extends JButton implements ActionListener{
		private JFileChooser myFileChooser;
		private File myFile;
		public FileButton() {
			this.setText("Send File");
			this.addActionListener(this);
			myFileChooser = new JFileChooser();
		}
		
		public void actionPerformed(ActionEvent e) {
			int returnValue = myFileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				myFile = myFileChooser.getSelectedFile();
				System.out.println(myFile.getAbsolutePath());
			}
		}
	}
	
	private class SettingsButton extends JButton implements ActionListener{

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
		private JButton myEncryptButton;
		
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
				}
			});
			
			myEncryptButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int n;
					Object[] options = {"Caesar","AES","Cancel"};
					n = JOptionPane.showOptionDialog(new JFrame(),
						    "What type of encryption would you want?",
						    "Encryption selection",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[2]);
					if(n==0){
						System.out.print("This");
						String keyCode = JOptionPane.showInputDialog("Enter integer key");
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
						SecretKeySpec AESkey = (SecretKeySpec)AESgen.generateKey();
						myKey = AESkey.getEncoded();
						encryptType = "AES";
						encrypted = true;
					}
				}
			});
		}
	}
	public byte[] getKey(){
		return myKey;
	}
	public void setType(String inString){
		if(inString.equals("AES"))
			encryptType = inString;
		else if(inString.equals("Caesar")){
			encryptType = inString;
		}
	}
	public String getType(){
		return encryptType;
	}
	public boolean isEncrypted(){
		return encrypted;
	}
	
}