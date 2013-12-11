package interpBuilder;

import interp.REPL.RuntimeError;

public class Variable {
	public enum VariableLocation {
		FIELD("field"), LOCAL("local"), PARAMETER("parameter"), NONE("none");

		private String variable_location;

		private VariableLocation(String variable_location) {
			this.variable_location = variable_location;
		}

		@Override
		public String toString() {
			return variable_location;
		}
	}

	public enum VariableType {

		BOOLEAN("boolean"), CLASS(""), INT("int"), STRING("string");

		public static VariableType find(String variable_type) {
			return null;
			// for (BinaryOp.BinaryOps x : BinaryOp.BinaryOps.values()) {
			// if (x.toString().equals(op))
			// return x;
			// }
			// throw new Error("internal error; binary operator not found: " +
			// op);
		}

		private String variable_type;

		private VariableType(String variable_type) {
			this.variable_type = variable_type;
		}

		@Override
		public String toString() {
			return variable_type;
		}
	}

	private boolean isInitialized = false;
	private VariableLocation location;
	private String name;
	private int scope;

	VariableType type;

	Object value = null;

	public Variable(VariableType type, VariableLocation location, String name, int scope) {
		this.type = type;
		this.location = location;
		this.name = name;
		this.scope = scope;
	}

	public Variable(VariableType type, VariableLocation location, String name, int scope,
			Object value) {
		this.type = type;
		this.location = location;
		this.name = name;
		switch (type) {
		case INT:
			try {
				this.value = Integer.parseInt(value.toString());
				break;
			} catch (RuntimeError error) {
				System.err.printf(
						"Error: variable is of type int, while value is %s",
						value.toString());
			}
		case STRING:
			this.value = value.toString();
			break;

		case BOOLEAN:
			this.value = value.toString().equals("true") ? true : false;
			break;
		default:
			break;
		}
		setInitialized();
	}

	public VariableLocation getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public VariableType getType() {
		return type;
	}

	public Object getValue() {
		if (isInitialized()) {
			return value;
		}
		return null;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized() {
		isInitialized = true;
	}

	public void setLocation(VariableLocation location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(VariableType type) {
		this.type = type;
	}

	public void setValue(Object value) {
		switch (type) {
		case INT:
			try {
				this.value = Integer.parseInt(value.toString());
				break;
			} catch (RuntimeError error) {
				System.err.printf(
						"Error: variable is of type int, while value is %s",
						value.toString());
			}
		case STRING:
			this.value = value.toString();
			break;

		case BOOLEAN:
			this.value = value.toString().equals("true") ? true : false;
			break;
		}
		this.setInitialized();
	}
	
	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

}
