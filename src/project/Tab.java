package project;
/**
 * Tab
 * 
 * Created 2018-02-19
 */


import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * Class for controlling a single chat, makes sure messages get from
 * user to server and vice versa. Creates a panel for the chat.
 * @author Gustav
 *
 */
public class Tab {
	private JPanel myPanel;
	private ChatPanel myChatPanel;
	private DisplayPanel myDisplayPanel;
	private JOptionPane myOptionPane;
	private Client myClient;
	private ChatObserver myChatObserver;
	private ClientObserver myClientObserver;
	private FileObserver myFileObserver;
	private FileReceiver myFileReceiver; 	//
	private FileSender myFileSender;		//
	private ReceiverObserver myReceiverObserver;
	private String myIP;
	private EncryptObserver myEncryptObserver;
	private byte[] myKey;
	private Cryptograph myCryptograph;	
	/**
	 * Constructor. Connects to server, creates panel and adds observers
	 */
	public Tab(String inIP, int inPort){
		myChatPanel = new ChatPanel();
		myDisplayPanel = new DisplayPanel();
		myClient = new Client();
		
		myIP = inIP;
		myClient.startConnection(myIP, inPort);
		
		myPanel = new JPanel();
//		myPanel.setLayout(new GridLayout(2,1,10,10));
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.add(myDisplayPanel);
		myPanel.add(myChatPanel);
		
		myChatObserver = new ChatObserver();
		myClientObserver = new ClientObserver();
		myFileObserver = new FileObserver();
		myEncryptObserver = new EncryptObserver();
		
		
		myChatPanel.getChatObservable().addObserver(myChatObserver);
		myChatPanel.getFileObservable().addObserver(myFileObserver);
		myClient.getObservable().addObserver(myClientObserver);
		myChatPanel.getEncryptObservable().addObserver(myEncryptObserver);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		this.attemptConnection();
	}
	
