package interp;

import interpBuilder.Method;
import interpBuilder.Variable;
import interpBuilder.Variable.VariableType;
import interpBuilder.interpClass;

import java.util.HashMap;

public class State {
	
	String class_name;
	String method_name;
	String[] arguments;
	int formal_index = 0;
	int scope = 0;
	int array_location;
	boolean ref_to_array = false;
	boolean assignment_stmt = false;
	boolean new_array = false;
	Object value;
	Object[] values;
	private HashMap<String, interpClass> classes;
	boolean return_stmt = false;

	public State(String class_name, String method_name, String[] arguments) {
		
		this.class_name = class_name;
		this.method_name = method_name;
		this.arguments = arguments;
		this.formal_index = 0;
		this.scope = 0;
		this.array_location = 0;
		this.value = null;
		this.values = null;
		this.classes = new HashMap<>();
	}
	
	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}
	
	public boolean checkClassExists() {
		return this.classes.containsKey(class_name);
	}

	public String getMethod_name() {
		return method_name;
	}

	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}
	
	public boolean checkMethodExists() {
		return this.classes.get(class_name).getMethods().containsKey(method_name);
	}

	public void addClass(interpClass class_instance) {
		this.classes.put(class_instance.getName(), class_instance);
	}
	
	public interpClass getClass(String class_name) {
		return this.classes.get(class_name);
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

	public Object getVariableValue(String class_name, String method_name,
			Variable variable) {
		return this.classes.get(class_name)
				.getVariableValue(method_name, variable);
	}
	
	public Variable getVariable(String class_name, String method_name, String variable_name) {
		return this.classes.get(class_name).getVariable(method_name, variable_name);
	}

	public void setVariableValue(String class_name, String method_name,
			Variable variable, Object value) {
		this.classes.get(class_name).setVariableValue(method_name, variable, value);
	}
	
	public boolean variableExists(String class_name, String method_name,
			String variable_name, int scope) {
		return this.classes.get(class_name).variableExists(method_name,
				variable_name, scope);
	}
	
	public boolean fieldExists(String class_name, String field_name) {
		return this.classes.get(class_name).fieldExists(field_name);
	}
	
	public void deleteVariableFromScope() {
		this.classes.get(class_name).getMethods().get(method_name).deleteVariablesFromScope(this.scope);
	}
	
	public void setReturnType(VariableType type) {
		this.classes.get(class_name).getMethods().get(method_name).setReturnType(type);
	}
	
	public VariableType getReturnType() {
		return this.classes.get(class_name).getMethods().get(method_name).getReturnType();
	}
	
	public void setReturnTypeDimensions(int dimensions) {
		this.classes.get(class_name).getMethods().get(method_name).setReturnTypeDimensions(dimensions);
	}
	
	public int getReturnTypeDimensions() {
		return this.classes.get(class_name).getMethods().get(method_name).getReturnTypeDimensions();
	}
}
