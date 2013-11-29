package interp;

import ast.Call;
import ast.ExprBinary.Add;
import ast.ExprBinary.Div;
import ast.ExprBinary.Eq;
import ast.ExprBinary.Mul;
import ast.ExprBinary.Pow;
import ast.ExprBinary.Sub;
import ast.ExprIf;
import ast.ExprUnary.Neg;
import ast.Func;
import ast.Node;
import ast.Num;
import ast.Program;
import ast.RefVar;
import ast.StmtAssign;


@SuppressWarnings("serial")
public class REPL implements Node.Visitor
{
	State state = new State();

	public static class RuntimeError extends Error {
		public RuntimeError(String msg) { super(msg); }
	}

	@Override
	public Object visit(Num e)
	{
		return e.value;
	}

	@Override
	public Object visit(RefVar e)
	{
		return state.lookup(e.name);
	}

	@Override
	public Object visit(Add e)
	{
		return (Double)e.a.accept(this) + (Double)e.b.accept(this);
	}

	@Override
	public Object visit(Sub e)
	{
		return (Double)e.a.accept(this) - (Double)e.b.accept(this);
	}

	@Override
	public Object visit(Mul e)
	{
		return (Double)e.a.accept(this) * (Double)e.b.accept(this);
	}

	@Override
	public Object visit(Div e)
	{
		return (Double)e.a.accept(this) / (Double)e.b.accept(this);
	}

	@Override
	public Object visit(Pow e)
	{
		return Math.pow((Double)e.a.accept(this), (Double)e.b.accept(this));
	}

	@Override
	public Object visit(Neg e)
	{
		return -(Double)e.operand.accept(this);
	}

	@Override
	public Object visit(StmtAssign s)
	{
		Double value = (Double)s.val.accept(this);
		state.a_stack.peek().values.put(s.var.name, value);
		return null;
	}

	@Override
	public Object visit(Program p)
	{
		Object value = null;
		// Execute statements
		for (Node statement : p.statements) {
			value = statement.accept(this);
			if (value != null)
				System.out.println(" = " + value);
		}
		// Print eventual state
		System.out.println(state);
		return value;
	}

	@Override
	public Object visit(Func func)
	{
		state.globals.put(func.name, func);
		return null;
	}

	@Override
	public Object visit(Call c)
	{
		Func f = state.globals.get(c.func);
		if (f == null)
			throw new RuntimeError("unknown function '" + c.func + "'");
		if (f.args.length != c.args.length)
			throw new RuntimeError("invalid number of arguments to '" + c.func + "'");
		ActivationRecord ar = new ActivationRecord();
		for (int i = 0; i < f.args.length; ++i) {
			ar.values.put(f.args[i].name, (Double)c.args[i].accept(this));
		}
		state.a_stack.push(ar);
		try {
			return f.body.accept(this);
		}
		finally { 
			state.a_stack.pop();
		}
	}
	
	@Override
	public Object visit(Eq eq)
	{
		return eq.a.accept(this).equals(eq.b.accept(this));
	}

	@Override
	public Object visit(ExprIf eif)
	{
		if ((Boolean)eif.cond.accept(this)) {
			return eif.then_e.accept(this);
		}
		else {
			return eif.else_e.accept(this);
		}
	}
}
