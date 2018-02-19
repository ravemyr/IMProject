
import java.net.*;
import java.io.*;
import java.util.*;

public class Client extends Thread{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientObservable myObservable;
    
    public Client(){
    	myObservable = new ClientObservable();
    }
    
    public void run() {
    	System.out.println("Client started");
        try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(
	        		clientSocket.getInputStream()));
	        String inputLine;
//	        while ((inputLine = in.readLine()) != null) {
	        while (true) {
	        	System.out.println("Client attempts to get message");
	        	inputLine = in.readLine();
	        	System.out.println("Cliend got message: " + inputLine);
	        	myObservable.sendUpdate(inputLine);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 
    public void startConnection(String ip, int port){
        try {
        	System.out.println("Client connected to Server");
			clientSocket = new Socket(ip, port);
		} catch (IOException e) {
			e.printStackTrace();
		}
        this.start();
    }
 
    public void sendMessage(String msg) throws IOException {
        out.println(msg);
//        String resp = in.readLine();
//        return resp;
    }
    
 
    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
    
    public ClientObservable getObservable(){
    	return myObservable;
    }
    
    class ClientObservable extends Observable{
    	public ClientObservable() {
    		
    	}
    	
    	public void sendUpdate(String inString){
    		setChanged();
    		notifyObservers(inString);
    	}
    	
    }

}
