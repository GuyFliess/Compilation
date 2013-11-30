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
import ic.ast.stmt.StmtAssignment;
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtBreak;
import ic.ast.stmt.StmtCall;
import ic.ast.stmt.StmtContinue;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtReturn;
import ic.ast.stmt.StmtWhile;

import java.util.HashMap;
import java.util.Map;

import scope.*;

public class BuildScope implements Visitor{
	
	GlobalScope scope = new GlobalScope();
	
	public GlobalScope build(Node programAst) {
		
		
		
		
		
		
		return null;
		
	}

	@Override
	public Object visit(Program program) {

		for (DeclClass icClass : program.getClasses()) {
			scope.add(icClass.accept(this));/// TODO add class to classScope
		}
		
		return null;
	}

	@Override
	public Object visit(DeclClass icClass) {
		// TODO Auto-generated method stub
		ClassScope classS = new ClassScope();
		
		for (DeclField field : icClass.getFields()) {
			classS.addField(field);
		}
		
		for (DeclMethod method : icClass.getMethods()) {
			
		}
		
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