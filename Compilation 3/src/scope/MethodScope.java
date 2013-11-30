package scope;

import ic.ast.decl.Parameter;
import ic.ast.decl.Type;

import java.util.HashMap;
import java.util.Map;


public class MethodScope extends  Scope {
	public MethodScope(Scope scope) {
		super(scope);
		// TODO Auto-generated constructor stub
	}
	Map<String, Type> Parameters = new HashMap<>();
	Map<String, Type> localVariables = new HashMap<>();
	StatementBlockScope stmtScope;
	
	public void AddParameter(Parameter parameter)
	{
		Parameters.put(parameter.getName(), parameter.getType());
	}
	
	public void AddLocalVariable(Type type)
	{
		localVariables.put(type.getDisplayName(), type);
	}
	
}
