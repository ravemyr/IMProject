/**
 * Tab
 * 
 * Created 2018-02-19
 */


import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

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
	
	/**
	 * Constructor. Connects to server, creates panel and adds observers
	 */
	public Tab(){
		myChatPanel = new ChatPanel();
		myDisplayPanel = new DisplayPanel();
		myClient = new Client();
		
		myClient.startConnection("130.229.186.209", 4000);
		
		myPanel = new JPanel();
//		myPanel.setLayout(new GridLayout(2,1,10,10));
		myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.Y_AXIS));
		myPanel.add(myDisplayPanel);
		myPanel.add(myChatPanel);
		
		myChatObserver = new ChatObserver();
		myClientObserver = new ClientObserver();
		
		
		myChatPanel.getObservable().addObserver(myChatObserver);
		myClient.getObservable().addObserver(myClientObserver);
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
				myDisplayPanel.display(tempString, myChatPanel.getKeyWord());
		    	
				myClient.sendMessage(encodeString(tempString));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public String encodeString(String inString){
			StringBuilder outString = new StringBuilder();
	    	outString.append("<message");
//	    	if(hasName()){
//				String name = userName();
//	    		outString.append(' name = ' + name);
//	    	}
//	    	color = currColor;
	    	outString.append(">");
	    	outString.append("<text color=");
//	    	outString.append(color+">");
	    	outString.append(inString);
	    	outString.append("</text>");
	    	outString.append("</message>");
	        return outString.toString();
		}
	}
	
	/**
	 * Class for observing the Client class
	 * @author Gustav
	 *
	 */
	private class ClientObserver implements Observer{
		/**
		 * If a message is received from the server, display it.
		 */
		public void update(Observable a, Object str){
			String tempString = (String) str;
			try{
				verifyMessage(tempString);
			}catch(Exception e){
				System.out.print(e.getMessage());
			}
			try {
				myDisplayPanel.display(tempString, myChatPanel.getKeyWord());     /* THIS SHOULD BE KEYWORD FROM ELSEWHERE */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
      public String verifyMessage(String msg) throws Exception{
    	String[] stringArray = msg.split("\\s");
    	ArrayList<Integer> markerArray = new ArrayList<Integer>();
    	int len = stringArray.length;
		if(!stringArray[0].equals("<message")){
			throw new Exception("Bad message");
		}
		if(!stringArray[len].equals("</message>")){
			throw new Exception("Bad message");
		}
    	for(int i=1; i<len;i++){
    		if(stringArray[i].contains("<")&&stringArray[i].contains(">")){
    			markerArray.add(i);
    		}
    		if(stringArray[i].contains(">")){
    			
    		}
    	}
    	StringBuilder buildFinal = new StringBuilder();
    	for(int j = 1; j<stringArray.length; j++){
    		if(!markerArray.contains(j)){
    			buildFinal.append(stringArray[j]);
    		}
    	}
		return buildFinal.toString();
    }
	}
}
