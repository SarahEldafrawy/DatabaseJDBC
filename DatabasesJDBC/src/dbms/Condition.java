package dbms;


public class Condition {
	private final String column;
	private final String value;
	private final char operation;

	public Condition(String column, String value, char operation) {
		super();
		this.column = column;
		this.value = value;
		this.operation = operation;
	}

	public String getColumn() {
		return column;
	}

	public String getValue() {
		return value;
	}

	public char getOperation() {
		return operation;
	}

}
