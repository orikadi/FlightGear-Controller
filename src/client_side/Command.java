package client_side;

import java.util.List;

public interface Command {
	public int execute(List<Expression> args);
	public int numOfArguments();
}
