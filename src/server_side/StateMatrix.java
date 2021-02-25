package server_side;

import java.util.HashSet;
import java.util.Set;

public class StateMatrix<T> implements Searchable<T> {

	 State<T>[][] mat;
	 State<T> initial, goal;
	
	public StateMatrix() {
		mat = null;
	}
	
	public StateMatrix(int rows, int cols) {
		mat = new State[rows][cols];
		this.initial = null;
		this.goal = null;
	}

	@Override
	public State<T> getInitialState() {
		return initial;
	}

	@Override
	public State<T> getGoalState() {
		return goal;
	}
	

	@Override
	public Set<State<T>> getAllPossibleStates(State<T> s) {
		// get s.state's coordinates, add all neighbors to set
		Set<State<T>> set = new HashSet<State<T>>();
		String str = s.getState().toString();
		int i = Integer.parseInt(str.split(",")[0]), j = Integer.parseInt(str.split(",")[1]);
		if (i > 0) set.add(mat[i-1][j]); //above
		if (i < mat.length-1) set.add(mat[i+1][j]); //below
		if (j > 0) set.add(mat[i][j-1]); //left
		if (j < mat[0].length-1) set.add(mat[i][j+1]); //right
		return set;
	}
	

/* only needed if mat is private
	public State<T> getStateInMat(int row, int col) {
		return mat[row][col];
	}

	public void setStateInMat(State<T> state, int row, int col) {
		mat[row][col] = state;
	}
*/
	
}
