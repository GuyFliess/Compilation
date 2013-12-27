package scope;

import ic.ast.decl.Parameter;
import ic.ast.decl.Type;
import ic.ast.stmt.LocalVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import TypeSafety.TypingRuleException;


public class MethodScope extends  StatementBlockScope {
	public MethodScope(Scope scope, String name, MethodType methodType) {
		super(scope, name);
		this.methodType = methodType;
	}
	private Map<String, Type> parameters = new LinkedHashMap<>();
	
	private Map<String, Integer> parametersRegs = new LinkedHashMap<>();
	
	public Map<String, Type> getParameters() { return parameters;}
	
	public MethodType methodType; 
	
	public enum MethodType 
	{
		Static,
		Virtual,
	}
	
	
	public void AddParameter(Parameter parameter)
	{
		parameters.put(parameter.getName(), parameter.getType());
	}

	
	@Override
	public Type GetVariable(String name)  {
		
		//search parameters
		Type resultType = parameters.get(name);
		// search local vars
		if (resultType == null)
		{
			resultType = super.GetVariable(name);
		}
		//search fields
		Scope currentScope = fatherScope;
		while (!(currentScope instanceof ClassScope)) currentScope = fatherScope;
		ClassScope classScope = (ClassScope) currentScope;
		if (classScope.getFields().containsKey(name))
		{
			if (methodType.equals(MethodType.Static))
			{
				throw new TypingRuleException("Use of field inside a static method is not allowed",-1);
			}
			if (methodType.equals(MethodType.Virtual))
			{
				resultType = classScope.getFields().get(name);
			}
		}
		
		return resultType;
	}
	
	@Override
	public MethodTypeWrapper GetMethodWithoutName() {
		return GetMethod(getName());
		//return ((ClassScope) fatherScope).GetMethodStaticOrVirtual(getName());		
	}
	
	@Override
	public MethodTypeWrapper GetMethod(String method) throws TypingRuleException {
		MethodTypeWrapper resultMethod;
		if (!(fatherScope instanceof ClassScope))
		{
			throw new Error("Internal error, method scope cann't be nested not in class scope");
		}
		ClassScope classScope = (ClassScope) fatherScope;
//		if (classScope.getStaticMethodScopes().containsKey(method))
//		{
//			return classScope.getStaticMethodScopes().get(method);
//		}
		resultMethod = classScope.getStaticMethod(method);
		if (resultMethod != null ) return resultMethod;
		resultMethod = classScope.getVirtualMethod(method);		
		if (resultMethod != null) {
			if (methodType.equals(MethodType.Static))
			{
				throw new TypingRuleException("Calling a local virtual method from inside a static method is not allowed", -1);
			}
			return resultMethod;
		}
		return fatherScope.GetMethod(method);
	}

	@Override
	public Integer getVaraibleReg(String name) {
		//Assuming correctness from typecheck the name must exist
		Integer regResult = parametersRegs.get(name);
		if (regResult == null )
		{
			regResult = super.getVaraibleReg(name);		
		}			
		return regResult;
	}

	public void AddParameterReg(String name, int reg) {
		parametersRegs.put(name, reg);
	}
}
