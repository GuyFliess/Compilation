package TypeSafety;

import java.util.ArrayList;
import java.util.List;

import ic.ast.decl.DeclClass;
import ic.ast.decl.Program;

public class CheckClassExtends {
	
	public void CheckExtends(Program p) throws ExtendsException {
		List<String> classLst = new ArrayList<String>();
		
		for (DeclClass icClass : p.getClasses()) {
			if (!icClass.hasSuperClass()) {
				classLst.add(icClass.getName());
			}
			else if (!classLst.contains(icClass.getSuperClassName())) {
				throw new ExtendsException("Class "+icClass.getName()+" cannot extend "+icClass.getSuperClassName()+", since it's not yet defined", icClass.getLine());
			} else {
				classLst.add(icClass.getName());
			}
		}
	}
	
}