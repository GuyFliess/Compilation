package TypeSafety;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ic.ast.decl.*;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Statement;
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtWhile;

public class ShadowingChecks {

	public void CheckShadowing(Program p) {

		for (DeclClass icClass : p.getClasses()) {
			CheckShadowing(icClass);
		}

	}

	private void CheckShadowing(DeclClass icClass) throws ShadowException{
		List<Type> lst = new ArrayList<Type>();

		for (DeclField field : icClass.getFields()) {
			Type typeLst = SearchString(lst, field.getName());
			if (typeLst != null) {
				String type = findType(field.getType());
				throw new ShadowException(type + " " + field.getName()
						+ " is shadowing a field with the same name",
						field.getLine());
			}
			lst.add(field.getType());
		}

		for (DeclMethod method : icClass.getMethods()) {
			Type typeLst = SearchString(lst, method.getName());
			if (typeLst != null) {
				String type = findType(method.getType());
				throw new ShadowException(type + " " + method.getName()
						+ " is shadowing a field with the same name",
						method.getLine());
			}
			lst.add(method.getType());
			CheckShadowing(method);
		}

		if (icClass.hasSuperClass()) {

		}

	}

	private void CheckShadowing(DeclMethod method) throws ShadowException{
		List<Type> lst = new ArrayList<Type>();

		for (Parameter parameter : method.getFormals()) {
			lst.add(parameter.getType());
		}

		List<Type> methodParms = new ArrayList<Type>();
		methodParms = FindAllStatementsParams(method);

		for (int i = 0; i < methodParms.size(); i++) {
			Type typeLst = SearchString(lst, methodParms.get(i).toString());
			if (typeLst != null) {
				String type = findType(methodParms.get(i));
				throw new ShadowException(type + " " + methodParms.get(i).toString()
						+ " is shadowing a parameter",
						 methodParms.get(i).getLine());
			}
		}

	}

	private List<Type> FindAllStatementsParams(DeclMethod method) {
		List<Type> lst = new ArrayList<Type>();

		for (Statement statement : method.getStatements()) {
			lst.addAll(FindAllStatementsParams(statement));
		}
		return lst;
	}

	private List<Type> FindAllStatementsParams(Statement statement) {
		List<Type> lst = new ArrayList<Type>();

		if (LocalVariable.class.isInstance(statement)) {
			LocalVariable var = (LocalVariable) statement;
			lst.addAll(FindAllStatementsParams(var));
		} else if (StmtBlock.class.isInstance(statement)) {
			StmtBlock block = (StmtBlock) statement;
			lst.addAll(FindAllStatementsParams(block));
		} else if (StmtIf.class.isInstance(statement)) {
			StmtIf stmtIf = (StmtIf) statement;
			lst.addAll(FindAllStatementsParams(stmtIf));
		} else if (StmtWhile.class.isInstance(statement)) {
			StmtWhile stmtWhile = (StmtWhile) statement;
			lst.addAll(FindAllStatementsParams(stmtWhile));
		}

		return lst;
	}

	private List<Type> FindAllStatementsParams(LocalVariable var) {
		List<Type> lst = new ArrayList<Type>();
		lst.add(var.getType());
		return lst;
	}

	private List<Type> FindAllStatementsParams(StmtWhile stmtWhile) {
		List<Type> lst = new ArrayList<Type>();

		lst.addAll(FindAllStatementsParams(stmtWhile.getOperation()));

		return lst;
	}

	private List<Type> FindAllStatementsParams(StmtIf stmtIf) {
		List<Type> lst = new ArrayList<Type>();

		lst.addAll(FindAllStatementsParams(stmtIf.getOperation()));
		lst.addAll(FindAllStatementsParams(stmtIf.getElseOperation()));

		return lst;
	}

	private List<Type> FindAllStatementsParams(StmtBlock block) {
		List<Type> lst = new ArrayList<Type>();

		for (Statement statement : block.getStatements()) {
			lst.addAll(FindAllStatementsParams(statement));
		}

		return lst;
	}

	private String findType(Type type) {
		if (DeclField.class.isInstance(type)) {
			return "Field";
		}
		if (DeclMethod.class.isInstance(type)) {
			return "Method";
		}
		if (LocalVariable.class.isInstance(type)) {
			return "Local variable";
		}

		return null;
	}

	private Type SearchString(List<Type> lst, String str) {

		for (int i = 0; i < lst.size(); i++) {
			if (lst.get(i).toString().equals(str)) {
				return lst.get(i);
			}
		}
		return null;

	}
}