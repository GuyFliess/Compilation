package lex;

public class Token {
	public String tag;
	public String value;
	public int line;
	public int column;

	public int start;
	public int end;

	public Token(String tag, int start, int end, String value, int line,
			int column) {
		this.tag = tag;
		this.start = start;
		this.end = end;
		this.value = value;
		this.line = line;
		this.column = column;
	}

	public Token(String tag, int start, String text, int line, int column) {
		this(tag, start, start + text.length(), text, line, column);
	}
	
	public Token(String tag, String value, int line,
			int column) {
		this.tag = tag;
		this.start = 0;
		this.end = 0;
		this.value = value;
		this.line = line;
		this.column = column;
	}
	
	public Token(String tag, String value) { 
		this(tag, value, 0, 0);
	}

}
