package client_side;

public class Position {

	int _i, _j;
	
	public Position(Position pos) {
		super();
		this._i = pos.get_i();
		this._j = pos.get_j();
	}
	
	public Position(int _i, int _j) {
		super();
		this._i = _i;
		this._j = _j;
	}
	
	public Position() {
		super();
		this._i = 0;
		this._j = 0;
	}
	
	public Position(String ij) {
		super();
		this._i = Integer.parseInt(ij.split(",")[0]);
		this._j = Integer.parseInt(ij.split(",")[1]);
	}
	
	public Position(int num) {
		super();
		this._i = num;
		this._j = num;
	}

	public int get_i() {
		return _i;
	}

	public void set_i(int _i) {
		this._i = _i;
	}

	public int get_j() {
		return _j;
	}

	public void set_j(int _j) {
		this._j = _j;
	}
	
	public int getTileNumber(int rowLength) {
		return _i*rowLength+_j;
	}
	
	public boolean isMinusOne() {
		return (_i==-1)||(_j==-1);
	}
	
	@Override
	public String toString() {
		return _i+","+_j;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		if(!Position.class.isAssignableFrom(obj.getClass()))
			return false;
		Position position=(Position)obj;
		return (_i==position.get_i())&&(_j==position.get_j());
	}
}
