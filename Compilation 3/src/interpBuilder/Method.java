package interpBuilder;

import interp.REPL.RuntimeError;
//import interp.Variable;


import interpBuilder.Variable.VariableLocation;
import interpBuilder.Variable.VariableType;

import java.util.HashMap;

public class Method {
	String name;
	private HashMap<String, Variable> variables;
	int scope;

	public Method(String name) {
		this.name = name;
		this.variables = new HashMap<>();
	}

	public void addVariable(Variable variable) {
		this.variables.put(variable.getName(), variable);
	}

	public void addVariable(VariableType variable_type,
			VariableLocation variable_location, String name, int scope, int dimensions) {
		this.variables.put(name, new Variable(variable_type, variable_location,
				name, scope, dimensions));
	}

	public void addVariable(VariableType variable_type,
			VariableLocation variable_location, String name, int scope, int dimensions, Object[] value) {
		this.variables.put(name, new Variable(variable_type, variable_location,
				name, scope, dimensions, value));
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

	public void setVariableValue(String name, Object[] value, int location) {
		if (this.variables.containsKey(name)) {
			this.variables.get(name).setValue(value, location);
		} else {
			throw new RuntimeError("The variable doesn't exist");
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
}
