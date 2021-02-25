package client_side;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;


//
public class Interpreter {
	LinkedList<Expression> expList; //expression list
	public static int returnValue; //find a way without using static
	
	public Interpreter() {
		expList = new LinkedList<>();
		returnValue = 0;
		SymbolTable.getTable();
	}

	
	//read lines from source to string list 
	public List<String> lexer(InputStream in) {
		Scanner s = new Scanner(in);
		List<String> script = new ArrayList<>();
		String line = null;
		while((line = s.nextLine()) != null)
			script.add(line);
		s.close();
		return script;
	}
	
	public List<String> lexer(String[] lines) {
		return Arrays.asList(lines);
	}
	
	//map script lines to commands and run them
	public void parser(List<String> script) {
		for (String line : script) 
			toExpressions(line);
		ListIterator<Expression> it = expList.listIterator();
		CommandFactory fc = new CommandFactory();
		while (it.hasNext()) {
			Expression e = it.next();
			Command c = fc.getCommand(e.calculate());
			if (c!=null) {
				int jump;
				//check if is a command with irregular arguments taking
				if (c instanceof EqualsCommand) {
					String right = it.next().calculate();
					if (right.equals("bind") || right.equals("--")) //in case of these sublist+1
						jump = c.execute(expList.subList(it.previousIndex()-2, it.nextIndex()+1));
					else jump = c.execute(expList.subList(it.previousIndex()-2, it.nextIndex()));
					jump--; //jumped once already
				}
				else if (c instanceof WhileCommand)
					jump = c.execute(expList.subList(it.nextIndex(), expList.size()));
				else jump = c.execute(expList.subList(it.nextIndex(), it.nextIndex()+c.numOfArguments()));
				it = expList.listIterator(it.nextIndex()+jump); //move list iterator
			}
		}
	}
	
	//turns script line to separate expressions using shunting yard algorithm
	private void toExpressions(String line) {
		Stack<Expression> expStack = new Stack<>();
		Queue<String> queue = new LinkedList<>();
		Stack<String> stack = new Stack<>();
		//String[] tokens2 = line.split("\\s(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
		String[] tokens = line.split("\\s|\"|(?=(?!^)[>!<=])(?<!=)|(?![>!<=])(?<==)|(?<=[-+*/()])|(?=[-+*/()])"); //(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)
		int length = tokens.length;
		for (int i=2; i<length; i++) if (tokens[i].equals("-") && tokens[i-2].equals("=")) tokens[i] = "--"; // when '-' means *(-1), catagorize as '--'
		boolean isPath = false;
		StringBuilder buildPath = new StringBuilder();
		for (String s : tokens) { //"(?<=[>!<=][=]?)|(?=[>!<=][=]?)|(?<=[-+*/=()])|(?=[-+*/()])" // keep  <,>,=<,=>, ==,!=      |(?<=([<>=!]+))|(?=([<>=!]+))
			if (s.equals("")) continue;
			if (isPath) {buildPath.append(s); continue;} //combine path strings
			switch(s) {
			case "*":
			case "/":
			case "(":	
				stack.push(s);
				break;
			case "+":
			case "-":	
				while (!stack.empty() && (!stack.peek().matches("[{(!><=]=?"))) //while x < 5 + 3 { // (|{|<|>|<=|>=|==|!=
					queue.add(stack.pop());
				stack.push(s);
				break;
			case "<":
			case ">":
			case "<=":
			case ">=":
			case "==":
			case "!=":
				while (!stack.empty() && !stack.peek().matches("(|{"))
					queue.add(stack.pop());
				stack.push(s);
				break;
			case ")":
				while (!stack.peek().equals("("))
					queue.add(stack.pop());
				stack.pop();
				break;
			case "{":
				while (!stack.empty() && !stack.peek().equals("{"))
					queue.add(stack.pop());
				stack.push(s);
				break;
			case "}":
				while (!stack.empty())
					queue.add(stack.pop());
				queue.add(s);
				break;
			case "bind":
				isPath = true;
				queue.add(s);
				break;
			default:
				queue.add(s);
				break;
			}
		}
		
		if (isPath) queue.add(buildPath.toString()); //build path
		while (!stack.isEmpty())
			queue.add(stack.pop());
		
		CommandFactory cf = new CommandFactory();
		for (String s : queue) { //find appropriate expressions
			if (cf.isCommand(s)) // if command
				expStack.push(new OtherToken(s));
			else if (isNumeric(s)) //if number
				expStack.push(new Number(Double.parseDouble(s)));
			else if (isVariable(s)) //if a variable
				expStack.push(new Variable(s));
			else if (isOtherToken(s))  //if other approved token
				expStack.push(new OtherToken(s));
			else {
				Expression right = expStack.pop();
				Expression left = expStack.pop();
				switch(s) {
				case "*":
					expStack.push(new Mul(left, right));
					break;
				case "/":
					expStack.push(new Div(left, right));
					break;
				case "+":
					expStack.push(new Plus(left, right));
					break;
				case "-":
					expStack.push(new Minus(left, right));
					break;
				case "<":
					expStack.push(new LessThan(left,right));
					break;
				case ">":
					expStack.push(new GreaterThan(left,right));
					break;
				case "<=":
					expStack.push(new LessEqualTo(left,right));
					break;
				case ">=":
					expStack.push(new GreaterEqualTo(left,right));
					break;
				case "==":
					expStack.push(new EqualTo(left,right));
					break;
				case "!=":
					expStack.push(new NotEqualTo(left,right));
					break;	
				default:
					System.out.println("Wrong syntax: "+s); //MIGHT NOT WORK IF RESTRICTIONS ON OTHERTOKEN ARE GONE
					//exit
				}
			}
		}
		//reverse order and put in expression list
		LinkedList<Expression> tempList = new LinkedList<>();
		while (!expStack.isEmpty())
			tempList.addFirst(expStack.pop());
		expList.addAll(tempList);
	}
	
	public boolean isNumeric(String str) {
		return str.matches("\\d+(\\.\\d+)?");
	}
	public boolean isVariable(String str) { // variables and paths
		return (str.matches("^[a-zA-Z0-9]+")||str.matches("^\\/([A-z0-9-_+]+\\/)*[A-z0-9-_+]+")) && !str.equals("bind"); 
	}
	public boolean isOtherToken(String str) { //other approved tokens
		String zeroTo255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
		String ip_regex = zeroTo255 + "\\." + zeroTo255 + "\\."
                + zeroTo255 + "\\." + zeroTo255;
		return str.matches("^[a-zA-Z]+") || str.matches("\\{|\\}") || str.matches(ip_regex) || str.matches("--");
	}

}
