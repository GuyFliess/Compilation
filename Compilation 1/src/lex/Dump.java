package lex;

import java.util.List;

public class Dump
{
	private String text;
	private List<Token> matches;
	
	public Dump(String text, List<Token> matches)
	{
		this.text = text;
		this.matches = matches;
	}
	
	private char stateAt(int index) {
		for (Token m : matches) {
			if (index == m.start) return (index == m.end-1) ? 'I' : '[';
			else if (index == m.end-1) return ']';
			else if (index > m.start && index < m.end-1) return '-';
		}
		return ' ';
	}
	
	private int matchAt(int index) {
		for (int mi = 0; mi < matches.size(); mi++) {
			Token m = matches.get(mi);
			if (m.start <= index && index < m.end) {
				return mi;
			}
		}
		return -1;
	}
	
	public String console()
	{
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			sb1.append(" " + text.charAt(i));
			char s = stateAt(i);
			sb2.append((s == '-' || s == ']' ? "-" : " ") + s);
		}
		return sb1.toString() + "\n" + sb2.toString();
	}
	
	public void output() {
		String dump, tag, token, line, column;
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

	public String html()
	{
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		StringBuilder sb4 = new StringBuilder();
		StringBuilder sb5 = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			String sc = (c == ' ') ? "&nbsp;" : (""+c);
			sb1.append(String.format("<td>%s</td>", sc));
			String cls;
			switch (stateAt(i)) {
			case '-': cls = "mid"; break;
			case '[': cls = "first"; break;
			case ']': cls = "last"; break;
			case 'I': cls = "first last"; break;
			default: cls = "";
			}
			int num = matchAt(i);
			if (num >= 0) cls += (" clr" + (num % 7)); 
			sb2.append(String.format("<td class='%s'>&nbsp;</td>", cls));
		}
		int pos = 0;
		for (Token tok : matches) {
			if (tok.start > pos) sb3.append(String.format("<td colspan='%d'></td>", tok.start-pos));
			sb3.append(String.format("<td colspan='%d'>%s</td>", tok.end-tok.start, tok.tag));
			sb4.append(String.format("<td colspan='%d'>%s</td>", tok.end-tok.start, tok.line));
			sb5.append(String.format("<td colspan='%d'>%s</td>", tok.end-tok.start, tok.column));
			pos = tok.end;
		}
		return String.format("<table>\n" + 
				"  <tr class='input'>%s</tr>\n  <tr class='matches'>%s</tr>" +
				"  <tr class='tags'>%s</tr>\n</table>" +
				"  <tr class='line'>%s</tr>\n</table>" +
				"  <tr class='column'>%s</tr>\n</table>", sb1, sb2, sb3, sb4, sb5);
	}
	
}
