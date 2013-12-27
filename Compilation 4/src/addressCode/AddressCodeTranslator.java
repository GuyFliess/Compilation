package addressCode;

import java.util.ArrayList;
import java.util.Set;

import scope.ClassScope;
import scope.MethodScope;
import scope.MethodTypeWrapper;
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
import interp.Interpreter.RuntimeError;
import interpBuilder.Variable;
import interpBuilder.Variable.VariableLocation;
import interpBuilder.Variable.VariableType;

public class AddressCodeTranslator implements Visitor {

	int currentRegister;
	int methodRegister;
	int currentLabel;
	ArrayList<String> instructions;
	
	public AddressCodeTranslator(String[] args) {
		super();
		this.currentLabel = 0;
		this.currentRegister = 0;
		this.instructions = new ArrayList<>();
		for (int i = 1; i < args.length; i++) {
			this.instructions.add("= " + args[i] + " $" + this.currentRegister++);
		}
	}

	@Override
	public Object visit(Program program) {

		for (DeclClass declClass : program.getClasses()) {
			declClass.accept(this);
		}
		return instructions;
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
		instructions.add(":" + currentLabel);
		currentLabel++;
		this.methodRegister = 0;
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

		return null;
	}

	@Override
	public Object visit(Parameter formal) {
		formal.getType().accept(this);
		// TODO initialize a register for the formal, update it in the method
		// scope
		((MethodScope) formal.GetScope()).AddParameterReg(formal.getName(), this.methodRegister++ );
		return null;
	}

	@Override
	public Object visit(PrimitiveType type) {
		// TODO ???
		return null;
	}

	@Override
	public Object visit(ClassType type) {
		// TODO throw errorsd
		return null;
	}

	@Override
	public Object visit(StmtAssignment assignment) {
		// TODO find the register of the right hand side of the assignment
		// (accept)
		// and the register of the left hand side (accept) and move register to
		// register
		// check what to do in case of RefArrayElement

		int reg1 = (int) assignment.getAssignment().accept(this);
		assignment.getVariable().accept(this);
		return null;
	}

	@Override
	public Object visit(StmtCall callStatement) {
		callStatement.getCall().accept(this);
		return null;
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
		// TODO find the register in which there's the condition result
		// initialize new 2 labels
		int elseLabel = currentLabel++;
		int endLabel = currentLabel++;
		int conditionReg = (int) ifStatement.getCondition().accept(this);
		instructions.add("if! $" + conditionReg + " :" + elseLabel);

		ifStatement.getOperation().accept(this);

		instructions.add("goto :" + endLabel);
		instructions.add(":" + elseLabel);
		if (ifStatement.hasElse()) {
			ifStatement.getElseOperation().accept(this);
		}
		instructions.add(":" + endLabel);
		return null;
	}

	@Override
	public Object visit(StmtWhile whileStatement) {
		// TODO find the register in which there's the condition result
		// initialize new 2 labels
		int startLabel = currentLabel++;
		int endLabel = currentLabel++;
		int conditionReg = (int) whileStatement.getCondition().accept(this);
		instructions.add(": " + startLabel);
		instructions.add("if! $" + conditionReg + " :" + endLabel); // if we need to get out of the while - 
		// condition is false (conditionReg == 0)
		whileStatement.getOperation().accept(this);
		instructions.add("goto :" + startLabel);
		instructions.add(":" + endLabel);

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
		for (Statement statement : statementsBlock.getStatements()) {
			statement.accept(this);
		}
		// iterate over the stmts and call accept(this)
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		// TODO initialize a new register and save that variable in the scope,
		// make sure to check if localVariable.isInitialized() and load it to
		// the register
		int varReg = currentRegister++;
		localVariable.GetScope().setReg(localVariable.getName(), varReg);

		if (localVariable.isInitialized()) {
			int regInit = (int) localVariable.getInitialValue().accept(this);
			instructions.add(String.format("= $%s $%s", regInit, varReg));
		}

		return null;
	}

	@Override
	public Object visit(RefVariable location) {
		// TODO find the variable in the scope and return its register

		return location.GetScope().getVaraibleReg(location.getName());
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
		// TODO find the method details: label and parameters registers, and add
		// param & call instructions
		// TODO add instruction of library call to the method

		MethodTypeWrapper methodSignature = call.GetScope().GetMethod(
				call.getMethod());

		for (Expression expr : call.getArguments()) {
			int reg = (int) expr.accept(this);
			instructions.add("param $" + reg);
		}
		if (methodSignature.getReturnType().getDisplayName()
				.equalsIgnoreCase("void")) {
			instructions.add("call :" + methodSignature.getLabel());
		} else {
			instructions.add("call :" + methodSignature.getLabel() + " $"
					+ currentRegister);
			return currentRegister++;
		}
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
		// TODO add a new variable to the scope and in the first slot keep the
		// length (allocate a register)
		return null;
	}

	@Override
	public Object visit(Length length) {
		// TODO find the register in the scope in which the array is stored and
		// return the first slot (load)
		int arrReg = (int) length.getArray().accept(this);
		int resultReg = currentRegister++;
		instructions.add("[] $" + arrReg + " $" + resultReg);
		return resultReg;
	}

	@Override
	public Object visit(Literal literal) {
		// TODO: add instruction of loading the literal into a new register and
		// return the currentRegister(++)
		int reg = currentRegister++;
		instructions.add("= " + literal.getValue() + " $" + reg);

		return reg;
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		int register = (int) unaryOp.getOperand().accept(this);
		String current_op = null;
		switch (unaryOp.getOperator()) {
		case LNEG:
			current_op = "!";
			break;
		case UMINUS:
			current_op = "-";
			break;
		}
		instructions.add(current_op + " $" + register + " $"
				+ currentRegister++);
		return null;
	}

	@Override
	public Object visit(BinaryOp binaryOp) {
		int register1 = (int) binaryOp.getFirstOperand().accept(this);
		int register2 = (int) binaryOp.getSecondOperand().accept(this);
		String current_op = null;
		switch (binaryOp.getOperator()) {
		case PLUS:
			current_op = "+";
			break;
		case MINUS:
			current_op = "-";
			break;
		case MULTIPLY:
			current_op = "*";
			break;
		case DIVIDE:
			current_op = "/";
			break;
		case MOD:
			current_op = "%";
			break;
		case LAND:
			current_op = "???"; // TODO
			break;
		case LOR:
			current_op = "???"; // TODO
			break;
		case LT:
			current_op = "<";
			break;
		case LTE:
			current_op = "<=";
			break;
		case GT:
			current_op = ">";
			break;
		case GTE:
			current_op = ">=";
			break;
		case EQUAL:
			current_op = "==";
			break;
		case NEQUAL:
			current_op = "!=";
			break;
		}
		instructions.add(current_op + " $" + register1 + " $" + register2
				+ " $" + currentRegister++);
		return null;
	}

}
