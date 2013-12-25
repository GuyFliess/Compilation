package scope;

import ic.ast.decl.Parameter;
import ic.ast.decl.Type;

import java.util.ArrayList;
import java.util.Collection;

public class MethodTypeWrapper {
	//for each method we need: name, parameters types, return type(, Scope??)
	private String name;
	private Collection<Type> parameters ; 
	private Type returnType;
	private MethodScope bodyScope;
	
	private int label;
	
	public MethodTypeWrapper (String name, Type returnType, Collection<Parameter> parameters, MethodScope scope)
	{
		this.name = name;
		this.returnType = returnType;
		this.parameters = new ArrayList<Type>();
		for (Parameter parameter : parameters) {
			this.parameters.add(parameter.getType());
		}
		this.bodyScope = scope; 
	}
	
	public String getName() { return name; }
	public Collection<Type> getParameters() {return parameters; } 
	public Type getReturnType() {return returnType; }
	public MethodScope getBodyScope() {return bodyScope; }
	
	public int getLabel() { return label;}
	public void setLabel(int label) {  this.label = label;}
}
