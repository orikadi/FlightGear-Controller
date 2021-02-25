package client_side;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DataSenderClient implements Observer {

	private String ip;
	private int port;
	private PrintWriter pw;
	volatile boolean stop;
	BlockingQueue<Runnable> dispatchQueue = new LinkedBlockingQueue<Runnable>();
	Socket theServer;
	
	public DataSenderClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	//connect to server and wait for inputs to send
	public void connect() {
		theServer = null;
		stop = false;
		try {
			theServer = new Socket(ip, port);
			System.out.println("client is connected to simulator");
			pw = new PrintWriter(theServer.getOutputStream());
			SymbolTable.getTable().addObserver(this);
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(!stop) {
						try {
							dispatchQueue.take().run();
						} catch (InterruptedException e) {e.printStackTrace();}
					}
					try {
						theServer.close(); //close socket after stop=true;
					} catch (IOException e) {e.printStackTrace();}
				}
			}).start();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public void stop() {
		try {
			dispatchQueue.put(new Runnable() {
				@Override
				public void run() {
					stop = true;
				}
			});
		} catch (InterruptedException e) {e.printStackTrace();}
		
	}
	
	// send to simulator
	public void send(String str) {
		try {
			dispatchQueue.put(new Runnable() {
				@Override
				public void run() {
					pw.println(str);
					pw.flush();
				}
			});
		} catch (InterruptedException e) {e.printStackTrace();}
	}

	@Override
	public void update(Observable obs, Object o) {
		String s = o.toString(); //received either a path or 'bye'
		if (s.equals("bye")) {
			send("bye");
			stop();
		}
		else {
			send("set "+s); //send in format
		}
	}
	



}
