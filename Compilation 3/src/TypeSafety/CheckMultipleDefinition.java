package TypeSafety;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
		ArrayList<String> typeLst = new ArrayList<String>();

		for (Parameter parameter : method.getFormals()) {
			if (typeLst.contains(parameter.getName())) {
				throw new MultipleDefineException(parameter.getName(),
						parameter.getLine());
			}
			typeLst.add(parameter.getName());
		}

		for (Statement statement : method.getStatements()) {
			Map<String, Integer> params = new LinkedHashMap<>();
			params = findStatementParams(statement);

			Iterator t = params.entrySet().iterator();
			
			while (t.hasNext()) {
				Map.Entry e = (Map.Entry) t.next();

				if (typeLst.contains(e.getKey())) {
					throw new MultipleDefineException((String) e.getKey(),
							(int) e.getValue());
				}
				typeLst.add((String) e.getKey());
				t.remove();
			}
		}

	}

	private Map<String, Integer> findStatementParams(Statement statement) {

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
		return null;

	}

	private Map<String, Integer> findStatementParams(StmtBlock statement) {
		Map<String, Integer> params = new LinkedHashMap<>();

		for (Statement stmt : statement.getStatements()) {
			params.putAll(findStatementParams(stmt));
		}

		return params;
	}

	private Map<String, Integer> findStatementParams(StmtIf statement) {
		Map<String, Integer> params = new LinkedHashMap<>();

		params.putAll(findStatementParams(statement.getOperation()));
		params.putAll(findStatementParams(statement.getElseOperation()));

		return params;
	}

	private Map<String, Integer> findStatementParams(StmtWhile statement) {
		Map<String, Integer> params = new LinkedHashMap<>();

		params.putAll(findStatementParams(statement.getOperation()));

		return params;
	}

	private Map<String, Integer> findStatementParams(LocalVariable var) {
		Map<String, Integer> params = new LinkedHashMap<>();

		params.put(var.getName(), var.getLine());

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