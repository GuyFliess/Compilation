package scope;

import ic.ast.decl.Type;

import java.util.HashMap;
import java.util.Map;

public class StatementBlockScope {
	Map<String, Type> localVariables = new HashMap<>();
	Map<String, StatementBlockScope> stmtBlocks;
}
