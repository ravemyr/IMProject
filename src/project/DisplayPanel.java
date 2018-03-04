package project;
/**
 * DisplayPanel
 * 
 * Created 2018-02-19
 */

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 * Class for displaying text/chat
 * @author Gustav
 *
 */
public class DisplayPanel extends JPanel {
	private JTextPane myTextPane;
	private StyledDocument myDoc;
	
	/**
	 * Constructor
	 */
	public DisplayPanel(){
		myTextPane = new JTextPane();
		myTextPane.setPreferredSize(new Dimension(520,550));
		myTextPane.setEditable(false);
		
//		this.setLayout(new GridLayout(1,1,0,0));
		this.add(myTextPane);
		this.setVisible(true);
		
		myDoc = myTextPane.getStyledDocument();
	}
	
	/**
	 * Method for displaying textstring in the panel, keyWord can be null
	 * @param str
	 * @throws BadLocationException
	 */
	public void display(String str, SimpleAttributeSet keyWord)
			throws BadLocationException{
//		if (keyWord == null){
//			keyWord = new SimpleAttributeSet();
//			StyleConstants.setBackground(keyWord, Color.decode("#ff642b"));
//		}
//		else{
//			StyleConstants.setBackground(keyWord, Color.decode("#ff642b"));
//		}
		
		myDoc.insertString(myDoc.getLength(), str, keyWord);
	}
}
