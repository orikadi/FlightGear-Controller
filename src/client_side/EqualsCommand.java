package client_side;

import java.util.Iterator;
import java.util.List;

public class EqualsCommand implements Command {

	@Override
	public int execute(List<Expression> args) {
		SymbolTable table =  SymbolTable.getTable();
		Iterator<Expression> it = args.iterator();
		Variable left = (Variable) it.next();
		it.next(); //skip
		Expression right = it.next();
		int i = 1;
		if (right.calculate().equals("bind")) { // in case of bind
			Variable v = (Variable) it.next();
			i++;
			table.bind(left.getName(), v.getName());
		}
		else { //can be variable or number
			boolean minusFlag = false;
			if (right.calculate().equals("--")) {
				right = it.next();
				minusFlag = true;
			}
			double value = Double.parseDouble(right.calculate());
			if (!minusFlag) table.setValue(left.getName(), value);
			else table.setValue(left.getName(), value*(-1));
		}
		return i;
	}



	@Override
	public int numOfArguments() {
		return 2; 
	}

}
