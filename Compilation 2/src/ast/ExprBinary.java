package ast;


public abstract class ExprBinary extends Expr
{
	protected Expr operand1;
	protected Expr operand2;
	
	public ExprBinary(Expr a, Expr b)
	{
		this.operand1 = a;
		this.operand2 = b;
	}
	
	public abstract String getOperator();
	public Expr[] getOperands() {
		return new Expr[] { operand1, operand2 };
	}
	
	@Override
	public String toString()
	{
		return "[ ⟨" +  getOperator()  + "⟩  " + operand1 + "  " + operand2 + " ]";
	}
	
	public static class Add extends ExprBinary
	{
		public Add(Expr a, Expr b) { super(a, b); }
		@Override public String getOperator() { return "+"; }
		@Override public double eval() 
		{ return operand1.eval() + operand2.eval(); }

		@Override
		public Object accept(Visitor visitor)
		{
			return visitor.visit(this);
		}
	}
	
	public static class Sub extends ExprBinary
	{
		public Sub(Expr a, Expr b) { super(a, b); }
		@Override public String getOperator() { return "-"; }
		@Override public double eval() 
		{ return operand1.eval() - operand2.eval(); }

		@Override
		public Object accept(Visitor visitor)
		{
			return visitor.visit(this);
		}
	}

	public static class Mul extends ExprBinary
	{
		public Mul(Expr a, Expr b) { super(a, b); }
		@Override public String getOperator() { return "*"; }
		@Override public double eval() 
		{ return operand1.eval() * operand2.eval(); }

		@Override
		public Object accept(Visitor visitor)
		{
			return visitor.visit(this);
		}
	}
	
	public static class Div extends ExprBinary
	{
		public Div(Expr a, Expr b) { super(a, b); }
		@Override public String getOperator() { return "/"; }
		@Override public double eval() 
		{ return operand1.eval() / operand2.eval(); }
	
		@Override
		public Object accept(Visitor visitor)
		{
			return visitor.visit(this);
		}
	}

	public static class Pow extends ExprBinary
	{
		public Pow(Expr a, Expr b) { super(a, b); }
		@Override public String getOperator() { return "^"; }
		@Override public double eval() 
		{ return Math.pow(operand1.eval(), operand2.eval()); }

		@Override
		public Object accept(Visitor visitor)
		{
			return visitor.visit(this);
		}
	}
}
