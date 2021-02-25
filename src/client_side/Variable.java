package client_side;

public class Variable implements Expression {
	private String name;
	Variable(String name) {
		this.name = name;
	}
	
	@Override
	public String calculate() {
		//get current value from symbol table
	//	if (!SymbolTable.getTable().isSymbol(name)) {
	//		System.out.println("Error: var "+name+" not initialized");		
	//	}
		Double val = SymbolTable.getTable().getValue(name);
		/* var can have null val so not an error
		if (val == null) {
			System.out.println("Error: var "+name+" has null value");
			//exit
		}*/	
		return val+"";
	}
	
	public String getName() {
		return name;
	}

}
