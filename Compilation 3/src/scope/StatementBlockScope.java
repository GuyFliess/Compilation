package scope;

import ic.ast.decl.Type;

import java.util.HashMap;
import java.util.Map;

public class StatementBlockScope extends Scope {
	public StatementBlockScope(Scope scope) {
		super(scope);
		// TODO Auto-generated constructor stub
	}
	Map<String, Type> localVariables = new HashMap<>();
	Map<String, StatementBlockScope> stmtBlocks;
}
