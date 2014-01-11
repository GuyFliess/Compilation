package interpBuilder;

import java.util.HashMap;

public class interpClass {
	private HashMap<String, Variable> fields;
	private HashMap<String, Method> methods;
	private String name;

	public interpClass(String name) {
		this.name = name;
		this.fields = new HashMap<>();
		this.methods = new HashMap<>();
	}

	public void addField(Variable field) {
		this.fields.put(field.getName(), field);
	}

	public void addMethod(Method method) {
		this.methods.put(method.getName(), method);
	}

	public void addVariableToMethod(String method_name, Variable variable) {
		this.methods.get(method_name).addVariable(variable);
	}

	public HashMap<String, Variable> getFields() {
		return fields;
	}

	public HashMap<String, Method> getMethods() {
		return methods;
	}

	public String getName() {
		return this.name;
	}

	public Object getVariableValue(String method_name, Variable variable) {
		return this.methods.get(method_name).getVariableValue(variable);
	}
	
	public Variable getVariable(String method_name, String variable_name) {
		return this.methods.get(method_name).getVariable(variable_name);
	}

	public void setFields(HashMap<String, Variable> fields) {
		this.fields = fields;
	}

	public void setMethods(HashMap<String, Method> methods) {
		this.methods = methods;
	}

	public void setVariableValue(String method_name, Variable variable, Object value) {
		this.methods.get(method_name).setVariableValue(variable, value);
	}

	public boolean variableExists(String method_name, String variable_name, int scope) {
		return this.methods.get(method_name).variableExists(variable_name, scope);
	}
	
	public boolean fieldExists(String field_name) {
		return this.fields.containsKey(field_name);
	}
}
