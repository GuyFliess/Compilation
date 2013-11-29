package scope;

import ic.ast.decl.Type;

import java.util.HashMap;
import java.util.Map;

public class ClassScope {

	//Static 
	//for each method we need: name, parametrs types, return type(, Scope??)
	Map<String, MethodTypeWrapper> staticMethodScopes = new HashMap<>();
	//instance
	Map<String, MethodTypeWrapper> virtualMethodScopes = new HashMap<>();
	Map<String, Type> fields = new HashMap<>();
}

