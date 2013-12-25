package scope;

import TypeSafety.TypingRuleException;
import ic.ast.decl.Type;

public abstract class Scope {
	
	public Scope fatherScope;
	private String name;
		
	public Scope(Scope fatherScope, String name)
	{
		this.name = name;
		
		if (fatherScope != null) {
			this.fatherScope = fatherScope; 
		//	parent = fatherScope.parent;
		}
		else {
			this.fatherScope = null;
		//	parent = null;
		}
		
	}

	public String getName()
	{
		return name;
	}
	
	public abstract void AddVar(Type type, String name);
	
	public abstract Type GetVariable(String name);

	public abstract MethodTypeWrapper GetMethod(String method) throws TypingRuleException ;

	public abstract MethodTypeWrapper GetMethodWithoutName() ;

	public void setReg(String name, int reg) {
		// TODO Auto-generated method stub
		
	}

	public Object getVaraibleReg(String name2) {
		// TODO Auto-generated method stub
		return null;
	}

}
