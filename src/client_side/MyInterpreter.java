package client_side;

import java.util.List;


import client_side.Interpreter;

public class MyInterpreter {

	public static int interpret(String[] lines){
		Interpreter in = new Interpreter();
		List<String> list = in.lexer(lines);
		in.parser(list);
		//SymbolTable.getTable().stopServer();
		return Interpreter.returnValue;
	}

}
