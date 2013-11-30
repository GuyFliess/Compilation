package scope;

import ic.ast.decl.Type;
import ic.ast.stmt.LocalVariable;

import java.util.HashMap;
import java.util.Map;

public class StatementBlockScope extends Scope {
	public StatementBlockScope(Scope scope) {
		super(scope);
		// TODO Auto-generated constructor stub
	}
	Map<String, Type> localVariables = new HashMap<>();
	Map<String, StatementBlockScope> stmtBlocks;
	@Override
	public void AddVar(Object type) {
		LocalVariable var =(LocalVariable) type;
		localVariables.put(var.getName(), var.getType());
	}
}
