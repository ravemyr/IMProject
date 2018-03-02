package project;
/**
 * Viewer
 * 
 * Created 2018-02-19
 */

import java.awt.Dimension;
import java.util.*;
import javax.swing.*;

/**
 * Class for displaying all parts of the program in a window
 * @author Gustav
 *
 */
public class Viewer {
	private static JFrame myFrame;
	private static JTabbedPane myTabPane;
	private static ArrayList<Tab> myTabList = new ArrayList<Tab>();
	private static Tab myTab;
	
	public static void main(String[] args){
//		ServerGUI myServerGUI = new ServerGUI();
		String[] options = {"Server","Client"};
		int i = JOptionPane.showOptionDialog(myFrame,
			    "Do you want to run as Server or Client?",
			    "Setup",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    null);
		if(i==0){
			String myPort = JOptionPane.showInputDialog("Enter socket to listen to");
			new ServerGUI(myPort);
			myTab = new Tab(myPort);
		}
		else if(i==1){
			String myIP = JOptionPane.showInputDialog("Enter IP adress to connect to");
			String myPort = JOptionPane.showInputDialog("Enter socket to connect to");
			myTab = new Tab(myIP,myPort);
		}
		myFrame = new JFrame();
		myFrame.setTitle("Chat");
		JPanel myPanel = myTab.getPanel();
		myTabPane = new JTabbedPane();
		myTabPane.addTab("Client", myPanel);
		myFrame.getContentPane().add(myTabPane);
		myFrame.setVisible(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(new Dimension(550,850));
		myFrame.setResizable(false);
		
	}
}
