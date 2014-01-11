package TypeSafety;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.RowFilter.Entry;

import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.PrimitiveType;
import ic.ast.decl.Program;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Statement;
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtWhile;

public class CheckMultipleDefinition {

	public void CheckDef(Program p) throws MultipleDefineException {

		CheckClass(p);

		for (DeclClass icClass : p.getClasses()) {
			CheckDef(icClass);
		}
	}

	private void CheckDef(DeclClass icClass) throws MultipleDefineException {
		for (DeclMethod method : icClass.getMethods()) {
			CheckParams(method);
		}

	}

	private void CheckParams(DeclMethod method) throws MultipleDefineException {
		List<String> typeLst = new ArrayList<String>();
/*
		for (Parameter parameter : method.getFormals()) {
			if (typeLst.contains(parameter.getName())) {
				throw new MultipleDefineException(parameter.getName(),
						parameter.getLine());
			}
			typeLst.add(parameter.getName());
		}
*/
		for (Statement statement : method.getStatements()) {
			List<LocalVariable> params = new ArrayList<LocalVariable>();
			params = findStatementParams(statement);

			for (int i = 0; i < params.size(); i++) {
				LocalVariable var = params.get(i);
				if (typeLst.contains(var.getName())) {
					throw new MultipleDefineException(var.getName(),
							var.getLine());
				}
				typeLst.add(var.getName());
			}

		}

	}

	private List<LocalVariable> findStatementParams(Statement statement) {

		if (LocalVariable.class.isInstance(statement)) {
			return findStatementParams((LocalVariable) statement);
		}
		if (StmtBlock.class.isInstance(statement)) {
			return findStatementParams((StmtBlock) statement);
		}
		if (StmtIf.class.isInstance(statement)) {
			return findStatementParams((StmtIf) statement);
		}
		if (StmtWhile.class.isInstance(statement)) {
			return findStatementParams((StmtWhile) statement);
		}
		return new ArrayList<LocalVariable>();

	}

	private List<LocalVariable> findStatementParams(StmtBlock statement) {
		List<LocalVariable> params = new ArrayList<LocalVariable>();

		for (Statement stmt : statement.getStatements()) {
			params.addAll(findStatementParams(stmt));
		}

		return params;
	}

	private List<LocalVariable> findStatementParams(StmtIf statement) {
		List<LocalVariable> params = new ArrayList<LocalVariable>();

		params.addAll(findStatementParams(statement.getOperation()));
		params.addAll(findStatementParams(statement.getElseOperation()));

		return params;
	}

	private List<LocalVariable> findStatementParams(StmtWhile statement) {
		List<LocalVariable> params = new ArrayList<LocalVariable>();

		params.addAll(findStatementParams(statement.getOperation()));

		return params;
	}

	private List<LocalVariable> findStatementParams(LocalVariable var) {
		List<LocalVariable> params = new ArrayList<LocalVariable>();

		params.add(var);

		return params;
	}

	private void CheckClass(Program p) throws MultipleDefineException {

		java.util.List<DeclClass> classLst = p.getClasses();

		for (int i = 0; i < classLst.size(); i++) {

			for (int j = i + 1; j < classLst.size(); j++) {

				String name1 = classLst.get(i).getName();
				String name2 = classLst.get(j).getName();
				if (name1.equals(name2)) {
					throw new MultipleDefineException(name2, classLst.get(j)
							.getLine());
				}
			}
		}
	}

}