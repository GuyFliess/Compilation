package scopeBuilder;

import ic.ast.Node;
import ic.ast.Visitor;
import ic.ast.decl.*;
import ic.ast.expr.BinaryOp;
import ic.ast.expr.Length;
import ic.ast.expr.Literal;
import ic.ast.expr.NewArray;
import ic.ast.expr.NewInstance;
import ic.ast.expr.RefArrayElement;
import ic.ast.expr.RefField;
import ic.ast.expr.RefVariable;
import ic.ast.expr.StaticCall;
import ic.ast.expr.This;
import ic.ast.expr.UnaryOp;
import ic.ast.expr.VirtualCall;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Statement;
import ic.ast.stmt.StmtAssignment;
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtBreak;
import ic.ast.stmt.StmtCall;
import ic.ast.stmt.StmtContinue;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtReturn;
import ic.ast.stmt.StmtWhile;
import scope.*;

public class BuildScope implements Visitor { 
	Scope currentScope;

	public GlobalScope build(Node programAst) {

		return null;

	}

	@Override
	public Object visit(Program program) {
		GlobalScope globalScope = new GlobalScope(null,"Global");
		program.SetScope(globalScope);
		for (DeclClass icClass : program.getClasses()) {
			currentScope = globalScope;
			globalScope.AddClassScope((ClassScope) icClass.accept(this),
					icClass);// / TODO add class to classScope
		}
		return globalScope;
	}

	@Override
	public Object visit(DeclClass icClass) {
		ClassScope classScope;
		if (icClass.hasSuperClass())
		{
			String superClassName = icClass.getSuperClassName();
			ClassScope superScope = ((GlobalScope) currentScope).GetclassesScopes().get(superClassName);
			classScope = new ClassScope(superScope, icClass.getName());
			classScope.HasSuperNode = true;
		}
		else
		{
			 classScope = new ClassScope(currentScope, icClass.getName());
		}
		
		currentScope = classScope;
		for (DeclField field : icClass.getFields()) {
			
			field.accept(this);
			
//			classScope.addField(field);
			classScope.AddVar(field);
		}

		for (DeclMethod method : icClass.getMethods()) {
			currentScope = classScope;
			MethodScope methodScope = (MethodScope) method.accept(this);
			if (method instanceof DeclStaticMethod) {
				classScope.addMethod((DeclStaticMethod) method, methodScope);
			}
			if (method instanceof DeclVirtualMethod) {
				classScope.addMethod((DeclVirtualMethod) method, methodScope);
			}
		}

		return classScope;
	}

	@Override
	public Object visit(DeclField field) {
		field.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(DeclVirtualMethod method) {
		MethodScope methodscope = new MethodScope(currentScope, method.getName());

		currentScope = methodscope;
		method.getType().accept(this);
		for (Parameter formal : method.getFormals()) {
			methodscope.AddParameter(formal);  //TODO change to Addvar
			formal.accept(this);
		}
		
		for (Statement statement : method.getStatements()) {
			statement.accept(this);
//			methodscope.AddStatement((StatementBlockScope) statement
//					.accept(this));
		}

		return methodscope;
	}

	@Override
	public Object visit(DeclStaticMethod method) {
		MethodScope methodscope = new MethodScope(currentScope, method.getName());

		currentScope = methodscope;
		method.getType().accept(this);
		for (Parameter formal : method.getFormals()) {
			methodscope.AddParameter(formal);
			formal.accept(this);
		}
		for (Statement statement : method.getStatements()) {
//			methodscope.AddStatement((StatementBlockScope) statement
					statement.accept(this);
		}

		return methodscope;
	}

	@Override
	public Object visit(DeclLibraryMethod method) {
		MethodScope methodscope = new MethodScope(currentScope, method.getName());

		currentScope = methodscope;
		method.getType().accept(this);
		for (Parameter formal : method.getFormals()) {
			methodscope.AddParameter(formal);
			formal.accept(this);
		}

		return null;
	}

	@Override
	public Object visit(Parameter formal) {
		formal.SetScope(currentScope);
		formal.getType().accept(this);
		return null;
	}

	@Override
	public Object visit(PrimitiveType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ClassType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtAssignment assignment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtCall callStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtReturn returnStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtIf ifStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtWhile whileStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtBreak breakStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtContinue continueStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtBlock statementsBlock) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		
		currentScope.AddVar(localVariable);
		return null;
	}

	@Override
	public Object visit(RefVariable location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(RefField location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(RefArrayElement location) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StaticCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(This thisExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewInstance newClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(NewArray newArray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Length length) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Literal literal) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BinaryOp binaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

}