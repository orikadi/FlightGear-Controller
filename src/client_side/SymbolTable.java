package client_side;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import application.MapController;


//thread safe singleton symbol table
public class SymbolTable extends Observable {
	
	//inner class loaded only upon demand - thread safe
	private static class SymbolTableHelper {
		public static final SymbolTable instance = new SymbolTable();
	}
	
	private Map<String, MyDouble> sMap;
	private DataReaderServer server; //added for closing the server via stop()
	
	private SymbolTable() {
		sMap = new HashMap<>();
		pathsFromSim();
	}
	
	
	public static SymbolTable getTable() {
		return SymbolTableHelper.instance;
	}
	
	//set var2 to point to the same value as var1
	public void bind(String var1, String var2) {
		//System.out.println("binding "+var1+" to "+var2);
		sMap.put(var1, sMap.get(var2));
	}
	
	//set value and send to client if path exists
	public void setValue(String var, double value) {
		if (!isSymbol(var)) {
			System.out.println("Error: tried retrieving a var that doesnt exist in Symbol Table");
			//exit
		}
		String path = sMap.get(var).getPath();
		if (path!=null)  //if has path - send update request to simulator
			sendToClient(path+" "+value);
		else sMap.get(var).setValue(value); //if no path - update values locally
	}
	public void sendToClient(String toSend) {
		setChanged();
		notifyObservers(toSend);
	}
	
	public Double getValue(String var) {
		try {
			return sMap.get(var).getValue();
		}catch (NullPointerException e) {
			System.out.println("Error: "+var+" is null");
			e.printStackTrace();
		}
		return null;
	}
	
	
	//add new symbol without a path
	public void addSymbol(Variable v) {
		sMap.put(v.getName(), new MyDouble());
	}

	public boolean isSymbol(String var) {
		return sMap.containsKey(var);
	}
	
	//given a list of vars, return a list of the matching value references
	public List<MyDouble> varsToValues(List<String> vars) {
		List<MyDouble> values = new LinkedList<>();
		for (String var : vars) 
			values.add(sMap.get(var));
		return values;
	}
	
	
	//initiate paths updated by sim
	private void pathsFromSim() {
		/*
		(sMap.put("simX", new MyDouble(0.0, "simX"));
		sMap.put("simY", new MyDouble(0.0, "simY"));
		sMap.put("simZ", new MyDouble(0.0, "simZ"));
		*/
		
		sMap.put("/controls/flight/speedbrake", new MyDouble(0.0,"/controls/flight/speedbrake"));
		sMap.put("/instrumentation/airspeed-indicator/indicated-speed-kt", new MyDouble(0.0,"/instrumentation/airspeed-indicator/indicated-speed-kt"));
		sMap.put("/instrumentation/altimeter/indicated-altitude-ft", new MyDouble(0.0,"/instrumentation/altimeter/indicated-altitude-ft"));
		sMap.put("/instrumentation/altimeter/pressure-alt-ft", new MyDouble(0.0,"/instrumentation/altimeter/pressure-alt-ft"));
		sMap.put("/instrumentation/attitude-indicator/indicated-pitch-deg", new MyDouble(0.0,"/instrumentation/attitude-indicator/indicated-pitch-deg"));
		sMap.put("/instrumentation/attitude-indicator/indicated-roll-deg", new MyDouble(0.0,"/instrumentation/attitude-indicator/indicated-roll-deg"));
		sMap.put("/instrumentation/attitude-indicator/internal-pitch-deg", new MyDouble(0.0,"/instrumentation/attitude-indicator/internal-pitch-deg"));
		sMap.put("/instrumentation/attitude-indicator/internal-roll-deg", new MyDouble(0.0,"/instrumentation/attitude-indicator/internal-roll-deg"));
		sMap.put("/instrumentation/encoder/indicated-altitude-ft", new MyDouble(0.0,"/instrumentation/encoder/indicated-altitude-ft"));
		sMap.put("/instrumentation/encoder/pressure-alt-ft", new MyDouble(0.0,"/instrumentation/encoder/pressure-alt-ft"));
		sMap.put("/instrumentation/gps/indicated-altitude-ft", new MyDouble(0.0,"/instrumentation/gps/indicated-altitude-ft"));
		sMap.put("/instrumentation/gps/indicated-ground-speed-kt", new MyDouble(0.0,"/instrumentation/gps/indicated-ground-speed-kt"));
		sMap.put("/instrumentation/gps/indicated-vertical-speed", new MyDouble(0.0,"/instrumentation/gps/indicated-vertical-speed"));
		sMap.put("/instrumentation/heading-indicator/indicated-heading-deg", new MyDouble(0.0,"/instrumentation/heading-indicator/indicated-heading-deg"));
		sMap.put("/instrumentation/magnetic-compass/indicated-heading-deg", new MyDouble(0.0,"/instrumentation/magnetic-compass/indicated-heading-deg"));
		sMap.put("/instrumentation/slip-skid-ball/indicated-slip-skid", new MyDouble(0.0,"/instrumentation/slip-skid-ball/indicated-slip-skid"));
		sMap.put("/instrumentation/turn-indicator/indicated-turn-rate", new MyDouble(0.0,"/instrumentation/turn-indicator/indicated-turn-rate"));
		sMap.put("/instrumentation/vertical-speed-indicator/indicated-speed-fpm", new MyDouble(0.0,"/instrumentation/vertical-speed-indicator/indicated-speed-fpm"));
		sMap.put("/controls/flight/aileron", new MyDouble(0.0,"/controls/flight/aileron"));
		sMap.put("/controls/flight/elevator", new MyDouble(0.0,"/controls/flight/elevator"));
		sMap.put("/controls/flight/rudder", new MyDouble(0.0,"/controls/flight/rudder"));	
		sMap.put("/controls/flight/flaps", new MyDouble(0.0,"/controls/flight/flaps"));
		sMap.put("/controls/engines/current-engine/throttle", new MyDouble(0.0,"/controls/engines/current-engine/throttle"));
		sMap.put("/engines/engine/rpm", new MyDouble(0.0,"/engines/engine/rpm"));
		
		sMap.put("/position/latitude-deg", new MyDouble(0.0,"/position/latitude-deg"));
		sMap.put("position/longitude-deg", new MyDouble(0.0,"position/longitude-deg"));
	}
		
