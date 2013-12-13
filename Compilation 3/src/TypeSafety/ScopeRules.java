package TypeSafety;

import ic.ast.decl.Program;

public class ScopeRules {
	
	public void CheckScopeRules(Program p) {
		
		CheckMultipleDefinition mulDef = new CheckMultipleDefinition();
		CheckClassExtends extendsClass = new CheckClassExtends();
		ShadowingChecks checkShadow = new ShadowingChecks();
		
		try {
			mulDef.CheckDef(p);
			extendsClass.CheckExtends(p);
			checkShadow.CheckShadowing(p);
			
		} catch (MultipleDefineException e) {
			System.err.println(e.lineNum+": semantic error; Id "+e.errorMSG+" already defined in current scope");
			throw new FoundException();
		} catch (ExtendsException e) {
			System.err.println(e.lineNum+": semantic error; "+e.errorMSG);
			throw new FoundException();
		} catch (ShadowException e) {
			System.err.println(e.lineNum+": semantic error; "+e.errorMSG);
			throw new FoundException();
		}
		
	}
	
	
	
	
	
	
	
}