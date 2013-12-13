package interp;

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
import interpBuilder.Method;
import interpBuilder.Variable;
import interpBuilder.Variable.VariableLocation;
import interpBuilder.Variable.VariableType;
import interpBuilder.interpClass;

@SuppressWarnings("serial")
public class Interpreter implements Visitor {
	State state;

	public Interpreter(String class_name, String method_name, String[] arguments) {
		this.state = new State(class_name, method_name, arguments);
	}

	public static class RuntimeError extends Error {
		public RuntimeError(String msg) {
			super("RuntimeError: " + msg);
		}
	}

	public static class Break extends Error {
	}

	public static class Continue extends Error {
	}

	public Object visit(Program program) {
		for (DeclClass decl_class : program.getClasses()) {
			this.state.addClass(new interpClass(decl_class.getName()));
		}
		for (DeclClass decl_class : program.getClasses()) {
			if (decl_class.getName().equals(this.state.class_name)) {
				try {
					decl_class.accept(this);
				} catch (RuntimeError e) {
					System.err.println(e.getMessage());
				}
				break;
			}
		}
		return null;
	}

	public Object visit(DeclClass icClass) {
		Variable field;
		Method method;
		for (DeclField decl_field : icClass.getFields()) {
			field = (Variable) decl_field.accept(this);
			this.state.addFieldToClass(this.state.class_name, field);
		}
		for (DeclMethod decl_method : icClass.getMethods()) {
			method = new Method(decl_method.getName());
			this.state.addMethodToClass(this.state.class_name, method);
		}
		for (DeclMethod decl_method : icClass.getMethods()) {
			if (decl_method.getName().equals(this.state.method_name)) {
				decl_method.accept(this);
				break;
			}
		}
		return null;
	}

	public Object visit(DeclField field) {
		VariableType field_type = (VariableType) field.getType().accept(this);
		return new Variable(field_type, VariableLocation.FIELD,
				field.getName(), this.state.scope, field.getType()
						.getArrayDimension() > 0, 1);
	}

	public Object visit(DeclVirtualMethod method) {
		throw new RuntimeError("Interpreter doesn't interpret virtual methods.");
	}

	public Object visit(DeclStaticMethod method) {
		if (method.getFormals().size() != state.arguments.length) {
			throw new RuntimeError("Invalid input: number of parameters for method " + 
					state.class_name + "." + state.method_name +
					" is " + method.getFormals().size() +
					" while the number of parameters given is " + state.arguments.length + ".");
		}
		for (Parameter formal : method.getFormals()) {
			formal.accept(this);
		}
		this.state.scope++;
		for (Statement stmt : method.getStatements()) {
			stmt.accept(this);
		}
		this.state.scope--;
		if (this.state.value != null) {
			System.out.println(this.state.value);
		} else if (this.state.values != null) {
			for (int i = 0; i < this.state.values.length; i++) {
				System.out.println(this.state.values[i]);
			}
		}
		return null;
	}

	public Object visit(DeclLibraryMethod method) {
		// TODO: check if it's ok
		throw new RuntimeError("Interpreter doesn't interpret library methods.");
	}

	public Object visit(Parameter formal) {
		VariableType variable_type = (VariableType) formal.getType().accept(
				this);
		if (variable_type != variable_type.INT && variable_type != variable_type.STRING) {
			throw new RuntimeError("Invalid parameter: parameters cannot be of type " + variable_type.toString() + ".");
		}
		if (formal.getType().getArrayDimension() > 0) {
			throw new RuntimeError("Invalid parameter: parameters cannot arrays.");
		}
		Object[] value = new Object[1];
		value[0] = this.state.arguments[this.state.formal_index++];
		Variable variable = new Variable(variable_type,
				VariableLocation.PARAMETER, formal.getName(), this.state.scope,
				false, 1, value);
		this.state.addVariableToMethod(this.state.class_name,
				this.state.method_name, variable);
		return null;
	}

	public Object visit(PrimitiveType type) {
		switch (type.getDisplayName()) {
		case "int":
			return VariableType.INT;
		case "string":
			return VariableType.STRING;
		case "boolean":
			return VariableType.BOOLEAN;
		}
		return null;
	}

