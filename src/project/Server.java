package project;
/**
 * Server
 * 
 * Created 2018-02-19
 */

import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

import java.io.*;

/**
 * Class for keeping track of connected clients and makes sure every
 * client stays updated
 * @author Gustav
 *
 */
public class Server extends Thread{
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> myClientHandlerList;
    private int myPort;
    
    /**
     * Constructor, create empty list to keep connected clients
     */
    public Server(int inPort){
    	myPort = inPort;
    	myClientHandlerList = new ArrayList<ClientHandler>();
    }
    
    public void run() {
    	try {
			this.startServer(myPort);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Starting sequence for the server. Creates server socket and
     * waits for client to connect. Add ClientHandler to list.
     * @param port
     * @throws IOException
     */
    public void startServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
//        ClientHandler tempClient;
        ProtoClient tempClient;
		while (true){
			tempClient = new ProtoClient(serverSocket.accept());
			tempClient.start();
//        	tempClient = new ClientHandler(serverSocket.accept());
//			tempClient.start();
//            myClientHandlerList.add(tempClient);
		}
    }
    
    /**
     * Stop/close server
     * @throws IOException
     */
    public void stopServer() throws IOException {
        serverSocket.close();
    }
    
//    /**
//     * main method to start server and select a port
//     * @param args
//     * @throws IOException
//     */
//    public static void main(String[] args) throws IOException {
//        Server server = new Server();
//        server.start(4000);
//    }
    
    public ArrayList<ClientHandler> getClients(){
    	return myClientHandlerList;
    }
    
    /**
     * Class for keeping a connection to the server
     * @author Gustav
     *
     */
    class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private int clientNumber;
        private String name;
        private FileSender myFileSender;
        private FileReceiver myFileReceiver;
        private ReceiverObserver myReceiverObserver;
        private boolean isOpen;
        
        /**
         * Constructor, set number for connection
         * @param socket
         */
        public ClientHandler(Socket socket) {
        	isOpen = true;
        	name = "Anon";
            this.clientSocket = socket;
            this.clientNumber = myClientHandlerList.size();
        }
        
        /**
         * Create new thread to listen for input
         */
        public void run(){
        	try{
	            out = new PrintWriter(clientSocket.getOutputStream(), true);
	            in = new BufferedReader(
	              new InputStreamReader(clientSocket.getInputStream()));
	             
	            String inputLine;
	            while (isOpen) {
	            	inputLine = in.readLine();
	            	String verifyStr = verifyType(inputLine);
	            	if (verifyStr.equals("text")) {
	            		name = updateName(inputLine);
	            		distributeMessage(inputLine);
	            	}
	            	else if (verifyStr.equals("encrypted")) {
	            		name = updateName(inputLine);
	            		distributeMessage(inputLine);
	            	}
	            	else if (verifyStr.equals("keyresponse")) {
	            		distributeMessage(inputLine);
	            	}
	            	else if (verifyStr.equals("keyrequest")) {
	            		distributeMessage(inputLine);
	            	}
	            	else if (verifyStr.equals("filerequest")) {
	            		myFileReceiver = new FileReceiver(inputLine, "Server");
						myReceiverObserver = new ReceiverObserver();
						myFileReceiver.getObservable().addObserver(myReceiverObserver);      		
	            	}
	            	else if (verifyStr.equals("filerespons")) {
						String[] tempStringArray = inputLine.split("\\s");
						StringBuilder tempBuilder = new StringBuilder();
						for (int i = 5; i < tempStringArray.length - 2; i++) {
							tempBuilder.append(tempStringArray[i]);
							tempBuilder.append(" ");
						}
						myFileSender.display("Reply: " + tempStringArray[3].substring(6, tempStringArray[3].length()) + "\n");
						myFileSender.display("Message: " + tempBuilder.toString() + "\n");
						if (tempStringArray[3].substring(6, tempStringArray[3].length()).equals("yes")){
							int port = Integer.parseInt(tempStringArray[4].substring(5,
									tempStringArray[4].length()-1));
							String tempIP = clientSocket.getInetAddress().toString().substring(1);
							System.out.println("Server: IP: " + tempIP 
							+ " Port: " + port);
							myFileSender.sendFileTo(tempIP, port);
						}
	            	}
	            	else {
	            		System.out.println("There is a bug in your code");
	            	}
	            }
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        }
        
        private String updateName(String msg) {
        	String[] tempArray = msg.split("\\s");
        	return tempArray[1].substring(7, tempArray[1].length()-1);
        }
        
    	private class ReceiverObserver implements Observer{
    		public void update(Observable a, Object str) {
    			String tempString = (String) str;
    			out.println(tempString);
    		}
    	}
    	
    	public void closeConnection() {
    		try {
    			out.println("<kick> </kick>");
    			isOpen = false;
	    		out.close();
	    		in.close();
	    		clientSocket.close();
	    		myClientHandlerList.remove(clientNumber);
    		}
    		catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
        
        public void setFileSender(byte[] inArray) {
        	myFileSender = new FileSender(inArray);
        }
        
        public String getClientName() {
        	return name;
        }
        
        /**
         * Method for sending a recieved message to every (other) connected
         * client.
         * @param msg
         */
        public void distributeMessage(String msg){
        	for (ClientHandler a : myClientHandlerList){
        		if (a.clientNumber != this.clientNumber){
        			a.out.println(msg);
        		}
        	}
        }
        
        public void sendMessage(String msg) {
        	out.println(msg);
        }
        
		public String verifyType(String msg) {
			String[] stringArray = msg.split("\\s");
			for (String a : stringArray) {
				if (a.startsWith("<filerequest")) {
					return "filerequest";
				}
				else if (a.startsWith("<text")) {
					return "text";
				}
				else if (a.startsWith("<filerespons")) {
					return "filerespons";
				}
				else if (a.startsWith("<keyrequest")){
					return "keyrequest";
				}
				else if (a.startsWith("<keyresponse")){
					return "keyresponse";
				}
				else if(a.startsWith("<encrypted")){
					return "encrypted";
				}			
			}
			return null;
		}
    }
    
    private class ProtoClient extends Thread{
    	private Socket myTempClient;
        private PrintWriter out;
        private BufferedReader in;
        
    	public ProtoClient(Socket inSocket) {
    		myTempClient = inSocket;
    	}
    	
    	public void run() {
            try {
				out = new PrintWriter(myTempClient.getOutputStream(), true);
	            in = new BufferedReader(
	                    new InputStreamReader(myTempClient.getInputStream()));
	            String inputLine;
	            while (true) {
	            	inputLine = in.readLine();
	            	String verifyStr = verifyType(inputLine);
	            	if (verifyStr.equals("request")){
	            		String[] stringArray = inputLine.split("\\s");
	            		StringBuilder messageBuilder = new StringBuilder();
	            		for (int i = 2; i < stringArray.length-1; i++) {
	            			messageBuilder.append(stringArray[i]);
	            			messageBuilder.append(" ");
	            		}
	            		int retVal = JOptionPane.showConfirmDialog(null, 
	            				messageBuilder.toString());
	            		if (retVal == JOptionPane.YES_OPTION) {
	            			ClientHandler tempClientHandler = new ClientHandler(
	            					myTempClient);
	            			tempClientHandler.start();
	            			myClientHandlerList.add(tempClientHandler);
	            			out.println("<response> ...Connected </response>");
	            			break;
	            		}
	            		else {
	            			out.println("<response> ...Refused </response>");
	            			out.flush();
	            			in.close();
	            			out.close();
	            			myTempClient.close();
	            			break;
	            		}
	            	}
	            	else if (verifyStr.equals("message")){
	            		int retVal = JOptionPane.showConfirmDialog(null, 
	            				"An older client is trying to connect. Accept connection?");
	            		if (retVal == JOptionPane.YES_OPTION) {
	            			ClientHandler tempClientHandler = new ClientHandler(
	            					myTempClient);
	            			tempClientHandler.start();
	            			myClientHandlerList.add(tempClientHandler);
	            			StringBuilder tempBuilder = new StringBuilder();
	            			tempBuilder.append("<message sender=Server> ");
	            			tempBuilder.append("<text color=#000000> ");
	            			tempBuilder.append("Connection accepted ");
	            			tempBuilder.append("</text> </message>");
	            			out.println(tempBuilder.toString());
	            			break;
	            		}
	            		else {
	            			StringBuilder tempBuilder = new StringBuilder();
	            			tempBuilder.append("<message sender=Server> ");
	            			tempBuilder.append("<text color=#000000> ");
	            			tempBuilder.append("Connection refused ");
	            			tempBuilder.append("</text> </message>");
	            			out.println(tempBuilder.toString());
	            			out.flush();
	            			in.close();
	            			out.close();
	            			myTempClient.close();
	            			break;
	            		}
	            	}
	            	
	            }
	            
				
			} catch (Exception e) {
				e.printStackTrace();
			}

    	}
    	
		public String verifyType(String msg) {
			String[] stringArray = msg.split("\\s");
			for (String a : stringArray) {
				if (a.startsWith("<request")) {
					return "request";
				}
				else if (a.startsWith("<message")) {
					return "message";
				}			
			}
			return null;
		}
    	
    }
    
}
