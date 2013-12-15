package TypeSafety;

import java.util.ArrayList;
import java.util.List;

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

	public void CheckThis(Program p) {

		for (DeclClass icClass : p.getClasses()) {
			CheckThis(icClass);
		}

	}

	private void CheckThis(DeclClass icClass) {

		for (DeclMethod method : icClass.getMethods()) {
			if (DeclStaticMethod.class.isInstance(method)) {
				CheckThisStatic(method);
			} else {
				CheckThisVirtual(MethodsList(icClass), method);
			}
		}

	}

	private List<DeclMethod> MethodsList(DeclClass icClass) {
		List<DeclMethod> methods = new ArrayList<DeclMethod>();
		for (DeclMethod method : icClass.getMethods()) {
			methods.add(method);
		}
		return methods;
	}

	private void CheckThisVirtual(List<DeclMethod> list, DeclMethod method) {
		for (Statement statement : method.getStatements()) {
			CheckThisVirtual(list, statement);
		}

	}

	private void CheckThisVirtual(List<DeclMethod> list, Statement statement) {
		if (StmtCall.class.isInstance(statement)) {
			CheckThisVirtual(list, (StmtCall) statement);
		}
		if (StmtBlock.class.isInstance(statement)) {
			CheckThisVirtual(list, (StmtBlock) statement);
		}
		if (StmtIf.class.isInstance(statement)) {
			CheckThisVirtual(list, (StmtIf) statement);
		}
		if (StmtWhile.class.isInstance(statement)) {
			CheckThisVirtual(list, (StmtWhile) statement);
		}

	}

	private void CheckThisVirtual(List<DeclMethod> list, StmtBlock statement) {
		for (Statement stat : statement.getStatements()) {
			CheckThisVirtual(list, stat);
		}
	}

	private void CheckThisVirtual(List<DeclMethod> list, StmtIf statement) {
		CheckThisVirtual(list, statement.getElseOperation());
		CheckThisVirtual(list, statement.getOperation());
	}

	private void CheckThisVirtual(List<DeclMethod> list, StmtWhile statement) {
		CheckThisVirtual(list, statement.getOperation());
	}

	private void CheckThisVirtual(List<DeclMethod> list, StmtCall statement) {
		if (statement.getCall().getClass().getName().equals("this")) {
			if (!ExistsCallMethod(list, statement.getCall().getMethod())) {
				throw new ThisException("Method "
						+ statement.getClass().getName() + "."
						+ statement.getCall().getMethod()
						+ " not found in type table", statement.getLine());
			}
		}

	}

	private boolean ExistsCallMethod(List<DeclMethod> list, String method) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equals(method)) {
				return true;
			}
		}
		return false;
	}

	private void CheckThisStatic(DeclMethod method) {

		for (Statement statement : method.getStatements()) {
			CheckThisStatic(statement);
		}

	}

	private void CheckThisStatic(Statement statement) {
		if (StmtCall.class.isInstance(statement)) {
			CheckThisStatic((StmtCall) statement);
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

	private void CheckThisStatic(StmtCall statement) throws ThisException {
		
		if (VirtualCall.class.isInstance(statement.getCall())) {
			VirtualCall call = (VirtualCall) statement.getCall();
			String s = call.getObject().toString();
			if (call.getObject().toString().contains("This")) {
				throw new ThisException(
						"Use of 'this' expression inside static method is not allowed",
						statement.getLine());
			}
		}

	}

}