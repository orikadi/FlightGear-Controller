package server_side;

import java.util.Set;

public interface Searchable<T> {
	State<T> getInitialState();
	State<T> getGoalState(); //if more than one possible goal - change to boolean isGoalState(State)
	Set<State<T>> getAllPossibleStates(State<T> s); //returns a set of all possible next states
}
