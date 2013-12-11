package interpBuilder;

import interp.REPL.RuntimeError;
//import interp.Variable;


import interpBuilder.Variable.VariableLocation;
import interpBuilder.Variable.VariableType;

import java.util.HashMap;

public class Method {
	String name;
	private HashMap<String, Variable> variables;

	public Method(String name) {
		this.name = name;
		this.variables = new HashMap<>();
	}

	public void addVariable(Variable variable) {
		this.variables.put(variable.getName(), variable);
	}

	public void addVariable(VariableType variable_type,
			VariableLocation variable_location, String name) {
		this.variables.put(name, new Variable(variable_type, variable_location,
				name));
	}

	public void addVariable(VariableType variable_type,
			VariableLocation variable_location, String name, Object value) {
		this.variables.put(name, new Variable(variable_type, variable_location,
				name, value));
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

	public void setVariableValue(String name, Object value) {
		if (this.variables.containsKey(name)) {
			this.variables.get(name).setValue(value);
		} else {
			throw new RuntimeError("The variable doesn't exist");
		}
	}

	public boolean variableExists(String variable_name) {
		return this.variables.containsKey(variable_name);
	}
}
