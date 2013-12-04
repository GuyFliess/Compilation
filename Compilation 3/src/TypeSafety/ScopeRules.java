package TypeSafety;

import ic.ast.decl.Program;

public class ScopeRules {
	
	public void CheckScopeRules(Program p) {
		
		CheckMultipleDefinition mulDef = new CheckMultipleDefinition();
		
		try {
			mulDef.CheckDef(p);
		} catch (MultipleDefineException e) {
			System.err.println(e.lineNum+": semantic error; Id "+e.errorMSG+" already defined in current scope");
		}
		
	}
	
	
	
	
	
	
	
}