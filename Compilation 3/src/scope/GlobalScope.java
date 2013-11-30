package scope;

import ic.ast.decl.DeclClass;

import java.util.HashMap;
import java.util.Map;

public class GlobalScope extends Scope {
	public GlobalScope(Scope scope) {
		super(scope);
		// TODO Auto-generated constructor stub
	}


	Map<String, ClassScope> classes = new HashMap<>(); // TODO not sure if we
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

}
