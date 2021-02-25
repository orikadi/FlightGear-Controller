package client_side;

public class GreaterThan extends BinaryExpression {

	GreaterThan(Expression left, Expression right) {
		super(left, right);
	}

	@Override
	public String calculate() {
		if (Double.parseDouble(left.calculate()) > Double.parseDouble(right.calculate()))
			return "true";
		return "false";
	}

}
