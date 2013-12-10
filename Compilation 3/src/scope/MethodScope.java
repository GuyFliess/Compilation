package scope;

import ic.ast.decl.Parameter;
import ic.ast.decl.Type;
import ic.ast.stmt.LocalVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MethodScope extends  StatementBlockScope {
	public MethodScope(Scope scope, String name) {
		super(scope, name);
		// TODO Auto-generated constructor stub
	}
	private Map<String, Type> parameters = new LinkedHashMap<>();
//	private Map<String, Type> localVariables = new HashMap<>();
//	private List<StatementBlockScope> stmtScopes = new ArrayList<>();
	
	public Map<String, Type> getParameters() { return parameters;}
//	public Map<String, Type> getLocalVariables() {return localVariables;}
	
	
	public void AddParameter(Parameter parameter)
	{
		parameters.put(parameter.getName(), parameter.getType());
	}
	
//	public void AddLocalVariable(Type type)
//	{
//		localVariables.put(type.getDisplayName(), type);
//	}

//	public void AddStatementScope(StatementBlockScope stmtScope) {
//		this.stmtScopes.add(stmtScope);
//		
//	}

	@Override
	public void AddVar(Object type) {
		LocalVariable var =(LocalVariable) type;
		localVariables.put(var.getName(), var.getType());
//		localVariables.put(type.getDisplayName(), type);
	}
	
	@Override
	public Type GetVariable(String name)  {
		
		Type resultType = parameters.get(name);
		if (resultType == null)
		{
			resultType = super.GetVariable(name);
		}
		
		return resultType;
	}
	
}
