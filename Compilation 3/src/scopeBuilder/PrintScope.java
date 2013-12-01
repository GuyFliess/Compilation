package scopeBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scope.ClassScope;
import scope.GlobalScope;
import scope.MethodScope;
import scope.MethodTypeWrapper;
import scope.StatementBlockScope;
import groovyjarjarantlr.StringUtils;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.Program;
import ic.ast.decl.Type;
import ic.ast.stmt.Statement;

public class PrintScope {
	
//	private GlobalScope globalscope;
//	
//	public PrintScope(GlobalScope globalScope) {
//		this.globalscope = globalScope;
//	}

	public void Print(GlobalScope globalScope)
	{
		System.out.println("Global Symbol Table");
		for (  String name : globalScope.GetclassesScopes().keySet())
			{
				System.out.println("    Class: "+ name);
			}
		System.out.println();
		for ( ClassScope  classScope : globalScope.GetclassesScopes().values())
		{
			print(classScope);
		}
	}
	


	private void print(ClassScope classScope) {
	
		System.out.print(String.format("Class Symbol Table: %s ",classScope.getName())); // TODO hadnle fathers
		if (classScope.HasSuperNode)
		{
			System.out.print(String.format("(parent = %s)",classScope.fatherScope.getName()));
		}
		System.out.println();
		
		Map<String, Type> fields = classScope.getFields();
		for (String name : fields.keySet()) {
			System.out.println(String.format("    Field:  %s : %s", name, fields.get(name).getDisplayName() ));
		}
		
		//first print every method signature
		Map<String, MethodTypeWrapper> staticMethods = classScope.getStaticMethodScopes();
		printMethod(staticMethods, "Static");
		
		Map<String, MethodTypeWrapper> virtualMethods = classScope.getVirtualMethodScopes();
		printMethod(virtualMethods, "Virtual");
		
		System.out.println();
		
		//visit and print every static method scope
		for  (MethodTypeWrapper methodWrapper : staticMethods.values())
		{
			print(methodWrapper.getBodyScope());
		}
		
		//visit and print every virtual method scope
				for  (MethodTypeWrapper methodWrapper : virtualMethods.values())
					
				{
					print(methodWrapper.getBodyScope());
				}
		
		
	}

	private void printMethod(Map<String, MethodTypeWrapper> methods, String typeOfMethods) {
		for (String name : methods.keySet())
		{
			MethodTypeWrapper methodWrapper = methods.get(name);
			StringBuffer output = new StringBuffer();
			output.append(String.format("    %s method:  %s : ",typeOfMethods, name ));
		
			
			for (Type type : methodWrapper.getParameters()) { //TODO fix last ','
				output.append(type.getDisplayName());
				output.append(",");
				output.append(" ");
			}
			output.deleteCharAt(output.lastIndexOf(","));
			output.append("-> ");
			output.append(methodWrapper.getReturnType().getDisplayName());
			
		
			System.out.println(output.toString());
//			print(methodWrapper.getBodyScope());

		}
	}

	private void print(MethodScope bodyScope) {		
		System.out.println(String.format("Method Symbol Table: %s (parent = %s)",bodyScope.getName(),bodyScope.fatherScope.getName())); // TODO hadnle fathers
		
		//print the symbol table
		Map<String, Type> parameters = bodyScope.getParameters();
		for (String name : parameters.keySet()) {
			System.out.println(String.format("    Parameter:  %s : %s", name, parameters.get(name).getDisplayName()));
		}
		Map<String, Type> localVariables = bodyScope.getLocalVariables();
		for (String name : localVariables.keySet()) {
			System.out.println(String.format("    Local variable:  %s : %s", name, localVariables.get(name).getDisplayName()));
		}
		System.out.println();
		
		//Recursively print child scopes
		
		for (StatementBlockScope blockScope : bodyScope.getBlockScopes()) {
			print(blockScope);
		}
		
	}

	private void print(StatementBlockScope blockScope) {
		// TODO Auto-generated method stub
		System.out.println(String.format("Statement Block Symbol Table: %s(parent = %s)", blockScope.getName(),blockScope.fatherScope.getName())); // TODO hadnle fathers
		
		Map<String, Type> localVariables = blockScope.getLocalVariables();
		for (String name : localVariables.keySet()) {
			System.out.println(String.format("    Local variable:  %s : %s", name, localVariables.get(name).getDisplayName()));
		}
		System.out.println();
		
		for (StatementBlockScope childScope : blockScope.getBlockScopes()) {
			print(childScope);
		}
		
		
	}

//TODO lital's code

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