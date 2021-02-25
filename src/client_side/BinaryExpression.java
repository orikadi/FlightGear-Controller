package client_side;


public abstract class BinaryExpression implements Expression {
	protected Expression left, right;
	
	BinaryExpression(Expression left, Expression right) {
		super();
		this.left = left;
		this.right = right;
	}
	
	@Override
	public abstract String calculate();

	public Expression getLeft() {
		return left;
	}

	public void setLeft(Expression left) {
		this.left = left;
	}

	public Expression getRight() {
		return right;
	}

	public void setRight(Expression right) {
		this.right = right;
	}
	
}
