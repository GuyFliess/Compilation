import java.util.LinkedList;
import java.util.List;

import lex.Lexer;
import lex.Token;
import pars.Calc;
import pars.LibCalc;
import ic.ast.Node;
import ic.ast.PrettyPrint;

public class Main {
	public static void main(String[] args) {
		// Node ast = new Program(
		// new StmtAssign(new RefVar("w"), new Num(1)),
		// new Func("f", new RefVar[] { new RefVar("x") },
		// new Mul(new RefVar("x"), new RefVar("x"))),
		// fib(),
		// new Call("fib", new Expr[] { new Num(6) })
		// );
		// System.out.println(ast);
		Lexer lex = new Lexer();
		Calc calc = new Calc();
		PrettyPrint printer = new PrettyPrint();
		List<Token> programTokens = new LinkedList<Token>();
		List<Token> tokensForLib = new LinkedList<Token>();
		Node libAst = null;
		try {
			lex.process(args[0], programTokens); // Process regular input
			if (args.length > 1 && args[1].startsWith("-L")) { // Handle Lib
				lex.process(args[1].substring(2), tokensForLib);
				LibCalc calcLib = new LibCalc();
				libAst = calcLib.process(tokensForLib);
				System.out.println(libAst.accept(printer));
			}
			Node programAst = calc.process(programTokens); // process program
			System.out.println(programAst.accept(printer));
			int interpStartLocation = -1;
			if (args.length > 1) {
				interpStartLocation = 1;

				if (args[1].startsWith("-L")) {
					interpStartLocation = 2;
				}
			}
			if (args.length >= interpStartLocation) // this means we have args
													// to interp, so do interp
													// flow
			{
				// TODO CAll Interp with
				System.out.println(String.format("method name %s",
						args[interpStartLocation])); // just an exmaple to see
														// how to get the args
				System.out.println("args are:");
				for (int i = interpStartLocation + 1; i < args.length; i++) {
					System.out.println(args[i]);
				}
			}
			else // SymbolTable and typecheck
			{
				System.out.println("Building symbol table");
				
				System.out.println("type checking");
			}

		} catch (Throwable e) {
			System.err.println(e);
			return;
		}

		// REPL interp = new REPL();
		// try {
		// ast.accept(interp);
		// }
		// catch (RuntimeError e) {
		// System.err.println("Run-time error: " + e.getMessage());
		// }
	}

	// static Node fib()
	// {
	// return
	// new Func("fib", new RefVar[] { new RefVar("i") },
	// new ExprIf(new Eq(new RefVar("i"), new Num(0)),
	// new Num(1),
	// new ExprIf(new Eq(new RefVar("i"), new Num(1)),
	// new Num(1),
	// new Add(new Call("fib", new Expr[] {
	// new Sub(new RefVar("i"), new Num(1)) }),
	// new Call("fib", new Expr[] {
	// new Sub(new RefVar("i"),
	// new Num(2)) })))));
	// }
}