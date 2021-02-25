package client_side;

//character values that are not variables and therefore do not appear in symbol table
public class OtherToken implements Expression {
	
	String symbol;
	
	public OtherToken(String symbol) {
		this.symbol = symbol;
	}


	
	@Override
	public String calculate() {
		return symbol;
	}

}
