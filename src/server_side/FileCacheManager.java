package server_side;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;



public class FileCacheManager<Problem, Solution> implements CacheManager<Problem, Solution> {
	
	HashMap<Problem, Solution> map;
	String fileName;
	static int updateFlag; 
	
	public FileCacheManager() {
		map = new HashMap<Problem, Solution>();
		fileName = "psCache.bin"; // txt / dat / bin?
		updateFlag = 0;
	}
	
	//c'tor for situations where a file already exists
	public FileCacheManager(String name) {
		fileName = name;
		map = new HashMap<Problem, Solution>();
		updateFlag = 0;
		loadMap(); //read map from file
	}
		
	@Override
	public boolean solutionExists(Problem problem) {
		return map.containsKey(problem);
	}
	
	@Override
	public Solution getSolution(Problem problem) {
		return map.get(problem);
	}
	
	@Override
	public void saveSolution(Problem problem, Solution solution) {
		//save to map
		map.put(problem, solution);
		updateFlag++;
		//save map to disk every 100 entrances
		if (updateFlag > 100) {
			updateFlag = 0;
			try {
				ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName)); //FOS with true argument - adding to an existing file
				out.writeObject(map);
				out.flush();
				out.close();
			} catch (FileNotFoundException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }		
		}
	}

	
	//reads map from file
	private void loadMap() {
		try {
			//need to create/open file or is it automatic?
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			map = (HashMap<Problem, Solution>) in.readObject();
			in.close();
		} catch (IOException | ClassNotFoundException e) { e.printStackTrace(); }
	}

}




