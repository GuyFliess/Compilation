import ic.ast.Node;
import ic.ast.PrettyPrint;
import java.util.LinkedList;
import java.util.List;
import lex.Lexer;
import lex.Token;
import pars.Calc;


public class Main
{

	public static void main(String[] args)
	{
		Lexer lex = new Lexer();
		Calc calc = new Calc();
		PrettyPrint printer = new PrettyPrint();
		List<Token> tokens = new LinkedList<Token>();
		try {
			lex.process(args[0], tokens);
			Node ast = calc.process(tokens);
			System.out.println(ast.accept(printer));
			
		}
		catch (Throwable e) {
			for (Token tok : tokens)  System.out.println(tok);
			System.err.println(e);
			System.exit(1);
		}
	}
}
