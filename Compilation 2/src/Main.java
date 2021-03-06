import ic.ast.Node;
import ic.ast.PrettyPrint;
import java.util.LinkedList;
import java.util.List;
import lex.Lexer;
import lex.Token;
import pars.Calc;
import pars.LibCalc;

public class Main {

	public static void main(String[] args) {
		Lexer lex = new Lexer();
		Calc calc = new Calc();
		PrettyPrint printer = new PrettyPrint();
		List<Token> tokens = new LinkedList<Token>();
		List<Token> tokensForLib = new LinkedList<Token>();
		try {
			lex.process(args[0], tokens);
			if (args.length == 2 && args[1].startsWith("-L")) {
				lex.process(args[1].substring(2), tokensForLib);
				LibCalc calcLib = new LibCalc();
				Node ast = calcLib.process(tokensForLib);
				System.out.println(ast.accept(printer));
			}
			Node ast = calc.process(tokens);
			System.out.println(ast.accept(printer));

		} catch (Throwable e) {
			return;
		}
	}
}
