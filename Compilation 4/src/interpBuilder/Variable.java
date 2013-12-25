package interpBuilder;

import interp.Interpreter.RuntimeError;

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

		BOOLEAN("boolean"), CLASS(""), INT("int"), STRING("string"), NULL(
				"null"), VOID("void");

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
	private boolean array;
	private int length;
	private VariableType type;
	private Object value;
	private Variable[] variables;
	private int index;
	private String class_name;

	public Variable(VariableType type, VariableLocation location, String name,
			int scope, boolean array, int length) {
		this.type = type;
		this.location = location;
		this.name = name;
		this.scope = scope;
		this.array = array;
		this.length = length;
		this.class_name = "";
		this.index = 0;
		if (array) {
			this.value = null;
			this.variables = new Variable[length];
			for (int i = 0; i < this.variables.length; i++) {
				this.variables[i] = new Variable(type, location, name, scope,
						false, 0);
			}
		} else {
			if (location == VariableLocation.FIELD) {
				switch (type) {
				case INT:
					this.value = new Integer(0);
					break;
				case BOOLEAN:
					this.value = new Boolean(false);
					break;
				case STRING:
					this.value = new String("");
					break;
				case CLASS:
					this.value = null;
					break;
				default:
					throw new RuntimeError("Invalid field type.");
				}
				this.setInitialized();
			} else {
				this.value = null;
				this.length = 0;
			}
		}
	}

	public Variable(VariableType type, VariableLocation location, String name,
			int scope, boolean array, int length, Object value) {
		this(type, location, name, scope, array, length);
		switch (type) {
		case INT:
			try {
				this.value = Integer.parseInt(value.toString());
				break;
			} catch (RuntimeError error) {
				System.err
						.printf("Error: variable is of type int, while value is of type %s",
								value.toString());
			}
		case STRING:
			try {
				this.value = value.toString();
				break;
			} catch (RuntimeError error) {
				System.err
						.printf("Error: variable is of type string, while value is of type %s",
								value.toString());
			}
		case BOOLEAN:
			try {
				this.value = (Boolean) (value.toString() == "true" ? true
						: false);
				break;
			} catch (RuntimeError error) {
				System.err
						.printf("Error: variable is of type boolean, while value is of type %s",
								value.toString());
			}
		default:
			break;
		}
		setInitialized();
	}

	public void setClass(String class_name) {
		if (this.type == VariableType.CLASS) {
			this.class_name = class_name;
		}
	}

	public String getClassName() {
		return this.class_name;
	}

	public void initializeArray(int length) {
		this.array = true;
		this.length = length;
		this.setInitialized();
		Object value = null;
		switch (type) {
		case INT:
			this.variables = new Variable[length];
			value = new Integer(0);
			break;
		case BOOLEAN:
			this.variables = new Variable[length];
			value = new Boolean(false);
			break;
		case STRING:
			this.variables = new Variable[length];
			value = new Boolean(false);
			break;
		case CLASS:
			this.value = new Variable[length];
			break;
		default:
			throw new RuntimeError("Invalid field type.");
		}
		for (int i = 0; i < this.variables.length; i++) {
			this.variables[i] = new Variable(type, location, name, scope,
					false, 0, value);
			this.variables[i].index = this.index++;
		}
		this.setInitialized();
	}

	public void setValue(Object value) {
		switch (type) {
		case INT:
			try {
				this.value = Integer.parseInt(value.toString());
				break;
			} catch (RuntimeError error) {
				System.err
						.printf("Error: variable is of type int, while value is of type %s",
								value.toString());
			}
		case STRING:
			try {
				if (value == null) {
					this.value = null;
				} else {
					this.value = value.toString();
				}
				break;
			} catch (RuntimeError error) {
				System.err
						.printf("Error: variable is of type string, while value is of type %s",
								value.toString());
			}
		case BOOLEAN:
			try {
				this.value = (Boolean) (value.toString() == "true" ? true
						: false);
				break;
			} catch (RuntimeError error) {
				System.err
						.printf("Error: variable is of type boolean, while value is of type %s",
								value.toString());
			}
		default:
			break;
		}
		this.setInitialized();
	}

	public int getLength() {
		return this.length;
	}

	public boolean isArray() {
		return this.array;
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

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public Variable[] getVariables() {
		return this.variables;
	}

	public int getIndex() {
		return index;
	}

}
