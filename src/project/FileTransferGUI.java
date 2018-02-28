package project;

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.text.*;

public class FileTransferGUI { //maybe extend thread
	private JTextPane myTextPane;
	private StyledDocument myDoc;
	private JFrame myFrame;
	private File myFile;
	
	public FileTransferGUI(){
		myFrame = new JFrame();
		myTextPane = new JTextPane();
		myTextPane.setPreferredSize(new Dimension(520,550));
		myTextPane.setEditable(false);
		
//		this.setLayout(new GridLayout(1,1,0,0));
		myFrame.add(myTextPane);
		myFrame.setVisible(true);
		
		myDoc = myTextPane.getStyledDocument();
	}

}
