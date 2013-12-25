package addressCode;

import java.util.ArrayList;

import scope.ClassScope;
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
import ic.ast.decl.Program;
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
import interp.Interpreter.RuntimeError;
import interpBuilder.Variable;
import interpBuilder.Variable.VariableLocation;
import interpBuilder.Variable.VariableType;

public class AdressCodeTranslator implements Visitor {

	int currentRegister = 0;
	int currentLabel = 0;
	ArrayList<String> instructions = new ArrayList<>();

	@Override
	public Object visit(Program program) {

		for (DeclClass declClass : program.getClasses()) {
			declClass.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(DeclClass icClass) {
		for (DeclField field : icClass.getFields()) {
			// TODO do nothing for now
		}
		for (DeclMethod method : icClass.getMethods()) {
			method.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(DeclField field) {
		// TODO throw error
		return null;
	}

	@Override
	public Object visit(DeclVirtualMethod method) {
		// TODO throw error
		return null;
	}

	@Override
	public Object visit(DeclStaticMethod method) {
		ClassScope classScope = (ClassScope) method.GetScope().fatherScope;
		classScope.GetMethod(method.getName()).setLabel(currentLabel);
		currentLabel++;

		for (Parameter parameter : method.getFormals()) {
			parameter.accept(this);
		}

		for (Statement statement : method.getStatements()) {
			statement.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(DeclLibraryMethod method) {
		// TODO add instruction of call to the method
		return null;
	}

	@Override
	public Object visit(Parameter formal) {
		// TODO initialize a register for the formal, update it in the method scope
		return null;
	}

	@Override
	public Object visit(PrimitiveType type) {
		// TODO ???
		return null;
	}

	@Override
	public Object visit(ClassType type) {
		// TODO throw error
		return null;
	}

	@Override
	public Object visit(StmtAssignment assignment) {
		// TODO find the register of the right hand side of the assignment (accept)
		// and the register of the left hand side (accept) and move register to register
		// check what to do in case of RefArrayElement
		return null;
	}

	@Override
	public Object visit(StmtCall callStatement) {
		return callStatement.getCall().accept(this);
	}

	@Override
	public Object visit(StmtReturn returnStatement) {

		if (returnStatement.hasValue()) {
			int value = (int) returnStatement.getValue().accept(this);
			this.instructions.add("ret $" + value);
		} else {
			this.instructions.add("ret");
		}
		return null;
	}

	@Override
	public Object visit(StmtIf ifStatement) {
		// TODO find the register in which there's the condition result initialize new 2 labels
		return null;
	}

	@Override
	public Object visit(StmtWhile whileStatement) {
		// TODO find the register in which there's the condition result initialize new 2 labels
		return null;
	}

	@Override
	public Object visit(StmtBreak breakStatement) {
		// TODO add a goto label
		return null;
	}

	@Override
	public Object visit(StmtContinue continueStatement) {
		// TODO add a goto label
		return null;
	}

	@Override
	public Object visit(StmtBlock statementsBlock) {
		// TODO iterate over the stmts and call accept(this)
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		// TODO initialize a new register and save that variable in the scope, 
		// make sure to check if localVariable.isInitialized() and load it to the register
		return null;
	}

	@Override
	public Object visit(RefVariable location) {
		// TODO find the variable in the scope and return its register
		return null;
	}

	@Override
	public Object visit(RefField location) {
		// TODO throw error
		return null;
	}

	@Override
	public Object visit(RefArrayElement location) {
		// TODO find the register in the scope and find the address + offset + 1
		return null;
	}

	@Override
	public Object visit(StaticCall call) {
		// TODO find the method details: label and parameters registers, and add param & call instructions
		return null;
	}

	@Override
	public Object visit(VirtualCall call) {
		// TODO throw error
		return null;
	}

	@Override
	public Object visit(This thisExpression) {
		// TODO throw error
		return null;
	}

	@Override
	public Object visit(NewInstance newClass) {
		// TODO throw error
		return null;
	}

	@Override
	public Object visit(NewArray newArray) {
		// TODO add a new variable to the scope and in the first slot keep the length (allocate a register)
		return null;
	}

	@Override
	public Object visit(Length length) {
		// TODO find the register in the scope in which the array is stored and return the first slot (load)
		return null;
	}

	@Override
	public Object visit(Literal literal) {
		// TODO: add instruction of loading the literal into a new register and return the currentRegister(++)
		return null;
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(BinaryOp binaryOp) {
		int register1 = (int) binaryOp.getFirstOperand().accept(this);
		int register2 = (int) binaryOp.getSecondOperand().accept(this);
		int result;
		String current_op;
		switch (binaryOp.getOperator()) {
		case PLUS:
			current_op = "+";
			instructions.add("+ $" + register1 + " $" + register2 + " $"
					+ currentRegister++);
			break;
		case MINUS:
			current_op = "+";
			break;
		case MULTIPLY:
			current_op = "+";
			instructions.add("+ $" + register1 + " $" + register2 + " $"
					+ currentRegister++);
			break;
		case DIVIDE:
			current_op = "+";
			break;
		case MOD:
			current_op = "+";
			break;
//		case LAND:
//			value = (Integer) first_value & second_value;
//			result = new Variable(VariableType.INT, VariableLocation.NONE,
//					"none", this.state.scope, false, 1, value);
//			break;
//		case LOR:
//			value = (Integer) first_value | second_value;
//			result = new Variable(VariableType.INT, VariableLocation.NONE,
//					"none", this.state.scope, false, 1, value);
//			break;
//		case LT:
//			value = (Boolean) (first_value < second_value) ? true : false;
//			result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE,
//					"none", this.state.scope, false, 1, value);
//			break;
//		case LTE:
//			value = (Boolean) (first_value <= second_value) ? true : false;
//			result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE,
//					"none", this.state.scope, false, 1, value);
//			break;
//		case GT:
//			value = (Boolean) (first_value > second_value) ? true : false;
//			result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE,
//					"none", this.state.scope, false, 1, value);
//			break;
//		case GTE:
//			value = (Boolean) (first_value >= second_value) ? true : false;
//			result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE,
//					"none", this.state.scope, false, 1, value);
//			break;
//		case EQUAL:
//			value = (Boolean) (first_value == second_value) ? true : false;
//			result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE,
//					"none", this.state.scope, false, 1, value);
//			break;
//		case NEQUAL:
//			value = (Boolean) (first_value != second_value) ? true : false;
//			result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE,
//					"none", this.state.scope, false, 1, value);
//			break;
		}
		return null;
	}

}
