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
//	private FileInputStream fis;
//	private BufferedInputStream bis;
	private OutputStream os;
//	private File myFile;
//	private String myFileName;
	private byte[] myByteArray;
	private JTextPane myTextPane;
	private StyledDocument myDoc;
	private JProgressBar myProgressBar;
	private String myEncryptionType;
	private String myEncryptionKey;
	
	public FileSender(byte[] inArray) {
//		myFile = inFile;
		
//		myEncryptionType = inType;
//		myEncryptionKey = inKey;
//		myByteArray = new byte[(int)myFile.length()];
		myByteArray = inArray;
//		fis = new FileInputStream(myFile);
//		bis = new BufferedInputStream(fis);
		
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
		
		
//		myProgressBar = new JProgressBar(0, (int)myFile.length());
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
	
	public void run() {
		try {
//			bis.read(myByteArray, 0, myByteArray.length);
//			if (!myEncryptionType.equals("None")){
//				System.out.println("FileSender: " + myEncryptionKey);
//				myByteArray = Cryptograph.encryptFile(myByteArray, myEncryptionType, myEncryptionKey);
//			}
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
//			os.write(myByteArray, 0, myByteArray.length);
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
