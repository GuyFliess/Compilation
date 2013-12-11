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
		interpClass ic_class;
		for (DeclClass decl_class : program.getClasses()) {
			ic_class = (interpClass) decl_class.accept(this);
			this.state.addClass(ic_class);
		}
		return null;
	}

	public Object visit(DeclClass icClass) {
		Variable field;
		Method method;
		interpClass ic_class = new interpClass(icClass.getName());
		for (DeclField decl_field : icClass.getFields()) {
			field = (Variable) decl_field.accept(this);
			ic_class.addField(field);
		}
		for (DeclMethod decl_method : icClass.getMethods()) {
			if (decl_method.getName().equals(method_name)) {
				method = (Method) decl_method.accept(this);
				ic_class.addMethod(method);
				break;
			}
		}
		return ic_class;
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
			// variable_type = (VariableType) formal.getType().accept(this);
			// ic_method.addVariable(variable_type, VariableLocation.PARAMETER,
			// formal.getName());
			// ic_method.setVariableValue(formal.getName(), arguments[index++]);
		}
		for (Statement stmt : method.getStatements()) {
			value = stmt.accept(this);
		}
		System.out.println(value);
		return value;
		//
		// List<Statement> statements = method.getStatements();
		// Statement stmt;
		// List<Parameter> formals = method.getFormals();
		// Object value = null;
		// for (int i = 0; i < formals.size(); i++) {
		// formals.get(i).accept(this);
		// }
		// for (int i = 0; i < statements.size(); i++) {
		// stmt = statements.get(i);
		// value = stmt.accept(this);
		// }
		// System.out.println(value);
		// return value;
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
		// formal_index++;
		this.state.addVariableToMethod(this.class_name, this.method_name,
				variable);
		// ic_method.setVariableValue(formal.getName(), arguments[index++]);
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
		Object value = assignment.getAssignment().accept(this);
		this.state.setVariableValue(class_name, method_name,
				variable.getName(), value);
		return null;
	}

	public Object visit(StmtCall callStatement) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(StmtReturn returnStatement) {
		if (returnStatement.hasValue()) {
			return returnStatement.getValue().accept(this);
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
		while ((boolean) whileStatement.getCondition().accept(this)) {
			whileStatement.getOperation().accept(this);
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
//		if (localVariable.getType().getDisplayName().equals("int")) {
//			// Object val = localVariable.getInitialValue().accept(
//			// this);
//			this.state.int_variables.put(localVariable.getName(),
//					(Integer) localVariable.getInitialValue().accept(this));
//		} else if (localVariable.getType().getDisplayName().equals("boolean")) {
//			this.state.bool_variables.put(localVariable.getName(),
//					((String) localVariable.getInitialValue().accept(this))
//							.equals("true") ? true : false);
//		} else if (localVariable.getType().getDisplayName().equals("string")) {
//			this.state.string_variables.put(localVariable.getName(),
//					(String) localVariable.getInitialValue().accept(this));
//		}
		return null;
	}

	public Object visit(RefVariable location) {
//		if (arguments.containsKey(location.getName())
//				|| state.bool_variables.containsKey(location.getName())
//				|| state.int_variables.containsKey(location.getName())
//				|| state.string_variables.containsKey(location.getName())) {
//			return location;
//		}
		return null;
		// return state.variables.get(location.getName()).getInitialValue()
		// .accept(this);
	}

	public Object visit(RefField location) {
		// TODO Auto-generated method stub
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
		return literal;
	}

	public Object visit(UnaryOp unaryOp) {
		switch (unaryOp.getOperator()) {
		case LNEG:
			boolean bool_value = (String) unaryOp.getOperand().accept(this) == "true";
			return !bool_value;
		case UMINUS:
			int int_value = Integer.parseInt((String) unaryOp.getOperand()
					.accept(this));
			return -int_value;
		}
		return null;
	}

	public Object visit(BinaryOp binaryOp) {
		Object first, second;
		first = binaryOp.accept(this);
		second = binaryOp.accept(this);
//		if (!first.getClass().toString().equals("class ic.ast.expr.Literal") &&
//				!second.getClass().toString().equals("class ic.ast.expr.Literal")) {
//			
//		}
		
		
		int first_value = 0, second_value = 0;
		Variable first_var = (Variable) first;
		Variable second_var = (Variable) second;
		first_value = Integer.parseInt(this.state.getVariableValue(class_name, method_name, first_var));
		second_value = Integer.parseInt(this.state.getVariableValue(class_name, method_name, second_var));
		
//		Object first, second;
//		RefVariable first_var = null, second_var = null;
//		Literal first_literal = null, second_literal = null;
//		first = binaryOp.getFirstOperand().accept(this);
//		String class_s = first.getClass().toString();
//		if (first.getClass().toString().equals("class ic.ast.expr.RefVariable")) {
//			first_var = (RefVariable) first;
//			if (arguments.containsKey(first_var.getName())) {
//				first_value = (int) Integer.parseInt(arguments.get(first_var
//						.getName()));
//			} else if (state.int_variables.containsKey(first_var.getName())) {
//				first_value = state.int_variables.get(first_var.getName());
//			}
//		} else if (first.getClass().toString()
//				.equals("class ic.ast.expr.Literal")) {
//			first_literal = (Literal) first;
//			first_value = (int) first_literal.getValue();
//		}
//
//		second = binaryOp.getSecondOperand().accept(this);
//		if (second.getClass().toString()
//				.equals("class ic.ast.expr.RefVariable")) {
//			second_var = (RefVariable) second;
//			if (arguments.containsKey(second_var.getName())) {
//				second_value = (int) Integer.parseInt(arguments.get(second_var
//						.getName()));
//			} else if (state.int_variables.containsKey(second_var.getName())) {
//				second_value = state.int_variables.get(second_var.getName());
//			}
//		} else if (second.getClass().toString()
//				.equals("class ic.ast.expr.Literal")) {
//			second_literal = (Literal) second;
//			second_value = (Integer) Integer.parseInt((String) second_literal
//					.getValue());
//		}

		switch (binaryOp.getOperator()) {
		case PLUS:
			return first_value + second_value;
		case MINUS:
			return first_value - second_value;
		case MULTIPLY:
			return first_value * second_value;
		case DIVIDE:
			return first_value / second_value;
		case MOD:
			return first_value % second_value;
		case LAND:
			return first_value & second_value;
		case LOR:
			return first_value | second_value;
		case LT:
			return first_value < second_value;
		case LTE:
			return first_value <= second_value;
		case GT:
			return first_value > second_value;
		case GTE:
			return first_value >= second_value;
		case EQUAL:
			return first_value == second_value;
		case NEQUAL:
			return first_value != second_value;
		}
		return null;

	}

	// Map<String, String> arguments = new HashMap<>();
	//
	// public void AddArgument(String name, String value) {
	// arguments.put(name, value);
	// }

	// @Override
	// public Object visit(Num e)
	// {
	// return e.value;
	// }
	//
	// @Override
	// public Object visit(RefVar e)
	// {
	// return state.lookup(e.name);
	// }
	//
	// @Override
	// public Object visit(Add e)
	// {
	// return (Double)e.a.accept(this) + (Double)e.b.accept(this);
	// }
	//
	// @Override
	// public Object visit(Sub e)
	// {
	// return (Double)e.a.accept(this) - (Double)e.b.accept(this);
	// }
	//
	// @Override
	// public Object visit(Mul e)
	// {
	// return (Double)e.a.accept(this) * (Double)e.b.accept(this);
	// }
	//
	// @Override
	// public Object visit(Div e)
	// {
	// return (Double)e.a.accept(this) / (Double)e.b.accept(this);
	// }
	//
	// @Override
	// public Object visit(Pow e)
	// {
	// return Math.pow((Double)e.a.accept(this), (Double)e.b.accept(this));
	// }
	//
	// @Override
	// public Object visit(Neg e)
	// {
	// return -(Double)e.operand.accept(this);
	// }
	//
	// @Override
	// public Object visit(StmtAssign s)
	// {
	// Double value = (Double)s.val.accept(this);
	// state.a_stack.peek().values.put(s.var.name, value);
	// return null;
	// }
	//
	// @Override
	// public Object visit(Program p)
	// {
	// Object value = null;
	// // Execute statements
	// for (Node statement : p.statements) {
	// value = statement.accept(this);
	// if (value != null)
	// System.out.println(" = " + value);
	// }
	// // Print eventual state
	// System.out.println(state);
	// return value;
	// }
	//
	// @Override
	// public Object visit(Func func)
	// {
	// state.globals.put(func.name, func);
	// return null;
	// }
	//
	// @Override
	// public Object visit(Call c)
	// {
	// Func f = state.globals.get(c.func);
	// if (f == null)
	// throw new RuntimeError("unknown function '" + c.func + "'");
	// if (f.args.length != c.args.length)
	// throw new RuntimeError("invalid number of arguments to '" + c.func +
	// "'");
	// ActivationRecord ar = new ActivationRecord();
	// for (int i = 0; i < f.args.length; ++i) {
	// ar.values.put(f.args[i].name, (Double)c.args[i].accept(this));
	// }
	// state.a_stack.push(ar);
	// try {
	// return f.body.accept(this);
	// }
	// finally {
	// state.a_stack.pop();
	// }
	// }
	//
	// @Override
	// public Object visit(Eq eq)
	// {
	// return eq.a.accept(this).equals(eq.b.accept(this));
	// }
	//
	// @Override
	// public Object visit(ExprIf eif)
	// {
	// if ((Boolean)eif.cond.accept(this)) {
	// return eif.then_e.accept(this);
	// }
	// else {
	// return eif.else_e.accept(this);
	// }
	// }

}
