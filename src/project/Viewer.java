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
	
	public static void main(String[] args){
//		ServerGUI myServerGUI = new ServerGUI();
		Tab myTab = new Tab();
		myFrame = new JFrame();
		myFrame.setTitle("Chat");
		JPanel myPanel = myTab.getPanel();
		myTabPane = new JTabbedPane();
		myTabPane.addTab("Client",myPanel);
		myFrame.getContentPane().add(myTabPane);
		myFrame.setVisible(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(new Dimension(550,850));
		myFrame.setResizable(false);
	}
}
