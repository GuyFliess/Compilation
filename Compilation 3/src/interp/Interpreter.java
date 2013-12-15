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
import ic.ast.decl.Type;
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
		try {
			checkClassExists();
		} catch (RuntimeError e) {
			System.err.println(e.getMessage());
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
		try {
			checkMethodExists();
		} catch (RuntimeError e) {
			System.err.println(e.getMessage());
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
		this.state.setReturnType((VariableType) method.getType().accept(this));
		this.state
				.setReturnTypeDimensions(method.getType().getArrayDimension());
		checkNumberOfParameters(method, method.getLine());
		for (Parameter formal : method.getFormals()) {
			formal.accept(this);
		}
		this.state.scope++;
		for (Statement stmt : method.getStatements()) {
			try {
				stmt.accept(this);
			} catch (Break e) {
				throw new RuntimeError("Invalid use of break statement.");
			} catch (Continue e) {
				throw new RuntimeError("invalid use of continue statement.");
			}
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
		throw new RuntimeError("Interpreter doesn't interpret library methods.");
	}

	public Object visit(Parameter formal) {
		VariableType variable_type = (VariableType) formal.getType().accept(
				this);
		Object value;
		value = this.state.arguments[this.state.formal_index++];
		checkParameterType(formal, variable_type, value.toString(), formal.getLine());
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
		throw new RuntimeError("Interpreter doesn't interpret class types.");
	}

	public Object visit(StmtAssignment assignment) {
		Variable value = (Variable) assignment.getAssignment().accept(this);
		Variable variable = (Variable) assignment.getVariable().accept(this);
		checkTypesAreEqual(value, variable, assignment.getLine());
		checkValueIsInitialized(value, assignment.getAssignment().getLine());
		if (variable.isArray() && !variable.isInitialized()) {
			checkNewArrayInitialization(variable, assignment.getVariable().getLine());
			this.state.getClass(this.state.class_name).getMethods()
					.get(this.state.method_name)
					.initializeArrayVariable(variable, (int) value.getValue());
			this.state.new_array = false;
		} else {
			this.state.setVariableValue(this.state.class_name,
					this.state.method_name, variable, value.getValue());
		}
		return null;
	}

	public Object visit(StmtCall callStatement) {
		throw new RuntimeError("Interpreter doesn't interpret call statements.");
	}

	public Object visit(StmtReturn returnStatement) {
		if (returnStatement.hasValue()) {
			Variable value = (Variable) returnStatement.getValue().accept(this);
			checkReturnType(value, returnStatement.getLine());
			if (!value.isArray()) {
				this.state.value = value.getValue();
			} else {
				this.state.values = new Object[value.getLength()];
				for (int i = 0; i < value.getLength(); i++) {
					this.state.values[i] = value.getVariables()[i].getValue();
				}
			}
		}
		return null;
	}

	public Object visit(StmtIf ifStatement) {
		Variable condition = (Variable) ifStatement.getCondition().accept(this);
		checkConditionType(condition, ifStatement.getCondition().getLine());
		Boolean result = (Boolean) condition.getValue();
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
		checkConditionType(condition, whileStatement.getCondition().getLine());
		Boolean result = (Boolean) condition.getValue();
		while (result) {
			try {
				whileStatement.getOperation().accept(this);
			} catch (Break e) {
				break;
			} catch (Continue e) {
				condition = (Variable) whileStatement.getCondition().accept(
						this);
				checkConditionType(condition, whileStatement.getCondition().getLine());
				result = (Boolean) condition.getValue();
				continue;
			}
			condition = (Variable) whileStatement.getCondition().accept(this);
			checkConditionType(condition, whileStatement.getCondition().getLine());
			result = (Boolean) condition.getValue();
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
		this.state.scope++;
		for (Statement stmt : statementsBlock.getStatements()) {
			stmt.accept(this);
		}
		this.state.deleteVariableFromScope();
		this.state.scope--;
		return null;
	}

	public Object visit(LocalVariable localVariable) {
		Variable variable = null;
		VariableType type = getType(localVariable.getType());
		variable = new Variable(type, VariableLocation.LOCAL,
				localVariable.getName(), this.state.scope, localVariable
						.getType().getArrayDimension() > 0, 1);
		if (localVariable.isInitialized()) {
			Variable value = (Variable) localVariable.getInitialValue().accept(
					this);
			checkValueIsInitialized(value, localVariable.getLine());
			if (variable.isArray()) {
				checkNewArrayInitialization(variable, localVariable.getLine());
				variable.initializeArray((int) value.getValue());
				this.state.new_array = false;
			} else {
				variable.setValue(value.getValue());
			}
		}
		this.state.addVariableToMethod(this.state.class_name,
				this.state.method_name, variable);

		return null;
	}

	public Object visit(RefVariable location) {
		checkThatVariableIsNotAField(location, location.getLine());
		checkVariableExists(location.getName(), location.getLine());
		return state.getVariable(this.state.class_name, this.state.method_name,
				location.getName());
	}

	public Object visit(RefField location) {
		throw new RuntimeError("Interpreter doesn't interpret 'this'.");
	}

	public Object visit(RefArrayElement location) {
		Variable variable = (Variable) location.getArray().accept(this);
		Variable array_index = (Variable) location.getIndex().accept(this);
		int index = (int) array_index.getValue();
		checkVariableExists(variable.getName(), location.getLine());
		checkVariableIsArray(variable, location.getLine());
		checkArrayVariableInitialized(variable, location.getLine());
		checkIndex(variable, index, location.getLine());
		return this.state.getVariable(this.state.class_name,
				this.state.method_name, variable.getName()).getVariables()[index];
	}

	public Object visit(StaticCall call) {
		throw new RuntimeError("Interpreter doesn't interpret static calls.");
	}

	public Object visit(VirtualCall call) {
		throw new RuntimeError("Interpreter doesn't interpret virtual calls.");
	}

	public Object visit(This thisExpression) {
		throw new RuntimeError("Interpreter doesn't interpret 'this'.");
	}

	public Object visit(NewInstance newClass) {
		throw new RuntimeError(
				"Interpreter doesn't interpret new class instances.");
	}

	public Object visit(NewArray newArray) {
		this.state.new_array = true;
		Variable size = (Variable) newArray.getSize().accept(this);
		return size;
	}

	public Object visit(Length length) {
		Variable variable = (Variable) length.getArray().accept(this);
		checkValidLengthStmt(variable, length.getLine());
		return new Variable(VariableType.INT, VariableLocation.NONE, "none",
				this.state.scope, false, 1, variable.getLength());
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
		return new Variable(type, VariableLocation.NONE, "none",
				this.state.scope, false, 1, literal.getValue());
	}

	public Object visit(UnaryOp unaryOp) {
		Variable result = null;
		Variable op = (Variable) unaryOp.getOperand().accept(this);
		switch (unaryOp.getOperator()) {
		case LNEG:
			if (op.getType() == VariableType.BOOLEAN) {
				boolean bool_value = op.getValue().toString() == "true";
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, !bool_value);
			} else {
				throw new RuntimeError(
						"Invalid unary operation: operand is of type "
								+ op.getType().toString()
								+ " while the operator is a logical negation (!).");
			}
			break;
		case UMINUS:
			if (op.getType() == VariableType.INT) {
				int int_value = Integer.parseInt(op.getValue().toString());
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, -int_value);
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
		int first_value = 0, second_value = 0;
		Object value;
		first = (Variable) binaryOp.getFirstOperand().accept(this);
		second = (Variable) binaryOp.getSecondOperand().accept(this);
		if (first.getType() == VariableType.INT
				&& second.getType() == VariableType.INT) {
			first_value = Integer.parseInt(first.getValue().toString());
			second_value = Integer.parseInt(second.getValue().toString());
			switch (binaryOp.getOperator()) {
			case PLUS:
				value = (Integer) first_value + second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case MINUS:
				value = (Integer) first_value - second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case MULTIPLY:
				value = (Integer) first_value * second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case DIVIDE:
				if (second_value == 0) {
					throw new RuntimeError(
							"Invalid binary operation: division by 0.");
				}
				value = (Integer) first_value / second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case MOD:
				value = (Integer) first_value % second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case LAND:
				value = (Integer) first_value & second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case LOR:
				value = (Integer) first_value | second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", this.state.scope, false, 1, value);
				break;
			case LT:
				value = (Boolean) (first_value < second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case LTE:
				value = (Boolean) (first_value <= second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case GT:
				value = (Boolean) (first_value > second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case GTE:
				value = (Boolean) (first_value >= second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case EQUAL:
				value = (Boolean) (first_value == second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case NEQUAL:
				value = (Boolean) (first_value != second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			}
		} else if (first.getType() == VariableType.STRING
				&& second.getType() == VariableType.STRING) {
			switch (binaryOp.getOperator()) {
			case PLUS:
				value = (String) first.getValue().toString()
						.concat(second.getValue().toString());
				result = new Variable(VariableType.STRING,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			case EQUAL:
				value = (Boolean) first.getValue().toString()
						.equals(second.getValue().toString());
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", this.state.scope, false,
						1, value);
				break;
			default:
				throw new RuntimeError(
						"Invalid binary operation: both operators are of type string.");
			}
		} else {
			throw new RuntimeError(
					"Invalid binary operation: first operand is of type "
							+ first.getType().toString()
							+ " while the second operand is of type "
							+ second.getType().toString() + ".");
		}
		return result;
	}

	private VariableType getType(Type variable_type) throws RuntimeError {
		VariableType type;
		switch (variable_type.getDisplayName()) {
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
			throw new RuntimeError("Invalid type: variables cannot be of type "
					+ variable_type.getDisplayName() + ".");
		}
		checkVariableDimensions(variable_type, variable_type.getLine());
		return type;
	}

	/* Checkers */

	private void checkClassExists() throws RuntimeError {
		if (!this.state.checkClassExists()) {
			throw new RuntimeError("Invalid class name: class "
					+ this.state.class_name + " doesn't exist.");
		}
	}

	private void checkMethodExists() throws RuntimeError {
		if (!this.state.checkMethodExists()) {
			throw new RuntimeError("Invalid method name: method "
					+ this.state.method_name + " doesn't exist in class "
					+ this.state.class_name + ".");
		}
	}

	private void checkNewArrayInitialization(Variable variable, int line)
			throws RuntimeError {
		if (!this.state.new_array) {
			throw new RuntimeError(
					"Invalid array initialization at line " + line + ": array variable "
							+ variable.getName()
							+ " is not initialized to a new array.");
		}
	}

	private void checkValueIsInitialized(Variable value, int line) throws RuntimeError {
		if (!value.isInitialized()) {
			throw new RuntimeError(
					"Invalid assignment statement at line " + line + ": the value is not initialized.");
		}
	}

	private void checkTypesAreEqual(Variable value, Variable variable, int line)
			throws RuntimeError {
		if (value.getType() != variable.getType()) {
			throw new RuntimeError(
					"Invalid assignment statement at line " + line + ": the variable is of type "
							+ variable.getType().toString()
							+ " while assignment is of type "
							+ value.getType().toString() + ".");
		}
	}

	private void checkNumberOfParameters(DeclStaticMethod method, int line)
			throws RuntimeError {
		if (method.getFormals().size() != state.arguments.length) {
			throw new RuntimeError(
					"Invalid input at line " + line + ": number of parameters for method "
							+ state.class_name + "." + state.method_name
							+ " is " + method.getFormals().size()
							+ " while the number of parameters given is "
							+ state.arguments.length + ".");
		}
	}

	private void checkParameterType(Parameter formal,
			VariableType variable_type, String value, int line) throws RuntimeError {
		if (variable_type != VariableType.INT
				&& variable_type != VariableType.STRING) {
			throw new RuntimeError(
					"Invalid parameter at line " + line + ": parameters cannot be of type "
							+ variable_type.toString() + ".");
		}
		if (formal.getType().getArrayDimension() > 0) {
			throw new RuntimeError(
					"Invalid parameter at line " + line + ": parameters cannot be arrays.");
		}
		if (variable_type == VariableType.INT) {
			try {
				Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new RuntimeError("Invalid input parameter at line " + line + ": \"" + value
						+ "\" cannot be an int.");
			}
		}
	}

	private void checkReturnType(Variable value, int line) throws RuntimeError {
		if (this.state.getReturnType() != value.getType()) {
			throw new RuntimeError(
					"Invalid return statement at line " + line + ": the value returned is of type "
							+ value.getType().toString()
							+ " while the returned type of the function "
							+ this.state.class_name + "."
							+ this.state.method_name + " is "
							+ this.state.getReturnType() + ".");
		}
		if (this.state.getReturnTypeDimensions() != (value.isArray() ? 1 : 0)) {
			throw new RuntimeError(
					"Invalid return statement at line " + line + ": the value returned is "
							+ (value.isArray() ? "" : "not")
							+ " an array while the returned type of the function is "
							+ (this.state.getReturnTypeDimensions() > 0 ? ""
									: "not ") + "an array.");
		}
	}

	private void checkConditionType(Variable condition, int line) throws RuntimeError {
		if (condition.getType() != VariableType.BOOLEAN) {
			throw new RuntimeError("Invalid condition at line " + line + ": not a boolean.");
		}
	}

	private void checkVariableDimensions(Type variable_type, int line)
			throws RuntimeError {
		if (variable_type.getArrayDimension() > 1) {
			throw new RuntimeError(
					"Invalid variable at line " + line + ": array dimensions cannot be "
							+ variable_type.getArrayDimension() + ".");
		}
	}

	private void checkThatVariableIsNotAField(RefVariable location, int line)
			throws RuntimeError {
		if (state.fieldExists(state.class_name, location.getName())) {
			throw new RuntimeError("Intepreter doesn't interpret fields, at line " + line + ".");
		}
	}

	private void checkVariableExists(String variable_name, int line) throws RuntimeError {
		if (!state.variableExists(this.state.class_name,
				this.state.method_name, variable_name, this.state.scope)) {
			throw new RuntimeError("Variable " + variable_name
					+ " does not exist in the current scope, at line " + line + ".");

		}
	}

	private void checkIndex(Variable variable, int index, int line) throws RuntimeError {
		if (index > variable.getLength() - 1) {
			throw new RuntimeError("Invalid array reference at line " + line + ": variable "
					+ variable.getName() + "[] is of length "
					+ variable.getLength() + " while trying to access index "
					+ index + ".");
		}
	}

	private void checkArrayVariableInitialized(Variable variable, int line)
			throws RuntimeError {
		if (!variable.isInitialized()) {
			throw new RuntimeError("Variable " + variable.getName()
					+ "[] is not initialized, at line " + line + ".");
		}
	}

	private void checkVariableIsArray(Variable variable, int line) throws RuntimeError {
		if (!variable.isArray()) {
			throw new RuntimeError("Variable " + variable.getName()
					+ " is not an array, at line " + line + ".");
		}
	}

	private void checkValidLengthStmt(Variable variable, int line) throws RuntimeError {
		if (!variable.isArray()) {
			throw new RuntimeError(
					"Invalid call to length at line " + line + ": variable is not an array.");
		}
	}

}