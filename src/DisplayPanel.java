import java.awt.Color;
import java.awt.Dimension;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

public class DisplayPanel extends JPanel {
	private JTextPane myTextPane;
	StyledDocument myDoc;

	public DisplayPanel(){
		super();
		myTextPane = new JTextPane();
		myTextPane.setPreferredSize(new Dimension(500,400));
		myTextPane.setEditable(false);
		this.add(myTextPane);
		this.setVisible(true);
		myDoc = myTextPane.getStyledDocument();
	}
	
	public void display(String str) throws BadLocationException{
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setForeground(keyWord, Color.RED);
		StyleConstants.setBackground(keyWord, Color.YELLOW);
		StyleConstants.setBold(keyWord, true);
		
		myDoc.insertString(myDoc.getLength(), str, null);
	}
}
