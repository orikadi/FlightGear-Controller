package client_side;


import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class WhileCommand implements Command{

	//read condition/s until '{'
	// until '}' - copy to new list (regular list)
	// while condition=true - new iterator, run through all expressions until end
	@Override
	public int execute(List<Expression> args) {
		ListIterator<Expression> it = args.listIterator();
		Expression condition = it.next();
		LinkedList<Expression> loopList = new LinkedList<>(); 
		String s = it.next().calculate();
		if (!s.equals("{")) {
			System.out.println("Wrong syntax: got "+s+" expected {");
			//exit
		}
		Expression e = it.next();
		CommandFactory fc = new CommandFactory();
		while (!e.calculate().equals("}")/*!OtherToken.class.isInstance(e) || fc.isCommand(e.calculate()) || e.calculate().equals("{")*/) { //create loop's list (runs until "}")
				loopList.add(e);
				e = it.next();
		}
		loopList.add(e);
		int loopEnd = it.previousIndex(); //loop end index
		while (condition.calculate().equals("true")) { //run looped list
			//System.out.println("starting loop. alt value is "+SymbolTable.getTable().getValue("alt"));
			it = loopList.listIterator(); 
			while (it.hasNext()) {
				e = it.next();
				Command c = fc.getCommand(e.calculate());
				if (c!=null) {
					int jump;
					//check if is a command with irregular arguments taking
					if (c instanceof EqualsCommand) {
						String right = it.next().calculate();
						if (right.equals("bind") || right.equals("--")) //in case of these sublist+1
							jump = c.execute(loopList.subList(it.previousIndex()-2, it.nextIndex()+1));
						else jump = c.execute(loopList.subList(it.previousIndex()-2, it.nextIndex()));
						jump--; //jumped once already
					}
					else if (c instanceof WhileCommand)
						jump = c.execute(loopList.subList(it.nextIndex(), loopList.size()-1));
					else jump = c.execute(loopList.subList(it.nextIndex(), it.nextIndex()+c.numOfArguments()));
					it = loopList.listIterator(it.nextIndex()+jump); //move list iterator
				}
			}
		}
		return loopEnd+1;
	}


	@Override
	public int numOfArguments() {
		return 0;
	}

}
	