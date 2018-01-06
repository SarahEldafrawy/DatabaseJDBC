package dbms;

import java.util.Map;

public class HandleCondition {
	private String conditionString;
	private Condition[] conditionsArray;

	public void setConditionsArray(Condition[] conditionsArray) {
		this.conditionsArray = conditionsArray;
	}

	private char operation;

	public HandleCondition() {

	}

	public HandleCondition(String conditionString) {
		super();
		this.conditionString = conditionString;
		final String[] conditionsArrayString = splitConditions();
		parseConditions(conditionsArrayString);

	}

	private void parseConditions(String[] conditionsArrayString) {
		conditionsArray = new Condition[2];
		for (int i = 0; i < conditionsArrayString.length; i++) {
			Condition condition = null;
			if (conditionsArrayString[i] != null) {
				if (conditionsArrayString[i].contains("<")) {
					final String tempArray[] = conditionsArrayString[i].split("<", 2);
					condition = new Condition(tempArray[0], tempArray[1], '<');
				} else if (conditionsArrayString[i].contains(">")) {
					final String tempArray[] = conditionsArrayString[i].split(">", 2);
					condition = new Condition(tempArray[0], tempArray[1], '>');
				} else if (conditionsArrayString[i].contains("=")) {
					final String tempArray[] = conditionsArrayString[i].split("=", 2);
					condition = new Condition(tempArray[0], tempArray[1], '=');
				}
				conditionsArray[i] = condition;
			}
		}
	}

	private String[] splitConditions() {
		String[] conditionsArrayString = null;
		if (conditionString != null) {

			if (conditionString.toLowerCase().contains("and")) {
				operation = 'a';
				conditionsArrayString = conditionString.toLowerCase().split("and", 2);

			} else if (conditionString.toLowerCase().contains("or")) {
				operation = 'o';
				conditionsArrayString = conditionString.toLowerCase().split("or", 2);
			} else if (conditionString.toLowerCase().contains("not")) {
				operation = 'n';
				conditionString = conditionString.toLowerCase().replaceAll("not", "");
				conditionsArrayString = new String[2];
				conditionsArrayString[0] = conditionString.trim();
			} else {
				operation = 'e';
				conditionsArrayString = new String[2];
				conditionsArrayString[0] = conditionString.trim();
			}
			return conditionsArrayString;
		}
		return conditionsArrayString;
	}

	public boolean checkCondition(Map<String, String> elementMap) {
		if (conditionsArray == null) {
			return true;
		}
		final boolean[] fitConditions = new boolean[2];
		for (int i = 0; i < conditionsArray.length; i++) {
			if (conditionsArray[i] != null) {
				final String columnName = conditionsArray[i].getColumn();
				final String value = elementMap.get(columnName);
				int integrValue;
				switch (conditionsArray[i].getOperation()) {

				case '=':
					fitConditions[i] = value.equals(conditionsArray[i].getValue());
					break;

				case '<':
					integrValue = Integer.parseInt(value);
					fitConditions[i] = integrValue < Integer.parseInt(conditionsArray[i].getValue());
					break;
				case '>':
					if (!value.equalsIgnoreCase("null")) {
						integrValue = Integer.parseInt(value);
						fitConditions[i] = integrValue > Integer.parseInt(conditionsArray[i].getValue());
					} else {
						fitConditions[i] = false;
					}
					break;
				}
			}
		}
		switch (operation) {
		case 'a':
			return fitConditions[0] && fitConditions[1];
		case 'o':
			return fitConditions[0] || fitConditions[1];
		case 'n':
			return !fitConditions[0];
		case 'e':
			return fitConditions[0];
		}
		return false;

	}

}
