package project;
import java.awt.event.*;

import javax.swing.*;

public class FileSelector extends JPanel{
	private JFileChooser myFileChooser;
	private SendFileButton mySendButton;
	
	public FileSelector() {
		this.setVisible(true);
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		myFileChooser = new JFileChooser();
		mySendButton = new SendFileButton();
		this.add(myFileChooser);
		this.add(mySendButton);
		
	}

	private class SendFileButton extends JButton implements ActionListener{
		public SendFileButton() {
			this.setText("Send File");
			this.addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e) {
			
		}
	}
}
