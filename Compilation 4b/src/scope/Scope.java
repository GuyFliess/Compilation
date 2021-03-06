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
		}
		else {
			this.fatherScope = null;
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

	public abstract void setVaraibleReg(String name, int reg) ;

	public abstract Integer getVaraibleReg(String name);

	public Integer getFieldOffset(String fieldName){
		return fatherScope.getFieldOffset(fieldName);
	}

	public int getMethodOffset(String methodName) {
		return fatherScope.getMethodOffset(methodName);		
	}

}
