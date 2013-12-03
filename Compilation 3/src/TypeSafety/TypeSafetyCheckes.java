package TypeSafety;

import ic.ast.decl.Program;

public class TypeSafetyCheckes{
	
	
	public void CheckTypeSafety(Program p) throws ContinueBreakException, MainException {
		LoopCheck loopCheck = new LoopCheck();
		MainCheck mainCheck = new MainCheck();
		
		loopCheck.ContBreak(p);
		mainCheck.CountMain(p);
	}
	
	
}