package application;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import client_side.Position;

public class MapController extends Canvas {

	static final double ONE_DEG_IN_KM = 98.1605;
	
	int[][] _mat;
	Color[] _colors;
	Position _currentPos = new Position(0);
	Position _dest = new Position(-1);
	Position _routeStartPosition = new Position(-1);
;	GraphicsContext _gContext = getGraphicsContext2D();
	String[] _route;
	double _0lat;
	double _0long;
	double _delt;
	double _width;
	double _height;
	
	public MapController() {super();}
	
	public int[][] get_mat() {
		return _mat;
	}

	public void set_mat(int[][] _mat) {
		this._mat = _mat;
		colorer();
		_width = getWidth() /_mat[0].length;
		_height = getHeight() /_mat.length;
	}

	public Position get_currentPos() {
		return _currentPos;
	}

	public void set_currentPos(Position _currentPos) {
		if(this._currentPos!=_currentPos) {
			this._currentPos = _currentPos;
			redraw();
		}
	}

	public Position get_dest() {
		return _dest;
	}

	public void set_dest(Position _dest) {
		if(this._dest!=_dest) {
			this._dest = _dest;
			redraw();
		}
	}

	public void set_route(String[] _route) {
		this._route = _route;
		this._routeStartPosition=new Position(_currentPos);
		redraw();
	}

	public void redraw() {
		if(_mat != null) {
			redrawColor();
			if(_mat.length <= 30 && _mat[0].length <= 30) {
				redrawHeight();
			}
			if(_route != null) {
				redrawRoute();
			}
			if(!_dest.isMinusOne()) {
				redrawDestination();
			}
			if(!_currentPos.isMinusOne()) {
				redrawCurrentPosition();
			}
		}
	}
	
	public void redrawCurrentPosition() {
		_gContext.setFill(Color.PINK);
		_gContext.fillOval(_currentPos.get_j()*_width, _currentPos.get_i()*_height, _width, _height);
	}

	public void redrawDestination() {
		_gContext.setFill(Color.BLUE);
		_gContext.fillOval(_dest.get_j()*_width, _dest.get_i()*_height, _width, _height);
	}

	public void redrawRoute() {
		double x=_routeStartPosition.get_j()*_width+_width/2-2;
		double y=_routeStartPosition.get_i()*_height+_height/2-2;
		_gContext.setFill(Color.BLACK);
		for(String direction : _route) {
			switch (direction) {
			case "Up":
				_gContext.fillRect(x, y-_height, 4, _height+4);
				y-=_height;
				break;
			case "Down":
				_gContext.fillRect(x, y, 4, _height+4);
				y+=_height;
				break;
			case "Left":
				_gContext.fillRect(x-_width, y, _width, 4);
				x-=_width;
				break;
			case "Right":
				_gContext.fillRect(x, y, _width, 4);
				x+=_width;
				break;
			}
		}
	}

	public void redrawColor() {
		for (int i = 0; i < _mat.length; i++) {
			for (int j = 0; j < _mat[i].length; j++) {
				_gContext.setFill(_colors[_mat[i][j]]);
				_gContext.fillRect(j*_width, i*_height, _width, _height);
			}
		}
	}
	
	public void redrawHeight() {
		_gContext.setFill(Color.BLACK);
		_gContext.setTextAlign(TextAlignment.CENTER);
		_gContext.setTextBaseline(VPos.CENTER);
		for (int i = 0; i < _mat.length; i++) {
			for (int j = 0; j < _mat[i].length; j++) {
				_gContext.fillText(String.valueOf(_mat[i][j]), j*_width+_width/2, i*_height+_height/2);
			}
		}
	}
	
	public int findMax() {
		int max = 0;
		for(int i=0 ; i<_mat.length ; i++){
			for(int j=0 ; j<_mat[0].length ; j++) {
				if(_mat[i][j] > max)
					max = _mat[i][j];
			}
		}
		return max;
	}
	
	public boolean isDrawn() {
		return _mat != null;
	}
	
	public void onMouseClick(double x , double y) {
		set_dest(new Position(((int)(y/_height)),(int)(x/_width)));
	}
	
	public boolean isDestOrCurrMinusOne() {
		return _currentPos.isMinusOne()||_dest.isMinusOne();
	}
	
	public void colorer() {
		int max = findMax();
		double inc = 255/(double)max;
		this._colors = new Color[max+1];
		for(int i=0 ; i<_colors.length ; i++) {
			this._colors[i] = Color.web(String.format("#%02x%02x%02x", (int)(255 - i*inc),(int)(i*inc),0));
		}
	}
	
	public void handleCSV(File file) {
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(file));
			String line = bReader.readLine();
			_0lat = Double.parseDouble(line.split(",")[0]);
			_0long = Double.parseDouble(line.split(",")[1]);
			line = bReader.readLine();
			_delt = kmToDeg(Math.sqrt(Double.parseDouble(line.split(",")[0])));
			LinkedList<String[]> list = new LinkedList<>();
			while((line=bReader.readLine()) != null) {
				list.add(line.split(","));
			}
			int[][] matrix = new int[list.size()][list.get(0).length];
			int i = 0;
			int j = 0;
			for(String[] sArr : list)
			{
				for(String s : sArr) {
					matrix[i][j] = Integer.parseInt(s);
					j++;
				}
				i++;
				j = 0;
			}
			set_mat(matrix);
			bReader.close();
			_dest = new Position(-1);
			_currentPos = new Position(0);
			_route=null;
			
		} catch (Exception e) {
			System.out.println("Error opening or reading file");
		}
		redraw();
	}
	
	public boolean isLatLongOutOfBounds(double latitude, double longitude) {
		return latitude<_0lat||longitude>_0long||
				latitude>_0lat+_mat[0].length*_delt||longitude<_0long-_mat.length*_delt;
	}
	
	public void newLatLong(double latitude, double longitude) {
			Position p=new Position(
					(int)(Math.abs(longitude-_0long)/_delt),
					(int)(Math.abs(latitude-_0lat)/_delt));
			set_currentPos(p);
	}
	
	static public double degToKm(double degrees) {
		return (degrees/Math.abs(degrees))*Math.toRadians(Math.abs(degrees))*ONE_DEG_IN_KM;
	}
	
	static public double kmToDeg(double km) {
		return km/ONE_DEG_IN_KM;
	}
}
