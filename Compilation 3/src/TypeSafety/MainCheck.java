package TypeSafety;

import java.awt.List;

import scope.ClassScope;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.PrimitiveType;
import ic.ast.decl.Program;

public class MainCheck {

	private int mainNum = 0;

	public void CountMain(Program p) throws MainException {
		for (DeclClass icClass : p.getClasses()) {
			CountMain(icClass);
		}
	}

	private void CountMain(DeclClass icClass) throws MainException {
		for (DeclMethod method : icClass.getMethods()) {
			if (method.getName().equalsIgnoreCase("main")) {
				mainNum++;
				if (mainNum != 1) {
					throw new MainException("Found more than one main in the file", method.getLine());
				}
				CheckCorrectMainSign(method);
			}
		}

	}

	private void CheckCorrectMainSign(DeclMethod method) throws MainException {
		
		if (DeclStaticMethod.class.isInstance(method)) {
			throw new MainException("Main method should be a 'static' type", method.getLine());
		}
		if (method.getType().getDisplayName()
				.compareToIgnoreCase("void") == 0) {
			throw new MainException("Main method should have 'void' return type", method.getLine());
		}
		if (IsStringArr(method)) {
			throw new MainException("Argument for main method should be 'string[] args'", method.getLine());
		}
	}

	private boolean IsStringArr(DeclMethod method) {
		if (method.getFormals().size() == 1) {
			for (Parameter parameter : method.getFormals()) {
				return ((parameter.getType().getArrayDimension() == 1) && (parameter
						.getType().getDisplayName()
						.compareToIgnoreCase("string") == 0));
			}
		}
		return false;
	}
}