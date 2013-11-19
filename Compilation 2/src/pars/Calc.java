package pars;

import ic.ast.Node;
import ic.ast.Visitor;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
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
import ic.ast.expr.ExpressionBlock;
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

public class Calc {
	Program program;
	List<DeclClass> classes = new ArrayList<DeclClass>();
	List<DeclField> fields = new ArrayList<DeclField>();
	List<DeclMethod> methods = new ArrayList<DeclMethod>();
	List<Parameter> formals = new ArrayList<Parameter>();
	List<Statement> statements = new ArrayList<Statement>();
	List<Expression> arguments = new ArrayList<Expression>();
	Type type, method_type;
	int dimensions;
	String method_name;
	UnaryOps unary_ops;
	BinaryOps binary_ops;

	String GRAMMAR = "S -> program \n"
			+ "program -> classDecl classDecl* |  \n"
			+ "classDecl* -> classDecl program |  \n"
			+ "classDecl -> class CLASS_ID { fieldORmethod* } | class CLASS_ID extends CLASS_ID { fieldORmethod* }\n"
			+ "fieldORmethod* -> nextMethod | nextField |  \n"
			+ "method* -> nextMethod |  \n"
			+ "nextMethod -> method fieldORmethod* \n"
			+ "method -> static methodDecl | methodDecl \n"
			+ "methodDecl -> methodType ID ( formals* ) { stmt* } \n"
			+ "methodType -> type | void \n"
			+ "void ->  \n"
			+ "formals* -> formal | , formal |  \n"
			+ "formal -> type array ID formals* \n"
			+ "stmt* -> nextStmt | { nextStmt } |  \n" // TODO: deal with block stmt
			+ "nextStmt -> stmt stmt* \n"
			+ "stmt -> location = expr ; | call ; | returnStmt ; | ifStmt | whileStmt | break ; | continue ; | localVar ; \n"
			+ "returnStmt -> return | return expr \n"
			+ "ifStmt -> if ( expr ) stmt elseStmt \n"
			+ "elseStmt -> else stmt |  \n"
			+ "whileStmt -> while ( expr ) stmt \n"
			+ "localVar -> type array ID | type array ID = expr \n"
			+ "expr ->  expr2 | paranthesisExpr   \n"		
			+ "expr2 ->  expr3 | unOpExpr \n"
			+ "expr3 ->  expr4 | binOpExpr \n"
			+ "expr4 -> location | call | lengthExpr | this | newClassExpr | newTypeExpr  | literal  \n"
			+ "newClassExpr -> new CLASS_ID ( ) \n"
			+ "paranthesisExpr -> ( expr ) \n"
			+ "binOpExpr -> expr3 binop expr3 \n"
			+ "unOpExpr -> unop expr2 \n"
			+ "newTypeExpr -> new type [ expr ] \n"
			+ "lengthExpr -> expr . length \n"
			+ "unop -> - | ! \n"
			+ "binop -> + | - | * | / | % | && | || | < | <= | > | >= | == | != \n"
			+ "location -> ID | expr . ID | expr [ expr ] \n"
			+ "call -> staticCall | virtualCall \n"
			+ "staticCall -> CLASS_ID . ID ( expr* ) \n"
			+ "virtualCall -> expr . ID ( expr* ) | ID ( expr* ) \n"
			+ "expr* -> expr moreExpr |  \n"
			+ "moreExpr -> , expr expr* |  \n"
			+ "literal -> INTEGER | STRING | true | false | null \n"
			+ "field* -> nextField |  \n"
			+ "nextField -> field fieldORmethod* \n"
			+ "field -> type array ID moreIDs* ; \n"
			+ "moreIDs* -> anotherID |  \n"
			+ "anotherID -> , ID moreIDs* \n"
			+ "type -> int | boolean | string | CLASS_ID \n"
			+ "array -> dimension |  \n"
			+ "dimension -> [ ] array \n";

;

	Grammar grammar;

