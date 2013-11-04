import java.util.LinkedList;
import java.util.List;
import lex.Dump;
import lex.Lexer;
import lex.Token;

public class Main {

	public static void main(String[] args) {
		Lexer lex = new Lexer();
		List<Token> tokens = new LinkedList<Token>();
		Dump dump = null;
		assert (args.length > 1);
		try {
			lex.process(args[0], tokens);
			dump = new Dump(tokens);
			dump.output();
		} catch (Exception e) {
			System.err.println(e);
		}
		
	}
}