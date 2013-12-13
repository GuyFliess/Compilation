package TypeSafety;

import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.Program;
import ic.ast.expr.VirtualCall;
import ic.ast.stmt.Statement;
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtCall;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtWhile;

public class ThisCheck {
	
	public void CheckThis(Program p){
		
		for (DeclClass icClass : p.getClasses()) {
			CheckThis(icClass);
		}
		
	}

	private void CheckThis(DeclClass icClass){
		
		for (DeclMethod method : icClass.getMethods()) {
			if (DeclStaticMethod.class.isInstance(method)) {
				CheckThisStatic(method);
			}
			else {
				
			}
		}
		
		
		
	}

	private void CheckThisStatic(DeclMethod method){
		
		for (Statement statement : method.getStatements()) {
			CheckThisStatic(statement);
		}
		
	}
	
	private void CheckThisStatic(Statement statement) {
		if (StmtCall.class.isInstance(statement)) {
			CheckThisStatic((StmtCall)statement);
		}
		if (StmtBlock.class.isInstance(statement)) {
			CheckThisStatic((StmtBlock) statement);
		}
		if (StmtIf.class.isInstance(statement)) {
			CheckThisStatic((StmtIf) statement);
		}
		if (StmtWhile.class.isInstance(statement)) {
			CheckThisStatic((StmtWhile) statement);
		}		
	}

	private void CheckThisStatic(StmtWhile statement) {
		CheckThisStatic(statement.getOperation());
	}

	private void CheckThisStatic(StmtIf statement) {
		CheckThisStatic(statement.getElseOperation());
		CheckThisStatic(statement.getOperation());
	}

	private void CheckThisStatic(StmtBlock statement) {
		for (Statement stat : statement.getStatements()) {
			CheckThisStatic(stat);
		}
		
	}

	private void CheckThisStatic(StmtCall statement) throws ThisException{
		if (VirtualCall.class.isInstance(statement.getCall())) {
			throw new ThisException("Use of 'this' expression inside static method is not allowed", statement.getLine());
		}
		
	}
	
}