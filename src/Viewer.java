import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

public class Viewer {
	private static JFrame myFrame;
	private static JTabbedPane myTabPane;
	private static ArrayList<Tab> myTabList = new ArrayList<Tab>();
	
	public static void main(String[] args){
		Tab myTab = new Tab();
		myFrame = new JFrame();
		JPanel myPanel = myTab.getPanel();
		myTabPane = new JTabbedPane();
		myTabPane.addTab("Client",myPanel);
		myFrame.getContentPane().add(myTabPane);
		myFrame.setVisible(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setSize(new Dimension(1000,650));

	}
}
