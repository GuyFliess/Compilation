import java.util.LinkedList;
import java.util.List;

import TypeSafety.ContinueBreakException;
import TypeSafety.FoundException;
import TypeSafety.MainException;
import TypeSafety.TypeSafetyCheckes;
import TypeSafety.TypeSafetyException;
import TypeSafety.TypingRuleException;
import lex.Lexer;
import lex.Token;
import pars.Calc;
import pars.LibCalc;
import scope.ClassScope;
import scope.GlobalScope;
import scopeBuilder.BuildScope;
import scopeBuilder.PrintScope;
import ic.ast.Node;
import ic.ast.PrettyPrint;
import ic.ast.Visitor;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.Program;
import interp.Interpreter;

public class Main {
	public static void main(String[] args) {

		Lexer lex = new Lexer();
		Calc calc = new Calc();
		PrettyPrint printer = new PrettyPrint();
		List<Token> programTokens = new LinkedList<Token>();
		List<Token> tokensForLib = new LinkedList<Token>();
		Node libAst = null;
		try {
			lex.process(args[0], programTokens); // Process regular input
			if (args.length > 1 && args[1].startsWith("-L")) { // Handle Lib
				lex.process(args[1].substring(2), tokensForLib);
				LibCalc calcLib = new LibCalc();
				libAst = calcLib.process(tokensForLib);
				//System.out.println(libAst.accept(printer));
			}
			Node programAst = calc.process(programTokens); // process program
			Program p = (Program) programAst;

			//System.out.println(programAst.accept(printer));
			int interpStartLocation = 1;
			if (args.length > 1) {
				interpStartLocation = 1;

				if (args[1].startsWith("-L")) {
					interpStartLocation = 2;
				}
			}
			if (args.length > interpStartLocation) // this means we have args
													// to interp, so do interp
													// flow
			{
				String class_method = args[1];
				String class_name = class_method.substring(0,
						class_method.indexOf("."));
				String method_name = class_method.substring(class_method
						.indexOf(".") + 1);
				
				String[] arguments = new String[args.length - 2];
				for (int i = 0; i < arguments.length; i++) {
					arguments[i] = args[i + 2];
				}
				
				Interpreter interp = new Interpreter(class_name, method_name, arguments);
				p.accept(interp);

			} else // SymbolTable and typecheck
			{

				//System.out.println("Building symbol table");
				//System.out.println();
				BuildScope scopeBuilder = new BuildScope();
				GlobalScope globalScope = scopeBuilder.MakeScopes((Program) programAst,(DeclClass) libAst);

				

				// printScope.Print(p);
				

			//	System.out.println("type checking");
				TypeSafetyCheckes checks = new TypeSafetyCheckes();
				checks.CheckTypeSafety(p, (DeclClass)libAst , globalScope);
				
				PrintScope printScope = new PrintScope();
				printScope.Print(globalScope);
				
				//System.out.println("All done!");
			}

		} catch (FoundException e) {
			
			//return;
		} catch (TypeSafetyException e) {
			System.out.println(e.lineNum + ": semantic error; " + e.errorMSG);
//			for (StackTraceElement element : e.getStackTrace()) { //TODO comment out
//				System.err.println(element);				
//			}
		}
		catch (Throwable e) {
			System.err.println(e);
			for (StackTraceElement element : e.getStackTrace()) {
				System.err.println(element);
				
			}
			//return;
		}

	}
}
