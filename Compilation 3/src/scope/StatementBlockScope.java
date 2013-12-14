package scope;

import ic.ast.decl.Type;
import ic.ast.stmt.LocalVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatementBlockScope extends Scope {
	public StatementBlockScope(Scope scope, String name) {
		super(scope, name);
		// TODO Auto-generated constructor stub
	}
	
	Map<String, Type> localVariables = new LinkedHashMap<>();
	
	List<StatementBlockScope> blockScopes = new ArrayList<>();
	
	public Map<String, Type> getLocalVariables() { return localVariables;}
	
	public List<StatementBlockScope> getBlockScopes() { return blockScopes;}
	
	public void AddStatementScope(StatementBlockScope stmtScope) {
		this.blockScopes.add(stmtScope);		
	}

	
	@Override
	public void AddVar(Type type, String name) {		
		localVariables.put(name, type);
	}

	@Override
	public Type GetVariable(String name)  {
		
		Type resultType = localVariables.get(name);
		if (resultType == null)
		{
			
			if (fatherScope instanceof StatementBlockScope)
			{
				resultType = fatherScope.GetVariable(name);
			}

		}
		
		return resultType;
	}

	@Override
	public MethodTypeWrapper GetMethod(String method) {
		return fatherScope.GetMethod(method);
	}

	@Override
	public MethodTypeWrapper GetMethodWithoutName() {
		return fatherScope.GetMethodWithoutName();
	}
	
}
