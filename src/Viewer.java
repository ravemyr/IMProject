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
		myDisplayPanel = new DisplayPanel();
		myChatPanel = new ChatPanel();
		myFrame = new JFrame();
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.getContentPane().add(myDisplayPanel);
		myFrame.getContentPane().add(myChatPanel,"South");
		myFrame.setSize(new Dimension(580,550));
		myFrame.setVisible(true);
		myFrame.setResizable(false);
	}
}
