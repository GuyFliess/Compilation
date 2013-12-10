package TypeSafety;

import scope.ClassScope;
import scope.GlobalScope;
import scope.Scope;
import ic.ast.Visitor;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclLibraryMethod;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.PrimitiveType;
import ic.ast.decl.PrimitiveType.DataType;
import ic.ast.decl.Program;
import ic.ast.decl.Type;
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

public class TypingRules implements Visitor {
	
	private GlobalScope globalScope;

	public TypingRules (GlobalScope globalScope) {
		this.globalScope = globalScope;
	}

	@Override
	public Object visit(Program program) {
		for (DeclClass icClass : program.getClasses()) {
			icClass.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(DeclClass icClass) {
		for (DeclField field : icClass.getFields())
			field.accept(this);
		for (DeclMethod method : icClass.getMethods())
			method.accept(this);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclField field) {
		field.getType().accept(this);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclVirtualMethod method) {
		for (Parameter formal : method.getFormals())
			formal.accept(this);
		for (Statement statement : method.getStatements())
			statement.accept(this);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclStaticMethod method) {
		// TODO Auto-generated method stub
		for (Parameter formal : method.getFormals())
			formal.accept(this);
		for (Statement statement : method.getStatements())
			statement.accept(this);
		return null;
	}

	@Override
	public Object visit(DeclLibraryMethod method) {
		// TODO Auto-generated method stub
		method.getType().accept(this);
		for (Parameter formal : method.getFormals())
			formal.accept(this);
		return null;
	}

	@Override
	public Object visit(Parameter formal) {
		// TODO Auto-generated method stub
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
		assignment.getVariable().accept(this);
		assignment.getAssignment().accept(this);
		return null;
	}

	@Override
	public Object visit(StmtCall callStatement) {
		callStatement.getCall().accept(this);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtReturn returnStatement) {
		if (returnStatement.hasValue()) {
			returnStatement.getValue().accept(this);
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(StmtIf ifStatement) {
		// TODO Auto-generated method stub
		ifStatement.getCondition().accept(this);
		ifStatement.getOperation().accept(this);
		if (ifStatement.hasElse())
			ifStatement.getElseOperation().accept(this);
		return null;
	}

	@Override
	public Object visit(StmtWhile whileStatement) {
		// TODO Auto-generated method stub
		whileStatement.getCondition().accept(this);
		whileStatement.getOperation().accept(this);
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
		for (Statement statement : statementsBlock.getStatements())
			statement.accept(this);
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		// TODO Auto-generated method stub
		if (localVariable.isInitialized()) {
			localVariable.getInitialValue().accept(this);
		}
		return null;
	}

	@Override
	public Object visit(RefVariable location) {
		// TODO Auto-generated method stub

		Scope scope = location.GetScope();
		return scope.GetVariable(location.getName());
	}

	@Override
	public Object visit(RefField location) {
		location.getObject().accept(this);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(RefArrayElement location) {
		// TODO Auto-generated method stub
		location.getArray().accept(this);
		location.getIndex().accept(this);
		return null;
	}

	@Override
	public Object visit(StaticCall call) {
		for (Expression argument : call.getArguments())
			argument.accept(this);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(VirtualCall call) {
		if (call.hasExplicitObject()) {
			call.getObject().accept(this);
		}
		for (Expression argument : call.getArguments())
			argument.accept(this);
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
		newArray.getSize().accept(this);
		newArray.getType().accept(this);
		return null;
	}

	@Override
	public Object visit(Length length) {
		Type type1 = (Type)  length.getArray().accept(this);
		if (type1.getArrayDimension() > 0)
		{
			return new PrimitiveType(length.getLine(), DataType.INT);
		}
		else
		{
			throw new TypingRuleException(String.format("invalid length operation, type %s is not an array",type1), length.getLine());
		}
	}

	@Override
	public Object visit(Literal literal) {
		return new PrimitiveType(literal.getLine(), literal.getType());
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		Type type1 = (Type) unaryOp.getOperand().accept(this);

		switch (unaryOp.getOperator()) {
		case LNEG: {
			if ((type1 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							DataType.INT)) {
				return new PrimitiveType(unaryOp.getLine(), DataType.INT);
			}
			else {
				throw new TypingRuleException(String.format("invalid logical unary op (%s) on non-integer expression",type1.getDisplayName()), unaryOp.getLine());
			}
				
		}
		case UMINUS:
			if ((type1 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							DataType.INT)) {
				return new PrimitiveType(unaryOp.getLine(), DataType.BOOLEAN);
			}
			else {
				throw new TypingRuleException(String.format("invalid logical unary op (%s) on non-boolean expression",type1.getDisplayName()), unaryOp.getLine());
			}
		default:
			throw new Error("internal error, unknown enum type");
		}		
	}

	@Override
	public Object visit(BinaryOp binaryOp) {
		Type type1 = (Type) binaryOp.getFirstOperand().accept(this);
		Type type2 = (Type) binaryOp.getSecondOperand().accept(this);
		switch (binaryOp.getOperator()) {
		case DIVIDE:
		case MINUS:
		case MOD:
		case MULTIPLY: {
			// int
			if (!(type1 instanceof PrimitiveType)
					|| !(type2 instanceof PrimitiveType)
					|| !((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.INT)
					|| !((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.INT)) {
				throw new TypingRuleException(
						String.format(
								"invalid logical binary op (%s) on non-integer expression",
								binaryOp.getOperator()), binaryOp.getLine()); // 9:
																				// semantic
																				// error;
																				// Invalid
																				// logical
																				// binary
																				// op
																				// (>)
																				// on
																				// non-integer
																				// expression
			}
			// return PrimitiveType.DataType.INT;
			return new PrimitiveType(-1, DataType.INT); // TODO what to return?
														// type1 is the wrong
														// location, don't want
														// to edit ast
		}
		case PLUS: {
			if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.INT)
					&& ((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.INT)) {
				return new PrimitiveType(-1, DataType.INT);
			} else if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.STRING)
					&& ((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.STRING)) {
				return type1;
			} else {
				throw new TypingRuleException(
						String.format(
								"invalid logical binary op (%s) on non-integer expression",
								binaryOp.getOperator()), binaryOp.getLine()); // 9:
																				// semantic
																				// error;
																				// Invalid
																				// logical
																				// binary
																				// op
																				// (>)
																				// on
																				// non-integer
																				// expression
			}
		}

		case GT:
		case GTE:
		case LT:
		case LTE: {
			if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.INT)
					&& ((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.INT)) {
				return new PrimitiveType(-1, DataType.BOOLEAN);
			} else {
				throw new TypingRuleException(
						String.format(
								"invalid logical binary op (%s) on non-integer expression",
								binaryOp.getOperator()), binaryOp.getLine());
			}
		}
		case LAND:
		case LOR: {
			if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.BOOLEAN)
					|| ((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.BOOLEAN)) {
				return new PrimitiveType(-1, DataType.BOOLEAN);
			}
		}
		case EQUAL:
		case NEQUAL: {
			// primitive case
			if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							((PrimitiveType) type1).getDataType())) {
				return new PrimitiveType(-1, DataType.BOOLEAN);
			}
			// class case
			else if ((isSubTypeOf(type1, type2)) || isSubTypeOf(type2, type1)) {
					// then one inherits from the other, we're good
					return new PrimitiveType(-1, DataType.BOOLEAN);
				}				
			
			//if one side is null
			else 
			{
			throw new TypingRuleException(String.format(
					"invalid logical binary op (%s) on ??? expression",
					binaryOp.getOperator()), binaryOp.getLine());
			}

		}
		default:
			throw new Error("internal error, unknown enum type");

		}
	}
public  Boolean isSubTypeOf(Type type1, Type type2)
{
	if ( (type1 instanceof PrimitiveType) && ((PrimitiveType) type1).getDataType().equals(DataType.VOID))
	{
		return true;
	}
	if ( (type2 instanceof PrimitiveType) && ((PrimitiveType) type2).getDataType().equals(DataType.VOID))
	{
		return true;
	}
	if ((!(type1 instanceof ClassType)) || (!(type2 instanceof ClassType)))
		return false;
	ClassType class1 = (ClassType) type1;
	ClassType class2 = (ClassType) type2;
	if (class1.getClassName().compareTo(class2.getClassName()) == 0)
	{
		// A<=A
		return  true;
	}
	 // class1 <= class2
	ClassScope extendingScope = globalScope.GetclassesScopes().get(class1.getClassName());
	ClassScope fatherScope = globalScope.GetclassesScopes().get(class2.getClassName());
	Scope tempScope = extendingScope;
	while (tempScope instanceof ClassScope)
	{
		if (tempScope.equals(fatherScope)) return true;
		tempScope = tempScope.fatherScope;
	}
	 extendingScope = globalScope.GetclassesScopes().get(class2.getClassName());
	 fatherScope = globalScope.GetclassesScopes().get(class1.getClassName());
	 tempScope = extendingScope;
	while (tempScope instanceof ClassScope)
	{
		if (tempScope.equals(fatherScope)) return true;
		tempScope = tempScope.fatherScope;
	}
	//class2 <= class1
	//TODO continue checks
	return false;
}
}

