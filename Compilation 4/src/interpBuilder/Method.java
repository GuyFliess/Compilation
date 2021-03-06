package interpBuilder;

import interp.Interpreter.RuntimeError;
import interpBuilder.Variable.VariableLocation;
import interpBuilder.Variable.VariableType;

import java.util.ArrayList;
import java.util.HashMap;

public class Method {
	String name;
	private HashMap<String, Variable> variables;
	int scope;
	VariableType return_type;
	int return_type_dimensions;

	public int getReturnTypeDimensions() {
		return return_type_dimensions;
	}

	public void setReturnTypeDimensions(int return_type_dimensions) {
		this.return_type_dimensions = return_type_dimensions;
	}

	public VariableType getReturnType() {
		return return_type;
	}

	public void setReturnType(VariableType return_type) {
		this.return_type = return_type;
	}

	public Method(String name) {
		this.name = name;
		this.variables = new HashMap<>();
	}

	public void addVariable(Variable variable) {
		this.variables.put(variable.getName(), variable);
	}

	public void addVariable(VariableType variable_type,
			VariableLocation variable_location, String name, int scope,
			boolean array, int length) {
		this.variables.put(name, new Variable(variable_type, variable_location,
				name, scope, array, length));
	}

	public void addVariable(VariableType variable_type,
			VariableLocation variable_location, String name, int scope,
			boolean array, int length, Object[] value) {
		this.variables.put(name, new Variable(variable_type, variable_location,
				name, scope, array, length, value));
	}

	public String getName() {
		return this.name;
	}

	public Object getVariableValue(Variable variable) {
		return this.variables.get(variable.getName()).getValue();
	}

	public Variable getVariable(String variable_name) {
		return this.variables.get(variable_name);
	}

	public void setVariableValue(Variable variable, Object value) {
		if (this.variables.containsKey(variable.getName())) {
			if (this.variables.get(variable.getName()).isArray()) {
				this.variables.get(variable.getName()).getVariables()[variable.getIndex()].setValue(value);
			} else {
				this.variables.get(variable.getName()).setValue(value);
			}
		} else {
			throw new RuntimeError("The variable " + variable.getName()
					+ " doesn't exist");
		}
	}

	public void initializeArrayVariable(Variable variable, int length) {
		if (this.variables.containsKey(variable.getName())) {
			this.variables.get(variable.getName()).initializeArray(length);
		} else {
			throw new RuntimeError("The variable " + variable.getName()
					+ " doesn't exist");
		}
	}

	public int getVariableScope(String variable_name) {
		return this.variables.get(variable_name).getScope();
	}

	public void setVariableScope(String variable_name, int scope) {
		this.variables.get(variable_name).setScope(scope);
	}

	public boolean variableExists(String variable_name, int scope) {
		if (this.variables.containsKey(variable_name)) {
			return variables.get(variable_name).getScope() <= scope;
		}
		return false;
	}

	public void deleteVariablesFromScope(int scope) {
		ArrayList<String> variables_to_remove = new ArrayList<>();
		for (Variable variable : this.variables.values()) {
			if (variable.getScope() == scope) {
				variables_to_remove.add(variable.getName());
			}
		}
		for (String variable_name : variables_to_remove) {
			this.variables.remove(variable_name);
		}
	}
}
