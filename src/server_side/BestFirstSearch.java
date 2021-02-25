package server_side;

import java.util.Set;
import java.util.HashSet;


public class BestFirstSearch<T> extends CommonSearcher<T> {

	@Override
	public State search(Searchable s) {
		addToOpenList(s.getInitialState());
		HashSet<State> closedSet = new HashSet<>();
		
		while (!openList.isEmpty()) {
			State<T> n = popOpenList();
			closedSet.add(n);
			if (n.equals(s.getGoalState())) 
				return backTrace(s.getGoalState(), s.getInitialState()); // private method, back traces through the parents
			
			Set<State> successors = s.getAllPossibleStates(n);
			for (State state : successors) {
				if (closedSet.contains(state)) {continue;} //if exists in closedSet, skip
				else if (!openList.contains(state))	{// if successor is new - add him
					state.setCameFrom(n);
					addToOpenList(state);
				}
				else if (n.getCost() < state.getCameFrom().getCost()) { //if this path is better than previous one (the weight is on the node and not on arches)
					state.setCameFrom(n);
					//update place in priority queue
					openList.remove(state);
					openList.add(state);
				}
			}
		}
		return backTrace(s.getGoalState(), s.getInitialState()); // private method, back traces through the parents
	}
	
	//if was protected could move to common searcher
	private State backTrace(State goalState, State initialState) {
		/*State s;
		s = goalState;
		while (!s.equals(initialState)) {
			System.out.println(); //path
		}
		System.out.println(); //initial
		return initialState;*/
		return goalState;
	}
}
