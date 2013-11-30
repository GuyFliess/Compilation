package scope;

import ic.ast.decl.Type;

import java.util.List;




public abstract class Scope {
	
	public Scope fatherScope;
		
	public Scope(Scope scope)
	{
		fatherScope = scope; 
	}
	
	public abstract void AddVar(Object type);
//	public List<Scope> childScopes;
	
// abstract public void add(Scope scope);
//// {
////	childScopes.add(scope);
// }
}