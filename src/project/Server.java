package project;
/**
 * Server
 * 
 * Created 2018-02-19
 */

import java.net.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
    
    /**
     * Constructor, create empty list to keep connected clients
     */
    public Server(){
    	myClientHandlerList = new ArrayList<ClientHandler>();
    }
    
    public void run() {
    	try {
			this.startServer(4000);
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
        ClientHandler tempClient;
		while (true){
        	tempClient = new ClientHandler(serverSocket.accept());
			tempClient.start();
            myClientHandlerList.add(tempClient);
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
        
        /**
         * Constructor, set number for connection
         * @param socket
         */
        public ClientHandler(Socket socket) {
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
	            while (true) {
	            	inputLine = in.readLine();
	            	String verifyStr = verifyType(inputLine);
	            	String tempString;
	            	if (verifyStr.equals("text")) {
	            		distributeMessage(inputLine);
	            	}
	            	else if (verifyStr.equals("filerequest")) {
	            		myFileReceiver = new FileReceiver(inputLine, "Server");
						myReceiverObserver = new ReceiverObserver();
						myFileReceiver.getObservable().addObserver(myReceiverObserver);
//						tempString = askFileAcceptance(inputLine);
//						out.println(tempString);	            		
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
							myFileSender.sendFileTo(clientSocket.getRemoteSocketAddress().toString(), port);							///////////////////////////////////IP HANDLE
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
        
    	private class ReceiverObserver implements Observer{
    		public void update(Observable a, Object str) {
    			String tempString = (String) str;
    			out.println(tempString);
    		}
    	}
        
        public void setFileSender(File inFile) {
        	try {
				myFileSender = new FileSender(inFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
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
			name = stringArray[1].substring(7, stringArray[1].length()-1);
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
			}
			return null;
		}
	
//		public String askFileAcceptance(String msg) {
//			String[] stringArray = msg.split("\\s");
////			for (String a : stringArray) {
////				System.out.println(a);
////			}
//			int len = stringArray.length;
//			String sender = stringArray[1].substring(7, stringArray[1].length()-1);
//			String fileName = stringArray[3].substring(5, stringArray[3].length());
//			String fileSize = stringArray[4].substring(5, stringArray[4].length()-1);
//			StringBuilder question = new StringBuilder();
//			question.append(sender);
//			question.append(" wants to send you file \"");
//			question.append(fileName);
//			question.append(" of size \"");
//			question.append(fileSize);
//			question.append(". Supplied message: ");
//			for (int i = 5; i < len - 2; i++) {
//				question.append(stringArray[i]);
//			}
//			int ans = JOptionPane.showConfirmDialog(new JFrame(), question.toString());
//			StringBuilder outString = new StringBuilder();
//			if (ans == JOptionPane.YES_OPTION) {
//				myFileReceiver = new FileReceiver(fileName, Integer.parseInt(fileSize));
//				myFileReceiver.start();
//				String respons = JOptionPane.showInputDialog("Leave reply message");
//		    	outString.append("<message");
//		    	outString.append(" sender=Server");
//		    	outString.append("> ");
//		    	outString.append("<filerespons");
//		    	outString.append(" reply=yes");
//		    	outString.append(" port=" + myFileReceiver.getPort() + "> ");
//		    	outString.append(respons);
//		    	outString.append(" </filerespons> ");
//		    	outString.append("</message> ");
//		        return outString.toString();
//			}
//			else {
//				String respons = JOptionPane.showInputDialog("Leave reply message");
//		    	outString.append("<message");
//				String name = "Server";
//		    	outString.append(" sender=" + name);
//		    	outString.append("> ");
//		    	outString.append("<filerespons");
//		    	outString.append(" reply=no");
//		    	outString.append(" port=99999> ");
//		    	outString.append(respons);
//		    	outString.append(" </filerespons> ");
//		    	outString.append("</message> ");
//		        return outString.toString();
//			}
//		}
		
		
    }
}
