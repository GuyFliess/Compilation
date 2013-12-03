package TypeSafety;

import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.Program;
import ic.ast.stmt.*;

public class LoopCheck {

	public void ContBreak(Program p) throws ContinueBreakException {
		for (DeclClass icClass : p.getClasses()) {
			ContBreak(icClass);
		}
	}

	private void ContBreak(DeclClass icClass) throws ContinueBreakException {
		for (DeclMethod method : icClass.getMethods()) {
			ContBreak(method);
		}

	}

	private void ContBreak(DeclMethod method) throws ContinueBreakException {
		for (Statement statement : method.getStatements()) {
			ContBreak(statement);
		}

	}

	private void ContBreak(Statement statement) throws ContinueBreakException {

		if (StmtBreak.class.isInstance(statement)) {
			throw new ContinueBreakException("'break'", statement.getLine());
		}
		if (StmtContinue.class.isInstance(statement)) {
			throw new ContinueBreakException("'countinue'", statement.getLine());
		}
/*		if (StmtWhile.class.isInstance(statement)) {

		}*/
		if (StmtBlock.class.isInstance(statement)) {
			StmtBlock block = (StmtBlock) statement;
			for (Statement stmt : block.getStatements()) {
				ContBreak(stmt);
			}
		}
		if (StmtIf.class.isInstance(statement)) {
			StmtIf ifBlock = (StmtIf) statement;
			ContBreak(ifBlock.getOperation());
			ContBreak(ifBlock.getElseOperation());
		}

	}

}