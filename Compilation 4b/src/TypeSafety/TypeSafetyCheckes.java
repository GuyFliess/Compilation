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

		loopCheck.ContBreak(p);
		mainCheck.CountMain(p);
		scopeCheck.CheckScopeRules(p);
		p.accept(typingRulesChecker);
		thisCheck.CheckThis(p);
	}

}