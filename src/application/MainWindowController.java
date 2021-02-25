package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import client_side.Position;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainWindowController implements Observer, Initializable {

	private static final double JOY_OFFSET_X = 865;
	private static final double JOY_OFFSET_Y = 70;
	ViewModel vm;
	@FXML
	RadioButton rbManual, rbAutopilot;
	@FXML
	Circle joystickCircle, areaCircle; 
	@FXML
	Slider rudderSlider, throttleSlider;
	@FXML
	TextArea scriptArea; 
	DoubleProperty aileron, elevator;
	long timeStamp; // joystick last update timestamp
	@FXML
	MapController mc;
	String _ip;
	int _port = 0;
	
	
	public MainWindowController() {
		aileron = new SimpleDoubleProperty();
		elevator = new SimpleDoubleProperty();
		timeStamp = 0;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Platform.runLater(() -> {
			Model m = new Model();
			ViewModel vm = new ViewModel(m);
			m.addObserver(vm);
			setViewModel(vm);
			vm.addObserver(this);
			System.out.println("opening server..");
			vm.vm_openDataServer("5400", "10");
		});
		// set radio buttons
		ToggleGroup radioButtons = new ToggleGroup();
		rbManual.setToggleGroup(radioButtons);
		rbAutopilot.setToggleGroup(radioButtons);
		mc.setOnMouseClicked(event -> {
			if (mc.isDrawn()) {
				mc.onMouseClick(event.getX(), event.getY());
				calculatePath();
			}
		});
	}

	public void joystickDrag(MouseEvent e) {
		if (rbManual.isSelected()) {
			double x = e.getSceneX() - areaCircle.getLayoutX() - JOY_OFFSET_X;
			double y = e.getSceneY() - areaCircle.getLayoutY() - JOY_OFFSET_Y;
			double r = areaCircle.getRadius();
			double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
			if (distance <= r) {
				joystickCircle.setTranslateX(x);
				joystickCircle.setTranslateY(y);
			} else {
				double angle = Math.atan2(y, x);
				joystickCircle.setTranslateX(areaCircle.getRadius() * Math.cos(angle));
				joystickCircle.setTranslateY(areaCircle.getRadius() * Math.sin(angle));
			}
			// update if 100ms have passed since the last update
			if ((System.currentTimeMillis() - timeStamp) > 100) {
				// normalize range [-r,r] to [-1,1]
				aileron.set(normalize(joystickCircle.getTranslateX(), -r, r));
				elevator.set(normalize(joystickCircle.getTranslateX(), -r, r));
				vm.setAileron();
				vm.setElevator();
				timeStamp = System.currentTimeMillis();
			}
		}
	}

	// normalize m from range [rMin,rMax] to range [-1,1]
	public static double normalize(double m, double rMin, double rMax) {
		return ((m - rMin) / (rMax - rMin)) * 2 - 1;
	}

	public void joystickDragRelease(MouseEvent e) {
		joystickCircle.setTranslateX(areaCircle.getCenterX());
		joystickCircle.setTranslateY(areaCircle.getCenterY());
		// set values to 0 CHECK IF WORKS
		aileron.set(0.0);
		elevator.set(0.0);
		vm.setAileron();
		vm.setElevator();
	}

	public void setViewModel(ViewModel vm) {
		this.vm = vm;
		vm.aileron.bindBidirectional(aileron);
		vm.elevator.bindBidirectional(elevator);
		rudderSlider.valueProperty().addListener((o, oldVal, newVal) -> {
			vm.rudder.setValue(newVal);
			vm.setRudder();
		});
		throttleSlider.valueProperty().addListener((o, oldVal, newVal) -> {
			vm.throttle.setValue(newVal);
			vm.setThrottle();
		});
	}

	// get ip and port input from user and connect as client
	@FXML
	public void connect() {
		Stage newStage = new Stage();
		VBox v = new VBox();
		TextField ip = new TextField("Enter IP");
		TextField port = new TextField("Enter port");
		Button btn = new Button("Connect");
		//btn.
		v.getChildren().add(ip);
		v.getChildren().add(port);
		v.getChildren().add(btn);
		btn.setPrefWidth(150);
		Scene stageScene = new Scene(v, 150, 75);
		newStage.setTitle("Enter server settings");
		newStage.setScene(stageScene);
		newStage.show();
		btn.setOnAction((e) -> {
			vm.vm_connect(ip.getText(), port.getText());
			newStage.close();
			getLatAndLong();
		});
	}

	//read map from file
	@FXML
	public void loadData() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open map file");
		fc.setInitialDirectory(new File("C:\\Users\\Ori\\eclipse-workspace\\Project_Mvvm\\resources"));
		fc.setSelectedExtensionFilter(new ExtensionFilter("CSV files", "*.csv"));
		File chosen = fc.showOpenDialog(null);
		if (chosen != null) {
			mc.handleCSV(chosen);
		}
	}
	
	@FXML
	public void loadScript() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open script file");
		fc.setInitialDirectory(new File("C:\\Users\\Ori\\eclipse-workspace\\Project_Mvvm\\resources"));
		fc.setSelectedExtensionFilter(new ExtensionFilter("TXT files", "*.txt"));
		File chosen = fc.showOpenDialog(null);
		if (chosen != null) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(chosen));
				String line;
				while ((line = br.readLine())!=null) {
					scriptArea.appendText(line);
					scriptArea.appendText("\r\n");
				}
				br.close();
			} catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		}
	}


	// calculate path using server
	@FXML
	public void calculatePathButton() {
		Stage newStage = new Stage();
		VBox v = new VBox();
		TextField ip = new TextField("Enter IP");
		TextField port = new TextField("Enter port");
		Button btn = new Button("Calculate path");
		v.getChildren().add(ip);
		v.getChildren().add(port);
		v.getChildren().add(btn);
		btn.setPrefWidth(150);
		Scene stageScene = new Scene(v, 150, 75);
		newStage.setTitle("Enter server settings");
		newStage.setScene(stageScene);
		newStage.show();
		btn.setOnAction((e) -> {
			_ip = ip.getText();
			_port = Integer.parseInt(port.getText());
			newStage.close();
			calculatePath();
		});
	}

	public void calculatePath() {
		if (!(_port == 0 || _ip == null || mc.isDestOrCurrMinusOne())) {
			vm.calculatePath(_ip, _port, mc.get_mat(), mc.get_currentPos(), mc.get_dest());
		}
	}

	// switch to manual control
	public void manual() {
		System.out.println("Switching to manual");
		vm.manual();
	}

	// read script from text area
	@FXML
	public void autopilot() {
		String script = scriptArea.getText();
		if (script != null) // works?
			vm.vm_readScript(script);
	}

	public void getLatAndLong() {
		vm.getLatAndLong();
	}

	public void newLatLong(double latitude, double longitude) {
		if (mc.isLatLongOutOfBounds(latitude, longitude)) {
			mc.set_currentPos(new Position(-1));
			mc.set_route(null);
		} else {
			mc.newLatLong(latitude, longitude);
		}
	}


	@Override
	public void update(Observable arg0, Object arg1) { // does view need to be an observer?
		String identifier = arg1.toString();
		if (identifier.contains("Up") || identifier.contains("Down")||identifier.contains("Left")||identifier.contains("Right")) 
			mc.set_route(identifier.split(","));
		else {
			double lng = Double.parseDouble(identifier.split(",")[0]);
			double lat = Double.parseDouble(identifier.split(",")[1]);
			newLatLong(lat, lng);
		}
	}
}
