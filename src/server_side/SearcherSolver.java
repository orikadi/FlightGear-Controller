package server_side;

public class SearcherSolver implements Solver<Searchable, State> { 
	// Problem - Searchable, Solution - T
	Searcher s;
	
	public SearcherSolver(Searcher s) {
		this.s = s;
	}
	@Override
	public State solve(Searchable p) {
		return s.search(p);
	}
	
}
