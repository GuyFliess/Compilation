package lex;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;


public class Lexer
{

	public void process(String text, 
			Collection<Token> out_tokens) throws Exception
	{
//		FileInputStream fis = new FileInputStream(text);
//		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
//		Scanner scanner = new Scanner(isr);
		Scanner scanner = new Scanner(new FileReader(text));
//		Scanner scanner = new Scanner(new StringReader(text));
		
		Token token = scanner.yylex();
        while (token != null) {
        	out_tokens.add(token);
//        	for (int j = 0; j <20; j++) {
//        		System.out.println(scanner.yycharat(j));
//			}
//        	scanner.zzCurrentPosL++;
            token = scanner.yylex();
        }
	}
}