	//update values sent from sim
	public void updateValuesFromSim(Double[] values) {
		/*
		if (!(sMap.get("simX").getValue() == values[0]))
			sMap.get("simX").setValue(values[0]);
		if (!(sMap.get("simY").getValue() == values[1]))
			sMap.get("simY").setValue(values[1]);
		if (!(sMap.get("simZ").getValue() == values[2]))
			sMap.get("simZ").setValue(values[2]);
		*/
		sMap.get("/controls/flight/speedbrake").setValue(values[0]);
		sMap.get("/instrumentation/airspeed-indicator/indicated-speed-kt").setValue(values[1]);
		sMap.get("/instrumentation/altimeter/indicated-altitude-ft").setValue(values[2]);
		sMap.get("/instrumentation/altimeter/pressure-alt-ft").setValue(values[3]);
		sMap.get("/instrumentation/attitude-indicator/indicated-pitch-deg").setValue(values[4]);
		sMap.get("/instrumentation/attitude-indicator/indicated-roll-deg").setValue(values[5]);
		sMap.get("/instrumentation/attitude-indicator/internal-pitch-deg").setValue(values[6]);
		sMap.get("/instrumentation/attitude-indicator/internal-roll-deg").setValue(values[7]);
		sMap.get("/instrumentation/encoder/indicated-altitude-ft").setValue(values[8]);
		sMap.get("/instrumentation/encoder/pressure-alt-ft").setValue(values[9]);
		sMap.get("/instrumentation/gps/indicated-altitude-ft").setValue(values[10]);
		sMap.get("/instrumentation/gps/indicated-ground-speed-kt").setValue(values[1]);
		sMap.get("/instrumentation/gps/indicated-vertical-speed").setValue(values[12]);
		sMap.get("/instrumentation/heading-indicator/indicated-heading-deg").setValue(values[13]);
		sMap.get("/instrumentation/magnetic-compass/indicated-heading-deg").setValue(values[14]);
		sMap.get("/instrumentation/slip-skid-ball/indicated-slip-skid").setValue(values[15]);
		sMap.get("/instrumentation/turn-indicator/indicated-turn-rate").setValue(values[16]);
		sMap.get("/instrumentation/vertical-speed-indicator/indicated-speed-fpm").setValue(values[17]);
		sMap.get("/controls/flight/aileron").setValue(values[18]);
		sMap.get("/controls/flight/elevator").setValue(values[19]);
		sMap.get("/controls/flight/rudder").setValue(values[20]);
		sMap.get("/controls/flight/flaps").setValue(values[21]);
		sMap.get("/controls/engines/current-engine/throttle").setValue(values[22]);
		sMap.get("/engines/engine/rpm").setValue(values[23]);
		
		
		sMap.get("position/longitude-deg").setValue(values[24]);
		sMap.get("/position/latitude-deg").setValue(values[25]);
		//MapController._lat= values[24];
		//MapController._long= values[25];
	}

	public void setServer(DataReaderServer server) {
		this.server = server;
	}
	public void stopServer() {
		if (server!=null)
			server.stop();
	}

	public boolean serverExists() {
		return server!=null;
	}

}
