package client_side;

import java.util.List;

public class DisconnectCommand implements Command {

	//disconnect server and client
	@Override
	public int execute(List<Expression> args) {
		SymbolTable.getTable().sendToClient("bye");
		//wait 5 seconds and then close the server
		new Thread(()-> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {e.printStackTrace();}
			SymbolTable.getTable().stopServer();
		});
		return 0;
	}

	@Override
	public int numOfArguments() {
		return 0;
	}

}
