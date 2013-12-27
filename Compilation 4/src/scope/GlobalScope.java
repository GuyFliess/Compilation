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
	
	public Map<String, ClassScope> GetclassesScopes()
	{
		return classes;
	}


	public void AddClassScope(ClassScope classScope, String string) {
		classes.put(string, classScope);
		
	}


	@Override
	public Type GetVariable(String name) {
		return null;
		//throw new ScopeExcecption("Scope type doesn't support variables");
	}

	public ClassScope getClassScope(String name)
	{
		return classes.get(name);
	}


	@Override
	public MethodTypeWrapper GetMethod(String method) {
		return null;
	}

	@Override
	public void AddVar(Type type, String name) {
		throw new ScopeException("Global scope doesn't have vars");
		
	}

	@Override
	public MethodTypeWrapper GetMethodWithoutName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVaraibleReg(String name, int reg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getVaraibleReg(String name2) {
		// TODO Auto-generated method stub
		return 0;
	}
}
