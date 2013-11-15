package pars;

import ic.ast.Node;
import ic.ast.decl.ClassType;
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
	List<DeclField> fields = new ArrayList<DeclField>();
	List<DeclMethod> methods = new ArrayList<DeclMethod>();
	Type type;
	int dimensions;

	String GRAMMAR = "S -> program \n"
			+ "program -> classDecl \n"
			+ "classDecl -> class CLASS_ID { fieldORmethod* } \n"
			+ "fieldORmethod* -> field* | method* \n"
			+ "method* -> nextMethod |  \n"
			+ "nextMethod -> method method* \n"
			+ "method -> static methodDecl | methodDecl \n"
			+ "methodDecl -> methodType ID ( formals* ) { stmt* } \n"
			+ "methodType -> type | void \n"
			+ "formals* ->  \n"
			+ "stmt* ->  \n"
			+ "field* -> nextField |  \n"
			+ "nextField -> field field* \n"
			+ "field -> type array ID moreIDs* ; \n"
			+ "moreIDs* -> anotherID |  \n"
			+ "anotherID -> , ID moreIDs* \n"
			+ "type -> int | boolean | string | CLASS_ID \n"
			+ "array -> dimension |  \n"
			+ "dimension -> [ ] array \n";

	String LibGRAMMAR = "S -> libic \n"
			+ "libic -> class CLASS_ID   { libmethod* } \n"
			+ "libmethod* ->  libmethod' | \n" 
			+ "libmethod' -> libmethod libmethod* \n"
			+ "libmethod -> static typeVoid ID ( formals* ) ; \n" //TODO add formals
			+ "typeVoid -> type | void \n"			
			+ "formals* -> formals' | \n"
			+ "formals' -> formals formals* \n"
			+ "formals -> type ID typeID* \n"
			+ "typeID* -> typeID' | \n"
			+ "typeID' -> typeID typeID* \n"
			+ "typeID -> , type ID \n"
			+ "type -> type2 typeArr \n"
			+ "type2 -> int | boolean | string | class \n"
			+ "typeArr -> [ ] typeArr |  \n";

	Grammar grammar;

	public Calc() {
		grammar = new Grammar(GRAMMAR);
	}

	public Calc(boolean lib) {
		grammar = new Grammar(LibGRAMMAR);
	}

	fun.parser.Tree parse(Iterable<Token> tokens) {
		EarleyParser e = new EarleyParser(tokens, grammar);
		List<EarleyState> pts = e.getCompletedParses();
		if (pts.size() != 1) {
			EarleyParser.PostMortem diagnosis = e.diagnoseError();
			System.out.println(String.format("Early parser failed  at token: %s  ",diagnosis.token));
			if (diagnosis.token instanceof Token)
			{
				Token token = (Token) diagnosis.token;
				System.out.println(String.format("Line %d column %d",token.line, token.column));
			}
			for (String  expected  : diagnosis.expecting) {
				System.out.println(String.format("Expected: %s", expected));
			}
			throw new Error("parse error");
		}
		return pts.get(0).parseTree();
	}

	Node constructAst(fun.parser.Tree parseTree) {
		int i;
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
			constructAst(s[3]); /* run on fieldORmethod* */
//			fields.add((DeclField) (constructAst(s[3])));
			int index = fields.size() - 1;
			while (fields.size() != 0 && fields.get(index) == null) {
				fields.remove(index);
				index = fields.size() - 1;
			}
			while (index != -1) {
				if (fields.get(index) == null) {
					fields.remove(index);
				}
				index--;
			}
			return new DeclClass(((Token) s[0].root).line,
					((Token) s[1].root).value, fields, methods);
		case "fieldORmethod*":
			return constructAst(s[0]);
		case "field*":
			if (s.length == 0) { // there aren't any more fields
				return null;
			} else {
				return constructAst(s[0]); /* run on nextField */
			}
		case "nextField":
			fields.add((DeclField) constructAst(s[0])); // run on field
			return constructAst(s[1]); // run on field*
		case "field":
			dimensions = 0;
			type = (Type) constructAst(s[0]); // run on type
			constructAst(s[1]);
			fields.add((DeclField) constructAst(s[3])); // add more IDs - run on											// moreIDs*
			return new DeclField(type, ((Token) s[2].root).value);
		case "type":
			switch (s[0].root.tag) {
			case "int":
				return new PrimitiveType(((Token) s[0].root).line, DataType.INT);
			case "boolean":
				return new PrimitiveType(((Token) s[0].root).line,
						DataType.BOOLEAN);
			case "string":
				return new PrimitiveType(((Token) s[0].root).line,
						DataType.STRING);
			case "CLASS_ID":
				return new ClassType(((Token) s[0].root).line,
						((Token) s[0].root).value);
			}
		case "array":
			if (s.length > 0) {
				constructAst(s[0]);
			}
			return null;
		case "dimension":
			type.incrementDimension();
			return constructAst(s[2]);
		case "moreIDs*":
			if (s.length == 0) { // there aren't any more IDs
				return null;
			} else { // there is another ID - run on anotherID
				return constructAst(s[0]);
			}
		case "anotherID":
			fields.add((DeclField) constructAst(s[2])); // run on moreIDs*
			return new DeclField(type, ((Token) s[1].root).value);

		/* Lib part */
		case "libic":
			
			return new DeclClass(((Token) s[0].root).line,
					((Token) s[1].root).value, fields, methods);
		default: /* should never get here */
			throw new Error("internal error (unimplemented ast)"); // TODO : clean the unimplemented part
		}
	}

	public Node process(Iterable<Token> tokens) {
		return constructAst(parse(tokens));
	}

}