	public Object visit(ClassType type) {
		// TODO: check if it's ok
		// throw new RuntimeError("Interpreter doesn't interpret class types.");
		return VariableType.CLASS;
	}

	public Object visit(StmtAssignment assignment) {
		Variable value = (Variable) assignment.getAssignment().accept(this);
		Variable variable = (Variable) assignment.getVariable().accept(this);
		if (value.getType() != variable.getType()) {
			throw new RuntimeError("Invalid assignment statement: the variable is of type " + variable.getType().toString() +
					" while assignment is of type " + value.getType().toString() + ".");
		}
		
		if (variable.isArray() != value.isArray()) {
			throw new RuntimeError("Invalid variable: variable dimensions is " + (variable.isArray() ? "1" : "0")
					+ " while it is initialized to a value whose dimensions is " + (value.isArray() ? "1" : "0") + ".");
		}
		if (!variable.isArray()) {
			this.state.setVariableValue(this.state.class_name,
					this.state.method_name, variable.getName(),
					value.getValue(), 0);
		} else {
			if (variable.isInitialized()) {
				this.state.setVariableValue(this.state.class_name,
						this.state.method_name, variable.getName(),
						value.getValue(), this.state.array_location);
				this.state.array_location = 0;
			} else {
				variable.setVariableToArray(value.getLength());
			}
		}
		return null;
	}

	public Object visit(StmtCall callStatement) {
		throw new RuntimeError("Interpreter doesn't interpret call statements.");
	}

	public Object visit(StmtReturn returnStatement) {
		if (returnStatement.hasValue()) {
			Variable value = (Variable) returnStatement.getValue().accept(this);
			if (!value.isArray()) {
				this.state.value = value.getValue()[0];
			} else {
				this.state.values = value.getValue();
			}
		}
		return null;
	}

	public Object visit(StmtIf ifStatement) {
		Variable condition = (Variable) ifStatement.getCondition().accept(this);
		Boolean result = (Boolean) condition.getValue()[0];
		if (result) {
			ifStatement.getOperation().accept(this);
		} else {
			if (ifStatement.hasElse()) {
				ifStatement.getElseOperation().accept(this);
			}
		}
		return null;
	}

	public Object visit(StmtWhile whileStatement) {
		Variable condition = (Variable) whileStatement.getCondition().accept(
				this);
		Boolean result = (Boolean) condition.getValue()[0];
		while (result) {
			try {
				whileStatement.getOperation().accept(this);
			} catch (Break e) {
				break;
			} catch (Continue e) {
				condition = (Variable) whileStatement.getCondition().accept(
						this);
				result = (Boolean) condition.getValue()[0];
				continue;
			}
			condition = (Variable) whileStatement.getCondition().accept(this);
			result = (Boolean) condition.getValue()[0];
		}
		return null;
	}

	public Object visit(StmtBreak breakStatement) {
		throw new Break();
	}

	public Object visit(StmtContinue continueStatement) {
		throw new Continue();
	}

	public Object visit(StmtBlock statementsBlock) {
		Object value = null;
		Statement stmt;
		this.state.scope++;
		for (int i = 0; i < statementsBlock.getStatements().size(); i++) {
			stmt = statementsBlock.getStatements().get(i);
			value = stmt.accept(this);
		}
		this.state.deleteVariableFromScope();
		this.state.scope--;
		return value;
	}

