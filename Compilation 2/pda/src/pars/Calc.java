package pars;

import java.util.*;

import lex.Token;


public class Calc {

	public class State {
		public Stack<Tree> stack = new Stack<Tree>();
	}
	
	private State state;
	
	public Calc() {
		state = new State();
	}
	
	public Tree process(Iterable<Token> expression) {
		for (Token tok : expression)
			processToken(tok);
		return state.stack.pop();
	}
	
	void processToken(Token token) {
		switch (token.tag) {
		case "#": 
			state.stack.push(new Tree(token));
			break;
		case "◇":
			Tree rhs = state.stack.pop();
			Tree lhs = state.stack.pop();
			state.stack.push(new Tree(token,
					new Tree[] { lhs, rhs }));
			break;
		default:
			throw new Error("Parse error");
		}
	}

	public double eval(Tree t) {
		Token r = (Token)t.root;
		switch (r.tag) {
		case "#": return Double.parseDouble(r.text);
		case "◇": 
			Double a = eval(t.subtrees[0]),
			       b = eval(t.subtrees[1]);
			switch (r.text) {
			case "+": return a + b;
			case "-": return a - b;
			case "*": return a * b;
			case "/": return a / b;
			case "^": return Math.pow(a, b);
			default:
				/* should never get here */
				throw new Error("corrupted ast!");
			}
		default:
			/* nor here */
			throw new Error("corrupted ast!");
		}
	}
	
	public String infix(Tree t) {
		Token r = (Token)t.root;
		if (t.subtrees.length == 0) {
			return r.text;
		}
		else {
			String a = infix_subex(t.subtrees[0]),
			       b = infix_subex(t.subtrees[1]);
			return a + " " + r.text + " " + b;
		}
	}
	
	String infix_subex(Tree t) {
		String a = infix(t); 
		if (t.subtrees.length > 0)
			a = "(" + a + ")";
		return a;
	}
}
