import java.awt.Dimension;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.text.BadLocationException;

public class Tab {
	private JPanel myPanel;
	private ChatPanel myChatPanel;
	private DisplayPanel myDisplayPanel;
	private JOptionPane myOptionPane;
	private Client myClient;
	private ChatObserver myChatObserver;
	private ClientObserver myClientObserver;
	
	public static void main(String[] args){
//		myOptionPane = new JOptionPane();
//		int selectedValue = myOptionPane.showConfirmDialog(null,"Run as Server?", "Welcome to Svammel!", JOptionPane.YES_NO_CANCEL_OPTION);
//		if(selectedValue == myOptionPane.YES_OPTION){
//			String inPort = myOptionPane.showInputDialog("Input port");
//			int port = Integer.parseInt(inPort);
//		}
//		else if(selectedValue==myOptionPane.NO_OPTION){
//			String serverAdress = myOptionPane.showInputDialog("Input web adress");
//			String inPort = myOptionPane.showInputDialog("Input port");
//			int port = Integer.parseInt(inPort);
//		}
//		else{
//			System.exit(0);
//		}
//		myDisplayPanel = new DisplayPanel();
//		myChatPanel = new ChatPanel();
//		myChatObserver = new ChatObserver();
//		myFrame = new JFrame();
//		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		myFrame.getContentPane().add(myDisplayPanel);
//		myFrame.getContentPane().add(myChatPanel,"South");
//		myFrame.setSize(new Dimension(580,550));
//		myFrame.setVisible(true);
//		myFrame.setResizable(false);
//		myChatPanel.getObservable().addObserver(myChatObserver);
	}
	
	public Tab(){
		myChatPanel = new ChatPanel();
		myDisplayPanel = new DisplayPanel();
		myClient = new Client();
		
		System.out.println("jells");
		myClient.startConnection("10.0.0.144", 4000);
		
		myPanel = new JPanel();
		myPanel.add(myDisplayPanel);
		myPanel.add(myChatPanel);
		
		myChatObserver = new ChatObserver();
		myClientObserver = new ClientObserver();
		
		
		myChatPanel.getObservable().addObserver(myChatObserver);
		myClient.getObservable().addObserver(myClientObserver);
	}
	
	public JPanel getPanel(){
		return myPanel;
	}
	
	private class ChatObserver implements Observer{
		
		public void update(Observable a, Object str){
			String tempString = (String) str;
			try {
				myDisplayPanel.display(tempString);
				myClient.sendMessage(tempString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ClientObserver implements Observer{
		
		public void update(Observable a, Object str){
			String tempString = (String) str;
			try {
				System.out.println(tempString + "update");
				myDisplayPanel.display(tempString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
