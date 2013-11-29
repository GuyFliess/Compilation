package pars;

import ic.ast.Node;
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
import ic.ast.expr.Call;
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
import ic.ast.stmt.StmtBlock;
import ic.ast.stmt.StmtBreak;
import ic.ast.stmt.StmtCall;
import ic.ast.stmt.StmtContinue;
import ic.ast.stmt.StmtIf;
import ic.ast.stmt.StmtReturn;
import ic.ast.stmt.StmtWhile;
import java.util.*;
import fun.grammar.Grammar;
import fun.grammar.Word;
import lex.Token;

public class Calc extends CalcBase {
	Program program;
	List<DeclClass> classes = new ArrayList<DeclClass>();
	List<DeclField> fields = new ArrayList<DeclField>();
	List<DeclMethod> methods = new ArrayList<DeclMethod>();
	List<Parameter> formals = new ArrayList<Parameter>();
	List<Statement> statements = new ArrayList<Statement>();
	List<Expression> arguments = new ArrayList<Expression>();
	List<Statement> statementsBlock = new ArrayList<Statement>();
	ArrayList<ArrayList<Statement>> stmt_list = new ArrayList<ArrayList<Statement>>();
	Type type, method_type;
	int dimensions;
	String method_name;
	UnaryOps unary_ops;
	BinaryOps binary_ops;
	Statement operation;
	StmtBlock stmt_block;
	boolean negativeInteger = false;

	String GRAMMAR = "S -> program \n"
			+ "program -> classDecl classDecl* |  \n"
			+ "classDecl* -> classDecl program |  \n"
			+ "classDecl -> class CLASS_ID { fieldORmethod* } | class CLASS_ID extends CLASS_ID { fieldORmethod* }\n"
			+ "fieldORmethod* -> nextMethod | nextField |  \n"
			+ "method* -> nextMethod |  \n"
			+ "nextMethod -> method fieldORmethod* \n"
			+ "method -> static methodDecl | methodDecl \n"
			+ "methodDecl -> methodType ID ( formals* ) { stmt* } \n"
			+ "methodType -> type | voidType \n"
			+ "voidType -> void \n"
			+ "formals* -> formal | , formal |  \n"
			+ "formal -> type array ID formals* \n"
			+ "stmt* -> nextStmt |  \n"
			+ "nextStmt ->  stmt stmt* | blockStmt stmt* \n"
			+ "blockStmt -> { stmt* } \n"
			+ "stmt -> location = expr ; | stmtCall ; | returnStmt ; | ifStmt* | whileStmt | break ; | continue ; | localVar ; \n"
			+ "stmtBlock -> stmt stmtBlock |  \n"
			+ "stmtCall -> call \n"
			+ "returnStmt -> return | return expr \n"
			+ "ifStmt* -> ifStmt | ifElseStmt \n"
			+ "ifStmt -> if ( expr ) ifOperation \n"
			+ "ifElseStmt -> if ( expr ) ifElseOperation else ifOperation \n"
			+ "ifElseOperation -> { ifElseBlockStmt* } | stmtWOIf \n"
			+ "ifElseBlockStmt* -> ifElseNextBlockStmt |  \n"
			+ "ifElseNextBlockStmt -> stmtWOIf ifElseBlockStmt* | ifBlockStmt ifElseBlockStmt* \n"
			+ "ifOperation -> { ifBlockStmt* } | stmtBonus1 \n"
			+ "ifBlockStmt* -> ifNextBlockStmt |  \n"
			+ "ifNextBlockStmt -> stmt ifBlockStmt* | ifBlockStmt ifBlockStmt* \n"
			+ "ifBlockStmt -> { ifBlockStmt* } \n"
			+ "stmtWOIf -> location = expr ; | stmtCall ; | returnStmt ; | whileStmt | break ; | continue ; | type array ID = expr ; \n"
			+ "stmtBonus1 -> location = expr ; | stmtCall ; | returnStmt ; | ifStmt* | whileStmt | break ; | continue ; | type array ID = expr ; \n"
			+ "whileStmt -> while ( expr ) whileOperation \n" 
			+ "whileOperation -> { whileBlockStmt* } | stmt \n"
			+ "whileBlockStmt* -> whileNextBlockStmt |  \n" 
			+ "whileNextBlockStmt -> stmt whileBlockStmt* | whileBlockStmt whileBlockStmt* \n"
			+ "whileBlockStmt -> { whileBlockStmt* } \n"
			+ "localVar -> type array ID | type array ID = expr \n" 
			+ "expr -> expr || expr7 | expr7 \n"
			+ "expr7 -> expr7 && expr6 | expr6 \n" + "expr6 -> expr6 == expr5 | expr6 != expr5 | expr5 \n"
			+ "expr5 -> expr5 < expr4 | expr5 <= expr4 | expr5 > expr4 | expr5 >= expr4 | expr4 \n"
			+ "expr4 -> expr4 + expr3 | expr4 - expr3 | expr3 \n"
			+ "expr3 -> expr3 * expr2 | expr3 / expr2 | expr3 % expr2 | expr2 \n"
			+ "expr2 -> ! expr2 | - expr2 | expr1 \n"
			+ "expr1 -> new type array [ expr1 ] | new CLASS_ID ( ) | expr0 \n"
			+ "expr0 -> ( expr ) | expr0 . length | location | call | this | literal \n"
			+ "location -> ID | expr0 . ID | expr0 [ expr ] \n" + "call -> staticCall | virtualCall \n"
			+ "staticCall -> CLASS_ID . ID ( expr* ) \n" + "virtualCall -> expr1 . ID ( expr* ) | ID ( expr* ) \n"
			+ "expr* -> expr moreExpr |  \n" + "moreExpr -> , expr moreExpr |  \n"
			+ "literal -> INTEGER | STRING | true | false | null \n" + "field* -> nextField |  \n"
			+ "nextField -> field fieldORmethod* \n" + "field -> type array ID moreIDs* ; \n"
			+ "moreIDs* -> anotherID |  \n" + "anotherID -> , ID moreIDs* \n"
			+ "type -> int | boolean | string | CLASS_ID \n" + "array -> dimension |  \n" + "dimension -> [ ] array \n";


