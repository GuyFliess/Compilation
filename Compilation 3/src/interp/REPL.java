package interp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ic.ast.Visitor;
import ast.ExprBinary.Add;
import ast.ExprBinary.Div;
import ast.ExprBinary.Mul;
import ast.ExprBinary.Pow;
import ast.ExprBinary.Sub;
import ast.ExprUnary.Neg;
import ast.Num;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclLibraryMethod;
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

@SuppressWarnings("serial")
public class REPL implements Visitor {
	State state = new State();
	Map<String, String> arguments = new HashMap<>();
	Map<String, Integer> int_variables = new HashMap<>();
	Map<String, Boolean> bool_variables = new HashMap<>();
	Map<String, String> string_variables = new HashMap<>();
	
	public void AddArgument(String name, String value) {
		arguments.put(name, value);
	}

	public static class RuntimeError extends Error {
		public RuntimeError(String msg) {
			super(msg);
		}
	}

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

	public Object visit(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(DeclClass icClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(DeclField field) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(DeclVirtualMethod method) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(DeclStaticMethod method) {
		List<Statement> statements = method.getStatements();
		Statement stmt;
		List<Parameter> formals = method.getFormals();
		Object value = null;
		for (int i = 0; i < formals.size(); i++) {
			formals.get(i).accept(this);
		}
		for (int i = 0; i < statements.size(); i++) {
			stmt = statements.get(i);
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
		this.state.parameters.put(formal.getName(), formal);
		return null;
	}

	public Object visit(PrimitiveType type) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(ClassType type) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visit(StmtAssignment assignment) {
		Object variable = assignment.getVariable().accept(this);
//		state.a_stack.p.peek().values.put(s.var.name, value);
//		if (Local)
//		LocalVariable value = (LocalVariable) assignment.getAssignment().accept(this);
//		state.variables.put(variable.toString(), (LocalVariable) value);
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
		// TODO Auto-generated method stub
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
		this.state.variables.put(localVariable.getName(), localVariable);
		return null;
	}

	public Object visit(RefVariable location) {
		if (arguments.containsKey(location.getName())) {
			return arguments.get(location.getName());
		}
		return state.variables.get(location.getName()).getInitialValue()
				.accept(this);
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
		return literal.getValue();
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
		int first_value = Integer.parseInt((String) binaryOp.getFirstOperand()
				.accept(this));
		int second_value = Integer.parseInt((String) binaryOp
				.getSecondOperand().accept(this));
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

}
