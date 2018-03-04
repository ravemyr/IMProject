package project;
/**
 * ServerGUI
 * Created 2018-02-22
 */
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

import project.Server.ClientHandler;
/**
 * Class for showing a GUI to operate a server
 * @author Gustav
 *
 */
public class ServerGUI extends JFrame{
	private Server myServer;
	private FileButton myFileButton;
	private byte[] myKey;
	private boolean encrypted;
	private String encryptType;
	private EncryptButton myEncryptButton;
	private KickButton myKickButton;
	
//	public static void main(String[] args) {
//		ServerGUI myServerGUI = new ServerGUI();
//	}
	
	/**
	 * Constructor
	 * @param inPort
	 */
	public ServerGUI(int inPort) {
		encryptType = "None";
		encrypted = false;
		
		this.setVisible(true);
		this.setTitle("Server");
		this.setLocation(700, 30);
//		this.setSize(700, 550);
//		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new BoxLayout(bringThePane, BoxLayout.X_AXIS));
		
		myServer = new Server(inPort);
		myServer.start();
		
		myFileButton = new FileButton();
		myEncryptButton = new EncryptButton();
		myKickButton = new KickButton();
		bringThePane.add(myFileButton);
		bringThePane.add(myEncryptButton);
		bringThePane.add(myKickButton);
		
		this.add(bringThePane);
		this.pack();
		
	}
	
	/**
	 * Class to handle kicking clients from server
	 * @author Gustav
	 *
	 */
	private class KickButton extends JButton implements ActionListener{
		/**
		 * Constructor
		 */
		public KickButton() {
			this.setText("Kick");
			this.addActionListener(this);
		}
		/**
		 * Open selection of clients possible to kick
		 */
		public void actionPerformed(ActionEvent e) {
			JFrame tempFrame = new JFrame();
			tempFrame.setTitle("Server: Choose target client");
			JPanel bringThePane = new JPanel();
			bringThePane.setLayout(new BoxLayout(bringThePane, BoxLayout.Y_AXIS));
			bringThePane.setVisible(true);
			ArrayList<ClientHandler> myClients = myServer.getClients();
			for (ClientHandler a : myClients) {
				TargetButton tempButton = new TargetButton(a);
				bringThePane.add(tempButton);
			}
			tempFrame.add(bringThePane);
			tempFrame.pack();
			tempFrame.setVisible(true);
		}
		
		/**
		 * Class for kicking specific client
		 * @author Gustav
		 *
		 */
		private class TargetButton extends JButton implements ActionListener{
			private ClientHandler targetClient;
			/**
			 * Constructor
			 * @param a
			 */
			public TargetButton(ClientHandler a) {
				targetClient = a;
				this.setText(targetClient.getClientName());
				this.addActionListener(this);
			}
			/**
			 * Kicks specific client
			 */
			public void actionPerformed(ActionEvent e) {
				targetClient.closeConnection();
			}
		}
	}
	
	/**
	 * Class for choosing encryption
	 * @author Gustav
	 *
	 */
	private class EncryptButton extends JButton implements ActionListener{
		/**
		 * Constructor
		 */
		public EncryptButton() {
			this.setText("Encryption");
			this.addActionListener(this);
		}
		/**
		 * Selects type of encryption
		 */
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
				    options[2]);
			if(n==0){
				String keyCode = JOptionPane.showInputDialog("Enter integer "
						+ "key");
//				try {
//					myKey = keyCode.getBytes("UTF8");
					myKey = keyCode.getBytes();
//				} catch (UnsupportedEncodingException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
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
			else if(n==2){
				encryptType = "None";
				encrypted = false;
			}
		}
	}
	
	/**
	 * Class for selecting and sending files
	 * @author Gustav
	 *
	 */
	private class FileButton extends JButton implements ActionListener{
		/**
		 * Constructor
		 */
		public FileButton() {
			this.setText("Send File");
			this.addActionListener(this);
		}
		/**
		 * Opens possible target clients
		 */
		public void actionPerformed(ActionEvent e) {
				JFrame tempFrame = new JFrame();
				tempFrame.setTitle("Server: Choose target client");
				JPanel bringThePane = new JPanel();
				bringThePane.setLayout(new BoxLayout(bringThePane, BoxLayout.Y_AXIS));
				bringThePane.setVisible(true);
				ArrayList<ClientHandler> myClients = myServer.getClients();
				for (ClientHandler a : myClients) {
					ClientButton tempButton = new ClientButton(a);
					bringThePane.add(tempButton);
				}
				tempFrame.add(bringThePane);
				tempFrame.pack();
				tempFrame.setVisible(true);
		}
	}
	
	/**
	 * Class for sending file to specific client
	 * @author Gustav
	 *
	 */
	private class ClientButton extends JButton implements ActionListener{
		private File myFile;
		private ClientHandler targetClient;
		private JFileChooser myFileChooser;
		
		/**
		 * Constructor
		 * @param a
		 */
		public ClientButton(ClientHandler a) {
			this.targetClient = a;
			this.setText(targetClient.getClientName());
			this.addActionListener(this);
			myFileChooser = new JFileChooser();
		}
		
		/**
		 * Ask to send file to target client
		 */
		public void actionPerformed(ActionEvent e) {
			int returnValue = myFileChooser.showOpenDialog(null);
			String input;
			try{
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					myFile = myFileChooser.getSelectedFile();
					input = JOptionPane.showInputDialog("Send message with file: ");
					
					StringBuilder outString = new StringBuilder();
			    	outString.append("<message");
					String name = "Server";
			    	outString.append(" sender=" + name);
			    	outString.append("> ");
			    	outString.append("<filerequest");
			    	outString.append(" name=" + myFile.getName());					
					
					
					String tempKey;
					if (myKey == null){
						tempKey = "";
					}
					else if (encryptType.equals("AES")){
						tempKey = Base64.getEncoder().encodeToString(myKey);
					}
					else{
//						tempKey = new String(myKey,"UTF8");
						tempKey = new String(myKey);
					}
					
					byte[] tempFileArray = Files.readAllBytes(myFile.toPath());
					
		    		byte[] outArray;
			    	if(!encryptType.equals("None")){
			    		
			    		outArray = Cryptograph.encryptFile(tempFileArray, encryptType, tempKey);
			    		outString.append(" size=" + outArray.length);
			    		
			    		
			 	    	outString.append(" type=" + encryptType);
			   		 	outString.append(" key=" + tempKey);
			    	}
			    	else{
			    		
			    		outArray = tempFileArray;
			    		outString.append(" size=" + outArray.length);
			    		
				    	outString.append(" type=" + encryptType);
				    	outString.append(" key=");
			    	}
			    	
			    	outString.append("> ");
			    	outString.append(input);
			    	outString.append(" </filerequest> ");
			    	outString.append("</message> ");
				
			    	targetClient.setFileSender(outArray);
			    	targetClient.sendMessage(outString.toString());
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}

	
}
	

