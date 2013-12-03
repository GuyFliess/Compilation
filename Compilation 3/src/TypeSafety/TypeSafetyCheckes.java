package TypeSafety;

import ic.ast.decl.Program;

public class TypeSafetyCheckes {

	public void CheckTypeSafety(Program p) {
		LoopCheck loopCheck = new LoopCheck();
		MainCheck mainCheck = new MainCheck();

		try {
			loopCheck.ContBreak(p);
			mainCheck.CountMain(p);
		} catch (ContinueBreakException e) {
			System.err.println(e.lineNum + ": semantic error; semantic error; Use of "
					+ e.errorMSG + " statement outside of loop not allowed");
		} catch (MainException e) {
			System.err.println(e.lineNum + ": semantic error; " + e.errorMSG);
		}

	}

}