/**
 * Server
 * 
 * Created 2018-02-19
 */

import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Class for keeping track of connected clients and makes sure every
 * client stays updated
 * @author Gustav
 *
 */
public class Server {
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> myClientHandlerList;
    
    /**
     * Constructor, create empty list to keep connected clients
     */
    public Server(){
    	myClientHandlerList = new ArrayList<ClientHandler>();
    }
    
    /**
     * Starting sequence for the server. Creates server socket and
     * waits for client to connect. Add ClientHandler to list.
     * @param port
     * @throws IOException
     */
    public void start(int port) throws IOException {
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
    public void stop() throws IOException {
        serverSocket.close();
    }
    
    /**
     * main method to start server and select a port
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(4000);
    }
    
    /**
     * Class for keeping a connection to the server
     * @author Gustav
     *
     */
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private int clientNumber;
        
        /**
         * Constructor, set number for connection
         * @param socket
         */
        public ClientHandler(Socket socket) {
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
	            	distributeMessage(inputLine);
	            }
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
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
    }
}