	public Calc() {
		grammar = new Grammar(GRAMMAR);
	}

	fun.parser.Tree parse(Iterable<Token> tokens) {
		EarleyParser e = new EarleyParser(tokens, grammar);
		List<EarleyState> pts = e.getCompletedParses();
		if (pts.size() != 1) {
			EarleyParser.PostMortem diagnosis = e.diagnoseError();
			if (diagnosis.token instanceof Token)
			{
				Token token = (Token) diagnosis.token;
				System.out.print(String.format("Line %d column %d",token.line, token.column));
			}
			System.out.println(String.format("syntex Error: %s  ",diagnosis.token));
			
			for (String  expected  : diagnosis.expecting) {
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
		case "program":
			if (s.length == 0) {
				program = new Program(classes);
				program.removeNulls();
				return program;
			}
			else {
				classes.add((DeclClass) constructAst(s[0])); /* run on classDecl */
				return constructAst(s[1]); /* run on classDecl* */
			}
//			DeclClass decl_class = (DeclClass) constructAst(s[0]);
//			decl_class.removeNulls();
//			return decl_class;
		case "classDecl*":
			if (s.length == 0) {
				program = new Program(classes);
				program.removeNulls();
				return program;
			}
			else {
				classes.add((DeclClass) constructAst(s[0])); /* run on classDecl */
				return constructAst(s[1]); /* run on program */
			}
		case "classDecl":
			fields = new ArrayList<DeclField>();
			methods = new ArrayList<DeclMethod>();
			if (s.length == 5) { /* not a derived class */
				constructAst(s[3]); /* run on fieldORmethod* */
				return new DeclClass(((Token) s[0].root).line, ((Token) s[1].root).value, fields, methods);
			}
			else if (s.length == 7) { /* extends a class */
				constructAst(s[5]); /* run on fieldORmethod* */
				return new DeclClass(((Token) s[0].root).line, ((Token) s[1].root).value, ((Token) s[3].root).value, fields, methods);
			}
//			else {
//				// TODO: throw error - invalid class declaration
//			}
			return null;
		case "fieldORmethod*":
			if (s.length == 1) {
				return constructAst(s[0]);
			}
			else {
				return null;
			}
		case "method*":
			if (s.length == 0) { /* there aren't any more methods */
				return null;
			} else {
				return constructAst(s[0]); /* run on nextMethod */
			}
		case "nextMethod":
			methods.add((DeclMethod) constructAst(s[0])); /* run on method */
			return constructAst(s[1]); /* run on method* */
		case "method":
			DeclMethod method = null;
			if (s.length == 1) { /* virtual method */
				constructAst(s[0]); /* run on methodDecl */
				method = new DeclVirtualMethod(method_type, method_name, formals, statements);
			}
			else if (s.length == 2) { /* static method */
				constructAst(s[1]); /* run on methodDecl */
				method = new DeclStaticMethod(method_type, method_name, formals, statements);
			}
//			else {
//				TODO: throw error: invalid method declaration
//			}
			method.removeNulls();
			return method;
		case "methodDecl":
			formals = new ArrayList<Parameter>();
			statements = new ArrayList<Statement>();
			dimensions = 0;
			method_type = (Type) constructAst(s[0]); /* run on methodType */
			method_name = ((Token) s[1].root).value;
			formals.add((Parameter) constructAst(s[3])); /* run on formals* */
			statements.add((Statement) constructAst(s[6])); /* run on stmt* */
			return null;
		case "methodType":
//			if (((Token) s[0].root).tag == "void") {
//				return new PrimitiveType(((Token) s[0].root).line, DataType.VOID);
//			}
//			else {
				return constructAst(s[0]); /* run on type / void */
//			}
		case "void":
			return new PrimitiveType(((Token) s[0].root).line, DataType.VOID);
		case "formals*":
			if (s.length == 0) { /* there aren't any more formals */
				return null;
			}
			else if (s.length == 1) { /* the first formal */
				return constructAst(s[0]); /* run on formal */
			}
			else if (s.length == 2) { /* there is more than one formal */ //TODO: make sure it's with ,
				return constructAst(s[1]);
			}
		case "formal":
			dimensions = 0;
			type = (Type) constructAst(s[0]); /* run on type */
			constructAst(s[1]); /* run on array */
			formals.add(new Parameter(type, ((Token) s[2].root).value));
			return constructAst(s[3]);			
		case "stmt*":
			if (s.length == 0) { /* there aren't any more statements */
				return null;
			}
			else if (s.length == 1) { /* in a case of stmt */
				return constructAst(s[0]); /* run on nextStmt */
			}
			else if (s.length == 3) { /* in a case of { stmt } */
				return constructAst(s[1]); /* run on nextStmt */
			}
//			else {
//				//TODO: throw error on stmt declaration
//			}
		case "nextStmt":
			statements.add((Statement) constructAst(s[0]));
			return constructAst(s[1]); /* run on stmt* */	
// "stmt -> location = expr ; | call ; | returnStmt | ifStmt | whileStmt | break ; | continue ; | localVar ; \n"
		case "stmt":
			switch (s.length) {
			case 1:
				return constructAst(s[0]); /* run on ifStmt / whileStmt */
			case 2:
				return constructAst(s[0]); /* run on returnStmt / call / break / continue / localVar */
			case 4:
				Ref variable = (Ref) constructAst(s[0]); /* run on location */
				expr1 = (Expression) constructAst(s[2]); /* run on expr */
				return new StmtAssignment(variable, expr1);
				// TODO throw error if '=' doesn't appear
			default:
				// TODO throw error invalid statement
			}
		case "break":
			return new StmtBreak(((Token) s[0].root).line);
		case "continue":
			return new StmtContinue(((Token) s[0].root).line);
		case "returnStmt":
			switch (s.length) {
			case 1:
				return new StmtReturn(((Token) s[0].root).line);
			case 2:
				expr1 = (Expression) constructAst(s[1]);
				return new StmtReturn(((Token) s[0].root).line, expr1);
			default:
				// TODO throw error invalid return statement
			}
		case "ifStmt":
			expr1 = (Expression) constructAst(s[2]);
			stmt = (Statement) constructAst(s[4]);
			elseStmt = (Statement) constructAst(s[5]);
			if (elseStmt == null) {
				return new StmtIf(expr1, stmt);
			}
			else {
				return new StmtIf(expr1, stmt, elseStmt);
			}
		case "elseStmt":
			if (s.length == 0) { /* there isn't an else statement */
				return null;
			}
			else if (s.length == 2) {
				return constructAst(s[1]); /* run on stmt */
			}
//			else {
//				// TODO: throw error invalid if-else statement
//			}
		case "whileStmt":
			expr1 = (Expression) constructAst(s[2]);
			stmt = (Statement) constructAst(s[4]);
			return new StmtWhile(expr1, stmt);
			
//			+ "localVar -> type array ID ; | type array ID = expr ; \n"

		case "localVar":
			dimensions = 0;
			type = (Type) constructAst(s[0]); /* run on type */
			constructAst(s[1]); /* run on array */
			if (s.length == 3) {
				return new LocalVariable(type.getLine(), type, ((Token) s[2].root).value);
			}
			else if (s.length == 5) {
				expr1 = (Expression) constructAst(s[4]); /* run on expr */
				return new LocalVariable(type.getLine(), type, ((Token) s[2].root).value, expr1);
			}
//			else {
//				// TODO throw error invalid local variable statement
//			}
			
//			+ "expr -> location | call | this | newClassExpr | newTypeExpr | lengthExpr | binOpExpr | unOpExpr | literal | paranthesisExpr \n"
//			+ "newClassExpr -> new CLASS_ID ( ) \n"
//			+ "paranthesisExpr -> ( expr ) \n"
//			+ "binOpExpr -> expr binop expr \n"
//			+ "unOpExpr -> unop expr \n"
//			+ "newTypeExpr -> new type [ expr ] \n"
//			+ "lengthExpr -> expr . length \n"
			
		case "expr":
			return constructAst(s[0]);
		case "newClassExpr":
			return new NewInstance(((Token) s[0].root).line, ((Token) s[1].root).value);
		case "paranthesisExpr":
			expr1 = (Expression) constructAst(s[1]); /* run on expr */
			return new ExpressionBlock(expr1);
		case "binOpExpr":
			expr1 = (Expression) constructAst(s[0]); /* run on first expr */
			expr2 = (Expression) constructAst(s[2]); /* run on second expr */
			constructAst(s[1]); /* run on binop */
			return new BinaryOp(expr1.getLine(), expr1, binary_ops, expr2);
		case "unOpExpr":
			expr1 = (Expression) constructAst(s[1]);
			constructAst(s[0]); /* run on unop */
			return new UnaryOp(((Token) s[0].root).line, unary_ops, expr1);
		case "newTypeExpr":
			type = (Type) constructAst(s[1]); /* run on type */
			expr1 = (Expression) constructAst(s[3]); /* run on expr */
			return new NewArray(type, expr1);
		case "lengthExpr":
			expr1 = (Expression) constructAst(s[0]); /* run on expr */
			return new Length(((Token) s[0].root).line, expr1);
//			+ "location -> ID | expr . ID | expr [ expr ] \n"
		case "location":
			switch (s.length) {
			case 1: /* run on ID */
				return new RefVariable(((Token) s[0].root).line, ((Token) s[0].root).value);
			case 3: /* run on expr.ID */
				if (s[1].root.tag != ".") {
					// TODO: throw error - invalid field reference
				}
				expr1 = (Expression) constructAst(s[0]);
				return new RefField(((Token) s[0].root).line, expr1, ((Token) s[2].root).value);
			case 4:
				if (s[1].root.tag != "[" && s[3].root.tag != "]") {
					// TODO: throw error - invalid array reference
				}
				expr1 = (Expression) constructAst(s[0]);
				expr2 = (Expression) constructAst(s[2]);
				return new RefArrayElement(expr1, expr2);
				
			default:
				// TODO: throw error - invalid reference declaration
				break;
			}
			return null;
		case "call":
			return constructAst(s[0]); /* run on staticCall / virtualCall */
			
//			+ "staticCall -> CLASS_ID . ID ( expr* ) \n"
//			+ "virtualCall -> expr . ID ( expr* ) | ID ( expr* ) \n"
//			+ "expr* -> expr moreExpr |  \n"
//			+ "moreExpr -> , expr expr* |  \n"
			
		case "staticCall":
			arguments.add((Expression) constructAst(s[4])); /* run on expr* */
			return new StaticCall(((Token) s[0].root).line, ((Token) s[0].root).value, ((Token) s[2].root).value, arguments);
		case "virtualCall":
			VirtualCall virtual_call = null;
			if (s.length == 4) { /* run on ID(expr*) */
				arguments.add((Expression) constructAst(s[2])); /* run on expr* */
				virtual_call = new VirtualCall(((Token) s[0].root).line, ((Token) s[0].root).value, arguments);
			}
			else if (s.length == 6) { /* run on expr.ID(expr*) */
				expr1 = (Expression) constructAst(s[0]); /* run on expr */
				arguments.add((Expression) constructAst(s[4])); /* run on expr* */
				virtual_call = new VirtualCall(((Token) s[1].root).line, expr1, ((Token) s[2].root).value, arguments);
			}
			virtual_call.removeNulls();
			return virtual_call;
//			else {
//				// TODO: throw error - invalid virtual call
//			}
		case "expr*":
			if (s.length == 0) { /* there aren't any more expressions */
				return null;
			}
			else {
				arguments.add((Expression) constructAst(s[1])); /* run on moreExpr */
				return constructAst(s[0]); /* run on expr */
			}
		case "moreExpr":
			if (s.length == 0) {
				return null;
			}
			else {
				arguments.add((Expression) constructAst(s[2])); /* run on expr* */
				return constructAst(s[1]); /* run on expr */
			}
		case "this":
			return new This(((Token) s[0].root).line);
		case "binop":
			switch (s[0].root.tag) {
			case "+":
				binary_ops = BinaryOps.PLUS;
				break;
			case "-":
				binary_ops = BinaryOps.MINUS;
				break;
			case "*":
				binary_ops = BinaryOps.MULTIPLY;
				break;
			case "/":
				binary_ops = BinaryOps.DIVIDE;
				break;
			case "%":
				binary_ops = BinaryOps.MOD;
				break;
			case "&&":
				binary_ops = BinaryOps.LAND;
				break;
			case "||":
				binary_ops = BinaryOps.LOR;
				break;
			case "<":
				binary_ops = BinaryOps.LT;
				break;
			case "<=":
				binary_ops = BinaryOps.LTE;
				break;
			case ">":
				binary_ops = BinaryOps.GT;
				break;
			case ">=":
				binary_ops = BinaryOps.GTE;
				break;
			case "==":
				binary_ops = BinaryOps.EQUAL;
				break;
			case "!=":
				binary_ops = BinaryOps.NEQUAL;
				break;
			default:
				// TODO: throw error - invalid binary operation
				break;
			}
			return null;
		case "unop":
			switch (s[0].root.tag) {
			case "-":
				unary_ops = UnaryOps.UMINUS;
				break;
			case "!":
				unary_ops = unary_ops.LNEG;
				break;
			default:
				// TODO: throw error - invalid unary operation
				break;
			}
			return null;
		case "literal":
			Object value = ((Token) s[0].root).value;
			switch (s[0].root.tag) {
			case "INTEGER":
				return new Literal(((Token) s[0].root).line, DataType.INT, value);
			case "STRING":
				return new Literal(((Token) s[0].root).line, DataType.STRING, value);
			case "true":
				return new Literal(((Token) s[0].root).line, DataType.BOOLEAN, value);
			case "false":
				return new Literal(((Token) s[0].root).line, DataType.BOOLEAN, value);
			case "null":
				return new Literal(((Token) s[0].root).line, DataType.VOID, value);
			default:
				// TODO: throw error - invalid literal
				break;
			}
			return null;
		case "field*":
			if (s.length == 0) { /* there aren't any more fields */
				return null;
			} else {
				return constructAst(s[0]); /* run on nextField */
			}
		case "nextField":
			fields.add((DeclField) constructAst(s[0])); /* run on field */
			return constructAst(s[1]); /* run on field* */
		case "field":
			dimensions = 0;
			type = (Type) constructAst(s[0]); /* run on type */
			constructAst(s[1]); /* run on array */
			fields.add((DeclField) constructAst(s[3])); // add more IDs - run on moreIDs*
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
			if (s.length == 0) { /* there aren't any more IDs */
				return null;
			} else { /* there is another ID - run on anotherID */
				return constructAst(s[0]);
			}
		case "anotherID":
			fields.add((DeclField) constructAst(s[2])); // run on moreIDs*
			return new DeclField(type, ((Token) s[1].root).value);


		default: /* should never get here */
			throw new Error("internal error (unimplemented ast)"); // TODO : clean the unimplemented part
		}
	}

	

	public Node process(Iterable<Token> tokens) {
		constructAst(parse(tokens));
		program.removeNulls();
		for (int i = 0; i < program.getClasses().size(); i++) {
			program.getClasses().get(i).removeNulls();
			for (int j = 0; j < program.getClasses().get(i).getMethods().size(); j++) {
				program.getClasses().get(i).getMethods().get(j).removeNulls();
			}
		}
		return program;
	}

}
