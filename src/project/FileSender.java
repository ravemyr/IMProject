package project;
/**
 * FileSender
 * 
 * Created 2018-02-19
 */

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.*;
import java.net.Socket;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

/**
 * Class for sending files
 * @author Gustav
 *
 */
public class FileSender extends Thread{
	private JFrame myFrame;
	private Socket clientSocket;
	private OutputStream os;
	private byte[] myByteArray;
	private JTextPane myTextPane;
	private StyledDocument myDoc;
	private JProgressBar myProgressBar;
	private String myEncryptionType;
	private String myEncryptionKey;
	
	/**
	 * Constructor
	 * @param inArray
	 */
	public FileSender(byte[] inArray) {
		myByteArray = inArray;
		myFrame = new JFrame();
		myFrame.setTitle("FileSender");
		myFrame.getContentPane().setLayout(new BoxLayout(myFrame.getContentPane(), BoxLayout.Y_AXIS));
		myTextPane = new JTextPane();
		myTextPane.setPreferredSize(new Dimension(400,350));
		myTextPane.setEditable(false);
		
		myDoc = myTextPane.getStyledDocument();
		try {
			myDoc.insertString(myDoc.getLength(), "Waiting...\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		myProgressBar = new JProgressBar(0, myByteArray.length);
		JScrollPane myScrollPane = new JScrollPane(myTextPane);
		myScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		myScrollPane.setPreferredSize(new Dimension(400, 350));
		myScrollPane.setMinimumSize(new Dimension(10, 10));	
		
		myFrame.getContentPane().add(myProgressBar);
		myFrame.getContentPane().add(myScrollPane);
		myFrame.pack();
		
		myFrame.setVisible(true);
		
	}
	
	/**
	 * Starts thread and sending the file
	 */
	public void run() {
		try {
			os = clientSocket.getOutputStream();
			int progress = 0;
			for (byte b : myByteArray) {
				os.write(b);
				progress++;
				myProgressBar.setValue(progress);
			}
			os.flush();
			os.close();
			try {
				myDoc.insertString(myDoc.getLength(), "Done\n", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method for displaying textstring in the panel
	 * @param str
	 * @throws BadLocationException
	 */
	public void display(String str)
			throws BadLocationException{
		myDoc.insertString(myDoc.getLength(), str, null);
	}
	
    /**
     * Connect to a server
     * @param ip
     * @param port
     */
    public void sendFileTo(String ip, int port){
        try {
			clientSocket = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
        this.start();
    }

}
