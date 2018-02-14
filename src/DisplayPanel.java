import java.awt.Dimension;
import java.util.*;
import javax.swing.*;

public class DisplayPanel extends JPanel implements Observer{
	private JTextArea myTextArea;
	public void update(Observable arg1, Object arg2){
		
	}
	public DisplayPanel(){
		super();
		myTextArea = new JTextArea();
		myTextArea.setPreferredSize(new Dimension(500,400));
		myTextArea.setEditable(false);
		this.add(myTextArea);
		this.setVisible(true);
	}
}
