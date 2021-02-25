package server_side;

public interface CacheManager<Problem, Solution> {
	public boolean solutionExists(Problem problem);
	public Solution getSolution(Problem problem);
	public void saveSolution(Problem problem, Solution solution);
}
