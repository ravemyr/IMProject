import java.net.*;
import java.util.*;
import java.io.*;

public class Server {
	
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> myClientHandlerList;
    
    public Server(){
    	myClientHandlerList = new ArrayList<ClientHandler>();
    }
    
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        ClientHandler tempClient;
		while (true){
        	tempClient = new ClientHandler(serverSocket.accept());
			tempClient.start();
            myClientHandlerList.add(tempClient);
		}
    }
 
    public void stop() throws IOException {
        serverSocket.close();
    }
    
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(4000);
    }
 
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private int clientNumber;
 
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientNumber = myClientHandlerList.size();
        }
 
        public void run(){
        	try{
	            out = new PrintWriter(clientSocket.getOutputStream(), true);
	            in = new BufferedReader(
	              new InputStreamReader(clientSocket.getInputStream()));
	             
	            String inputLine;
//	            while ((inputLine = in.readLine()) != null) {
	            while (true) {
	            	System.out.println("Sup2");
	            	inputLine = in.readLine();
	            	System.out.println(inputLine + "END");
	            	distributeMessage(inputLine);
	            	System.out.println("Sup");
//	                if (".".equals(inputLine)) {
//	                    out.println("bye");
//	                    break;
//	                }
//	                out.println(inputLine);
	            }
	 
//	            in.close();
//	            out.close();
//	            clientSocket.close();
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        }
        
        public void distributeMessage(String msg){
        	for (ClientHandler a : myClientHandlerList){
        		if (a.clientNumber != this.clientNumber){
        			a.out.println(msg);
        		}
        	}
        }
    }

}
