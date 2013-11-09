

import java.util.LinkedList;
import java.util.List;

import lex.Lexer;
import lex.Token;

import pars.Calc;
import pars.Tree;


public class Main
{

	public static void main(String[] args)
	{
		Lexer lex = new Lexer();
		Calc calc = new Calc();
		List<Token> tokens = new LinkedList<Token>();
		try {
			lex.process(args[0], tokens);
			Tree ast = calc.process(tokens);
			System.out.println(ast /* +
					"\n" + calc.infix(ast) +
					"\n    = " + calc.eval(ast) */);
		}
		catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}
}