	public Object visit(LocalVariable localVariable) {
		Variable variable;
		VariableType type;
		switch (localVariable.getType().getDisplayName()) {
		case "int":
			type = VariableType.INT;
			break;
		case "string":
			type = VariableType.STRING;
			break;
		case "boolean":
			type = VariableType.BOOLEAN;
			break;
		default:
			throw new RuntimeError("Invalid type: variables cannot be of type " + localVariable.getType().getDisplayName() + ".");
		}
		if (localVariable.getType().getArrayDimension() > 1) {
			throw new RuntimeError("Invalid variable: array dimensions cannot be " + localVariable.getType().getArrayDimension() + ".");
		}
		if (localVariable.isInitialized()) {
			Variable value = (Variable) localVariable.getInitialValue().accept(
					this);
			if (type != value.getType()) {
				throw new RuntimeError("Invalid initialization of local variable: the variable type is " +
						type + " while it is initialized to type " + value.getType().toString() + ".");
			}
			if (localVariable.getType().getArrayDimension() != (value.isArray() ? 1 : 0)) {
				throw new RuntimeError("Invalid variable: variable dimensions is " + localVariable.getType().getArrayDimension()
						+ " while it is initialized to a value whose dimensions is " + (value.isArray() ? "1" : "0") + ".");
			}
			if (value.isArray()) {
				variable = new Variable(type, VariableLocation.LOCAL,
						localVariable.getName(), this.state.scope, true,
						value.getLength());
				variable.setInitialized();
			} else {
				variable = new Variable(type, VariableLocation.LOCAL,
						localVariable.getName(), this.state.scope, false, 1,
						value.getValue());
			}
		} else {
			variable = new Variable(type, VariableLocation.LOCAL,
					localVariable.getName(), this.state.scope, localVariable.getType().getArrayDimension() > 0, 1);
		}
		this.state.addVariableToMethod(this.state.class_name,
				this.state.method_name, variable);
		return null;
	}

	public Object visit(RefVariable location) {
		if (state.fieldExists(state.class_name, location.getName())) {
			throw new RuntimeError("Intepreter doesn't interpret fields.");
		}
		if (state.variableExists(this.state.class_name, this.state.method_name,
				location.getName(), this.state.scope)) {
			return state.getVariable(this.state.class_name,
					this.state.method_name, location.getName());
		}
		throw new RuntimeError("Variable " + location.getName()
				+ " does not exist.");
	}

	public Object visit(RefField location) {
		throw new RuntimeError("Interpreter doesn't interpret 'this'.");
	}

	public Object visit(RefArrayElement location) {
		Variable variable = (Variable) location.getArray().accept(this);
		Variable array_index = (Variable) location.getIndex().accept(this);
		if (state.variableExists(this.state.class_name, this.state.method_name,
				variable.getName(), this.state.scope)) {
			this.state.array_location = (int) array_index.getValue()[0];
			return state.getVariable(this.state.class_name,
					this.state.method_name, variable.getName());
		}
		throw new RuntimeError("Variable " + variable.getName()
				+ " does not exist.");
	}

	public Object visit(StaticCall call) {
		throw new RuntimeError("Interpreter doesn't interpret static calls.");
	}

	public Object visit(VirtualCall call) {
		throw new RuntimeError("Interpreter doesn't interpret virtual calls.");
	}

	public Object visit(This thisExpression) {
		// TODO: check if it's ok
		throw new RuntimeError("Interpreter doesn't interpret 'this'.");
		// return this.state.getClass(this.state.class_name);
	}

	public Object visit(NewInstance newClass) {
		// TODO: check if it's ok
		throw new RuntimeError(
				"Interpreter doesn't interpret new class instances.");
		// Variable variable = new Variable(VariableType.CLASS,
		// VariableLocation.LOCAL, newClass.getName(), this.state.scope,
		// false, 1);
		// variable.setInitialized();
		// variable.setClass(this.state.class_name);
		// this.state.addVariableToMethod(this.state.class_name,
		// this.state.method_name, variable);
		// return null;
	}

	public Object visit(NewArray newArray) {
		VariableType type;
		switch (newArray.getType().getDisplayName()) {
		case "int":
			type = VariableType.INT;
			break;
		case "string":
			type = VariableType.STRING;
			break;
		case "boolean":
			type = VariableType.BOOLEAN;
			break;
		default:
			type = VariableType.CLASS;
			break;
		}
		Variable size = (Variable) newArray.getSize().accept(this);
		return new Variable(type, VariableLocation.NONE, "none",
				this.state.scope, true, Integer.parseInt(size.getValue()[0]
						.toString()));
	}

