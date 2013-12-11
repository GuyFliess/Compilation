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
	private int dimensions;
	private VariableType type;
	private Object[] value = null;

	public Variable(VariableType type, VariableLocation location, String name,
			int scope, int dimensions) {
		this.type = type;
		this.location = location;
		this.name = name;
		this.scope = scope;
		this.dimensions = dimensions;
		this.value = new Object[dimensions];
	}

	public Variable(VariableType type, VariableLocation location, String name,
			int scope, int dimensions, Object[] value) {
		this(type, location, name, scope, dimensions);
		switch (type) {
		case INT:
			try {
				this.value = new Integer[this.dimensions + 1];
				for (int i = 0; i < this.value.length; i++) {
					this.value[i] = Integer.parseInt(value[i].toString());
				}
				break;
			} catch (RuntimeError error) {
				System.err.printf(
						"Error: variable is of type int, while value is %s",
						value.toString());
			}
		case STRING:
			this.value = new String[this.dimensions + 1];
			for (int i = 0; i < this.value.length; i++) {
				this.value[i] = value[i].toString();
			}
			break;
		case BOOLEAN:
			this.value = new Boolean[this.dimensions + 1];
			for (int i = 0; i < this.value.length; i++) {
				this.value[i] = value[i].toString().equals("true") ? true
						: false;
			}
			break;
		default:
			break;
		}
		setInitialized();
	}

	public void setDimension(int dimensions) {
		this.dimensions = dimensions;
		this.value = new Object[dimensions];
	}

	public int getDimension() {
		return this.dimensions;
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

	public Object[] getValue() {
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

	public void setValue(Object[] value, int location) {
		switch (type) {
		case INT:
			try {
				if (location < 0) {
					for (int i = 0; i < this.value.length; i++) {
						this.value[i] = Integer.parseInt(value[i].toString());
					}
				} else {
					this.value[location] = Integer.parseInt(value[0]
							.toString());
				}
				break;
			} catch (RuntimeError error) {
				System.err.printf(
						"Error: variable is of type int, while value is %s",
						value.toString());
			}
		case STRING:
			if (location < 0) {
				for (int i = 0; i < this.value.length; i++) {
					this.value[i] = value[i].toString();
				}
			} else {
				this.value[location] = value[0].toString();
			}
			break;
		case BOOLEAN:
			if (location < 0) {
				for (int i = 0; i < this.value.length; i++) {
					this.value[i] = value[i].toString().equals("true") ? true
							: false;
				}
			} else {
				this.value[location] = value[0].toString().equals("true") ? true
						: false;
			}
			break;
		default:
			break;
		}
		this.setInitialized();
	}

//	public void setValue(Object[] value) {
//		if (this.dimensions == 0) {
//			throw new RuntimeError("Error: variable " + this.name
//					+ " is not an array");
//		}
//		switch (type) {
//		case INT:
//			try {
//				for (int i = 0; i < values.length; i++) {
//					this.values[i] = Integer.parseInt(value[i].toString());
//				}
//				break;
//			} catch (RuntimeError error) {
//				System.err.printf(
//						"Error: variable is of type int, while value is %s",
//						value.toString());
//			}
//		case STRING:
//			for (int i = 0; i < values.length; i++) {
//				this.values[i] = value[i].toString();
//			}
//			break;
//		case BOOLEAN:
//			for (int i = 0; i < values.length; i++) {
//				this.values[i] = value[i].toString().equals("true") ? true
//						: false;
//			}
//			break;
//		default:
//			break;
//		}
//		this.setInitialized();
//	}
//
//	public void setValue(Object value) {
//		if (this.dimensions != 0) {
//			throw new RuntimeError("Error: variable " + this.name
//					+ " is an array");
//		}
//		switch (type) {
//		case INT:
//			try {
//				this.value = Integer.parseInt(value.toString());
//				break;
//			} catch (RuntimeError error) {
//				System.err.printf(
//						"Error: variable is of type int, while value is %s",
//						value.toString());
//			}
//		case STRING:
//			this.value = value.toString();
//			break;
//		case BOOLEAN:
//			this.value = value.toString().equals("true") ? true : false;
//			break;
//		default:
//			break;
//		}
//		this.setInitialized();
//	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

}
