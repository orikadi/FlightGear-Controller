package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Observable;

import client_side.MyInterpreter;
import client_side.Position;
import client_side.SymbolTable;

public class Model extends Observable {

	private SymbolTable symbolTable;
	Thread t1;

	public Model() {
		symbolTable = SymbolTable.getTable();
	}

	// open server
	public void openDataServer(String port, String lps) {
		String[] lines = { "openDataServer " + port + " " + lps };
		MyInterpreter.interpret(lines);
	}

	// connect to server as client
	public void connect(String ip, String port) {
		String[] lines = { "connect " + ip + " " + port };
		MyInterpreter.interpret(lines);
	}

	public double getAileron() {
		return SymbolTable.getTable().getValue("/controls/flight/aileron"); 
	}

	public void setAileron(double value) {
		symbolTable.sendToClient("/controls/flight/aileron " + value); 
		setChanged();
		notifyObservers("aileron");
	}

	public double getElevator() {
		return SymbolTable.getTable().getValue("/controls/flight/elevator");
	}

	public void setElevator(double value) {
		symbolTable.sendToClient("/controls/flight/elevator " + value);
		setChanged();
		notifyObservers("elevator");
	}

	public double getRudder() {
		return SymbolTable.getTable().getValue("/controls/flight/rudder");
	}

	public void setRudder(double value) {
		symbolTable.sendToClient("/controls/flight/rudder " + value);
		setChanged();
		notifyObservers("rudder");
	}

	public double getThrottle() {
		return SymbolTable.getTable().getValue("/controls/engines/current-engine/throttle");
	}

	public void setThrottle(double value) {
		symbolTable.sendToClient("/controls/engines/current-engine/throttle " + value);
		setChanged();
		notifyObservers("throttle");
	}

	public void sendLatLong(String location) {
		setChanged();
		notifyObservers(location);
	}

	public void readScript(String[] lines) {
		t1 = new Thread(() -> MyInterpreter.interpret(lines));
		t1.start();
	}

	// switch to manual input (joystick)
	public void manual() {
		if (t1 != null) {
			System.out.println("Interrupting autopilot thread");
			t1.interrupt(); // stop?
		}
	}

	//update location
	public void getLatAndLong() {
		new Thread(() -> {
			while (true) {
				Double lat = SymbolTable.getTable().getValue("/position/latitude-deg");
				Double lng = SymbolTable.getTable().getValue("position/longitude-deg");
				sendLatLong(lat.toString() + "," + lng.toString()); // new Pair<Double,Double>(lat,lng)
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	//connect to path finding server and receive route
	public void calculatePath(String ip, int port, int[][] matrix, Position current, Position destination) {
		Socket s = null;
		PrintWriter out = null;
		BufferedReader in = null;
		String sol = null;
		try {
			// Open client
			s = new Socket(ip, port);
			s.setSoTimeout(3000);
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));

			// Send problem
			int i, j;
			for (i = 0; i < matrix.length; i++) {
				for (j = 0; j < matrix[i].length - 1; j++) {
					out.print(matrix[i][j] + ",");
				}
				out.println(matrix[i][j]);
			}
			out.println("end");
			out.println(current);
			out.println(destination);
			out.flush();

			// Receive solution
			sol = in.readLine();
		} catch (SocketTimeoutException e) {
		} catch (IOException e) {
		} finally {
			try {
				in.close();
				out.close();
				s.close();
			} catch (Exception e) {
			}
		}
		if (sol != null) {
			setChanged();
			notifyObservers(sol);
		}
	}
}
