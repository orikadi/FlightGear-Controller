package client_side;

import java.util.List;

public class SleepCommand implements Command {

	@Override
	public int execute(List<Expression> args) {
		long time = (long) Double.parseDouble(args.get(0).calculate());
		try {
			Thread.sleep(time);
		} catch (NumberFormatException e) {e.printStackTrace();} 
		catch (InterruptedException e) {e.printStackTrace();}
		return 1;
	}

	@Override
	public int numOfArguments() {
		return 1;
	}

}
