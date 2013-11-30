package scope;






public abstract class Scope {
	
	public Scope fatherScope;
	private String name;
	public String parent; //TODO Lital, I added a name for the scope itself, so I think we can remove this and go to fatherScope.GetName()
		
	public Scope(Scope fatherScope, String name)
	{
		this.name = name;
		
		if (fatherScope != null) {
			this.fatherScope = fatherScope; 
			parent = fatherScope.parent;
		}
		else {
			this.fatherScope = null;
			parent = null;
		}
		
	}

	public String getName()
	{
		return name;
	}
	
	public abstract void AddVar(Object type);
	
	
//	public List<Scope> childScopes;
	
// abstract public void add(Scope scope);
//// {
////	childScopes.add(scope);
// }
}