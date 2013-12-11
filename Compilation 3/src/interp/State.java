package interp;

import interpBuilder.Method;
import interpBuilder.Variable;
import interpBuilder.interpClass;

import java.util.HashMap;

public class State {
	// private HashMap<String, Variable> variables;
	private HashMap<String, interpClass> classes;

	public State() {
		this.classes = new HashMap<>();
		// this.variables = new HashMap<>();
	}

	public void addClass(interpClass class_instance) {
		this.classes.put(class_instance.getName(), class_instance);
	}
	
	public void addFieldToClass(String class_name, Variable field) {
		this.classes.get(class_name).addField(field);
	}
	
	public void addMethodToClass(String class_name, Method method) {
		this.classes.get(class_name).addMethod(method);
	}

	public void addVariableToMethod(String class_name, String method_name,
			Variable variable) {
		this.classes.get(class_name).addVariableToMethod(method_name, variable);
	}

	public String getVariableValue(String class_name, String method_name,
			Variable variable) {
		return this.classes.get(class_name)
				.getVariableValue(method_name, variable).toString();
	}
	
	public Variable getVariable(String class_name, String method_name, String variable_name) {
		return this.classes.get(class_name).getVariable(method_name, variable_name);
	}

	public void setVariableValue(String class_name, String method_name,
			String variable_name, Object[] value, int location) {
		this.classes.get(class_name).setVariableValue(method_name,
				variable_name, value, location);
	}

	public boolean variableExists(String class_name, String method_name,
			String variable_name, int scope) {
		return this.classes.get(class_name).variableExists(method_name,
				variable_name, scope);
	}
	
	public boolean fieldExists(String class_name, String field_name) {
		return this.classes.get(class_name).fieldExists(field_name);
	}

	// Stack<ActivationRecord> a_stack = new Stack<>(); //TODO: remove
	// Map<String, String> ref_variables = new HashMap<>();
	// Map<String, String> ref_fields = new HashMap<>();
	// Map<String, String> ref_array_elements = new HashMap<>();
	// Map<String, String> parameters = new HashMap<>();
	// Map<String, String> local_variables = new HashMap<>();
	// // Map<String, Integer> int_variables = new HashMap<>();
	// // Map<String, Boolean> bool_variables = new HashMap<>();
	// // Map<String, String> string_variables = new HashMap<>();
	//
	// State() { a_stack.push(new ActivationRecord()); }
	//
	// /**
	// * Find a variable by its name (dynamic lookup).
	// */
	// Double lookup(String byName)
	// {
	// ListIterator<ActivationRecord> iter =
	// a_stack.listIterator(a_stack.size());
	// while (iter.hasPrevious()) {
	// ActivationRecord ar = iter.previous();
	// if (ar.values.containsKey(byName))
	// return ar.values.get(byName);
	// }
	// throw new RuntimeError("undefined variable '" + byName + "'");
	// }
	//
	// @Override
	// public String toString()
	// {
	// return a_stack.peek().toString();
	// }
}
