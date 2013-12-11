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
			decl_class.accept(this);
		}
		return null;
	}

	public Object visit(DeclClass icClass) {
		Variable field;
		Method method;
		// interpClass ic_class = new interpClass(icClass.getName());
		for (DeclField decl_field : icClass.getFields()) {
			field = (Variable) decl_field.accept(this);
			this.state.addFieldToClass(class_name, field);// new
															// Variable((VariableType)
															// decl_field.getType().accept(this),
															// VariableLocation.FIELD,
															// decl_field.getName()));
			// field = (Variable) decl_field.accept(this);
			// ic_class.addField(field);
		}
		for (DeclMethod decl_method : icClass.getMethods()) {
			if (decl_method.getName().equals(method_name)) {
				method = new Method(method_name);
				this.state.addMethodToClass(class_name, method);
				decl_method.accept(this);
				// this.state..addMethod(method);
				break;
			}
		}
		return null;
	}

	public Object visit(DeclField field) {
		VariableType field_type = (VariableType) field.getType().accept(this);
		return new Variable(field_type, VariableLocation.FIELD, field.getName());
	}

	public Object visit(DeclVirtualMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(DeclStaticMethod method) {
		int index = 0;
		Object value = null;
		VariableType variable_type;
		Method ic_method = new Method(method.getName());
		for (Parameter formal : method.getFormals()) {
			formal.accept(this);
		}
		for (Statement stmt : method.getStatements()) {
			value = stmt.accept(this);
		}
		System.out.println(value);
		return value;
	}

	public Object visit(DeclLibraryMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(Parameter formal) {
		VariableType variable_type = (VariableType) formal.getType().accept(
				this);
		Variable variable = new Variable(variable_type,
				VariableLocation.PARAMETER, formal.getName(),
				arguments[formal_index++]);
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
		this.state.setVariableValue(class_name, method_name,
				variable.getName(), value.getValue());
		return null;
	}

	public Object visit(StmtCall callStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(StmtReturn returnStatement) {
		if (returnStatement.hasValue()) {
			Variable value = (Variable) returnStatement.getValue().accept(this);
			return value.getValue();
		}
		return null;
	}

	public Object visit(StmtIf ifStatement) {
		if ((boolean) ifStatement.getCondition().accept(this)) {
			return ifStatement.getOperation().accept(this);
		}
		if (ifStatement.hasElse()) {
			if ((boolean) ifStatement.getElseOperation().accept(this)) {
				return ifStatement.getElseOperation().accept(this);
			}
		}
		return null;
	}

	public Object visit(StmtWhile whileStatement) {
		Variable condition = (Variable) whileStatement.getCondition().accept(this);
		Boolean result = (Boolean) condition.getValue();
		while (result) {
			whileStatement.getOperation().accept(this);
			condition = (Variable) whileStatement.getCondition().accept(this);
			result = (Boolean) condition.getValue();
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
		for (int i = 0; i < statementsBlock.getStatements().size(); i++) {
			stmt = statementsBlock.getStatements().get(i);
			value = stmt.accept(this);
		}
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
			Variable value = (Variable) localVariable.getInitialValue().accept(this);
			variable = new Variable(type, VariableLocation.LOCAL, localVariable.getName(), value.getValue());
		}
		else {
			variable = new Variable(type, VariableLocation.LOCAL, localVariable.getName());
		}
		this.state.addVariableToMethod(class_name, method_name, variable);
		return null;
	}

	public Object visit(RefVariable location) {
		if (state.variableExists(class_name, method_name, location.getName())) {
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
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
			//TODO: throw exception
			break;
		}
		return new Variable(type, VariableLocation.NONE, "none", literal.getValue());
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
		Object value = null;
		first = (Variable) binaryOp.getFirstOperand().accept(this);
		second = (Variable) binaryOp.getSecondOperand().accept(this);
		if (first.getType() == VariableType.INT && second.getType() == VariableType.INT) {
			first_value = Integer.parseInt(first.getValue().toString());
			second_value = Integer.parseInt(second.getValue().toString());

			switch (binaryOp.getOperator()) {
			case PLUS:
				value = (Integer) first_value + second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE, "none", value);
				break;
			case MINUS:
				value = (Integer) first_value - second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE, "none", value);
				break;
			case MULTIPLY:
				value = (Integer) first_value * second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE, "none", value);
				break;
			case DIVIDE:
				value = (Integer) first_value / second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE, "none", value);
				break;
			case MOD:
				value = (Integer) first_value % second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE, "none", value);
				break;
			case LAND:
				value = (Integer) first_value & second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE, "none", value);
				break;
			case LOR:
				value = (Integer) first_value | second_value;
				result = new Variable(VariableType.INT, VariableLocation.NONE, "none", value);
				break;
			case LT:
				value = (Boolean) (first_value < second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE, "none", value);
				break;
			case LTE:
				value = (Boolean) (first_value <= second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE, "none", value);
				break;
			case GT:
				value = (Boolean) (first_value > second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE, "none", value);
				break;
			case GTE:
				value = (Boolean) (first_value >= second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE, "none", value);
				break;
			case EQUAL:
				value = (Boolean) (first_value == second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE, "none", value);
				break;
			case NEQUAL:
				value = (Boolean) (first_value != second_value) ? true : false;
				result = new Variable(VariableType.BOOLEAN, VariableLocation.NONE, "none", value);
				break;
			}
		}
		else if (first.getType() == VariableType.STRING && second.getType() == VariableType.STRING) {
			value = (String) first.getValue().toString().concat(second.getValue().toString());
			result = new Variable(VariableType.STRING, VariableLocation.NONE, "none", value);
		}
		else {
			// TODO: throw exception
		}
		return result;

	}

}
