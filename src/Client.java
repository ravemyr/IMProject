
import java.net.*;
import java.io.*;
import java.util.*;

public class Client extends Thread{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientObservable myObservable;
 
    public void startConnection(String ip, int port) 
    		throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(
        		clientSocket.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
        	myObservable.sendUpdate(inputLine);
        }
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
    
    private class ClientObservable extends Observable{
    	public void sendUpdate(String inString){
    		setChanged();
    		notifyObservers(inString);
    	}
    	
    }

}
