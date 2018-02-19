
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
        try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(
	        		clientSocket.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	        	myObservable.sendUpdate(inputLine);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
 
    public void startConnection(String ip, int port){
        try {
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
    	public void sendUpdate(String inString){
    		setChanged();
    		notifyObservers(inString);
    	}
    	
    }

}
