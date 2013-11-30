package scope;

import ic.ast.decl.Type;
import ic.ast.stmt.LocalVariable;

import java.util.HashMap;
import java.util.Map;

public class StatementBlockScope extends Scope {
	public StatementBlockScope(Scope scope, String name) {
		super(scope, name);
		// TODO Auto-generated constructor stub
	}
	
	Map<String, Type> localVariables = new HashMap<>();
	
	Map<String, StatementBlockScope> stmtBlocks = new HashMap<>();
	
	public Map<String, Type> getLocalVariables() { return localVariables;}
	
	public Map<String, StatementBlockScope> getStmtBlocks() { return stmtBlocks;}
	
	@Override
	public void AddVar(Object type) {
		LocalVariable var =(LocalVariable) type;
		localVariables.put(var.getName(), var.getType());
	}
	
}
