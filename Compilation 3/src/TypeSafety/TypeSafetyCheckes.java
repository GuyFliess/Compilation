package TypeSafety;

import scope.GlobalScope;
import ic.ast.Node;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.Program;

public class TypeSafetyCheckes {

	public void CheckTypeSafety(Program p, DeclClass libAst,GlobalScope globalScope) {
		LoopCheck loopCheck = new LoopCheck();
		MainCheck mainCheck = new MainCheck();
		ScopeRules scopeCheck = new ScopeRules();
		TypingRules typingRulesChecker = new TypingRules(globalScope);
		
		try {
			loopCheck.ContBreak(p);
			mainCheck.CountMain(p);
			scopeCheck.CheckScopeRules(p);//  commented - throwing null 
			p.accept(typingRulesChecker);
			
			
			
		} catch (ContinueBreakException e) {
			System.err.println(e.lineNum + ": semantic error; semantic error; Use of "
					+ e.errorMSG + " statement outside of loop not allowed");
		} catch (MainException e) {
			System.err.println(e.lineNum + ": semantic error; " + e.errorMSG);
		}
		catch (TypeSafetyException e)
		{
			System.err.println(e.lineNum + ": semantic error; " + e.errorMSG);
		}

	}

}