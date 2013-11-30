package scope;

import ic.ast.decl.Type;

import java.util.List;




public abstract class Scope {
	
	public Scope fatherScope;
	public String parent;
		
	public Scope(Scope scope)
	{
		if (scope != null) {
			fatherScope = scope.fatherScope; 
			parent = scope.parent;
		}
		else {
			fatherScope = null;
			parent = null;
		}
		
	}
	
	public abstract void AddVar(Object type);
//	public List<Scope> childScopes;
	
// abstract public void add(Scope scope);
//// {
////	childScopes.add(scope);
// }
}