package ic.ast.expr;

import ic.ast.Visitor;

/**
 * AST node for expression in parentheses.
 * 
 */
public class ExpressionBlock extends Expression {

	private Expression expression;

	public Object accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Constructs a new expression in parentheses node.
	 * 
	 * @param expression
	 *            The expression.
	 */
	public ExpressionBlock(Expression expression) {
		super(expression.getLine());
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}

}
