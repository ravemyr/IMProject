package project;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.*;
import java.net.Socket;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

public class FileSender extends Thread{
	private JFrame myFrame;
	private Socket clientSocket;
	private FileInputStream fis;
	private BufferedInputStream bis;
	private OutputStream os;
	private File myFile;
	private byte[] myByteArray;
	private JTextPane myTextPane;
	private StyledDocument myDoc;
	private JProgressBar myProgressBar;
	
	public FileSender(File inFile) throws FileNotFoundException{
		myFile = inFile;
		myByteArray = new byte[(int)myFile.length()];
		fis = new FileInputStream(myFile);
		bis = new BufferedInputStream(fis);
		
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
		
		
		myProgressBar = new JProgressBar(0, (int)myFile.length());
		JScrollPane myScrollPane = new JScrollPane(myTextPane);
		myScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		myScrollPane.setPreferredSize(new Dimension(400, 350));
		myScrollPane.setMinimumSize(new Dimension(10, 10));	
		
		myFrame.getContentPane().add(myProgressBar);
		myFrame.getContentPane().add(myScrollPane);
		myFrame.pack();
		
		myFrame.setVisible(true);
		
	}
	
	public void run() {
		try {
			bis.read(myByteArray, 0, myByteArray.length);
			os = clientSocket.getOutputStream();
			int progress = 0;
			for (byte b : myByteArray) {
				os.write(b);
				progress++;
				myProgressBar.setValue(progress);
			}
			try {
				myDoc.insertString(myDoc.getLength(), "Done\n", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
//			os.write(myByteArray, 0, myByteArray.length);
		} catch (IOException e) {
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
