	package client_side;

import java.util.HashMap;

public class CommandFactory {

	private interface CommandCreator{
		public Command create();
	}
	
	HashMap<String,CommandCreator> commandCreators;
	
	public CommandFactory() {
		commandCreators = new HashMap<>();
		commandCreators.put("openDataServer", ()->new OpenServerCommand());
		commandCreators.put("connect", ()->new ConnectCommand());
		commandCreators.put("var", ()->new DefineVarCommand());
		commandCreators.put("=", ()->new EqualsCommand());
		commandCreators.put("while", ()->new WhileCommand());
		//commandCreators.put("if", ()->new IfCommand());
		commandCreators.put("disconnect", ()->new DisconnectCommand());
		commandCreators.put("return", ()->new ReturnCommand());
		commandCreators.put("print", ()->new PrintCommand());
		commandCreators.put("sleep", ()->new SleepCommand());
	}
	
	public Command getCommand(String commandName) {
		CommandCreator c = commandCreators.get(commandName);
		if (c!=null)
			return c.create();
		return null;
	}
	
	public boolean isCommand(String commandName) {
		CommandCreator c = commandCreators.get(commandName);
		return c!=null;
	}
}
