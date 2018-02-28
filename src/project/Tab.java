package project;
/**
 * Tab
 * 
 * Created 2018-02-19
 */


import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
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
	private FileTransferGUI myFileTransferGUI;
	private FileReceiver myFileReceiver; 	//
	private FileSender myFileSender;		//
	private ReceiverObserver myReceiverObserver;
	private String myIP;
	
	/**
	 * Constructor. Connects to server, creates panel and adds observers
	 */
	public Tab(){
		myChatPanel = new ChatPanel();
		myDisplayPanel = new DisplayPanel();
		myClient = new Client();
		
		myIP = "10.0.0.144";
		myClient.startConnection(myIP, 4000);
		
		myPanel = new JPanel();
//		myPanel.setLayout(new GridLayout(2,1,10,10));
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.add(myDisplayPanel);
		myPanel.add(myChatPanel);
		
		myChatObserver = new ChatObserver();
		myClientObserver = new ClientObserver();
		myFileObserver = new FileObserver();
		
		myChatPanel.getChatObservable().addObserver(myChatObserver);
		myChatPanel.getFileObservable().addObserver(myFileObserver);
		myClient.getObservable().addObserver(myClientObserver);
	}
	
	/**
	 * Returns the UI as a JPanel
	 * @return
	 */
	public JPanel getPanel(){
		return myPanel;
	}
	
	private class ReceiverObserver implements Observer{
		public void update(Observable a, Object str) {
			String tempString = (String) str;
			try {
				myClient.sendMessage(tempString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class FileObserver implements Observer{
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
		 */
		private String encodeString(String inString){
			StringBuilder outString = new StringBuilder();
	    	outString.append("<message");
			String name = myChatPanel.getName();
	    	outString.append(" sender=" + name);
	    	outString.append("> ");
	    	outString.append("<filerequest");
	    	File tempFile = myChatPanel.getFile();

	    	outString.append(" name=" + tempFile.getName());
	    	outString.append(" size=" + tempFile.length());
	    	outString.append("> ");
	    	outString.append(inString);
	    	outString.append(" </filerequest> ");
	    	outString.append("</message> ");
	    	
	    	try {
//	    		myFileTransferGUI = new FileTransferGUI(outString.toString(),  tempFile);
				myFileSender = new FileSender(tempFile);
			} catch (FileNotFoundException e) {
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
	    	outString.append("<message");
			String name = myChatPanel.getName();
	    	outString.append(" sender=" + name);
	    	outString.append("> ");
	    	String thisColor = myChatPanel.getHexColor();
	    	outString.append("<text color=");
	    	outString.append(thisColor+"> ");
	    	outString.append(inString);
	    	outString.append(" </text> ");
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
				if (verifyStr.equals("filerequest")) {
					myFileReceiver = new FileReceiver(tempString, myChatPanel.getName());
					myReceiverObserver = new ReceiverObserver();
					myFileReceiver.getObservable().addObserver(myReceiverObserver);
					

//					myClient(myFileReceiver.getOutString());
					
//					myFileTransferGUI = new FileTransferGUI(tempString);
//					newTempString = askFileAcceptance(tempString);				/////////////////////////////////////////////////
//					myClient.sendMessage(newTempString);
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
					myFileSender.display("Reply: " + tempStringArray[3].substring(6, tempStringArray[3].length()) + "\n");
					myFileSender.display("Message: " + tempBuilder.toString() + "\n");
					if (tempStringArray[3].substring(6, tempStringArray[3].length()).equals("yes")){
						int port = Integer.parseInt(tempStringArray[4].substring(5,
								tempStringArray[4].length()-1));	
						myFileSender.sendFileTo(myIP, port);						
					}
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
			if(!((stringArray[0].equals("<message"))||(stringArray[0].equals("<encrypted>"))
					||(stringArray[0].equals("<message>")))){
				throw new Exception("Message start error");
			}
			if(!((stringArray[len-1].equals("</message>"))||
					(stringArray[len-1].equals("</encrypted>")))){
				throw new Exception("Bad ending message");
			}
			markerArray.add(0);
			int textActive = 0;
	    	for(int i=1; i<len-1;i++){
	    		if(stringArray[i].startsWith("sender=")&&textActive==0){
	    			sender = stringArray[i].substring(7, stringArray[i].length()-1);
	    			markerArray.add(i);
	    		}
	//    		else if(stringArray[i].startsWith(("color="))&&textActive==0){
	//    			markerArray.add(i);
	//    		}
	    		else if((stringArray[i].contains("<")&&stringArray[i].contains(">"))
	    				&&textActive==0){
	    			markerArray.add(i);
	    		}
	    		else if(((stringArray[i].contains("</")&&stringArray[i].contains(">"))
	    				&&textActive==0)){
	        		markerArray.add(i);
	        	}
	    		else if(stringArray[i].startsWith("<text")&&textActive==0){
	    			markerArray.add(i);
	    			if(stringArray[i+1].startsWith("color")){
	    				colorString = stringArray[i+1].substring(6,stringArray[i+1].length()-1);
	    				markerArray.add(i+1);
	    			}
	    			
	    			if(textActive==0){
	    				textActive = 1;
	    			}
	    		}
	    		else if(stringArray[i].startsWith("</text>")){
	    			if(i==len-3&&stringArray[len-1].equals("</encrypted>")){
	    				textActive = 0;
	    				markerArray.add(i);
	    			}
	    			else if(i==len-2&&stringArray[len-1].equals("</message>")){
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
			return ender;
		}
		
//		/**
//		 * Send question to user if it wants to receive file or not
//		 * @param msg
//		 * @return
//		 */
//		public String askFileAcceptance(String msg) {
//			String[] stringArray = msg.split("\\s");
////			for (String a : stringArray) {
////				System.out.println(a);
////			}
//			int len = stringArray.length;
//			String sender = stringArray[1].substring(7, stringArray[1].length()-1);
//			String fileName = stringArray[3].substring(5, stringArray[3].length());
//			String fileSize = stringArray[4].substring(5, stringArray[4].length()-1);
//			StringBuilder question = new StringBuilder();
//			question.append(sender);
//			question.append(" wants to send you file \"");
//			question.append(fileName);
//			question.append(" of size \"");
//			question.append(fileSize);
//			question.append(". Supplied message: ");
//			for (int i = 5; i < len - 2; i++) {
//				question.append(stringArray[i]);
//			}
//			
//			myFileReceiver = new FileReceiver(fileName, Integer.parseInt(fileSize));   ///////////////////////////////////////////////
//			myFileReceiver.openGUI();
//			
//			
////			int ans = JOptionPane.showConfirmDialog(new JFrame(), question.toString());
//			
//			StringBuilder outString = new StringBuilder();
//			
//			if (ans == JOptionPane.YES_OPTION) {
////				myFileReceiver = new FileReceiver(fileName, Integer.parseInt(fileSize));
////				myFileReceiver.start();
//				
//				String respons = JOptionPane.showInputDialog("Leave reply message");
//				
//		    	outString.append("<message");
//				String name = myChatPanel.getName();
//		    	outString.append(" sender=" + name);
//		    	outString.append("> ");
//		    	outString.append("<filerespons");
//		    	outString.append(" reply=yes");
//		    	outString.append(" port=" + myFileReceiver.getPort() + "> ");
//		    	outString.append(respons);
//		    	outString.append(" </filerespons> ");
//		    	outString.append("</message> ");
//		        return outString.toString();
//			}
//			else {
//				
//				String respons = JOptionPane.showInputDialog("Leave reply message");
//				
//		    	outString.append("<message");
//				String name = myChatPanel.getName();
//		    	outString.append(" sender=" + name);
//		    	outString.append("> ");
//		    	outString.append("<filerespons");
//		    	outString.append(" reply=no");
//		    	outString.append(" port=99999> ");
//		    	outString.append(respons);
//		    	outString.append(" </filerespons> ");
//		    	outString.append("</message> ");
//		        return outString.toString();
//			}
//		}
		
		public String verifyType(String msg) {
			String[] stringArray = msg.split("\\s");
			for (String a : stringArray) {
				if (a.startsWith("<filerequest")) {
					return "filerequest";
				}
				else if (a.startsWith("<text")) {
					return "text";
				}
				else if (a.startsWith("<filerespons")) {
					return "filerespons";
				}				
			}
			return null;
		}
	}
}
