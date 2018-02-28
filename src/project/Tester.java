package project;


import java.net.*;
import java.util.Enumeration;
import java.io.*;

public class Tester {
	public static void main(String[] args){
		System.out.println(System.getProperty("user.dir"));
		
//		Client client1 = new Client();
//		try {
//			client1.startConnection("10.0.0.144", 4000);
//			client1.sendMessage("send");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		InetAddress addr;
//		try {
//			addr = InetAddress.getLocalHost();
//			String ip = addr.getHostAddress();
//			System.out.println("Ip: " + ip);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
		
		try{
		    System.out.println("Your Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
		    Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		    for (; n.hasMoreElements();)
		    {
		        NetworkInterface e = n.nextElement();
	
		        Enumeration<InetAddress> a = e.getInetAddresses();
		        for (; a.hasMoreElements();)
		        {
		            InetAddress addr = a.nextElement();
		            System.out.println("  " + addr.getHostAddress());
		        }
		    }
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

}
