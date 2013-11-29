//package interp;
//
//import interp.REPL.RuntimeError;
//
//import java.util.HashMap;
//import java.util.ListIterator;
//import java.util.Map;
//import java.util.Stack;
//
//import ast.Func;
//
//
//public class State
//{
//	Stack<ActivationRecord> a_stack = new Stack<>();
//	Map<String, Func> globals = new HashMap<>();
//	
//	State() { a_stack.push(new ActivationRecord()); }
//	
//	/**
//	 * Find a variable by its name (dynamic lookup).
//	 */
//	Double lookup(String byName)
//	{
//		ListIterator<ActivationRecord> iter =
//				a_stack.listIterator(a_stack.size());
//		while (iter.hasPrevious()) {
//			ActivationRecord ar = iter.previous();
//			if (ar.values.containsKey(byName))
//				return ar.values.get(byName);
//		}
//		throw new RuntimeError("undefined variable '" + byName + "'");
//	}
//	
//	@Override
//	public String toString()
//	{
//		return a_stack.peek().toString();
//	}
//}
//
