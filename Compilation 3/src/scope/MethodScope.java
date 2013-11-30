package scope;

import ic.ast.decl.Parameter;
import ic.ast.decl.Type;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MethodScope extends  Scope {
	public MethodScope(Scope scope) {
		super(scope);
		// TODO Auto-generated constructor stub
	}
	Map<String, Type> Parameters = new HashMap<>();
	Map<String, Type> localVariables = new HashMap<>();
	List<StatementBlockScope> stmtScopes = new ArrayList<>();
	
	public void AddParameter(Parameter parameter)
	{
		Parameters.put(parameter.getName(), parameter.getType());
	}
	
//	public void AddLocalVariable(Type type)
//	{
//		localVariables.put(type.getDisplayName(), type);
//	}

	public void AddStatementScope(StatementBlockScope stmtScope) {
		this.stmtScopes.add(stmtScope);
		
	}

	@Override
	public void AddVar(Object type) {
		LocalVariable var =(LocalVariable) type;
		localVariables.put(var.getName(), var.getType());
//		localVariables.put(type.getDisplayName(), type);
	}
	
}
