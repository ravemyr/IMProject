package project;
/**
 * Client
 * 
 * Created 2018-02-19
 */
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Class for handling the connection to a server
 * @author Gustav
 *
 */
public class Client extends Thread{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientObservable myObservable;
    
    /**
     * Constructor
     */
    public Client(){
    	myObservable = new ClientObservable();
    }
    
    /**
     * Client must be run on a new thread and wait for input
     */
    public void run() {
        try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(
	        		clientSocket.getInputStream()));
	        String inputLine;
	        while (true) {
	        	inputLine = in.readLine();
	        	myObservable.sendUpdate(inputLine);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Connect to a server
     * @param ip
     * @param port
     */
    public void startConnection(String ip, int port){
        try {
			clientSocket = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
        this.start();
    }
    
    /**
     * Send string to server
     * @param msg
     * @throws IOException
     */
    public void sendMessage(String msg) throws IOException {
        out.println(msg);
    }
    
    public void sendFile(File myFile) {

    }
    
    /**
     * Stop connection to a server
     * @throws IOException
     */
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    
    /**
     * Return the observable
     * @return
     */
    public ClientObservable getObservable(){
    	return myObservable;
    }
    
    /**
     * Class for sending incoming strings to observers
     * @author Gustav
     *
     */
    class ClientObservable extends Observable{

    	/**
    	 * Send string to observers
    	 * @param inString
    	 */
    	public void sendUpdate(String inString){
    		setChanged();
    		notifyObservers(inString);
    	}
    	
    }

}
