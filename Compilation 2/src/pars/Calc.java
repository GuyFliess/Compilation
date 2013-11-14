package pars;

import ic.ast.Node;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.PrimitiveType;
import ic.ast.decl.PrimitiveType.DataType;
import ic.ast.decl.Type;
import ic.ast.expr.Expression;
import ic.ast.expr.Literal;

import java.util.*;

import fun.grammar.Grammar;
import fun.grammar.Word;
import fun.parser.earley.EarleyParser;
import fun.parser.earley.EarleyState;
import lex.Token;

public class Calc {

	// String GRAMMAR = "S0 -> S | program | libic \n"
	// + "program -> classDecl* \n"
	// + "classDecl -> class CLASS_ID [extends CLASS_ID] '{' type '}' \n"
	// + // (field | method)* '}' \n" +
	// "S -> type | formals \n"
	// + "type -> int | boolean | string | class | type '['']' \n"
	// + "formals -> type ID (',' type ID)* \n"
	// + "libic -> class Library '{' libmethod* '}' \n"
	// + "libmethod -> static (type | void) ID '(' [formals] ')' ';' \n";

	// String GRAMMAR = "S -> type  \n" //TODO remove S, it's just for
	// developing
	// + "formals -> type ID moreFormals \n "
	// + "moreFormals ->  ',' type ID |  \n" // TODO how to write epsilon -
	// empty word
	// + "type -> type2 typeArr ';' \n"
	// + "type2 -> int | boolean | string | class \n"
	// + "typeArr -> '['']' typeArr |  \n";

	// String GRAMMAR = "program"

	String GRAMMAR = "S -> program \n" // TODO remove S, it's just for
										// developing
			+ "program -> classDecl \n"
			+ "classDecl -> class CLASS_ID { field* } \n"
			// + "classDecl -> field \n" //class CLASS_ID '{' (field | method)*
			// '}' \n "
			+ "field* -> f |  \n"
			+ "f -> field field* \n"
			+ "field -> type ID ; \n"
			+ "type -> int | boolean \n";
//			+ "E ->  \n";
	// + "; ->  \n";
	Grammar grammar;

	public Calc() {
		grammar = new Grammar(GRAMMAR);
	}

	fun.parser.Tree parse(Iterable<Token> tokens) {
		EarleyParser e = new EarleyParser(tokens, grammar);
		List<EarleyState> pts = e.getCompletedParses();
		if (pts.size() != 1)
			throw new Error("parse error");
		return pts.get(0).parseTree();
	}

	Node constructAst(fun.parser.Tree parseTree) {
		Word r = parseTree.root;
		fun.parser.Tree[] s = parseTree.subtrees
				.toArray(new fun.parser.Tree[0]);
		/* Branch according to root */
		switch (r.tag) {
		case "S":
			return constructAst(s[0]);
		case "program":
			return constructAst(s[0]);
		case "classDecl":
			List<DeclField> fields = new ArrayList<DeclField>();
			List<DeclMethod> methods = new ArrayList<DeclMethod>();
			for (int i = 1; i < s.length; i++) {
				if (s[i].subtrees.size() != 0) {
					fields.add(new DeclField((DeclField) constructAst(s[i])));
				}
			}
			return new DeclClass(((Token) s[0].root).line,
					((Token) s[1].root).value, fields, methods);
		case "f":
		case "field*":
			for (int i = 0; i < s.length; i++) {
				if (s[i].subtrees.size() != 0) {
					return constructAst(s[i]);
				}
			}
		case "field":
			Type type = (Type) constructAst(s[0]);
			return new DeclField(type, ((Token) s[1].root).value);
		case "type":
			switch (s[0].root.tag) {
			case "int":
				return new PrimitiveType(((Token) s[0].root).line, DataType.INT);
			case "boolean":
				return new PrimitiveType(((Token) s[0].root).line,
						DataType.BOOLEAN);

			}
		default: /* should never get here */
			throw new Error("internal error");
		}
	}

	public Node process(Iterable<Token> tokens) {
		return constructAst(parse(tokens));
	}

}
