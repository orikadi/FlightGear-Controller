package server_side;

public class State<T> implements Comparable<State<T>> {
	
	private T state;
	private double cost;
	private State<T> cameFrom;
	
	//IMPLEMENT HASHCODE FUNC FOR THE ALGORITHMS
	//NEED TO TURN SERIALIZABLE?
	public State() {
		state = null;
		cost = 0.0; //considering 0 isn't an option
		cameFrom = null;
	}
	public State(T state) {
		this.state = state;
	}
	
	//is the hashcode okay?
	public int hashCode() {
		return state.hashCode() + (int)cost;
	}
	
	public boolean equals(State s) {
		return state.equals(s.state);
	}

	//Getters&Setters
	public T getState() {
		return state;
	}

	public void setState(T state) {
		this.state = state;
	}
	
	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public State getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(State cameFrom) {
		this.cameFrom = cameFrom;
	}
	
	@Override
	public int compareTo(State<T> other) {
		if (this.cost < other.cost) return -1;
		else if (this.cost > other.cost) return 1;
		return 0;
	}


}
