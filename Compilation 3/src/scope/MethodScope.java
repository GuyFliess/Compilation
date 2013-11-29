package scope;

import ic.ast.decl.Type;

import java.util.HashMap;
import java.util.Map;


public class MethodScope {
	Map<String, Type> Parameters = new HashMap<>();
	Map<String, Type> localVariables = new HashMap<>();
	StatementBlockScope stmtScope;
}
