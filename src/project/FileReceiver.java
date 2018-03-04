package project;
/**
 * FileReceiver
 * 
 * Created 2018-02-19
 */
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import project.Server.ClientHandler;

/**
 * Class for receiving files
 * @author Gustav
 *
 */
public class FileReceiver extends Thread{
	private JFrame myFrame;
	private Socket clientSocket;
    private ServerSocket receiveSocket;
    private int bytesRead;
    private int current;
    private FileOutputStream fos;
    private BufferedOutputStream bos;
    private File myFile;
    private int fileSize;
    private String fileName;
    private int usedPort;
	private JTextPane myTextPane;
	private StyledDocument myDoc;
	private YesButton myYesButton;
	private NoButton myNoButton;
	private ReceiverObservable myObservable;
	private String myName;
	private JProgressBar myProgressBar;
	private String encryptionType;
	private String encryptionKey;
	
    /**
     * Constructor
     */
    public FileReceiver(String inMsg, String inName){
    	myObservable = new ReceiverObservable();
    	myName = inName;
    	
    	current = 0;
		String[] stringArray = inMsg.split("\\s");
		int len = stringArray.length;
		String sender = stringArray[1].substring(7, stringArray[1].length());
		fileName = stringArray[3].substring(5, stringArray[3].length());
		fileSize = Integer.parseInt(stringArray[4].substring(5,
				stringArray[4].length()));
		encryptionType = stringArray[5].substring(5, stringArray[5].length());
		if (!encryptionType.equals("None")){
			encryptionKey = stringArray[6].substring(4, stringArray[6].length()
					-1);
		}
		StringBuilder question = new StringBuilder();
		question.append(sender);
		question.append(" wants to send you file \"");
		question.append(fileName);
		question.append("\" of size \"");
		question.append(fileSize);
		question.append("\" using cryto: \"");
		question.append(encryptionType);
		question.append("\". Supplied message: ");
		for (int i = 7; i < len - 2; i++) {
			question.append(stringArray[i]);
			question.append(" ");
		}
		question.append("\n");
		
		myFrame = new JFrame();
		myFrame.setTitle("FileReceiver");
		myFrame.getContentPane().setLayout(new BoxLayout(
				myFrame.getContentPane(), BoxLayout.Y_AXIS));
		
		myTextPane = new JTextPane();
		myTextPane.setPreferredSize(new Dimension(400,350));
		myTextPane.setEditable(false);
		
		myDoc = myTextPane.getStyledDocument();
		try {
			myDoc.insertString(myDoc.getLength(), question.toString(), null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		myProgressBar = new JProgressBar(0, fileSize);
		
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new GridLayout(1, 2));
		myYesButton = new YesButton();
		myNoButton = new NoButton();
		bringThePane.add(myYesButton);
		bringThePane.add(myNoButton);
		
		myFrame.getContentPane().add(myProgressBar);
		myFrame.getContentPane().add(myTextPane);
		myFrame.getContentPane().add(bringThePane);
		myFrame.pack();
		
		myFrame.setVisible(true);
		
    }
    
    /**
     * Return port to be used
     * @return
     */
    public int getPort() {
    	return usedPort;
    }
    
    /**
     * Client must be run on a new thread and wait for input
     */
    public void run() {
    	usedPort = 5000;
    	boolean tempBool = true;
    	while(tempBool) {
	    	try {
				this.startServer(usedPort);
				tempBool = false;
			} catch (IOException e1) {
				usedPort++;
				tempBool = true;
			}
    	}
    	
    	
        try {        	
        	byte[] myByteArray = new byte[fileSize];
        	InputStream is = clientSocket.getInputStream();
        	fos = new FileOutputStream(System.getProperty("user.dir") + "\\" 
        			+ fileName);    	
        	bos = new BufferedOutputStream(fos);
        	bytesRead = is.read(myByteArray, 0, myByteArray.length);
        	current = bytesRead;
        	
        	do {
        		bytesRead = is.read(myByteArray, current,
        				myByteArray.length-current);
        		
        		if (bytesRead >= 0) {
        			current += bytesRead;
        			myProgressBar.setValue(current);
        		}
        	} while(current < fileSize);
        	
        	if (!encryptionType.equals("None")){
        		System.out.println("FileReceiver: " + encryptionType);
        		myByteArray = Cryptograph.decryptFile(myByteArray,
        				encryptionType, encryptionKey);
        	}
        	
        	bos.write(myByteArray, 0, myByteArray.length);
        	bos.flush();
        	bos.close();
        	
			try {
				myDoc.insertString(myDoc.getLength(), "Done\n", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} 
        catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Sets up a server socket and waits for connection
     * @param port
     * @throws IOException
     */
    public void startServer(int port) throws IOException {
        receiveSocket = new ServerSocket(port);
        clientSocket = receiveSocket.accept();
    }
    
    /**
     * Returns the ReceiverObservable
     * @return
     */
    public ReceiverObservable getObservable() {
    	return myObservable;
    }
    
    /**
     * Sends message to observers
     * @author Gustav
     *
     */
    class ReceiverObservable extends Observable{
    	public void sendUpdate(String msg) {
    		setChanged();
    		notifyObservers(msg);
    	}
    }
    
    /**
     * Class for accepting file
     * @author Gustav
     *
     */
    private class YesButton extends JButton implements ActionListener{
    	/**
    	 * Constructor
    	 */
    	public YesButton(){
    		this.setText("Yes");
    		this.addActionListener(this);
    	}
    	
    	/**
    	 * Accepts file and sends appropriate message to observers
    	 */
		public void actionPerformed(ActionEvent arg0) {
			myYesButton.setEnabled(false);
			myNoButton.setEnabled(false);
			
			
			FileReceiver.this.start();
			
			String respons = JOptionPane.showInputDialog("Leave reply message");
			StringBuilder outString = new StringBuilder();
	    	outString.append("<message");
			String name = myName;
	    	outString.append(" sender=" + name);
	    	outString.append("> ");
	    	outString.append("<filerespons");
	    	outString.append(" reply=yes");
	    	outString.append(" port=" + FileReceiver.this.getPort() + "> ");
	    	outString.append(respons);
	    	outString.append(" </filerespons> ");
	    	outString.append("</message> ");
	        myObservable.sendUpdate(outString.toString());
		}
    	
    }
    
    /**
     * Class for rejecting file
     * @author Gustav
     *
     */
    private class NoButton extends JButton implements ActionListener{
    	/**
    	 * Constructor
    	 */
    	public NoButton(){
    		this.setText("No");
    		this.addActionListener(this);
    	}
    	/**
    	 * Rejects file and sends appropriate message to observers
    	 */
		public void actionPerformed(ActionEvent arg0) {
			String respons = JOptionPane.showInputDialog("Leave reply message");
			StringBuilder outString = new StringBuilder();
	    	outString.append("<message");
			String name = myName;
	    	outString.append(" sender=" + name);
	    	outString.append("> ");
	    	outString.append("<filerespons");
	    	outString.append(" reply=no");
	    	outString.append(" port=99999> ");
	    	outString.append(respons);
	    	outString.append(" </filerespons> ");
	    	outString.append("</message> ");
	    	myObservable.sendUpdate(outString.toString());
			myFrame.dispose();
		}
    	
    }
    

	
}