	public Calc() {
		grammar = new Grammar(GRAMMAR);
	}


	Node constructAst(fun.parser.Tree parseTree) {
		Expression expr1, expr2;
		Statement elseStmt = null;

		Word r = parseTree.root;
		fun.parser.Tree[] s = parseTree.subtrees.toArray(new fun.parser.Tree[0]);

		/* Branch according to root */
		switch (r.tag) {
		case "S":
			return constructAst(s[0]);
		case "program":
			if (s.length == 0) {
				program = new Program(classes);
				return program;
			} else {
				classes.add((DeclClass) constructAst(s[0])); /* run on classDecl */
				return constructAst(s[1]); /* run on classDecl* */
			}
		case "classDecl*":
			if (s.length == 0) {
				program = new Program(classes);
				return program;
			} else {
				classes.add((DeclClass) constructAst(s[0])); /* run on classDecl */
				return constructAst(s[1]); /* run on program */
			}
		case "classDecl":
			fields = new ArrayList<DeclField>();
			methods = new ArrayList<DeclMethod>();
			if (s.length == 5) { /* not a derived class */
				constructAst(s[3]); /* run on fieldORmethod* */
				return new DeclClass(((Token) s[0].root).line, ((Token) s[1].root).value, fields, methods);
			} else if (s.length == 7) { /* extends a class */
				constructAst(s[5]); /* run on fieldORmethod* */
				return new DeclClass(((Token) s[0].root).line, ((Token) s[1].root).value, ((Token) s[3].root).value,
						fields, methods);
			}
		case "fieldORmethod*":
			if (s.length == 1) {
				return constructAst(s[0]); /* run on nextMethod / nextField */
			} else {
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
			return constructAst(s[1]); /* run on fieldORMethod* */
		case "method":
			DeclMethod method = null;
			if (s.length == 1) { /* virtual method */
				constructAst(s[0]); /* run on methodDecl */
				method = new DeclVirtualMethod(method_type, method_name, formals, statements);
			} else if (s.length == 2) { /* static method */
				constructAst(s[1]); /* run on methodDecl */
				method = new DeclStaticMethod(method_type, method_name, formals, statements);
			}
			return method;
		case "methodDecl":
			formals = new ArrayList<Parameter>();
			statements = new ArrayList<Statement>();
			dimensions = 0;
			method_type = (Type) constructAst(s[0]); /* run on methodType */
			method_name = ((Token) s[1].root).value;
			constructAst(s[3]); /* run on formals* */
			constructAst(s[6]); /* run on stmt* */
			return null;
		case "methodType":
			return constructAst(s[0]); /* run on type / voidType */
		case "voidType":
			return new PrimitiveType(((Token) s[0].root).line, DataType.VOID);
		case "formals*":
			if (s.length == 0) { /* there aren't any more formals */
				return null;
			} else if (s.length == 1) { /* the first formal */
				return constructAst(s[0]); /* run on formal */
			} else if (s.length == 2) { /* there is more than one formal */
				return constructAst(s[1]); /* run on formal */
			}
		case "formal":
			dimensions = 0;
			type = (Type) constructAst(s[0]); /* run on type */
			constructAst(s[1]); /* run on array */
			formals.add(new Parameter(type, ((Token) s[2].root).value));
			return constructAst(s[3]); /* run on formals* */
		case "stmt*":
			if (s.length == 0) { /* there aren't any more statements */
				return null;
			}
			if (s.length == 1) { /* in a case of stmt */
				return constructAst(s[0]); /* run on nextStmt */
			}
		case "nextStmt":
			statements.add((Statement) constructAst(s[0])); /* run on stmt / blockStmt */
			return constructAst(s[1]); /* run on stmt* */
		case "blockStmt":
			stmt_list.add(new ArrayList<Statement>());
			constructAst(s[1]); /* run on stmt* */
			stmt_block = new StmtBlock(((Token) s[0].root).line, stmt_list.get(stmt_list.size() - 1));
			stmt_list.remove(stmt_list.size() - 1);
			return stmt_block;
			
			
			
		case "stmt":
			switch (s.length) {
			case 1:
				return constructAst(s[0]); /* run on ifStmt* / whileStmt */
			case 2:
				return constructAst(s[0]); /* run on stmtCall / returnStmt / break / continue / localVar */
			case 4:
				Ref variable = (Ref) constructAst(s[0]); /* run on location */
				expr1 = (Expression) constructAst(s[2]); /* run on expr */
				return new StmtAssignment(variable, expr1);
			}
		case "stmtCall":
			return new StmtCall((Call) constructAst(s[0])); /* run on call */
		case "returnStmt":
			switch (s.length) {
			case 1:
				return new StmtReturn(((Token) s[0].root).line);
			case 2:
				expr1 = (Expression) constructAst(s[1]); /* run on expr */
				return new StmtReturn(((Token) s[0].root).line, expr1);
			}
		case "ifStmt*":
			return constructAst(s[0]); /* run on ifStmt / ifElseStmt */
		case "ifStmt":
			expr1 = (Expression) constructAst(s[2]); /* run on expr */
			operation = (Statement) constructAst(s[4]); /* run on ifOperation */
			return new StmtIf(expr1, operation);
		case "ifElseStmt":
			expr1 = (Expression) constructAst(s[2]); /* run on expr */
			operation = (Statement) constructAst(s[4]); /* run on ifElseOperation */
			elseStmt = (Statement) constructAst(s[6]); /* run on ifOperation */
			return new StmtIf(expr1, operation, elseStmt);
		case "ifElseOperation":
			statementsBlock = new ArrayList<Statement>();
			if (s.length == 1) {
				return constructAst(s[0]); /* run on stmtWOIf */
			} else if (s.length == 3) {
				stmt_list.add(new ArrayList<Statement>());
				constructAst(s[1]); /* run on ifElseBlockStmt* */
				stmt_block = new StmtBlock(((Token) s[0].root).line, stmt_list.get(stmt_list.size() - 1));
				stmt_list.remove(stmt_list.size() - 1);
				return stmt_block;
			}
		case "ifElseBlockStmt*":
			if (s.length == 0) {
				return null;
			} else {
				return constructAst(s[0]); /* run on ifElseNextBlockStmt */
			}
		case "ifElseNextBlockStmt":
			stmt_list.get(stmt_list.size() - 1).add((Statement) constructAst(s[0])); /* run on stmtWOIf */
			return constructAst(s[1]); /* run on ifElseBlockStmt* */
		case "ifOperation":
			if (s.length == 1) {
				return constructAst(s[0]); /* run on stmtBonus1 */
			} else if (s.length == 3) {
				stmt_list.add(new ArrayList<Statement>());
				constructAst(s[1]); /* run on ifBlockStmt* */
				stmt_block = new StmtBlock(((Token) s[0].root).line, stmt_list.get(stmt_list.size() - 1));
				stmt_list.remove(stmt_list.size() - 1);
				return stmt_block;
			}
		case "ifBlockStmt*":
			if (s.length == 0) {
				return null;
			} else {
				return constructAst(s[0]); /* run on ifNextBlockStmt */
			}
		case "ifNextBlockStmt":
			stmt_list.get(stmt_list.size() - 1).add((Statement) constructAst(s[0])); /* run on stmt */
			return constructAst(s[1]); /* run on ifBlockStmt* */			
		case "ifBlockStmt":
			stmt_list.add(new ArrayList<Statement>());
			constructAst(s[1]); /* run on stmt* */
			StmtBlock if_stmt_block = new StmtBlock(((Token) s[0].root).line, stmt_list.get(stmt_list.size() - 1));
			stmt_list.remove(stmt_list.size() - 1);
			return if_stmt_block;
		case "stmtWOIf":
			switch (s.length) {
			case 1:
				return constructAst(s[0]); /* run on whileStmt */
			case 2:
				return constructAst(s[0]); /* run on stmtCall / returnStmt / break / continue / localVar */
			case 4:
				Ref variable = (Ref) constructAst(s[0]); /* run on location */
				expr1 = (Expression) constructAst(s[2]); /* run on expr */
				return new StmtAssignment(variable, expr1);
			case 6:
				dimensions = 0;
				type = (Type) constructAst(s[0]); /* run on type */
				constructAst(s[1]); /* run on array */
				expr1 = (Expression) constructAst(s[4]); /* run on expr */
				return new LocalVariable(type.getLine(), type, ((Token) s[2].root).value, expr1);
			}
		case "stmtBonus1":
			switch (s.length) {
			case 1:
				return constructAst(s[0]); /* run on ifStmt* / whileStmt */
			case 2:
				return constructAst(s[0]); /* run on stmtCall / returnStmt / break / continue */
			case 4:
				Ref variable = (Ref) constructAst(s[0]); /* run on location */
				expr1 = (Expression) constructAst(s[2]); /* run on expr */
				return new StmtAssignment(variable, expr1);
			case 6:
				dimensions = 0;
				type = (Type) constructAst(s[0]); /* run on type */
				constructAst(s[1]); /* run on array */
				expr1 = (Expression) constructAst(s[4]); /* run on expr */
				return new LocalVariable(type.getLine(), type, ((Token) s[2].root).value, expr1);
			}
		case "whileStmt":
			expr1 = (Expression) constructAst(s[2]); /* run on expr */
			operation = (Statement) constructAst(s[4]); /* run on whileOperation */
			return new StmtWhile(expr1, operation);
		case "whileOperation":
			if (s.length == 1) {
				return constructAst(s[0]); /* run on stmt */
			} else if (s.length == 3) {
				stmt_list.add(new ArrayList<Statement>());
				constructAst(s[1]); /* run on whileBlockStmt* */
				stmt_block = new StmtBlock(((Token) s[0].root).line, stmt_list.get(stmt_list.size() - 1));
				stmt_list.remove(stmt_list.size() - 1);
				return stmt_block;
			}
		case "whileBlockStmt*":
			if (s.length == 0) {
				return null;
			} else {
				return constructAst(s[0]); /* run on whileNextBlockStmt */
			}
		case "whileNextBlockStmt":
			stmt_list.get(stmt_list.size() - 1).add((Statement) constructAst(s[0])); /* run on stmt */
			return constructAst(s[1]); /* run on whileBlockStmt* */
		case "whileBlockStmt":
			stmt_list.add(new ArrayList<Statement>());
			constructAst(s[1]); /* run on stmt* */
			StmtBlock while_stmt_block = new StmtBlock(((Token) s[0].root).line, stmt_list.get(stmt_list.size() - 1));
			stmt_list.remove(stmt_list.size() - 1);
			return while_stmt_block;
		case "localVar":
			dimensions = 0;
			type = (Type) constructAst(s[0]); /* run on type */
			constructAst(s[1]); /* run on array */
			if (s.length == 3) {
				return new LocalVariable(type.getLine(), type, ((Token) s[2].root).value);
			} else if (s.length == 5) {
				expr1 = (Expression) constructAst(s[4]); /* run on expr */
				return new LocalVariable(type.getLine(), type, ((Token) s[2].root).value, expr1);
			}
		case "break":
			return new StmtBreak(((Token) r).line);
		case "continue":
			return new StmtContinue(((Token) r).line);
		case "expr":
			if (s.length == 3) {
				expr1 = (Expression) constructAst(s[0]); /* run on expr */
				expr2 = (Expression) constructAst(s[2]); /* run on expr7 */
				return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.LOR, expr2);
			}
			return constructAst(s[0]); /* run on expr7 */
		case "expr7":
			if (s.length == 3) {
				expr1 = (Expression) constructAst(s[0]); /* run on expr7 */
				expr2 = (Expression) constructAst(s[2]); /* run on expr6 */
				return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.LAND, expr2);
			}
			return constructAst(s[0]); /* run on expr6 */
		case "expr6":
			if (s.length == 3) {
				expr1 = (Expression) constructAst(s[0]); /* run on expr6 */
				expr2 = (Expression) constructAst(s[2]); /* run on expr5 */
				if (s[1].root.tag == "==") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.EQUAL, expr2);
				}
				if (s[1].root.tag == "!=") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.NEQUAL, expr2);
				}
			}
			return constructAst(s[0]); /* run on expr5 */
		case "expr5":
			if (s.length == 3) {
				expr1 = (Expression) constructAst(s[0]); /* run on expr5 */
				expr2 = (Expression) constructAst(s[2]); /* run on expr4 */
				if (s[1].root.tag == "<=") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.LTE, expr2);
				}
				if (s[1].root.tag == "<") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.LT, expr2);
				}
				if (s[1].root.tag == ">=") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.GTE, expr2);
				}
				if (s[1].root.tag == ">") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.GT, expr2);
				}
			}
			return constructAst(s[0]); /* run on expr4 */
		case "expr4":
			if (s.length == 3) {
				expr1 = (Expression) constructAst(s[0]); /* run on expr4 */
				expr2 = (Expression) constructAst(s[2]); /* run on expr3 */
				if (s[1].root.tag == "+") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.PLUS, expr2);
				}
				if (s[1].root.tag == "-") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.MINUS, expr2);
				}
			}
			return constructAst(s[0]); /* run on expr3 */
		case "expr3":
			if (s.length == 3) {
				expr1 = (Expression) constructAst(s[0]); /* run on expr3 */
				expr2 = (Expression) constructAst(s[2]); /* run on expr2 */
				if (s[1].root.tag == "*") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.MULTIPLY, expr2);
				}
				if (s[1].root.tag == "/") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.DIVIDE, expr2);
				}
				if (s[1].root.tag == "%") {
					return new BinaryOp(((Token) s[1].root).line, expr1, BinaryOps.MOD, expr2);
				}
			}
			return constructAst(s[0]); /* run on expr2 */
		case "expr2":
			if (s.length == 2) {
				if (s[0].root.tag == "!") {
					expr1 = (Expression) constructAst(s[1]); /* run on expr2 */
					return new UnaryOp(((Token) s[0].root).line, UnaryOps.LNEG, expr1);
				}
				if (s[0].root.tag == "-") {
					negativeInteger = !negativeInteger;
					expr1 = (Expression) constructAst(s[1]); /* run on expr2 */
					UnaryOp unaryOp = new UnaryOp(((Token) s[0].root).line, UnaryOps.UMINUS, expr1);
					negativeInteger = !negativeInteger;
					return unaryOp;
				}
			}
			return constructAst(s[0]); /* run on expr1 */
		case "expr1":
			if (s.length == 6) {
				type = (Type) constructAst(s[1]); /* run on type */
				constructAst(s[2]); /* run on array */
				expr1 = (Expression) constructAst(s[4]); /* run on expr1 */
				return new NewArray(type, expr1);
			}
			if (s.length == 4) {
				return new NewInstance(((Token) s[0].root).line, ((Token) s[1].root).value);
			}
			return constructAst(s[0]); /* run on expr0 */
		case "expr0":
			if (s.length == 3) {
				if (s[0].root.tag == "(" && s[2].root.tag == ")") {
					return constructAst(s[1]); /* run on expr */
				} else if (s[1].root.tag == ".") {
					expr1 = (Expression) constructAst(s[0]); /* run on expr0 */
					return new Length(((Token) s[1].root).line, expr1);
				}
			}
			return constructAst(s[0]); /* run on location / call / this / literal */
		case "location":
			switch (s.length) {
			case 1: /* ID */
				return new RefVariable(((Token) s[0].root).line, ((Token) s[0].root).value);
			case 3:
				expr1 = (Expression) constructAst(s[0]); /* run on expr0 */
				return new RefField(((Token) s[1].root).line, expr1, ((Token) s[2].root).value);
			case 4:
				expr1 = (Expression) constructAst(s[0]); /* run on expr0 */
				expr2 = (Expression) constructAst(s[2]); /* run on expr */
				return new RefArrayElement(expr1, expr2);
			}
			return null;
		case "call":
			return constructAst(s[0]); /* run on staticCall / virtualCall */
		case "staticCall":
			arguments = new ArrayList<Expression>();
			constructAst(s[4]); /* run on expr* */
			StaticCall static_call = new StaticCall(((Token) s[0].root).line, ((Token) s[0].root).value,
					((Token) s[2].root).value, arguments);
			return static_call;
		case "virtualCall":
			VirtualCall virtual_call = null;
			arguments = new ArrayList<Expression>();
			if (s.length == 4) { /* run on ID(expr*) */
				constructAst(s[2]); /* run on expr* */
				virtual_call = new VirtualCall(((Token) s[0].root).line, ((Token) s[0].root).value, arguments);
			} else if (s.length == 6) { /* run on expr.ID(expr*) */
				expr1 = (Expression) constructAst(s[0]); /* run on expr */
				constructAst(s[4]); /* run on expr* */
				virtual_call = new VirtualCall(((Token) s[1].root).line, expr1, ((Token) s[2].root).value, arguments);
			}
			return virtual_call;
		case "expr*":
			if (s.length == 0) { /* there aren't any more expressions */
				return null;
			} else {
				arguments.add((Expression) constructAst(s[0])); /* run on expr */
				return constructAst(s[1]); /* run on moreExpr */
			}
		case "moreExpr":
			if (s.length == 0) {
				return null;
			} else {
				arguments.add((Expression) constructAst(s[1])); /* run on expr */
				return constructAst(s[2]); /* run on moreExpr */
			}
		case "this":
			return new This(((Token) s[0].root).line);
		case "literal":
			Object value = ((Token) s[0].root).value;
			Token token = (Token) s[0].root;
			switch (s[0].root.tag) {
			case "INTEGER":
				long max = 2147483647;
				long parsedNumber = Long.parseLong((String) value);
				if (negativeInteger && (parsedNumber > max + 1)) {
					String format = String.format("%d:%d : syntax error; numeric literal out of range: %s", token.line,
							token.column, value);
					System.out.println(format);
					throw new Error("parse error");
				}
				if (!negativeInteger && (parsedNumber > max)) {
					String format = String.format("%d:%d : syntax error; numeric literal out of range: %s", token.line,
							token.column, value);
					System.out.println(format);
					throw new Error("parse error");
				}
				return new Literal(((Token) s[0].root).line, DataType.INT, value);
			case "STRING":
				value = value.toString().replaceAll("\"", "");
				value = value.toString().replaceAll("\\\\n", "\n");
				value = value.toString().replaceAll("\\\\t", "\t");
				value = value.toString().replaceAll("\\\\", "\\");
				return new Literal(((Token) s[0].root).line, DataType.STRING, value);
			case "true":
				return new Literal(((Token) s[0].root).line, DataType.BOOLEAN, value);
			case "false":
				return new Literal(((Token) s[0].root).line, DataType.BOOLEAN, value);
			case "null":
				return new Literal(((Token) s[0].root).line, DataType.VOID, value);
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
			return constructAst(s[1]); /* run on fieldORMethod* */
		case "field":
			dimensions = 0;
			type = (Type) constructAst(s[0]); /* run on type */
			constructAst(s[1]); /* run on array */
			constructAst(s[3]); /* run on moreIDs* */
			return new DeclField(type, ((Token) s[2].root).value);
		case "moreIDs*":
			if (s.length == 0) { /* there aren't any more IDs */
				return null;
			} else { 
				return constructAst(s[0]); /* run on anotherID */
			}
		case "anotherID":
			fields.add(new DeclField(type, ((Token) s[1].root).value)); /* run on moreIDs* */
			return constructAst(s[2]); /* run on moreIDs* */
		case "type":
			switch (s[0].root.tag) {
			case "int":
				return new PrimitiveType(((Token) s[0].root).line, DataType.INT);
			case "boolean":
				return new PrimitiveType(((Token) s[0].root).line, DataType.BOOLEAN);
			case "string":
				return new PrimitiveType(((Token) s[0].root).line, DataType.STRING);
			case "CLASS_ID":
				return new ClassType(((Token) s[0].root).line, ((Token) s[0].root).value);
			}
		case "array":
			if (s.length > 0) {
				return constructAst(s[0]); /* run on dimension */
			}
			return null;
		case "dimension":
			type.incrementDimension();
			return constructAst(s[2]); /* run on array */
		
		default: /* should never get here */
			throw new Error("internal error (unimplemented ast)");
		}
	}

}
