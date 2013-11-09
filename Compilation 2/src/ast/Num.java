package ast;


public class Num extends Expr
{
	private double value;
	
	public Num(double value) 
	{
		this.value = value; 
	}
	
	@Override
	public String toString()
	{
		return "[" + value + "]";
	}

	@Override
	public double eval()
	{
		return value;
	}

	@Override
	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}

