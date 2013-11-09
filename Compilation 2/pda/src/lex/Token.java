package lex;



public class Token
{
	public String tag;
	public int start;
	public int end;
	public String text;
	
	public Token(String tag, int start, int end, String text)
	{
		this.tag = tag;
		this.start = start;
		this.end = end;
		this.text = text;
	}
	
	public Token(String tag, int start, String text)
	{
		this(tag, start, start + text.length(), text);
	}

	@Override
	public String toString()
	{
		return tag + "⋮" + text;
	}
}
