package client_side;


import java.util.List;

//connects to a server
public class ConnectCommand implements Command {

	@Override
	public int execute(List<Expression> args) {
		String ip = args.get(0).calculate(); 
		int port = (int) Double.parseDouble(args.get(1).calculate());
		DataSenderClient client = new DataSenderClient(ip, port);
		new Thread(()->client.connect()).start();
		return 2;
	}


	@Override
	public int numOfArguments() {
		return 2;
	}

}
