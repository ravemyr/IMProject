package project;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;

import project.Server.ClientHandler;

public class ServerGUI extends JFrame{
	private Server myServer;
	private FileButton myFileButton;
	private byte[] myKey;
	private boolean encrypted;
	private String encryptType;
	private EncryptButton myEncryptButton;
	
	public static void main(String[] args) {
		ServerGUI myServerGUI = new ServerGUI();
	}
	
	public ServerGUI() {
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
		
		myServer = new Server();
		myServer.start();
		
		myFileButton = new FileButton();
		myEncryptButton = new EncryptButton();
		bringThePane.add(myFileButton);
		bringThePane.add(myEncryptButton);
		
		this.add(bringThePane);
		this.pack();
		
	}
	
	private class EncryptButton extends JButton implements ActionListener{
		public EncryptButton() {
			this.setText("Encryption");
			this.addActionListener(this);
		}
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
			else if(n==2){
				encrypted = false;
			}
		}
	}
	
	private class FileButton extends JButton implements ActionListener{
		public FileButton() {
			this.setText("Send File");
			this.addActionListener(this);
		}
		
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
	
	private class ClientButton extends JButton implements ActionListener{
		private File myFile;
		private ClientHandler targetClient;
		private JFileChooser myFileChooser;
		
		public ClientButton(ClientHandler a) {
			this.targetClient = a;
			this.setText(targetClient.getClientName());
			this.addActionListener(this);
			myFileChooser = new JFileChooser();
		}
		
		public void actionPerformed(ActionEvent e) {
			int returnValue = myFileChooser.showOpenDialog(null);
			String input;
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				myFile = myFileChooser.getSelectedFile();
				input = JOptionPane.showInputDialog("Send message with file: ");
				targetClient.setFileSender(myFile);
				
				StringBuilder outString = new StringBuilder();
		    	outString.append("<message");
				String name = "Server";
		    	outString.append(" sender=" + name);
		    	outString.append("> ");
		    	outString.append("<filerequest");
		    	outString.append(" name=" + myFile.getName());
		    	outString.append(" size=" + myFile.length());
		    	outString.append(" type=" + encryptType);
		    	if(encrypted){
		    		outString.append(" key=" + Base64.getEncoder()
							.encodeToString(myKey));
		    	}
		    	else{
		    		outString.append(" key=");
		    	}
		    	outString.append("> ");
		    	outString.append(input);
		    	outString.append(" </filerequest> ");
		    	outString.append("</message> ");
		        targetClient.sendMessage(outString.toString());
			}
		}
	}

	
}
	

