package server_side;

public interface Searcher<T> {
	public State<T> search(Searchable<T> s);
	public int getNumberOfNodesEvaluated(); //how many actions did the algorithm do. is used to compare algorithms(the less the better)
}
