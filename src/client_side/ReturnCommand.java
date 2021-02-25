package client_side;

import java.util.List;

public class ReturnCommand implements Command {

	@Override
	public int execute(List<Expression> args) {
		int value = 0;
		//thread sleep so sim client's values will be updated
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {e.printStackTrace();}
		try {
			value = (int) Double.parseDouble(args.get(0).calculate());
		} catch(NumberFormatException e) {System.out.println("Invalid syntax in return argument"); e.printStackTrace();}
		Interpreter.returnValue = value;
		return 1;
	}


	@Override
	public int numOfArguments() {
		return 1;
	}

}
