package scope;

import ic.ast.decl.Type;

import java.util.ArrayList;
import java.util.Collection;

public class MethodTypeWrapper {
	//for each method we need: name, parametrs types, return type(, Scope??)
	String Name;
	Collection<Type> Parameters ; 
	Type ReturnType;
	
	public MethodTypeWrapper (String name, Type returnType, Type[] parameters)
	{
		Name = name;
		ReturnType = returnType;
		Parameters = new ArrayList<Type>();
		for (Type type : parameters) {
			Parameters.add(type);
		}
	}
}
