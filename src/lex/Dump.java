package lex;

import java.util.List;

public class Dump
{	
	private List<Token> matches;
	
	public Dump (List<Token> matches)
	{		
		this.matches = matches;
	}

	public void output() {
		String tag, token, line, column;
		System.out.format("%-13s%-13s%-5s%-8s\n", "token", "tag", "line", ": column");
		for (Token tok: matches) {
			tag = tok.tag;
			token = tok.value;
			line = Integer.toString(tok.line);
			column = Integer.toString(tok.column);
			if (tag=="ERROR" || tag == "STRING_ERROR") {
				System.out.format("%s:%s : lexical error; %s\n", line, column, token);
				break;
			}
			System.out.format("%-12s %-11s%4s%8s\n", token, tag, line, column);
		}
	}
}
