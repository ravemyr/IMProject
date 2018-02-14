import java.awt.Dimension;

import javax.swing.*;

public class Viewer {
	private static ChatPanel myChatPanel;
	private static DisplayPanel myDisplayPanel;
	private static JOptionPane myOptionPane;
	private static Communicator myCommunicator;
	private static JFrame myFrame;
	public static void main(String[] args){
		myOptionPane = new JOptionPane();
		int selectedValue = myOptionPane.showConfirmDialog(null,"Run as Server?", "Welcome to Svammel!", JOptionPane.YES_NO_CANCEL_OPTION);
		if(selectedValue == myOptionPane.YES_OPTION){
			String inPort = myOptionPane.showInputDialog("Input port");
			int port = Integer.parseInt(inPort);
//			myCommunicator = new Communicator(true, port);
		}
		else if(selectedValue==myOptionPane.NO_OPTION){
			String serverAdress = myOptionPane.showInputDialog("Input web adress");
			String inPort = myOptionPane.showInputDialog("Input port");
			int port = Integer.parseInt(inPort);
//			myCommunicator = new Communicator(false, serverAdress, port);
		}
		else{
			System.exit(0);
		}
		myDisplayPanel = new DisplayPanel();
		myChatPanel = new ChatPanel();
		myFrame = new JFrame();
		myFrame.getContentPane().add(myDisplayPanel);
		myFrame.getContentPane().add(myChatPanel,"South");
		myFrame.setSize(new Dimension(580,550));
		myFrame.setVisible(true);
		myFrame.setResizable(false);
	}
}
