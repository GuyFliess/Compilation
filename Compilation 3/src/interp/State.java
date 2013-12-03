package interp;

import ic.ast.decl.Parameter;
import ic.ast.stmt.LocalVariable;
import interp.REPL.RuntimeError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;

public class State
{
	Stack<ActivationRecord> a_stack = new Stack<>();
//	Map<String, Func> globals = new HashMap<>();
	Map<String, LocalVariable> variables = new HashMap<>();
	Map<String, Parameter> parameters = new HashMap<>();
	
	State() { a_stack.push(new ActivationRecord()); }
	
	/**
	 * Find a variable by its name (dynamic lookup).
	 */
	Double lookup(String byName)
	{
		ListIterator<ActivationRecord> iter =
				a_stack.listIterator(a_stack.size());
		while (iter.hasPrevious()) {
			ActivationRecord ar = iter.previous();
			if (ar.values.containsKey(byName))
				return ar.values.get(byName);
		}
		throw new RuntimeError("undefined variable '" + byName + "'");
	}
	
	@Override
	public String toString()
	{
		return a_stack.peek().toString();
	}
}

