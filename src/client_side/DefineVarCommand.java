package client_side;

import java.util.List;

//defines new variables by putting them into the symbol table
public class DefineVarCommand implements Command {
	@Override
	public int execute(List<Expression> args) {
		Expression varName = args.get(0);
		//add safety-checks
		Variable v = (Variable) varName;
		if (v==null) {
			System.out.println("Error: added non-variable to Define Var Command");
			//exit
			//system.exit?
		}
		SymbolTable.getTable().addSymbol(v);
		return 1;
	}


	@Override
	public int numOfArguments() {
		return 1;
	}

}
