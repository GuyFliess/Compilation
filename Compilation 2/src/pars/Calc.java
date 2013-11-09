package pars;

import ic.ast.decl.PrimitiveType;
import ic.ast.decl.PrimitiveType.DataType;

import java.util.*;

import ast.Expr;
import ast.ExprBinary;
import ast.ExprBinary.Add;
import ast.ExprBinary.Sub;
import ast.ExprBinary.Mul;
import ast.ExprBinary.Div;
import ast.ExprBinary.Pow;
import ast.ExprUnary.Neg;
import ast.Num;
import fun.grammar.Grammar;
import fun.grammar.Word;
import fun.parser.earley.EarleyParser;
import fun.parser.earley.EarleyState;
import lex.Token;

public class Calc {

	String GRAMMAR = "S0 -> S | program | libic \n"
			+ "program -> classDecl* \n"
			+ "classDecl -> class CLASS_ID [extends CLASS_ID] '{' type '}' \n"
			+ // (field | method)* '}' \n" +
			"S -> type | formals \n"
			+ "type -> int | boolean | string | class | type '['']' \n"
			+ "formals -> type ID (',' type ID)* \n"
			+ "libic -> class Library '{' libmethod* '}' \n"
			+ "libmethod -> static (type | void) ID '(' [formals] ')' ';' \n";

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

	Expr constructAst(fun.parser.Tree parseTree)
	{
		Word r = parseTree.root;
		fun.parser.Tree[] s = 
			parseTree.subtrees.toArray(new fun.parser.Tree[0]);
		/* Branch according to root */
		switch (r.tag) {
		case "S": System.out.println("bla");
		case "TYPE" :
			if (s.length == 1)
			{
				switch (s[0].root.tag)
				{
				case "int": return new PrimitiveType(0,DataType.INT); 
				}
			}
		case "E":
		case "X":
		case "T":
			if (s.length == 1)
				return constructAst(s[0]);
			else {
				Expr a = constructAst(s[0]);
				Expr b = constructAst(s[2]);
				switch (s[1].root.tag)
				{
				case "+": return new Add(a, b);
				case "-": return new Sub(a, b);
				case "*": return new Mul(a, b);
				case "/": return new Div(a, b);
				case "^": return new Pow(a, b);
				default:
					throw new Error("internal error");
				}
			}
		case "F":
			switch (s[0].root.tag)
			{
			case "#": return constructAst(s[0]);
			case "-": return new Neg(constructAst(s[1]));
			default:  return constructAst(s[1]);
			}
		case "#": {
			Token tok = (Token)r;
			return new Num(Double.parseDouble(tok.value));
		}
		default: /* should never get here */
			throw new Error("internal error");
		}
	}

	public Expr process(Iterable<Token> tokens) {
		return constructAst(parse(tokens));
	}

	public String infix(Expr ast) {
		String s = ast.accept(new Expr.Visitor() {

			@Override
			public Object visit(Neg e) {
				return "(-" + e.getOperand().accept(this) + ")";
			}

			@Override
			public Object visit(Pow e) {
				return join(e);
			}

			@Override
			public Object visit(Div e) {
				return join(e);
			}

			@Override
			public Object visit(Mul e) {
				return join(e);
			}

			@Override
			public Object visit(Sub e) {
				return join(e);
			}

			@Override
			public Object visit(Add e) {
				return join(e);
			}

			@Override
			public Object visit(Num e) {
				return e.eval();
			}

			protected String join(ExprBinary e) {
				Expr[] ab = e.getOperands();
				return "(" + ab[0].accept(this) + " " + e.getOperator() + " "
						+ ab[1].accept(this) + ")";
			}
		}).toString();
		// chop ( )
		if (s.startsWith("("))
			s = s.substring(1, s.length() - 1);
		return s;
	}
}
