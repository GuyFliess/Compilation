package scope;

import java.util.List;




public abstract class Scope {
	
	public Scope fatherScope;
		
	public Scope(Scope scope)
	{
		fatherScope = scope; 
	}
//	public List<Scope> childScopes;
	
// abstract public void add(Scope scope);
//// {
////	childScopes.add(scope);
// }
}