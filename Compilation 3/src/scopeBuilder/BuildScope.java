package scopeBuilder;

import ic.ast.Visitor;
import ic.ast.decl.*;
import ic.ast.expr.BinaryOp;
import ic.ast.expr.Expression;
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
	GlobalScope f_globalScope;

	public GlobalScope MakeScopes(Program program, DeclClass library)
	{
		GlobalScope globalScope = new GlobalScope(null, "Global");
		currentScope = globalScope;		
		f_globalScope = globalScope;
		if (library != null)
		{
			library.SetScope(globalScope);
			ClassScope libScope = (ClassScope) library.accept(this);
			globalScope.AddClassScope(libScope, library);
		}
		program.SetScope(globalScope);
		for (DeclClass icClass : program.getClasses()) {
			currentScope = globalScope;
			globalScope.AddClassScope((ClassScope) icClass.accept(this),
					icClass);// / TODO add class to classScope
		}		
		return globalScope;
	}
	
	
//	public GlobalScope build(Node programAst) {
//
//		return null;
//
//	}
	
	

	@Override
	public Object visit(Program program) {
		GlobalScope globalScope = new GlobalScope(null, "Global");
		program.SetScope(globalScope);
		for (DeclClass icClass : program.getClasses()) {
			currentScope = globalScope;
	//		globalScope.AddClassScope((ClassScope) icClass.accept(this),
				//	icClass);// / classes are added to global scope even if they are nested in their super class 
		}
		return globalScope;
	}

	@Override
	public Object visit(DeclClass icClass) {
		ClassScope classScope;
		if (icClass.hasSuperClass()) {
			String superClassName = icClass.getSuperClassName();
			ClassScope superScope = ((GlobalScope) currentScope)
					.GetclassesScopes().get(superClassName);
			classScope = new ClassScope(superScope, icClass.getName());
			classScope.HasSuperNode = true;
		} else {
			classScope = new ClassScope(currentScope, icClass.getName()); // must be class scope
		}
		
		icClass.SetScope(classScope);
		currentScope = classScope;
		f_globalScope.AddClassScope(classScope, icClass.getName());
		for (DeclField field : icClass.getFields()) {

			field.accept(this);

			// classScope.addField(field);
			classScope.AddVar(field);
		}

		for (DeclMethod method : icClass.getMethods()) {
			currentScope = classScope;
			MethodScope methodScope = (MethodScope) method.accept(this);
			if (method instanceof DeclStaticMethod) {
				classScope.addMethod((DeclStaticMethod) method, methodScope);
				continue;
			}
			if (method instanceof DeclVirtualMethod) {
				classScope.addMethod((DeclVirtualMethod) method, methodScope);
				continue;
			}
			if (method instanceof DeclLibraryMethod)
			{
				classScope.addMethod((DeclLibraryMethod) method, methodScope);
				continue;
			}
		}
//		//TODO at Shachar's suggetion, adding 
//		g_globalScope.AddClassScope(classScope, classDecl);
		return classScope;
	}

	@Override
	public Object visit(DeclField field) {
		field.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(DeclVirtualMethod method) {
		MethodScope methodscope = new MethodScope(currentScope,
				method.getName());
		method.SetScope(methodscope);
		currentScope = methodscope;
		method.getType().accept(this);
		for (Parameter formal : method.getFormals()) {
			methodscope.AddParameter(formal); // TODO change to Addvar
			formal.accept(this);
		}

		for (Statement statement : method.getStatements()) {
			statement.accept(this);
			// methodscope.AddStatement((StatementBlockScope) statement
			// .accept(this));
		}

		return methodscope;
	}

	@Override
	public Object visit(DeclStaticMethod method) {
		MethodScope methodscope = new MethodScope(currentScope,
				method.getName());
		method.SetScope(methodscope);
		currentScope = methodscope;
		method.getType().accept(this);
		for (Parameter formal : method.getFormals()) {
			methodscope.AddParameter(formal);
			formal.accept(this);
		}
		for (Statement statement : method.getStatements()) {
			// methodscope.AddStatement((StatementBlockScope) statement
			statement.accept(this);
		}

		return methodscope;
	}

	@Override
	public Object visit(DeclLibraryMethod method) {
		MethodScope methodscope = new MethodScope(currentScope,
				method.getName());
		method.SetScope(methodscope);
		currentScope = methodscope;
		method.getType().accept(this);
		for (Parameter formal : method.getFormals()) {
			methodscope.AddParameter(formal);
			formal.accept(this);
		}

		return methodscope;
	}

	@Override
	public Object visit(Parameter formal) {
		formal.SetScope(currentScope);
		formal.getType().accept(this);
		return null;
	}

	@Override
	public Object visit(PrimitiveType type) {
		type.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(ClassType type) {
		type.SetScope(f_globalScope.GetclassesScopes().get(type.getClassName()));
//		type.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(StmtAssignment assignment) {
		assignment.SetScope(currentScope);
		assignment.getVariable().accept(this);
		assignment.getAssignment().accept(this);
		return null;
	}

	@Override
	public Object visit(StmtCall callStatement) {
		callStatement.SetScope(currentScope);
		callStatement.getCall().accept(this);
		return null;
	}

	@Override
	public Object visit(StmtReturn returnStatement) {
		returnStatement.SetScope(currentScope);
		if (returnStatement.hasValue())
		{
			returnStatement.getValue().accept(this);
		}
		return null;
	}

	@Override
	public Object visit(StmtIf ifStatement) {
		ifStatement.SetScope(currentScope);

		ifStatement.getCondition().accept(this);

		currentScope = ifStatement.GetScope();
		ifStatement.getOperation().accept(this);
		currentScope = ifStatement.GetScope();
		if (ifStatement.hasElse())
			ifStatement.getElseOperation().accept(this);
		return null;
	}

	@Override
	public Object visit(StmtWhile whileStatement) {
		whileStatement.SetScope(currentScope);

		whileStatement.getCondition().accept(this);
		currentScope = whileStatement.GetScope();

		whileStatement.getOperation().accept(this);

		return null;
	}

	@Override
	public Object visit(StmtBreak breakStatement) {
		breakStatement.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(StmtContinue continueStatement) {
		continueStatement.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(StmtBlock statementsBlock) {
		StatementBlockScope blockscope;
		if (currentScope instanceof MethodScope) // TODO a more proper
													// inheritance would help
													// avoid this switch
		{
			blockscope = new StatementBlockScope(currentScope, String.format(
					"@%s", currentScope.getName()));
			// ((MethodScope) currentScope).AddStatementScope(blockscope);
		} else if (currentScope instanceof StatementBlockScope) {
			blockscope = new StatementBlockScope(currentScope,
					currentScope.getName());
			// ((StatementBlockScope)
			// currentScope).AddStatementScope(blockscope);
		} else {
			throw new Error(
					"Internal error, current scope should by either stmt block or method scope");
		}
		((StatementBlockScope) currentScope).AddStatementScope(blockscope);

		statementsBlock.SetScope(blockscope);

		for (Statement statement : statementsBlock.getStatements()) {
			currentScope = blockscope;
			statement.accept(this);
		}

		return blockscope;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		
		localVariable.SetScope(currentScope);
		
		currentScope.AddVar(localVariable);
		localVariable.getType().accept(this);
		currentScope = localVariable.GetScope();
		if (localVariable.isInitialized())
		{
			localVariable.getInitialValue().accept(this);
		}
		return null;
	}

	@Override
	public Object visit(RefVariable location) {
		location.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(RefField location) {
		location.SetScope(currentScope);
		location.getObject().accept(this);
		return null;
	}

	@Override
	public Object visit(RefArrayElement location) {
		location.SetScope(currentScope);
		location.getArray().accept(this);
		currentScope = location.GetScope();
		location.getIndex().accept(this);

		return null;
	}

	@Override
	public Object visit(StaticCall call) {
		call.SetScope(currentScope);
		for (Expression argument : call.getArguments()) {
			currentScope = call.GetScope();
			argument.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(VirtualCall call) {
		call.SetScope(currentScope);
		if (call.hasExplicitObject())
		{
			call.getObject().accept(this);
		}
		for (Expression argument : call.getArguments())
		{
			currentScope = call.GetScope();
			argument.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(This thisExpression) {
		thisExpression.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(NewInstance newClass) {

		newClass.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(NewArray newArray) {
		newArray.SetScope(currentScope);
		newArray.getType().SetScope(currentScope);
		newArray.getSize().SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(Length length) {
		length.SetScope(currentScope);
		length.getArray().accept(this);		
		return null;
	}

	@Override
	public Object visit(Literal literal) {
		literal.SetScope(currentScope);
		return null;
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		unaryOp.SetScope(currentScope);
		unaryOp.getOperand().accept(this);
		return null;
	}

	@Override
	public Object visit(BinaryOp binaryOp) {
		binaryOp.SetScope(currentScope);
		binaryOp.getFirstOperand().accept(this);
		binaryOp.SetScope(currentScope);
		binaryOp.getSecondOperand().accept(this);
		return null;
	}

}