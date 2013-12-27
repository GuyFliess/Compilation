import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import addressCode.AddressCodeTranslator;
import TypeSafety.FoundException;
import TypeSafety.TypeSafetyCheckes;
import TypeSafety.TypeSafetyException;
import lex.Lexer;
import lex.Token;
import pars.Calc;
import pars.LibCalc;
import scope.GlobalScope;
import scopeBuilder.BuildScope;
import scopeBuilder.PrintScope;
import ic.ast.Node;
import ic.ast.decl.DeclClass;
import ic.ast.decl.Program;
import interp.Interpreter;

public class Main {
	public static void main(String[] args) {

		Lexer lex = new Lexer();
		Calc calc = new Calc();
		Node programAst;
		List<Token> programTokens = new LinkedList<Token>();
		List<Token> tokensForLib = new LinkedList<Token>();
		Node libAst = null;
		try {
			lex.process(args[0], programTokens); /* Process regular input */
			if (args.length > 1 && args[1].startsWith("-L")) { /*
																 * Handle Lib
																 */
				lex.process(args[1].substring(2), tokensForLib);
				LibCalc calcLib = new LibCalc();
				libAst = calcLib.process(tokensForLib);
			}
			programAst = calc.process(programTokens); /* process program */
			BuildScope scopeBuilder = new BuildScope();
			 GlobalScope globalScope = scopeBuilder.MakeScopes(
			 (Program) programAst, (DeclClass) libAst);
			Program p = (Program) programAst;
			TypeSafetyCheckes checks = new TypeSafetyCheckes();
			checks.CheckTypeSafety(p, (DeclClass) libAst, globalScope);
			AddressCodeTranslator ac = new AddressCodeTranslator(globalScope);
			libAst.accept(ac);
			p.accept(ac);
		}			
		 catch (TypeSafetyException e) {
		 System.out.println(e.lineNum + ": semantic error; " + e.errorMSG);
		 for (StackTraceElement element : e.getStackTrace()) {
			 System.err.println(element);
			 }
		 } catch (Throwable e) {
			 System.err.println(e);
			 for (StackTraceElement element : e.getStackTrace()) {
			 System.err.println(element);
			 }
		 }
		

		// int interpStartLocation = 1;
		// if (args.length > 1) {
		// interpStartLocation = 1;
		// if (args[1].startsWith("-L")) {
		// interpStartLocation = 2;
		// }
		// }
		// if (args.length > interpStartLocation) {/* go to the interpreter */
		// String class_method = args[1];
		// String class_name = class_method.substring(0,
		// class_method.indexOf("."));
		// String method_name = class_method.substring(class_method
		// .indexOf(".") + 1);
		// String[] arguments = new String[args.length - 2];
		// for (int i = 0; i < arguments.length; i++) {
		// arguments[i] = args[i + interpStartLocation + 1];
		// }
		// Interpreter interp = new Interpreter(class_name, method_name,
		// arguments);
		// p.accept(interp);
		// } else { /* SymbolTable and typecheck */
		// BuildScope scopeBuilder = new BuildScope();
		// GlobalScope globalScope = scopeBuilder.MakeScopes(
		// (Program) programAst, (DeclClass) libAst);
		// TypeSafetyCheckes checks = new TypeSafetyCheckes();
		// checks.CheckTypeSafety(p, (DeclClass) libAst, globalScope);
		// PrintScope printScope = new PrintScope();
		// printScope.Print(globalScope);
		// }
		// } catch (FoundException e) {
		// } catch (TypeSafetyException e) {
		// System.out.println(e.lineNum + ": semantic error; " + e.errorMSG);
		// } catch (Throwable e) {
		// System.err.println(e);
		// for (StackTraceElement element : e.getStackTrace()) {
		// System.err.println(element);
		// }
		// }

	}
}
