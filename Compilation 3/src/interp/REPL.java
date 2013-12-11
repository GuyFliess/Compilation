package interp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class REPL implements Visitor {
	State state;
	String class_name;
	String method_name;
	String[] arguments;
	private static int formal_index = 0;
	private static int scope = 0;
	private int array_location;
	Object value;
	Object[] values;

	public REPL() {
		this.state = new State();
	}

	public REPL(String class_name, String method_name, String[] arguments) {
		this.state = new State();
		this.class_name = class_name;
		this.method_name = method_name;
		this.arguments = arguments;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getMethod_name() {
		return method_name;
	}

	public void setMethod_name(String method_name) {
		this.method_name = method_name;
	}

	public static class RuntimeError extends Error {
		public RuntimeError(String msg) {
			super(msg);
		}
	}

	public Object visit(Program program) {
		for (DeclClass decl_class : program.getClasses()) {
			this.state.addClass(new interpClass(decl_class.getName()));
		}
		for (DeclClass decl_class : program.getClasses()) {
			if (decl_class.getName().equals(class_name)) {
				decl_class.accept(this);
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
			this.state.addFieldToClass(class_name, field);
		}
		for (DeclMethod decl_method : icClass.getMethods()) {
			method = new Method(decl_method.getName());
			this.state.addMethodToClass(class_name, method);
		}
		for (DeclMethod decl_method : icClass.getMethods()) {
			if (decl_method.getName().equals(method_name)) {
				decl_method.accept(this);
				break;
			}
		}
		return null;
	}

	public Object visit(DeclField field) {
		VariableType field_type = (VariableType) field.getType().accept(this);
		return new Variable(field_type, VariableLocation.FIELD,
				field.getName(), scope, field.getType().getArrayDimension());
	}

	public Object visit(DeclVirtualMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(DeclStaticMethod method) {
		// Object value = null;
		// Object[] values = null;
		for (Parameter formal : method.getFormals()) {
			formal.accept(this);
		}
		scope++;
		for (Statement stmt : method.getStatements()) {
			// if (!method.getType().getDisplayName().equals("void") &&
			// method.getType().getArrayDimension() == 0) {
			stmt.accept(this);
			// }
			// else if (!method.getType().getDisplayName().equals("void") &&
			// method.getType().getArrayDimension() > 0) {
			// values = (Object[]) stmt.accept(this);
			// }
		}
		scope--;
		if (this.value != null) {
			System.out.println(value);
		}
		else if (this.values != null) {
			for (int i = 0; i < values.length; i++) {
				System.out.println(values[i]);
			}
		}
		// if (!method.getType().getDisplayName().equals("void") &&
		// method.getType().getArrayDimension() == 0) {
		// System.out.println(value);
		// }
		// else if (!method.getType().getDisplayName().equals("void") &&
		// method.getType().getArrayDimension() > 0) {
		// for (int i = 0; i < values.length; i++) {
		// System.out.println(values[i]);
		// }
		// }
		return null;
	}

	public Object visit(DeclLibraryMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(Parameter formal) {
		VariableType variable_type = (VariableType) formal.getType().accept(
				this);
		Object[] value = new Object[1];
		value[0] = this.arguments[formal_index++];
		Variable variable = new Variable(variable_type,
				VariableLocation.PARAMETER, formal.getName(), scope, 0, value);
		this.state.addVariableToMethod(this.class_name, this.method_name,
				variable);
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
		return VariableType.CLASS;
	}

	public Object visit(StmtAssignment assignment) {
		Variable variable = (Variable) assignment.getVariable().accept(this);
		Variable value = (Variable) assignment.getAssignment().accept(this);
		if (variable.getDimension() == 0) {
			this.state.setVariableValue(class_name, method_name,
					variable.getName(), value.getValue(), 0);
		} else {
			if (value.getValue().length > 1) {
				this.state.setVariableValue(class_name, method_name,
						variable.getName(), value.getValue(), -1);
			} else {
				// int location = 0; // TODO: change
				this.state.setVariableValue(class_name, method_name,
						variable.getName(), value.getValue(), array_location);
			}
		}
		return null;
	}

	public Object visit(StmtCall callStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(StmtReturn returnStatement) {
		if (returnStatement.hasValue()) {
			Variable value = (Variable) returnStatement.getValue().accept(this);
			if (value.getDimension() == 0) {
				this.value = value.getValue()[0];
			}
			else {
				this.values = value.getValue();
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
			whileStatement.getOperation().accept(this);
			condition = (Variable) whileStatement.getCondition().accept(this);
			result = (Boolean) condition.getValue()[0];
		}
		return null;
	}

	public Object visit(StmtBreak breakStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(StmtContinue continueStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(StmtBlock statementsBlock) {
		Object value = null;
		Statement stmt;
		scope++;
		for (int i = 0; i < statementsBlock.getStatements().size(); i++) {
			stmt = statementsBlock.getStatements().get(i);
			value = stmt.accept(this);
		}
		scope--;
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
			type = VariableType.CLASS;
			break;
		}
		if (localVariable.isInitialized()) {
			Variable value = (Variable) localVariable.getInitialValue().accept(
					this);
			if (value.getDimension() > 0) {
				variable = new Variable(type, VariableLocation.LOCAL,
						localVariable.getName(), scope, value.getDimension());
				variable.setInitialized();
			} else {
				variable = new Variable(type, VariableLocation.LOCAL,
						localVariable.getName(), scope, 0, value.getValue());
			}
			// variable.setValue(value.getValue(), 0); //TODO: change location
		} else {
			variable = new Variable(type, VariableLocation.LOCAL,
					localVariable.getName(), scope, localVariable.getType()
							.getArrayDimension());
		}
		this.state.addVariableToMethod(class_name, method_name, variable);
		return null;
	}

	public Object visit(RefVariable location) {
		if (state.variableExists(class_name, method_name, location.getName(),
				scope)) {
			return state.getVariable(class_name, method_name,
					location.getName());
		}
		// TODO: throw exception if variable doesn't exist
		return null;
	}

	public Object visit(RefField location) {
		if (state.fieldExists(class_name, location.getField())) {
			return state.getVariable(class_name, method_name,
					location.getField());
		}
		// TODO: throw exception if field doesn't exist
		return null;
	}

	public Object visit(RefArrayElement location) {
		Variable variable = (Variable) location.getArray().accept(this);
		Variable array_index = (Variable) location.getIndex().accept(this);
		if (state.variableExists(class_name, method_name, variable.getName(),
				scope)) {
			this.array_location = (int) array_index.getValue()[0];
			return state.getVariable(class_name, method_name,
					variable.getName());
		}
		// TODO: throw exception if field doesn't exist
		return null;
	}

	public Object visit(StaticCall call) {

		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(VirtualCall call) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(This thisExpression) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(NewInstance newClass) {
		// TODO Auto-generated method stub
		return null;
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
		return new Variable(type, VariableLocation.NONE, "none", scope,
				Integer.parseInt(size.getValue()[0].toString()));
	}

	public Object visit(Length length) {
		// TODO Auto-generated method stub
		return null;
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
			// TODO: throw exception
			break;
		}
		Object[] value = new Object[1];
		value[0] = literal.getValue();
		return new Variable(type, VariableLocation.NONE, "none", scope, 0,
				value);
	}

	public Object visit(UnaryOp unaryOp) {
		Variable op = (Variable) unaryOp.getOperand().accept(this);
		switch (unaryOp.getOperator()) {
		case LNEG:
			boolean bool_value = op.getValue().toString() == "true";
			return !bool_value;
		case UMINUS:
			int int_value = Integer.parseInt(op.getValue().toString());
			return -int_value;
		}
		return null;
	}

	public Object visit(BinaryOp binaryOp) {
		Variable first, second, result = null;
		int first_value = 0, second_value = 0;
		Object[] value = new Object[1];
		first = (Variable) binaryOp.getFirstOperand().accept(this);
		second = (Variable) binaryOp.getSecondOperand().accept(this);
		if (first.getType() == VariableType.INT
				&& second.getType() == VariableType.INT) {
			first_value = Integer.parseInt(first.getValue()[0].toString());
			second_value = Integer.parseInt(second.getValue()[0].toString());

			switch (binaryOp.getOperator()) {
			case PLUS:
				value[0] = (Integer) first_value + second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", scope, 0, value);
				break;
			case MINUS:
				value[0] = (Integer) first_value - second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", scope, 0, value);
				break;
			case MULTIPLY:
				value[0] = (Integer) first_value * second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", scope, 0, value);
				break;
			case DIVIDE:
				value[0] = (Integer) first_value / second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", scope, 0, value);
				break;
			case MOD:
				value[0] = (Integer) first_value % second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", scope, 0, value);
				break;
			case LAND:
				value[0] = (Integer) first_value & second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", scope, 0, value);
				break;
			case LOR:
				value[0] = (Integer) first_value | second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE,
						"none", scope, 0, value);
				break;
			case LT:
				value[0] = (Boolean) (first_value < second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", scope, 0, value);
				break;
			case LTE:
				value[0] = (Boolean) (first_value <= second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", scope, 0, value);
				break;
			case GT:
				value[0] = (Boolean) (first_value > second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", scope, 0, value);
				break;
			case GTE:
				value[0] = (Boolean) (first_value >= second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", scope, 0, value);
				break;
			case EQUAL:
				value[0] = (Boolean) (first_value == second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", scope, 0, value);
				break;
			case NEQUAL:
				value[0] = (Boolean) (first_value != second_value) ? true
						: false;
				result = new Variable(VariableType.BOOLEAN,
						VariableLocation.NONE, "none", scope, 0, value);
				break;
			}
		} else if (first.getType() == VariableType.STRING
				&& second.getType() == VariableType.STRING) {
			value[0] = (String) first.getValue()[0].toString().concat(
					second.getValue()[0].toString());
			result = new Variable(VariableType.STRING, VariableLocation.NONE,
					"none", scope, 0, value);
		} else {
			// TODO: throw exception
		}
		return result;

	}

}