package application;

import java.util.Observable;
import java.util.Observer;
import client_side.Position;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ViewModel extends Observable implements Observer {

	private Model m;
	public DoubleProperty aileron, elevator, throttle, rudder;

	public ViewModel(Model m) {
		this.m = m;
		aileron = new SimpleDoubleProperty();
		elevator = new SimpleDoubleProperty();
		throttle = new SimpleDoubleProperty();
		rudder = new SimpleDoubleProperty();
	}

	public void vm_openDataServer(String port, String lps) {
		if (port != null && lps != null)
			m.openDataServer(port, lps);
	}

	// connect to server as client
	public void vm_connect(String ip, String port) {
		if (ip != null && port != null)
			m.connect(ip, port);
	}

	// organize script by lines and send to model
	public void vm_readScript(String script) {
		m.readScript(script.split("\\r?\\n"));
	}

	// get updated value from model
	@Override
	public void update(Observable o, Object obj) {
		String identifier = obj.toString();
		if (identifier.contains("aileron"))
			aileron.set(m.getAileron());
		else if (identifier.contains("elevator"))
			elevator.set(m.getElevator());
		else if (identifier.contains("throttle"))
			throttle.set(m.getThrottle());
		else if (identifier.contains("rudder"))
			rudder.set(m.getRudder());
		else { //path or location
			setChanged();
			notifyObservers(identifier);
		}
	}

	// transfer data binded property to model
	public void setAileron() {
		m.setAileron(aileron.get());
	}

	public void setElevator() {
		m.setElevator(elevator.get());
	}

	public void setThrottle() {
		m.setThrottle(throttle.get());
	}

	public void setRudder() {
		m.setRudder(rudder.get());
	}

	public void manual() {
		m.manual();
	}

	public void getLatAndLong() {
		m.getLatAndLong();
	}

	public void calculatePath(String ip, int port, int[][] mapData, Position current, Position destination) {
		m.calculatePath(ip, port, mapData, current, destination);
	}

}