	/**
	 * Method for attempting to connect to server
	 */
	private void attemptConnection() {
		try {
			myDisplayPanel.display("Waiting...", null);
			StringBuilder outString = new StringBuilder();
	    	outString.append("<request> ");
			String name = myChatPanel.getName();
			outString.append("User: \"");
	    	outString.append(name);
	    	outString.append("\" Is trying to connect. Allow connection?");
	    	outString.append(" </request>");
	    	myClient.sendMessage(outString.toString());
	    	
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Disconnect from server
	 */
	public void disconnectTab() {
		StringBuilder outString = new StringBuilder();
    	outString.append("<message");
		String name = myChatPanel.getName();
    	outString.append(" sender=" + name);
    	outString.append("> ");
    	outString.append("<disconnect> </disconnect>");
    	outString.append("</message>");
    	try {
			myClient.sendMessage(outString.toString());
			myClient.stopConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the UI as a JPanel
	 * @return
	 */
	public JPanel getPanel(){
		return myPanel;
	}
	
	/**
	 * Observer for the FileReceiver object
	 * @author Gustav
	 *
	 */
	private class ReceiverObserver implements Observer{
		/**
		 * Relay message from FileReceiver
		 */
		public void update(Observable a, Object str) {
			String tempString = (String) str;
			try {
				myClient.sendMessage(tempString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Class for asking to send file
	 * @author Gustav
	 *
	 */
	private class FileObserver implements Observer{
		/**
		 * Relay message
		 */
		public void update(Observable a, Object str) {
			String tempString = (String) str;
			try {
				myClient.sendMessage(encodeString(tempString));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/**
		 * encodes a written string into valid
		 * XML-code format for handling
		 * @param inString
		 * @return
		 * @throws UnsupportedEncodingException 
		 */
		private String encodeString(String inString) 
				throws UnsupportedEncodingException{
			StringBuilder outString = new StringBuilder();
	    	outString.append("<message");
			String name = myChatPanel.getName();
	    	outString.append(" sender=" + name);
	    	outString.append("> ");
	    	outString.append("<filerequest");
	    	File tempFile = myChatPanel.getFile();
	    	outString.append(" name=" + tempFile.getName());
	    	
	    	String encryptionType;
	    	String encryptionKey;
	    	
	    	try{
//	    		byte[] tempFileArray = new byte[(int)tempFile.length()];
//	    		BufferedInputStream tempBis = new BufferedInputStream(new FileInputStream(tempFile));
//	    		tempBis.read(tempFileArray, 0, tempFileArray.length);
//	    		tempBis.close();
	    		
	    		byte[] tempFileArray = Files.readAllBytes(tempFile.toPath());
	    		
	    		byte[] outArray;
		    	if(myChatPanel.isEncrypted()){
		    		encryptionType = myChatPanel.getEncryptType();
		    		if (encryptionType.equals("AES")){
		    			encryptionKey = Base64.getEncoder()
							.encodeToString(myChatPanel.getKey());
		    		}
		    		else{
//		    			encryptionKey = new String(myChatPanel.getKey(),"UTF8");
		    			encryptionKey = new String(myChatPanel.getKey());
		    		}
		    		
		    		outArray = Cryptograph.encryptFile(tempFileArray,
		    				encryptionType, encryptionKey);
		    		outString.append(" size=" + outArray.length);
		    		
		 	    	outString.append(" type=" + encryptionType);
		   		 	outString.append(" key=" + encryptionKey);
		    	}
		    	else{
		    		encryptionType = "None";
		    		encryptionKey = "";
		    		
		    		outArray = tempFileArray;
		    		outString.append(" size=" + outArray.length);
		    		
			    	outString.append(" type=" + encryptionType);
			    	outString.append(" key=");
		    	}
		    	
		    	outString.append("> ");
		    	outString.append(inString);
		    	outString.append(" </filerequest> ");
		    	outString.append("</message> ");
		    	
		    	myFileSender = new FileSender(outArray);		    	
		    	
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	        return outString.toString();
		}
	}
	
	/**
	 * Class for observing the ChatPanel
	 * @author Gustav
	 *
	 */
	private class ChatObserver implements Observer{
		/**
		 * If the user wants to send a message, make sure that it is displayed
		 * and sent to the server
		 */
		public void update(Observable a, Object str){
			String tempString = (String) str;
			try {
				myDisplayPanel.display(myChatPanel.getName() +": " + 
						tempString + "\n", myChatPanel.getKeyWord());
				myClient.sendMessage(encodeString(tempString));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/**
		 * encodes a written string into valid
		 * XML-code format for handling
		 * @param inString
		 * @return
		 */
		private String encodeString(String inString){
			StringBuilder outString = new StringBuilder();
			StringBuilder tempString = new StringBuilder();
	    	outString.append("<message");
			String name = myChatPanel.getName();
	    	outString.append(" sender=" + name);
	    	outString.append("> ");
	    	String thisColor = myChatPanel.getHexColor();
	    	tempString.append("<text color=");
	    	tempString.append(thisColor+"> ");
	    	tempString.append(inString);
	    	tempString.append(" </text> ");
	    	System.out.print("Am I here?");
	    	if(myChatPanel.isEncrypted()){
	    		System.out.println(myChatPanel.getEncryptType());
	    		String encryptedString = null;
	    		try {
	    			if(myChatPanel.getEncryptType().equals("AES")){
					encryptedString = Cryptograph.encode(tempString.toString(),
							myChatPanel.getEncryptType(), Base64.getEncoder()
								.encodeToString(myChatPanel.getKey())); 
	    			}
	    			else if(myChatPanel.getEncryptType().equals("Caesar")){
	    				encryptedString = Cryptograph.encode(tempString.toString(),
								myChatPanel.getEncryptType(), 
									new String(myChatPanel.getKey(),"UTF8"));
		    			}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		outString.append(encryptedString);
	    	}
	    	else{
	    		outString.append(tempString);
	    	}
	    	outString.append("</message> ");
	        return outString.toString();
		}
	}
	
	/**
	 * Class for observing the Client class
	 * @author Gustav
	 *
	 */
	private class ClientObserver implements Observer{
		SimpleAttributeSet myKeyWord = myChatPanel.getKeyWord();
		/**
		 * If a message is received from the server, display it.
		 */
		public void update(Observable a, Object str){
			String newTempString = null;
			String tempString = (String) str;
			try{
				String verifyStr = verifyType(tempString);
				if (verifyStr.equals("kick")) {
					myClient.stopConnection();
					myDisplayPanel.display("You were kicked from the server",
							null);
				}
				else if (verifyStr.equals("disconnect")) {
					String[] tempStringArray = tempString.split("\\s");
					String tempName = tempStringArray[1].substring(7,
							tempStringArray[1].length()-1);
					myDisplayPanel.display(tempName + " has disconnected.",
							null);
				}
				else if (verifyStr.equals("filerequest")) {
					myFileReceiver = new FileReceiver(tempString, myChatPanel.
							getName());
					myReceiverObserver = new ReceiverObserver();
					myFileReceiver.getObservable().
							addObserver(myReceiverObserver);
				}
				else if (verifyStr.equals("response")) {
					String[] tempStringArray = tempString.split("\\s");
					if (tempStringArray[1].equals("...Connected")) {
						myDisplayPanel.display("...Connected\n", null);
					}
					else {
						myDisplayPanel.display("...Refused\n", null);
						myClient.stopConnection();
					}
				}
				else if (verifyStr.equals("text")) {
					newTempString = verifyMessage(tempString);
					myDisplayPanel.display(newTempString + "\n", myKeyWord);      //W THIS SHOULD BE KEYWORD FROM ELSEWHERE */ jag ändrade lite här /Gustav
				}
				else if (verifyStr.equals("filerespons")) {
					String[] tempStringArray = tempString.split("\\s");
					StringBuilder tempBuilder = new StringBuilder();
					for (int i = 5; i < tempStringArray.length - 2; i++) {
						tempBuilder.append(tempStringArray[i]);
						tempBuilder.append(" ");
					}
					myFileSender.display("Reply: " + tempStringArray[3].
							substring(6, tempStringArray[3].length()) + "\n");
					myFileSender.display("Message: " + tempBuilder.toString() 
							+ "\n");
					if (tempStringArray[3].substring(6, tempStringArray[3].
							length()).equals("yes")){
						int port = Integer.parseInt(tempStringArray[4].substring(5,
								tempStringArray[4].length()-1));	
						myFileSender.sendFileTo(myIP, port);						
					}
				}
				else if(verifyStr.equals("keyrequest")){
					String[] controlArray=tempString.split("\\s");
					if(controlArray[3].substring(6,controlArray[3].length()-1).
							equals("AES")||controlArray[3].substring(6,
							controlArray[3].length()-1).equals("Caesar")){
						try {
							myClient.sendMessage("<message> <keyresponse "
									+ "answer=true> "
									+ "</keyresponse> </message>");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						try {
							myClient.sendMessage("<message> <keyresponse "
									+ "answer=false> </keyresponse> </message>");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				else if(verifyStr.equals("keyresponse")){
					String[] controlArray = tempString.split("\\s");
					if(controlArray[3].substring(8,controlArray[3].length()-1).
							equals("false")){
						JOptionPane.showMessageDialog(new JFrame(), "Other user"
								+ " does not support"
								+ " your encryption, please change it.");
					}
				}
				else if(verifyStr.equals("encrypted")){
					newTempString = Cryptograph.decode(tempString);
					String finalString = verifyMessage(newTempString);
					myDisplayPanel.display(finalString + "\n", myKeyWord);
				}				
				else {
					newTempString = "There is a bug in your CODE!";
				}
				
			}catch(Exception e){
				e.printStackTrace();
				System.out.print(e.getMessage());
			}
		}
		
		
		/**
		 * Method to control correctness of received message
		 * and converting it into valid text to be displayed.
		 * @param msg
		 * @return
		 * @throws Exception
		 */
		public String verifyMessage(String msg) throws Exception{
			String[] stringArray = msg.split("\\s");
	    	int len = stringArray.length;
	    	String sender = "Anon";
	    	String colorString = "#000000"; //Black is default.
	    	Color thisColor;
	    	ArrayList<Integer> markerArray = new ArrayList<Integer>();
			if(!stringArray[0].equals("<message")){
				throw new Exception("Message start error");
			}
			if(!stringArray[len-1].equals("</message>")){
				throw new Exception("Bad ending message");
			}
			markerArray.add(0);
			int textActive = 0;
	    	for(int i=1; i<len-1;i++){
	    		if(stringArray[i].startsWith("sender=")&&textActive==0){
	    				sender = stringArray[i].substring(7, stringArray[i].
	    						length()-1);
	    			markerArray.add(i);
	    		}
	    		else if(stringArray[i].startsWith("<text")&&textActive==0){
	    			System.out.println("Do I get here?");
	    			markerArray.add(i);
	//    			if(stringArray[i+1].startsWith("color")){
	    			colorString = stringArray[i+1].substring(6,stringArray[i+1].
	    					length()-1);
	    			markerArray.add(i+1);
	//    			}
	    			if(textActive==0){
	    				textActive = 1;
	    			}
	    		}
	    		else if((stringArray[i].contains("<")||stringArray[i].
	    				contains(">"))&&textActive==0){
	    			markerArray.add(i);
	    		}
	    		else if(stringArray[i].startsWith("type=")&&textActive==0){
	    			markerArray.add(i);
	    		}
	    		else if(((stringArray[i].contains("</")&&stringArray[i].
	    				contains(">"))&&textActive==0)){
	        		markerArray.add(i);
	        	}
	    		else if(stringArray[i].startsWith("</text>")){
	    			if(i==len-3&&stringArray[len-1].equals("</encrypted>")){
	    				textActive = 0;
	    				markerArray.add(i);
	    			}
	    			else if((i==len-2&&stringArray[len-1].equals("</message>"))||
	    					(i==len-3&&stringArray[len-1].equals("</message>"))){
	    				textActive = 0;
	    				markerArray.add(i);
	    			}
	    		}
	    	}
	    	StringBuilder buildFinal = new StringBuilder();
	    	buildFinal.append(sender + ": ");
	    	for(int j = 0; j<len-1; j++){
	    		if(!(markerArray.contains(j))){
	    			buildFinal.append(stringArray[j]);
	    			buildFinal.append(" ");
	    		}
	    	}
	    	thisColor = Color.decode(colorString);
	    	myKeyWord = new SimpleAttributeSet();
	    	StyleConstants.setForeground(myKeyWord, thisColor);
	    	buildFinal.delete(buildFinal.length()-1,buildFinal.length()-1);
	    	String ender = buildFinal.toString();
	    	System.out.println(colorString);
			return ender;
		}
		
		/**
		 * Method for verifing type of incoming message
		 * @param msg
		 * @return
		 */
		public String verifyType(String msg) {
			String[] stringArray = msg.split("\\s");
			for (String a : stringArray) {
				if (a.startsWith("<kick")) {
					return "kick";
				}
				else if (a.startsWith("<disconnect")) {
					return "disconnect";
				}
				else if (a.startsWith("<filerequest")) {
					return "filerequest";
				}
				else if (a.startsWith("<text")) {
					return "text";
				}
				else if (a.startsWith("<filerespons")) {
					return "filerespons";
				}
				else if (a.startsWith("<keyrequest")){
					return "keyrequest";
				}
				else if (a.startsWith("<keyresponse")){
					return "keyresponse";
				}
				else if (a.startsWith("<encrypted")){
					return "encrypted";
				}
				else if (a.startsWith("<response")) {
					return "response";
				}
			}
			return null;
		}
		
	}
	
	/**
	 * Class for observing encryption from chatpanel
	 * @author Gustav
	 *
	 */
	private class EncryptObserver implements Observer{
		/**
		 * Sends keyrequest when applying encryption
		 */
		@Override
		public void update(Observable b, Object type) {
			String encrypt = (String) type;
			System.out.print(type);
			StringBuilder keyReq = new StringBuilder();
			keyReq.append("<message sender="+myChatPanel.getName()+"> ");
			keyReq.append("<keyrequest type="+type+"> ");
			keyReq.append("</keyrequest> </message>");
			try{
				myClient.sendMessage(keyReq.toString());
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
}
