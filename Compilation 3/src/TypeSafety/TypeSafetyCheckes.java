package TypeSafety;

import scope.GlobalScope;
import ic.ast.Node;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.Program;

public class TypeSafetyCheckes {

	public void CheckTypeSafety(Program p, DeclClass libAst,
			GlobalScope globalScope) {
		LoopCheck loopCheck = new LoopCheck();
		MainCheck mainCheck = new MainCheck();
		ScopeRules scopeCheck = new ScopeRules();
		TypingRules typingRulesChecker = new TypingRules(globalScope);
		ThisCheck thisCheck = new ThisCheck();

		try {
			loopCheck.ContBreak(p);
			mainCheck.CountMain(p);
			scopeCheck.CheckScopeRules(p);
			p.accept(typingRulesChecker);
			thisCheck.CheckThis(p);

		} catch (ThisException e) {
			System.out.println(e.lineNum + ": semantic error; " + e.errorMSG);
			for (StackTraceElement trace : e.getStackTrace()) //TODO commetn out
			{
				System.out.println(trace);
			}
			throw new FoundException();
		} catch (ContinueBreakException e) {
			System.out.println(e.lineNum + ": semantic error; Use of "
					+ e.errorMSG + " statement outside of loop not allowed");
			throw new FoundException();
		} catch (MainException e) {
			System.out.println(e.lineNum + ": semantic error; " + e.errorMSG);
			throw new FoundException();
		} catch (TypeSafetyException e) {
			System.out.println(e.lineNum + ": semantic error; " + e.errorMSG);
			throw new FoundException();
		} catch (FoundException e) {
			throw new FoundException();
		} 

	}

}