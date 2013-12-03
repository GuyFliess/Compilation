package scope;

import ic.ast.decl.DeclClass;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalScope extends Scope {
	public GlobalScope(Scope scope, String name) {
		super(scope, name);
		// TODO Auto-generated constructor stub
	}


	private Map<String, ClassScope> classes = new LinkedHashMap<>(); // TODO not sure if we
														// need the class scopes
														// or just the class
														// names, but this seems
														// comfortable
	
	
	public void AddClassScope(ClassScope classScope, DeclClass classDecl) {
		classes.put(classDecl.getName(), classScope);
		
	}


	@Override
	public void AddVar(Object type) {
		// TODO Auto-generated method stub
		
	}
	
	public Map<String, ClassScope> GetclassesScopes()
	{
		return classes;
	}

}
