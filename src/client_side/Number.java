package client_side;

public class Number implements Expression {
	private double value;
	Number(double value) {
		this.value = value;
	}
	@Override
	public String calculate() {
		return value+"";
	}
}
