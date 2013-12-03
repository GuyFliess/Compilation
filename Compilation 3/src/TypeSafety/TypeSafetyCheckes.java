package TypeSafety;

import ic.ast.decl.Program;

public class TypeSafetyCheckes{
	
	
	public void CheckTypeSafety(Program p) throws ContinueBreakException {
		LoopCheck loopCheck = new LoopCheck();
		
		
		loopCheck.ContBreak(p);
	}
	
	
}