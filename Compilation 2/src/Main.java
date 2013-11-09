

import java.util.LinkedList;
import java.util.List;

import ast.Expr;

import lex.Lexer;
import lex.Token;

import pars.Calc;


public class Main
{

	public static void main(String[] args)
	{
		Lexer lex = new Lexer();
		Calc calc = new Calc();
		List<Token> tokens = new LinkedList<Token>();
		try {
			lex.process(args[0], tokens);
			Expr ast = calc.process(tokens);
			System.out.println(ast +
					"\n" + calc.infix(ast) +
					"\n    = " + ast.eval());
		}
		catch (Throwable e) {
			for (Token tok : tokens)  System.out.println(tok);
			System.err.println(e);
			System.exit(1);
		}
	}
}
