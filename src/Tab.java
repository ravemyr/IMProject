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
	private EncryptObserver myEncryptObserver;
	private byte[] myKey;
	private Cryptograph myCryptograph;
	
	/**
	 * Constructor. Connects to server, creates panel and adds observers
	 */
	public Tab(){
		myChatPanel = new ChatPanel();
		myDisplayPanel = new DisplayPanel();
		myClient = new Client();
		myClient.startConnection("127.0.0.1", 4000);
		
		myPanel = new JPanel();
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.add(myDisplayPanel);
		myPanel.add(myChatPanel);
		
		myChatObserver = new ChatObserver();
		myClientObserver = new ClientObserver();
		myEncryptObserver = new EncryptObserver();
		myChatPanel.getObservable().addObserver(myChatObserver);
		myClient.getObservable().addObserver(myClientObserver);
		myChatPanel.getEncryptObservable().addObserver(myEncryptObserver);
	}
	
	/**
	 * Returns the UI as a JPanel
	 * @return
	 */
	public JPanel getPanel(){
		return myPanel;
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
		public String encodeString(String inString){
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
	    		System.out.println(myChatPanel.getType());
	    		String encryptedString = null;
	    		try {
					encryptedString = Cryptograph.encode(tempString.toString(),
							myChatPanel.getType(), new String(myChatPanel.getKey(),"UTF8"));
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
				System.out.print(verifyStr);
				if (verifyStr.equals("filerequest")) {
//					myFileReceiver = new FileReceiver(tempString);
					
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
					int port = Integer.parseInt(tempStringArray[4].substring(5,
							tempStringArray[4].length()-1));		
//					myFileSender.sendFileTo(myIP, port);
				}
				else if(verifyStr.equals("keyrequest")){
					String[] controlArray=tempString.split("\\s");
					if(controlArray[3].substring(6,controlArray[3].length()-1).equals("AES")||
							controlArray[3].substring(6,controlArray[3].length()-1).equals("Caesar")){
						try {
							myClient.sendMessage("<message> <keyresponse answer=true> "
									+ "</keyresponse> </message>");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						try {
							myClient.sendMessage("<message> <keyresponse answer=false> "
										+ "</keyresponse> </message>");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				else if(verifyStr.equals("keyresponse")){
					String[] controlArray = tempString.split("\\s");
					if(controlArray[3].substring(8,controlArray[3].length()-1).equals("false")){
						JOptionPane.showMessageDialog(new JFrame(), "Other user does not support"
								+ " your encryption, please change it.");
					}
				}
				else if(verifyStr.equals("encrypted")){
					System.out.print(tempString);
					newTempString = Cryptograph.decode(tempString);
					System.out.print(newTempString);
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
//			String classType = verifyType(tempString);
//			System.out.print(classType);
//			if(classType.equals("text")){
//				try{
//					newTempString = verifyMessage(tempString);
//				}catch(Exception e){
//					e.printStackTrace();
//					System.out.print(e.getMessage());
//				}
//				try {
//					myDisplayPanel.display(newTempString + "\n", myKeyWord);     //W THIS SHOULD BE KEYWORD FROM ELSEWHERE */
//				} catch (Exception e) {
//					e.printStackTrace();
//				}


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
    				sender = stringArray[i].substring(7, stringArray[i].length()-1);
    			markerArray.add(i);
    		}
    		else if((stringArray[i].contains("<")||stringArray[i].contains(">"))
    				&&textActive==0){
    			markerArray.add(i);
    		}
    		else if(stringArray[i].startsWith("type=")&&textActive==0){
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
	}
	private class EncryptObserver implements Observer{

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
			else if (a.startsWith("<keyrequest")){
				return "keyrequest";
			}
			else if (a.startsWith("<keyresponse")){
				return "keyresponse";
			}
			else if(a.startsWith("<encrypted")){
				return "encrypted";
			}
		}
		return null;
	}
}

