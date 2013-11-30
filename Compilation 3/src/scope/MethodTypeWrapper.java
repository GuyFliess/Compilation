package scope;

import ic.ast.decl.Parameter;
import ic.ast.decl.Type;

import java.util.ArrayList;
import java.util.Collection;

public class MethodTypeWrapper {
	//for each method we need: name, parameters types, return type(, Scope??)
	String Name;
	Collection<Type> Parameters ; 
	Type ReturnType;
	MethodScope bodyScope;
	
	public MethodTypeWrapper (String name, Type returnType, Collection<Parameter> parameters, MethodScope scope)
	{
		Name = name;
		ReturnType = returnType;
		Parameters = new ArrayList<Type>();
		for (Parameter parameter : parameters) {
			Parameters.add(parameter.getType());
		}
		this.bodyScope = scope; 
	}
}
