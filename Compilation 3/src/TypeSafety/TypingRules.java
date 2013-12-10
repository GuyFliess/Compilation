package TypeSafety;

import javax.swing.JTable.PrintMode;

import ic.ast.Node;
import ic.ast.Visitor;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclLibraryMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.PrimitiveType;
import ic.ast.decl.PrimitiveType.DataType;
import ic.ast.decl.Program;
import ic.ast.decl.Type;
import ic.ast.expr.BinaryOp;
import ic.ast.expr.Length;
import ic.ast.expr.Literal;
import ic.ast.expr.New;
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
import ic.ast.stmt.StmtAssignment;
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtBreak;
import ic.ast.stmt.StmtCall;
import ic.ast.stmt.StmtContinue;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtReturn;
import ic.ast.stmt.StmtWhile;

public class TypingRules implements Visitor {

	@Override
	public Object visit(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclClass icClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclField field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclVirtualMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclStaticMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclLibraryMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Parameter formal) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		return literal.getType();		
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BinaryOp binaryOp){
		Type type1 = (Type) binaryOp.getFirstOperand().accept(this);
		Type type2 = (Type) binaryOp.getSecondOperand().accept(this);
		switch (binaryOp.getOperator())
		{
		case DIVIDE:
		case MINUS:			
		case MOD:			
		case MULTIPLY:
		{
			//int
			if (!(type1 instanceof PrimitiveType) || !(type2 instanceof PrimitiveType) || 
					!((PrimitiveType) type1).getDataType().equals(PrimitiveType.DataType.INT) ||
					!((PrimitiveType) type2).getDataType().equals(PrimitiveType.DataType.INT))
			{				
				throw new TypingRuleException(String.format("invalid logical binary op (%s) on non-integer expression", binaryOp.getOperator()),binaryOp.getLine()); //9: semantic error; Invalid logical binary op (>) on non-integer expression
			}
		//	return PrimitiveType.DataType.INT;
			return new PrimitiveType(-1, DataType.INT); //TODO what to return? type1 is the wrong location, don't want to edit ast
		}
		case PLUS:
		{
			if ((type1 instanceof PrimitiveType) || (type2 instanceof PrimitiveType) || 
					((PrimitiveType) type1).getDataType().equals(PrimitiveType.DataType.INT) ||
					((PrimitiveType) type2).getDataType().equals(PrimitiveType.DataType.INT))
			{
				return new PrimitiveType(-1, DataType.INT);
			}
			else if  ((type1 instanceof PrimitiveType) || (type2 instanceof PrimitiveType) || 
					((PrimitiveType) type1).getDataType().equals(PrimitiveType.DataType.STRING) ||
					((PrimitiveType) type2).getDataType().equals(PrimitiveType.DataType.STRING))
			{
				return type1;
			}
			else
			{
				throw new TypingRuleException(String.format("invalid logical binary op (%s) on non-integer expression", binaryOp.getOperator()),binaryOp.getLine()); //9: semantic error; Invalid logical binary op (>) on non-integer expression
			}
		}
		
		case GT:
		case GTE:
		case LT:
		case LTE:
		{
			if ((type1 instanceof PrimitiveType) || (type2 instanceof PrimitiveType) || 
					((PrimitiveType) type1).getDataType().equals(PrimitiveType.DataType.INT) ||
					((PrimitiveType) type2).getDataType().equals(PrimitiveType.DataType.INT))
			{
				return new PrimitiveType(-1,DataType.BOOLEAN);
			}
			else
			{
				throw new TypingRuleException(String.format("invalid logical binary op (%s) on non-integer expression", binaryOp.getOperator()),binaryOp.getLine()); //9: semantic error; Invalid logical binary op (>) on non-integer expression
			}
		}
		case LAND:
		case LOR:
		{
			if ((type1 instanceof PrimitiveType) || (type2 instanceof PrimitiveType) || 
					((PrimitiveType) type1).getDataType().equals(PrimitiveType.DataType.BOOLEAN) ||
					((PrimitiveType) type2).getDataType().equals(PrimitiveType.DataType.BOOLEAN))
			{
				return new PrimitiveType(-1,DataType.BOOLEAN);
			}
		}
		case EQUAL:
			break;
		case NEQUAL:
			break;
		default:
			break;
			
		}
		return null;
	}

}
