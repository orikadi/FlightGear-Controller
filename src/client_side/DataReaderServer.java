package client_side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


public class DataReaderServer {
	
	private int port, lps;
	volatile boolean stop;
	
	public DataReaderServer(int port, int lps) {
		this.port = port;
		this.lps = lps;
	}
	
	public void open() {
		try {
			stop = false;
			ServerSocket server = new ServerSocket(port, lps);
			SymbolTable.getTable().setServer(this); //set server for future closing
			server.setSoTimeout(20000);
			Socket client = server.accept(); //blocking call
			System.out.println("server is connected to simulator");
			read(client.getInputStream());
			client.close();
			server.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void stop() {
		stop = true;
	}
	
	//reads current values from simulator client - 10 lines per second
	public void read(InputStream in) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		SymbolTable table = SymbolTable.getTable();
		try {
			while (!stop) {
				while ((line = reader.readLine()) != null) {
					Double[] values = Arrays.stream(line.split(","))
	                        .map(Double::valueOf)
	                        .toArray(Double[]::new);
					table.updateValuesFromSim(values);
					Thread.sleep(100);
				}
			}
		} catch (IOException | InterruptedException e) {e.printStackTrace();}
	}
	

}
