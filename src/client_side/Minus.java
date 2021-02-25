package client_side;

public class Minus extends BinaryExpression{
	Minus(Expression left, Expression right) {
		super(left, right);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String calculate() {
		double outcome = Double.parseDouble(left.calculate()) - Double.parseDouble(right.calculate());
		return outcome+"";
		//return left.calculate() - right.calculate();
	}
}
