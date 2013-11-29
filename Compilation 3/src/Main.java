import interp.REPL;
import interp.REPL.RuntimeError;
import ast.Call;
import ast.Expr;
import ast.ExprIf;
import ast.Func;
import ast.Node;
import ast.Num;
import ast.Program;
import ast.RefVar;
import ast.StmtAssign;
import ast.ExprBinary.Mul;
import ast.ExprBinary.Sub;
import ast.ExprBinary.Add;
import ast.ExprBinary.Eq;


public class Main
{
	public static void main(String[] args)
	{
		Node ast = new Program(
			new StmtAssign(new RefVar("w"), new Num(1)),
			new Func("f", new RefVar[] { new RefVar("x") }, 
					new Mul(new RefVar("x"), new RefVar("x"))),
			fib(),
			new Call("fib", new Expr[] { new Num(6) })
		);
		System.out.println(ast);
		REPL interp = new REPL();
		try {
			ast.accept(interp);
		}
		catch (RuntimeError e) {
			System.err.println("Run-time error: " + e.getMessage());
		}
	}
	
	
	static Node fib()
	{
		return
		new Func("fib", new RefVar[] { new RefVar("i") },
				new ExprIf(new Eq(new RefVar("i"), new Num(0)),
							  new Num(1),
							  new ExprIf(new Eq(new RefVar("i"), new Num(1)),
											 new Num(1),
											 new Add(new Call("fib", new Expr[] {
												 new Sub(new RefVar("i"), new Num(1)) }),
														new Call("fib", new Expr[] {
															new Sub(new RefVar("i"),
																	  new Num(2)) })))));		
	}
}
