import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import lex.Dump;
import lex.Lexer;
import lex.Token;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		String file_string;
		Lexer lex = new Lexer();
		List<Token> tokens = new LinkedList<Token>();
		Dump dump = null;
		try {
			file_string = new String(Files.readAllBytes(Paths.get(args[0])));
			lex.process(args[0], tokens);
			dump = new Dump(file_string, tokens);
			dump.output();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
