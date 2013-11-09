package pars;

public class Tree
{
	public Object root;
	public Tree[] subtrees;
	
	public Tree(Object root) 
	{ this(root, new Tree[] {}); }
	
	public Tree(Object root, Tree[] subs)
	{
		this.root = root;
		this.subtrees = subs;
	}
	
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("[ " + root + "");
		for (Tree x : subtrees) 
			sb.append("  " + x);
		sb.append(" ]");
		return sb.toString();
	}
}
