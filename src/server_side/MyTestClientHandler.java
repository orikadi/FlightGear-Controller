package server_side;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;



public class MyTestClientHandler implements ClientHandler {

	Solver<String,String> solver = (String s)->{ return new StringBuilder(s).reverse().toString(); };
	FileCacheManager cm = new FileCacheManager();
	
	@Override
	public void handleClient(InputStream in, OutputStream out) {
		BufferedReader fromClient = new BufferedReader(new InputStreamReader(in)); //BR is a decorator and ISR is an object adapter
		PrintWriter toClient = new PrintWriter(out);
		String problem, solution;
		try {
			while (!(problem = fromClient.readLine()).equals("end")) {
				//checks if there's an existing solution,if none - save solution
				if (cm.solutionExists(problem)) 
					solution = cm.getSolution(problem).toString();
				else {
					solution = solver.solve(problem).toString();
					cm.saveSolution(problem, solution);
				}
				//reply to client
				toClient.println(solution);
				toClient.flush();
				//System.out.println("To Client: "+solution); //show on screen
			}
			fromClient.close();
			toClient.close();
			//System.out.println("Connection ended");
			} catch (IOException e) {e.printStackTrace();}
	}
}


