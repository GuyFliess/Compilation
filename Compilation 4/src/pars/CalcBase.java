package pars;

import ic.ast.Node;
import java.util.ArrayList;
import java.util.List;
import lex.Token;
import fun.grammar.Grammar;
import fun.parser.earley.EarleyParser;
import fun.parser.earley.EarleyState;

public abstract class CalcBase {

	Grammar grammar;
	
	fun.parser.Tree parse(Iterable<Token> tokens) {
		EarleyParser e = new EarleyParser(tokens, grammar);
		List<EarleyState> pts = e.getCompletedParses();
		if (pts.size() != 1) {
			EarleyParser.PostMortem diagnosis = e.diagnoseError();
			ArrayList<String> expectedList = new ArrayList<>();
			for (String expected : diagnosis.expecting) {
				expectedList.add(expected);
			}
			StringBuilder builder = new StringBuilder("");
			for (int i = 0; i < expectedList.size(); i++) {
				builder.append("'");
				builder.append(expectedList.get(i));
				builder.append("'");
				if (i != expectedList.size() - 1) {
					builder.append(" or ");
				}
			}

			String tmpString = builder.toString();

			if (diagnosis.token instanceof Token) {
				Token token = (Token) diagnosis.token;
				String errmsg = String.format("%d:%d : syntax error; expected %s, but found '%s'", token.line,
						token.column, tmpString, diagnosis.token);
				System.out.println(errmsg);
			} else {
				System.out.println(String.format("at end of input : syntax error; expected %s", tmpString));
			}
			throw new Error("parse error");
		}
		return pts.get(0).parseTree();
	}
	
	 abstract Node constructAst(fun.parser.Tree parseTree);
	 
	 public Node process(Iterable<Token> tokens) {
			return constructAst(parse(tokens));
		}
}
