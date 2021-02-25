package server_side;

import java.util.PriorityQueue;

public abstract class CommonSearcher<T> implements Searcher<T> {
	
	protected PriorityQueue<State> openList;
	private int evaluatedNodes;
	
	public CommonSearcher() {
		openList = new PriorityQueue<State>();
		evaluatedNodes = 0;
	}
	
	protected State popOpenList() {
	evaluatedNodes++;
	return openList.poll();
	}
	
	protected boolean addToOpenList(State s) {
		return openList.add(s);
	}
	
	@Override
	public abstract State search(Searchable s);
	
	@Override
	public int getNumberOfNodesEvaluated() {
		return evaluatedNodes;
	}
}
