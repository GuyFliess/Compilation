package pars;

import ic.ast.Node;
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

//	String GRAMMAR = "S0 -> S | program | libic \n"
//			+ "program -> classDecl* \n"
//			+ "classDecl -> class CLASS_ID [extends CLASS_ID] '{' type '}' \n"
//			+ // (field | method)* '}' \n" +
//			"S -> type | formals \n"
//			+ "type -> int | boolean | string | class | type '['']' \n"
//			+ "formals -> type ID (',' type ID)* \n"
//			+ "libic -> class Library '{' libmethod* '}' \n"
//			+ "libmethod -> static (type | void) ID '(' [formals] ')' ';' \n";

	String GRAMMAR = "S -> type  \n" //TODO remove S, it's just for developing
			+ "formals -> type ID moreFormals \n "
			+ "moreFormals ->  ',' type ID |  \n" // TODO how to write epsilon - empty word
			+ "type -> type2 typeArr \n"
			+ "type2 -> int | boolean | string | class \n"
			+ "typeArr -> '['']' typeArr |  \n";
	
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

	Node constructAst(fun.parser.Tree parseTree)
	{
		Word r = parseTree.root;		
		fun.parser.Tree[] s = 
			parseTree.subtrees.toArray(new fun.parser.Tree[0]);
		/* Branch according to root */
		switch (r.tag) {
		case "S": return constructAst(s[0]); 
		case "type" :
			if (s.length == 1)
			{				
				switch (s[0].root.tag)
				{
				case "int": return new PrimitiveType(  ((Token) s[0].root).line, DataType.INT);
				case "boolean" : return new PrimitiveType(((Token) s[0].root).line , DataType.BOOLEAN);
				 
				}
			}
			if (s.length == 2)
			{ 
				
			}
			else if (s.length == 3)
			{
				// type '['']' handle array
				Type node = null ;
				switch (s[0].root.tag)
				{
				case "int": node = new PrimitiveType(  ((Token) s[0].root).line, DataType.INT);
				case "boolean" : node = new PrimitiveType(((Token) s[0].root).line , DataType.BOOLEAN);
				 
				}
				node.incrementDimension(); //increase dimension because we saw []
				return node;
			}
		case "formals" : 

		default: /* should never get here */
			throw new Error("internal error");
		}
	}

	public Node process(Iterable<Token> tokens) {
		return constructAst(parse(tokens));
	}

}
