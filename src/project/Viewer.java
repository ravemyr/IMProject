package project;
/**
 * Viewer
 * 
 * Created 2018-02-19
 */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Class for displaying all parts of the program in a window
 * @author Gustav
 *
 */
public class Viewer {
	private JFrame myFrame;
	private JTabbedPane myTabPane;
	private ArrayList<Tab> myTabList;
	private ArrayList<DisconnectButton> myButtonList;
	
	public static void main(String[] args){
		Viewer myViewer = new Viewer();
	}
	
	public Viewer(){
		myFrame = new JFrame();
		myFrame.setTitle("Chat");
		myTabPane = new JTabbedPane();
		myTabList = new ArrayList<Tab>();
		myButtonList = new ArrayList<DisconnectButton>();
		
		myFrame.getContentPane().setLayout(new BoxLayout(
				myFrame.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new BoxLayout(bringThePane,
				BoxLayout.X_AXIS));
		bringThePane.add(new NewTabButton());
		
		myFrame.getContentPane().add(bringThePane);
		myFrame.getContentPane().add(myTabPane);
		
		String[] options = {"Server","Client"};
		int i = JOptionPane.showOptionDialog(myFrame,
			    "Do you want to run as Server or Client?",
			    "Setup", JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE, null, options, null);
		if(i==JOptionPane.YES_OPTION){
			String myPort = JOptionPane.showInputDialog("Enter serverport");
			new ServerGUI(Integer.parseInt(myPort));
			this.addTab("127.0.0.1", Integer.parseInt(myPort));
		}
		else if(i==JOptionPane.NO_OPTION){
			String myIP = JOptionPane.showInputDialog("Enter IP adress to connect to");
			String myPort = JOptionPane.showInputDialog("Enter port to connect to");
			this.addTab(myIP, Integer.parseInt(myPort));
		}
		
		myFrame.setVisible(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(new Dimension(550,850));
		myFrame.setResizable(false);
	}
	
	public void addTab(String IP, int port) {
		myTabList.add(new Tab(IP, port));
		myButtonList.add(new DisconnectButton(myTabList.get(myTabList.size()-1), myTabList.size()-1));
		JPanel tempPanel = new JPanel();
		tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
		tempPanel.add(myButtonList.get(myButtonList.size()-1));
		tempPanel.add(myTabList.get(myTabList.size()-1).getPanel());
		myTabPane.addTab("Client", tempPanel);
	}
	
	private class NewTabButton extends JButton implements ActionListener{
		public NewTabButton() {
			this.setText("New Tab");
			this.addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e) {
			String ip = JOptionPane.showInputDialog("Enter IP adress to connect to");
			int port = Integer.parseInt(JOptionPane.showInputDialog("Enter port to connect to"));
			addTab(ip, port);
		}
	}
	
	private class DisconnectButton extends JButton implements ActionListener{
		private Tab myTab;
		private int number;
		public DisconnectButton(Tab inTab, int nr) {
			number = nr;
			myTab = inTab;
			this.setText("Disconnect");
			this.addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e) {
			myTab.disconnectTab();
			myTabPane.remove(number);
			myButtonList.remove(number);
			for (int i = number; i < myButtonList.size(); i++) {
				myButtonList.get(i).setNumber(i);
			}
		}
		
		public void setNumber(int num) {
			number = num;
		}
	}
}