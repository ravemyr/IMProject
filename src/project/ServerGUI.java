package project;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import project.Server.ClientHandler;

public class ServerGUI extends JFrame{
	private Server myServer;
	private FileButton myFileButton;
	
	public static void main(String[] args) {
		ServerGUI myServerGUI = new ServerGUI();
	}
	
	public ServerGUI() {
		this.setVisible(true);
		this.setTitle("Server");
		this.setLocation(700, 30);
//		this.setSize(700, 550);
//		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel bringThePane = new JPanel();
		bringThePane.setLayout(new BoxLayout(bringThePane, BoxLayout.Y_AXIS));
		
		myServer = new Server();
		myServer.start();
		
		myFileButton = new FileButton();
		bringThePane.add(myFileButton);
		
		this.add(bringThePane);
		this.pack();
		
	}
	
	private class FileButton extends JButton implements ActionListener{
		public FileButton() {
			this.setText("Send File");
			this.addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e) {
				JFrame tempFrame = new JFrame();
				tempFrame.setTitle("Server: Choose target client");
				JPanel bringThePane = new JPanel();
				bringThePane.setLayout(new BoxLayout(bringThePane, BoxLayout.Y_AXIS));
				bringThePane.setVisible(true);
				ArrayList<ClientHandler> myClients = myServer.getClients();
				for (ClientHandler a : myClients) {
					ClientButton tempButton = new ClientButton(a);
					bringThePane.add(tempButton);
				}
				tempFrame.add(bringThePane);
				tempFrame.pack();
				tempFrame.setVisible(true);
		}
	}
	
	private class ClientButton extends JButton implements ActionListener{
		private File myFile;
		private ClientHandler targetClient;
		private JFileChooser myFileChooser;
		
		public ClientButton(ClientHandler a) {
			this.targetClient = a;
			this.setText(targetClient.getClientName());
			this.addActionListener(this);
			myFileChooser = new JFileChooser();
		}
		
		public void actionPerformed(ActionEvent e) {
			int returnValue = myFileChooser.showOpenDialog(null);
			String input;
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				myFile = myFileChooser.getSelectedFile();
				input = JOptionPane.showInputDialog("Send message with file: ");
				targetClient.setFileSender(myFile);
				
				StringBuilder outString = new StringBuilder();
		    	outString.append("<message");
				String name = "Server";
		    	outString.append(" sender=" + name);
		    	outString.append("> ");
		    	outString.append("<filerequest");
		    	outString.append(" name=" + myFile.getName());
		    	outString.append(" size=" + myFile.length());
		    	outString.append("> ");
		    	outString.append(input);
		    	outString.append(" </filerequest> ");
		    	outString.append("</message> ");
		        targetClient.sendMessage(outString.toString());
			}
		}
	}

	
}
	

