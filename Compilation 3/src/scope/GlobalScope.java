package scope;

import ic.ast.decl.DeclClass;
import ic.ast.decl.Type;

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
		AddClassScope(classScope,classDecl.getName());
		
	}


	@Override
	public void AddVar(Object type) {
		// TODO Auto-generated method stub
		
	}
	
	public Map<String, ClassScope> GetclassesScopes()
	{
		return classes;
	}


	public void AddClassScope(ClassScope classScope, String string) {
		classes.put(string, classScope);
		
	}


	@Override
	public Type GetVariable(String name) throws ScopeExcecption {
		throw new ScopeExcecption("Scope type doesn't support variables");
	}

}
