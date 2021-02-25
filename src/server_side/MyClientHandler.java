package server_side;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MyClientHandler implements ClientHandler {
	
	Solver<Searchable, State> solver;
	//FileCacheManager cm = new FileCacheManager();
	
	//EITHER USE ARRAYLIST 2D IN STATEMATRIX OR USE IT TO STORE UNTIL WE HAVE ALL THE DATA
	//need to check if solution exists in file first
	@Override
	public void handleClient(InputStream in, OutputStream out) {
		BufferedReader fromClient = new BufferedReader(new InputStreamReader(in)); //BR is a decorator and ISR is an object adapter
		PrintWriter toClient = new PrintWriter(out);
		solver = new SearcherSolver(new BestFirstSearch<String>()); //solver.solve
		State<String> tempState = null;
		String str, initialPos = null, goalPos = null;
		int i = 0, j = 0;
		int rows, cols = 0;
		ArrayList<ArrayList<State<String>>> stateList = new ArrayList<ArrayList<State<String>>>();
		try {
			while (!(str = fromClient.readLine()).equals("end")) { //get matrix row
				String[] values = str.split(",");
				cols = values.length;
				ArrayList<State<String>> aRow = new ArrayList<>(); //add row
				for(String v: values) {
					tempState = new State<>();
					tempState.setState(i+","+j);
					tempState.setCost(Double.parseDouble(v));
					aRow.add(tempState); //add state to current row
					j++;
				}
				stateList.add(aRow); //add current row to matrix
				j=0;
				i++;
			}
			//read entrance and exit states. coordinates - "i,j"
			initialPos = fromClient.readLine();
			goalPos = fromClient.readLine();
			rows = i;
			//transfer data from temp storage to matrix
			/* int[][] A = new int[mat.size()][];
			   int i=0;
			   for (List<Integer> row : mat){
    				A[i++] = row.toArray(A[i]);
			   }*/
			StateMatrix<String> stateMat = new StateMatrix<>(rows, cols);
			for (i = 0; i<rows; i++) {
				for (j=0; j<cols; j++) {
					stateMat.mat[i][j]= stateList.get(i).get(j);
				}
			}
			//set initial and goal
			int iInitial = Integer.parseInt(initialPos.split(",")[0]), jInitial = Integer.parseInt(initialPos.split(",")[1]);
			int iGoal = Integer.parseInt(goalPos.split(",")[0]), jGoal = Integer.parseInt(goalPos.split(",")[1]);
			stateMat.initial = stateMat.mat[iInitial][jInitial];
			stateMat.goal = stateMat.mat[iGoal][jGoal];
			//solve the problem
			State<String> state = solver.solve(stateMat);
			//return string with directions separated with ","
			//write to string with " ". split by " ". run on string array from the end and re-add ","
			StringBuilder builder = new StringBuilder();
			int iPrev, jPrev;
			while (state.getCameFrom() != null) { //!state.equals(stateMat.getInitialState())
				i = state.getState().charAt(0); j = state.getState().charAt(2);
				tempState = state.getCameFrom();
				iPrev = tempState.getState().charAt(0); jPrev = tempState.getState().charAt(2);
				if (jPrev == j && iPrev > i) builder.append("Up");
				else if (jPrev == j && iPrev < i) builder.append("Down");
				else if (iPrev == i && jPrev < j) builder.append("Right");
				else if (iPrev == i && jPrev > j) builder.append("Left");
				else { System.out.println("Error in path"); fromClient.close(); toClient.close();}
				builder.append(" ");
				state = tempState;
			}
			str = builder.toString();
			builder.setLength(0); //clear builder
			String[] words = str.split(" ");
			for (i = words.length-1; i>0; i--) {
				builder.append(words[i]);
				builder.append(",");
			}
			builder.append(words[i]);
			str = builder.toString();
			//System.out.println(str); //show path on screen
			toClient.println(str);
			toClient.flush();
			fromClient.close();
			toClient.close();
			} catch(Exception e) { e.printStackTrace(); }
		}
}