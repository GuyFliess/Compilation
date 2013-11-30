package scopeBuilder;

import java.util.List;

import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.Program;
import ic.ast.stmt.Statement;

public class PrintScope {
	
	public void Print(Program program) {
		System.out.println("Global Symbol Table");
		for (DeclClass icClass : program.getClasses()) {
			System.out.println("    Class: "+ icClass.getName());
		}
		System.out.println();
		for (DeclClass icClass : program.getClasses()) {
			Print(icClass);
		}
	
	}

	private void Print(DeclClass icClass) {
		System.out.println("Class Symbol Table: "+icClass.getName());
		
		for (DeclField field : icClass.getFields()) {
			System.out.println("    Field:  "+field.getName()+" : "+field.getType().getDisplayName());
		}
		
		for (DeclMethod method : icClass.getMethods()) {
			
			
			if (method instanceof DeclStaticMethod){
				System.out.print("    Static method:  ");
			}
			if (method instanceof DeclVirtualMethod){
				System.out.print("    Virtual method:  ");
			}
			System.out.print(method.getName()+" : ");
			Print(method.getFormals());
			System.out.println(" -> "+method.getType().getDisplayName());
		}
		System.out.println();
		for (DeclMethod method : icClass.getMethods()) {
	
			Print(method);
		}
		
	}
	
	private void Print(DeclMethod method) {
		System.out.println("Method Symbol Table: "+method.getName()+"  (parent = "+method.GetScope()+")");
		
		for (Parameter parameter : method.getFormals()) {
			System.out.println("    Parameter:  "+parameter.getName()+" : "+parameter.getType().getDisplayName());
		}
		
		for (Statement statement : method.getStatements()){
			System.out.println("Statement Block Symbol Table: @"+method.getName()+"  (parent = "+statement.GetScope().parent);
		}
		
	}

	private void Print(List<Parameter> formals){
		for (int i = 0; i < formals.size(); i++) {
			System.out.print(formals.get(i).getType().getDisplayName());
			if (i < formals.size()-1) {
				System.out.print(", ");
			}
		}
	}
	
}