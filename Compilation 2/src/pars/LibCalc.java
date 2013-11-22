package pars;

import ic.ast.Node;
import ic.ast.Visitor;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclLibraryMethod;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.PrimitiveType;
import ic.ast.decl.PrimitiveType.DataType;
import ic.ast.decl.Program;
import ic.ast.decl.Type;
import ic.ast.expr.BinaryOp;
import ic.ast.expr.BinaryOp.BinaryOps;
import ic.ast.expr.Expression;
import ic.ast.expr.Length;
import ic.ast.expr.Literal;
import ic.ast.expr.NewArray;
import ic.ast.expr.NewInstance;
import ic.ast.expr.Ref;
import ic.ast.expr.RefArrayElement;
import ic.ast.expr.RefField;
import ic.ast.expr.RefVariable;
import ic.ast.expr.StaticCall;
import ic.ast.expr.This;
import ic.ast.expr.UnaryOp;
import ic.ast.expr.UnaryOp.UnaryOps;
import ic.ast.expr.VirtualCall;
import ic.ast.stmt.LocalVariable;
import ic.ast.stmt.Statement;
import ic.ast.stmt.StmtAssignment;
import ic.ast.stmt.StmtBreak;
import ic.ast.stmt.StmtContinue;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtReturn;
import ic.ast.stmt.StmtWhile;

import java.util.*;

import fun.grammar.Grammar;
import fun.grammar.Word;
import fun.parser.earley.EarleyParser;
import fun.parser.earley.EarleyState;
import lex.Token;

public class LibCalc {
	Program program;
	List<DeclMethod> methods = new ArrayList<DeclMethod>();
	List<Parameter> formals = new ArrayList<Parameter>();

	Type type, method_type;
	int dimensions;
	String method_name;
	UnaryOps unary_ops;
	BinaryOps binary_ops;

	

	String LibGRAMMAR = "S -> libic \n"
			+ "libic -> class CLASS_ID   { libmethod* } \n"
			+ "libmethod* ->  libmethod' | \n"
			+ "libmethod' -> libmethod libmethod* \n"
			+ "libmethod -> static typeVoid ID ( formals* ) ; \n" 
			+ "typeVoid -> type array | void \n"
			+ "formals* -> formal | , formal |  \n"
			+ "formal -> type array ID formals* \n"
			+ "type -> int | boolean | string | CLASS_ID \n"
			+ "array -> dimension |  \n" + "dimension -> [ ] array \n"

	;

	Grammar grammar;

	

	public LibCalc() {
		grammar = new Grammar(LibGRAMMAR);
	}

	fun.parser.Tree parse(Iterable<Token> tokens) {
		EarleyParser e = new EarleyParser(tokens, grammar);
		List<EarleyState> pts = e.getCompletedParses();
		if (pts.size() != 1) {
			EarleyParser.PostMortem diagnosis = e.diagnoseError();
			if (diagnosis.token instanceof Token) {
				Token token = (Token) diagnosis.token;
				System.out.print(String.format("Line %d column %d", token.line,
						token.column));
			}
			System.out.println(String.format("syntex Error: %s  ",
					diagnosis.token));

			for (String expected : diagnosis.expecting) {
				System.out.println(String.format("Expected: %s", expected));
			}
			throw new Error("parse error");
		}
		return pts.get(0).parseTree();
	}

	Node constructAst(fun.parser.Tree parseTree) {
		Expression expr1, expr2;
		Statement stmt, elseStmt = null;
		Word r = parseTree.root;
		fun.parser.Tree[] s = parseTree.subtrees
				.toArray(new fun.parser.Tree[0]);

		/* Branch according to root */
		switch (r.tag) {
		case "S":
			return constructAst(s[0]);
	
		case "void":
			return new PrimitiveType(((Token) s[0].root).line, DataType.VOID);
		case "formals*":
			if (s.length == 0) { /* there aren't any more formals */
				return null;
			} else if (s.length == 1) { /* the first formal */
				return constructAst(s[0]); /* run on formal */
			} else if (s.length == 2) { /* there is more than one formal */// TODO:
																			// make
																			// sure
																			// it's
																			// with
																			// ,
				return constructAst(s[1]);
			}
		case "formal":
			dimensions = 0;
			type = (Type) constructAst(s[0]); /* run on type */
			constructAst(s[1]); /* run on array */
			formals.add(new Parameter(type, ((Token) s[2].root).value));
			return constructAst(s[3]);
		
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
	
			/* Lib part */
		case "libic":
		   methods = new ArrayList<DeclMethod>();
			if (s.length == 5) {
				constructAst(s[3]);
			}
			DeclClass declClass = new DeclClass(((Token) s[0].root).line,
					((Token) s[1].root).value, new ArrayList<DeclField>(), methods);
			declClass.removeNulls();			
			return declClass;
		case "libmethod*": {
			if (s.length == 1) {
				
				return constructAst(s[0]);
			}
			if (s.length == 0) {
				return null;
			}
		}
		case "libmethod'":
			methods.add((DeclMethod) constructAst(s[0]));

			return constructAst(s[1]);
		case "libmethod":
			dimensions = 0;
			method_type = (Type) constructAst(s[1]); /* run on methodType */
			method_name = ((Token) s[2].root).value;
			formals  = new ArrayList<Parameter>();
			formals.add((Parameter) constructAst(s[4])); /* run on formals* */
			return new DeclLibraryMethod(method_type, method_name, formals);
		case "typeVoid":
			if (s.length == 1) {
				// void case
				return new PrimitiveType(((Token) s[0].root).line,
						DataType.VOID);
			} else {
				// type with array
				dimensions = 0;
				type = (Type) constructAst(s[0]); /* run on type */
				constructAst(s[1]); /* run on array */
				return type;

			}
		default: /* should never get here */
			throw new Error("internal error (unimplemented ast)"); // TODO :
																	// clean the
																	// unimplemented
																	// part
		}
	}

	public Node process(Iterable<Token> tokens) {
		DeclClass resultClass = (DeclClass) constructAst(parse(tokens));
		resultClass.removeNulls();
		for (int j = 0; j < resultClass.getMethods()
				.size(); j++) {
			resultClass.getMethods().get(j).removeNulls();
		}
		if (program != null) {
			program.removeNulls();
			for (int i = 0; i < program.getClasses().size(); i++) {
				program.getClasses().get(i).removeNulls();
				for (int j = 0; j < program.getClasses().get(i).getMethods()
						.size(); j++) {
					program.getClasses().get(i).getMethods().get(j)
							.removeNulls();
				}
			}
		}		
		return resultClass;
	}
	
}
