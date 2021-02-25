package server_side;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;



public class MySerialServer implements Server {

	private int port;
	private volatile boolean stop;
	
	public MySerialServer(int port) {
		this.port = port;
	}
	
	@Override
	public void start(ClientHandler ch) {
		stop = false;
		new Thread(()->{
			try { open(ch); } 
			catch (Exception e) {e.printStackTrace();}
		}).start();
	}
	
	
	@Override
	public void stop() {
		stop = true;
	}
	
	private void open(ClientHandler client) throws Exception { 
		ServerSocket server = new ServerSocket(port);
		server.setSoTimeout(50000);
		System.out.println("Server is listening to port 5055..");
		while(!stop){
			try{
					Socket aClient = server.accept(); // blocking call
				try {
						client.handleClient(aClient.getInputStream(), aClient.getOutputStream());
						aClient.close();
						Thread.sleep(1); //fixes socket timeout
				} catch (IOException e) {System.out.println("IOException"); e.printStackTrace();}
			} catch (SocketTimeoutException e) {System.out.println("Socket Timeout Exception"); /*e.printStackTrace();*/}	
		}
		//System.out.println("Closing server...");
	server.close();
	}
}












