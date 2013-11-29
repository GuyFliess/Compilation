package ast;

public abstract class Expr
{
	public abstract double eval();
	
	public abstract Object accept(Visitor visitor);
	
	/** Visitor Pattern */
	public interface Visitor
	{
		public Object visit(Num e);
		public Object visit(ExprBinary.Add e);
		public Object visit(ExprBinary.Sub e);
		public Object visit(ExprBinary.Mul e);
		public Object visit(ExprBinary.Div e);
		public Object visit(ExprBinary.Pow e);
		public Object visit(ExprUnary.Neg e);
	}
}