	public Object visit(Length length) {
		// TODO: check if it' ok
		Variable variable = (Variable) length.getArray().accept(this);
		Object[] value = new Object[1];
		value[0] = variable.getLength();
		return new Variable(VariableType.INT, VariableLocation.NONE, "none",
				this.state.scope, false, 1, value);
	}

	public Object visit(Literal literal) {
		VariableType type = null;
		switch (literal.getType()) {
		case INT:
			type = VariableType.INT;
			break;
		case STRING:
			type = VariableType.STRING;
			break;
		case BOOLEAN:
			type = VariableType.BOOLEAN;
			break;
		default:
			throw new RuntimeError("The literal " + literal.getValue()
					+ " is not an int / string / boolean.");
		}
		Object[] value = new Object[1];
		value[0] = literal.getValue();
		return new Variable(type, VariableLocation.NONE, "none",
				this.state.scope, false, 1, value);
	}

	public Object visit(UnaryOp unaryOp) {
		Variable result = null;
		Variable op = (Variable) unaryOp.getOperand().accept(this);
		Object[] value = new Object[1];
		int location = op.isArray() ? this.state.array_location : 0;
		switch (unaryOp.getOperator()) {
		case LNEG:
			if (op.getType() == VariableType.BOOLEAN) {
				boolean bool_value = op.getValue()[location].toString() == "true";
				value[0] = !bool_value;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
			} else {
				throw new RuntimeError(
						"Invalid unary operation: operand is of type "
								+ op.getType().toString()
								+ " while the operator is a logical negation (!).");
			}
			break;
		case UMINUS:
			if (op.getType() == VariableType.INT) {
				int int_value = Integer.parseInt(op.getValue()[location]
						.toString());
				value[0] = -int_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
			} else {
				throw new RuntimeError(
						"Invalid unary operation: operand is of type "
								+ op.getType().toString()
								+ " while the operator is a unary minus (-).");
			}
			break;
		}
		return result;
	}

	public Object visit(BinaryOp binaryOp) {
		Variable first, second, result = null;
		int first_value = 0, second_value = 0, first_location, second_location;
		Object[] value = new Object[1];
		first = (Variable) binaryOp.getFirstOperand().accept(this);
		first_location = first.isArray() ? this.state.array_location : 0;
		second = (Variable) binaryOp.getSecondOperand().accept(this);
		second_location = second.isArray() ? this.state.array_location : 0;
		if (first.getType() == VariableType.INT
				&& second.getType() == VariableType.INT) {
			first_value = Integer.parseInt(first.getValue()[first_location]
					.toString());
			second_value = Integer.parseInt(second.getValue()[second_location]
					.toString());

			switch (binaryOp.getOperator()) {
			case PLUS:
				value[0] = (Integer) first_value + second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case MINUS:
				value[0] = (Integer) first_value - second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case MULTIPLY:
				value[0] = (Integer) first_value * second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case DIVIDE:
				value[0] = (Integer) first_value / second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case MOD:
				value[0] = (Integer) first_value % second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case LAND:
				value[0] = (Integer) first_value & second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case LOR:
				value[0] = (Integer) first_value | second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case LT:
				value[0] = (Boolean) (first_value < second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case LTE:
				value[0] = (Boolean) (first_value <= second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case GT:
				value[0] = (Boolean) (first_value > second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case GTE:
				value[0] = (Boolean) (first_value >= second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case EQUAL:
				value[0] = (Boolean) (first_value == second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case NEQUAL:
				value[0] = (Boolean) (first_value != second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			}
		} else if (first.getType() == VariableType.STRING
				&& second.getType() == VariableType.STRING) {
			value[0] = (String) first.getValue()[0].toString().concat(
					second.getValue()[0].toString());
			result = new Variable(VariableType.STRING, VariableLocation.NONE,
					"none", this.state.scope, false, 1, value);
		} else {
			throw new RuntimeError(
					"Invalid binary operation: first operand is of type "
							+ first.getType().toString()
							+ " while the second operand is of type "
							+ second.getType().toString() + ".");
		}
		return result;

	}

}