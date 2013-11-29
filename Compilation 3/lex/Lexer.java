package lex;

import java.io.FileReader;
import java.io.StringReader;
import java.util.Collection;


public class Lexer
{

	public void process(String programName, 
			Collection<Token> out_tokens) throws Exception
	{
		Scanner scanner = new Scanner(new FileReader(programName));
		Token token = scanner.yylex();
        while (token != null) {
        	out_tokens.add(token);
            token = scanner.yylex();
        }
	}

	public void processLib(String libraryName,
			Collection<Token> out_tokens) throws Exception {
		Scanner scanner = new Scanner(new FileReader(libraryName));
		Token token = scanner.yylex();
        while (token != null) {
        	out_tokens.add(token);
            token = scanner.yylex();
        }
		
	}
}
