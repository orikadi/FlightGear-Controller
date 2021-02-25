package client_side;

import java.util.List;

public class OpenServerCommand implements Command {

	@Override
	public int execute(List<Expression> args) {
		int port = (int) Double.parseDouble(args.get(0).calculate());
		int lps = (int) Double.parseDouble(args.get(1).calculate());
		
		
		new Thread(()->{
			DataReaderServer server = new DataReaderServer(port, lps);
			server.open();
			}).start();
		return 2;
	}


	@Override
	public int numOfArguments() {
		return 2;
	}

}
