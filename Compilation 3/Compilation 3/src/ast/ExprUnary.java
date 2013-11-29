package ast;



public abstract class ExprUnary extends Expr
{
	protected Expr operand;
	
	public ExprUnary(Expr a) { operand = a; }
	
	abstract String getOperator();
	public Expr getOperand() { return operand; }
	
	@Override
	public String toString()
	{
		return "[ ⟨" +  getOperator()  + "⟩  " + operand  + " ]";
	}
	
	public static class Neg extends ExprUnary
	{
		public Neg(Expr a) { super(a); }
		@Override String getOperator() { return "-"; }
		@Override public double eval() 
		{ return -operand.eval(); }

		@Override
		public Object accept(Visitor visitor)
		{
			return visitor.visit(this);
		}
	}
}
