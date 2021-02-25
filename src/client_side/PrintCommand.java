package client_side;

import java.util.List;

public class PrintCommand implements Command {

	@Override
	public int execute(List<Expression> args) {
		Variable v = (Variable) args.get(0);
		System.out.println(v.getName());
	/*	if (toPrint.equals("done"))
			new DisconnectCommand().execute(args); //when 'done' is printed, close server and client*/
		return 1;
	}

	@Override
	public int numOfArguments() {
		return 1;
	}

}
