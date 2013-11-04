package lex;

import java.io.FileReader;
import java.util.Collection;

public class Lexer
{

	public void process(String text, 
			Collection<Token> out_tokens) throws Exception
	{
		Scanner scanner = new Scanner(new FileReader(text));
		Token token = scanner.yylex();
        while (token != null) {
        	out_tokens.add(token);
            token = scanner.yylex();
        }
	}
}
