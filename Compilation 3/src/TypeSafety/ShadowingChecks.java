package TypeSafety;

import java.util.ArrayList;
import java.util.List;

import ic.ast.decl.*;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Statement;
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtWhile;

public class ShadowingChecks {

	
	private List<String> superClassParams = new ArrayList<String>();
	private List<String> superClassMethods = new ArrayList<String>();
	
	public void CheckShadowing(Program p) throws ShadowException{

		for (DeclClass icClass : p.getClasses()) {
			CheckShadowing(icClass,p);
		}

	}

	private void CheckShadowing(DeclClass icClass, Program p) throws ShadowException{
		List<String> lst = new ArrayList<String>();
		
		if (icClass.hasSuperClass()) {
			superClassParams = FindClassParams(p,icClass.getSuperClassName());
			superClassMethods = FindClassMethods(p,icClass.getSuperClassName());
		}

		for (DeclField field : icClass.getFields()) {
			if (icClass.hasSuperClass()) {
				if (superClassParams.contains(field.getName())) {
					throw new ShadowException("Field " + field.getName()
							+ " is shadowing a field with the same name",
							field.getLine());
				} 
			}
			
			lst.add(field.getName());
		}

		for (DeclMethod method : icClass.getMethods()) {
			
			if (icClass.hasSuperClass()) {
				if (superClassMethods.contains(method.getName())) {
					throw new ShadowException("method '" + method.getName()
							+ "' overloads a different method with the same name",
							method.getLine());
				} 
			}
			
			if (lst.contains(method.getName())) {
				throw new ShadowException("Method " + method.getName()
						+ " is shadowing a field with the same name",
						method.getLine());
			}
			lst.add(method.getName());
			CheckShadowing(method);
		}

		

	}

	

	

	private void CheckShadowing(DeclMethod method) throws ShadowException{
		List<String> lst = new ArrayList<String>();

		for (Parameter parameter : method.getFormals()) {
			lst.add(parameter.getName());
		}

		List<LocalVariable> methodParms = new ArrayList<LocalVariable>();
		methodParms = FindAllStatementsParams(method);

		for (int i = 0; i < methodParms.size(); i++) {
			if (lst.contains(methodParms.get(i).getName())) {
				throw new ShadowException("Local variable " + methodParms.get(i).getName()
						+ " is shadowing a parameter",
						 methodParms.get(i).getLine());
			}
		}

	}
	
	private List<String> FindClassMethods(Program p, String superClassName) {
		List<String> lst = new ArrayList<String>();
		DeclClass icClass = FindClass(p, superClassName);
		
		
		for (DeclMethod method : icClass.getMethods()) {
			lst.add(method.getName());
		}	
		
		return lst;
	}
	
	private List<String> FindClassParams(Program p, String superClassName) {
		List<String> lst = new ArrayList<String>();
		DeclClass icClass = FindClass(p, superClassName);
		
		for (DeclField field : icClass.getFields()) {
			lst.add(field.getName());
		}
		for (DeclMethod method : icClass.getMethods()) {
			for (Parameter parameter : method.getFormals()) {
				lst.add(parameter.getName());
			}
			List<LocalVariable> methodParms = new ArrayList<LocalVariable>();
			methodParms = FindAllStatementsParams(method);
			for (int i = 0; i < methodParms.size(); i++) {
				lst.add(methodParms.get(i).getName());
			}
		}
		
		return lst;
	}
	
	

	private DeclClass FindClass(Program p, String superClassName) {
		
		for (DeclClass icClass : p.getClasses()) {
			if (superClassName.compareTo(icClass.getName())==0) {
				return icClass;
			}
		}
		
		return null;
	}

	private List<LocalVariable> FindAllStatementsParams(DeclMethod method) {
		List<LocalVariable> lst = new ArrayList<LocalVariable>();

		for (Statement statement : method.getStatements()) {
			lst.addAll(FindAllStatementsParams(statement));
		}
		return lst;
	}

	private List<LocalVariable> FindAllStatementsParams(Statement statement) {
		List<LocalVariable> lst = new ArrayList<LocalVariable>();

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

	private List<LocalVariable> FindAllStatementsParams(LocalVariable var) {
		List<LocalVariable> lst = new ArrayList<LocalVariable>();
		lst.add(var);
		return lst;
	}

	private List<LocalVariable> FindAllStatementsParams(StmtWhile stmtWhile) {
		List<LocalVariable> lst = new ArrayList<LocalVariable>();

		lst.addAll(FindAllStatementsParams(stmtWhile.getOperation()));

		return lst;
	}

	private List<LocalVariable> FindAllStatementsParams(StmtIf stmtIf) {
		List<LocalVariable> lst = new ArrayList<LocalVariable>();

		lst.addAll(FindAllStatementsParams(stmtIf.getOperation()));
		lst.addAll(FindAllStatementsParams(stmtIf.getElseOperation()));

		return lst;
	}

	private List<LocalVariable> FindAllStatementsParams(StmtBlock block) {
		List<LocalVariable> lst = new ArrayList<LocalVariable>();

		for (Statement statement : block.getStatements()) {
			lst.addAll(FindAllStatementsParams(statement));
		}

		return lst;
	}

}