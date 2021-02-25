package client_side;

public class Plus extends BinaryExpression {

	Plus(Expression left, Expression right) {
		super(left, right);
		
	}

	@Override
	public String calculate() {
		double outcome = Double.parseDouble(left.calculate()) + Double.parseDouble(right.calculate());
		return outcome+"";
	}
	
}
