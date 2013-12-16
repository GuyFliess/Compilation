package TypeSafety;


import java.util.List;


import scope.ClassScope;
import scope.GlobalScope;
import scope.MethodTypeWrapper;
import scope.Scope;
import ic.ast.Visitor;
import ic.ast.decl.ClassType;
import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclLibraryMethod;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Parameter;
import ic.ast.decl.PrimitiveType;
import ic.ast.decl.PrimitiveType.DataType;
import ic.ast.decl.Program;
import ic.ast.decl.Type;
import ic.ast.expr.BinaryOp;
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

public class TypingRules implements Visitor {

	private GlobalScope globalScope;

	public TypingRules(GlobalScope globalScope) {
		this.globalScope = globalScope;
	}

	@Override
	public Object visit(Program program) {
		for (DeclClass icClass : program.getClasses()) {
			icClass.accept(this);
		}
		return null;
	}

	@Override
	public Object visit(DeclClass icClass) {
		for (DeclField field : icClass.getFields())
			field.accept(this);
		for (DeclMethod method : icClass.getMethods())
			method.accept(this);	
		return null;
	}

	@Override
	public Object visit(DeclField field) {
		field.getType().accept(this);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclVirtualMethod method) {
		boolean hasReturn = false;
		for (Parameter formal : method.getFormals())
			formal.accept(this);
		for (Statement statement : method.getStatements())
		{
			hasReturn |= (boolean) statement.accept(this);
		}
		if (!hasReturn && !isOfType(method.getType(),DataType.VOID))
		{
			throw new TypeSafetyException(String.format("non void method %s dosen't return value in every control path ",method.getName()), method.getLine());
		}
//		met
//		MethodTypeWrapper methodWrraper = new MethodTypeWrapper(method.getName(), returnType, parameters, scope)
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(DeclStaticMethod method) {
		boolean hasReturn = false;
		for (Parameter formal : method.getFormals())
			formal.accept(this);
		for (Statement statement : method.getStatements())
			hasReturn |= (boolean)statement.accept(this);
		if (!hasReturn && !isOfType(method.getType(),DataType.VOID))
		{
			throw new TypeSafetyException(String.format("non void method %s dosen't return value in every control path ",method.getName()), method.getLine());
		}
		return null;
	}

	@Override
	public Object visit(DeclLibraryMethod method) {
		// TODO Auto-generated method stub
		method.getType().accept(this);
		for (Parameter formal : method.getFormals())
			formal.accept(this);
		return null;
	}

	@Override
	public Object visit(Parameter formal) {
		// TODO Auto-generated method stub
		formal.getType().accept(this);
		formal.typeAtcheck = formal.getType();
		return false;
	}

	@Override
	public Object visit(PrimitiveType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object visit(ClassType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object visit(StmtAssignment assignment) {
		// TODO Auto-generated method stub
		Ref variable = assignment.getVariable();
		variable.accept(this);
		Expression assignmentValue = assignment.getAssignment();
		assignmentValue.accept(this);
		if (!(isSubTypeOf(assignmentValue.typeAtcheck, variable.typeAtcheck)))
			throw new TypingRuleException(String.format(
					"Invalid assignment of type %s to variable of type %s",
					assignmentValue.typeAtcheck.getDisplayName(),
					variable.typeAtcheck.getDisplayName()),
					assignment.getLine());
		assignment.typeAtcheck = variable.typeAtcheck;
		return false;
	}

	@Override
	public Object visit(StmtCall callStatement) {
		callStatement.getCall().accept(this);


		return false;
	}

	@Override
	public Object visit(StmtReturn returnStatement) {
		Type returnType;
		if (returnStatement.hasValue()) {
			returnStatement.getValue().accept(this);
			returnType = returnStatement.getValue().typeAtcheck;
		} else {
			// void
			returnType = null;
		}
		MethodTypeWrapper method = returnStatement.GetScope()
				.GetMethodWithoutName();
		Type wantedReturntype = method.getReturnType();
		if (wantedReturntype instanceof PrimitiveType) {
			PrimitiveType primitiveType = (PrimitiveType) wantedReturntype;
			if ((primitiveType.getDataType().equals(DataType.VOID))
					&& (returnType != null)) {
				// this is ugly, if the method is void we want
				throw new TypingRuleException(
						"trying to return value in void method",
						returnStatement.getLine());
			}
		}
		if (!(isSubTypeOf(returnType, method.getReturnType()))) {
			throw new TypingRuleException(String.format("Return statement is not of type %s",method.getReturnType().getDisplayName()),
					returnStatement.getLine());
		}
		
		return true;
	}

	@Override
	public Object visit(StmtIf ifStatement) {
		boolean hasReturn = false;
		ifStatement.getCondition().accept(this);
		boolean thenHasReturn = (boolean) ifStatement.getOperation().accept(this);
		if (ifStatement.hasElse())
		{
			hasReturn =((thenHasReturn) && (boolean)  ifStatement.getElseOperation().accept(this));
		}
		if (!isOfType(ifStatement.getCondition().typeAtcheck, DataType.BOOLEAN)) {
			throw new TypingRuleException(
					"Non boolean condition for if statement",
					ifStatement.getLine());
		}

		return hasReturn;
	}

	@Override
	public Object visit(StmtWhile whileStatement) {
		whileStatement.getCondition().accept(this);
		boolean hasreturn = (boolean) whileStatement.getOperation().accept(this);
		if (!isOfType(whileStatement.getCondition().typeAtcheck,
				DataType.BOOLEAN)) {
			throw new TypingRuleException(
					"Non boolean condition for while statement",
					whileStatement.getLine());
		}
		return hasreturn;
	}

	@Override
	public Object visit(StmtBreak breakStatement) {

		return false;
	}

	@Override
	public Object visit(StmtContinue continueStatement) {

		return false;
	}

	@Override
	public Object visit(StmtBlock statementsBlock) {
		boolean hasReturn = false;
		for (Statement statement : statementsBlock.getStatements())
			hasReturn |= (boolean) statement.accept(this);
		return hasReturn;
	}

	@Override
	public Object visit(LocalVariable localVariable) {

		if (localVariable.isInitialized()) {
			Expression initialValue = localVariable.getInitialValue();
			initialValue.accept(this);
			Type typeAtcheck = initialValue.typeAtcheck;
			if (!isSubTypeOf(typeAtcheck, localVariable.getType())) {
				throw new TypingRuleException(String.format(
						"Invalid assignment of type %s to variable of type %s",
						typeAtcheck.getDisplayName(), localVariable.getType()
								.getDisplayName()), localVariable.getLine());
			}
			localVariable.GetScope().AddVar(localVariable.getType(),
					localVariable.getName());
		}
		localVariable.GetScope().AddVar(localVariable.getType(),
				localVariable.getName());
		// localVariable.typeAtcheck = localVariable.getType(); // can remove
		return false;
	}

	@Override
	public Object visit(RefVariable location) {

		Scope scope = location.GetScope();
		Type var ;
		try 
		{
			var = scope.GetVariable(location.getName());
		}
		catch (TypingRuleException e) 
		{
			throw new TypingRuleException(e.errorMSG, location.getLine());
		}
		if (var == null) {
			throw new TypingRuleException(String.format(
					"%s not found in symbol table", location.getName()),
					location.getLine());
		}
		location.typeAtcheck = var;
		return false;
	}

	@Override
	public Object visit(RefField location) {
		location.getObject().accept(this);

		ClassScope classScope = (ClassScope) location.getObject().typeAtcheck
				.GetScope();
		if (!classScope.getFields().containsKey(location.getField())) {
			;
			throw new TypingRuleException(String.format(
					"field %s is not undefined", location.getField()),
					location.getLine());
		}
		Type result = classScope.getFields().get(location.getField());
		location.typeAtcheck = result;
		return false;
	}

	@Override
	public Object visit(RefArrayElement location) {

		location.getArray().accept(this);
		location.getIndex().accept(this);
		Type arrType = location.getArray().typeAtcheck;
		Type resultType;
		if (!(location.getIndex().typeAtcheck instanceof PrimitiveType)) {
			throw new TypingRuleException(
					"Invalid array operation, type %s is not an integer",
					location.getLine());
		}

		PrimitiveType index = (PrimitiveType) location.getIndex().typeAtcheck;
		if (!(index.getDataType().equals(DataType.INT))
				|| !(arrType.getArrayDimension() > 0)) // must be an array
		{
			throw new TypingRuleException(
					"Invalid array operation, type %s is not an array",
					location.getLine());
		}
		if (arrType instanceof PrimitiveType) {
			resultType = new PrimitiveType(-1,
					((PrimitiveType) arrType).getDataType());
		} else if (arrType instanceof ClassType) {
			resultType = new ClassType(-1, ((ClassType) arrType).getClassName());
		} else
			throw new Error("internal error");
		location.typeAtcheck = resultType;// make new type of type of
		return false;
	}

	@Override
	public Object visit(StaticCall call) {
		for (Expression argument : call.getArguments()) {
			argument.accept(this);
		}

		ClassScope classScope = globalScope.getClassScope(call.getClassName());		
		if ((classScope == null) || 
				(!classScope.getStaticMethodScopes().containsKey(call.getMethod()))) {
			throw new TypingRuleException(String.format(
					"Method %s doesn't exist", call.getMethod()),
					call.getLine());
		}
		MethodTypeWrapper methodInClass = classScope.getStaticMethodScopes()
				.get(call.getMethod());
		List<Expression> calledParams = call.getArguments();
		Type[] methodParams = (Type[]) methodInClass.getParameters().toArray(
				new Type[methodInClass.getParameters().size()]);
		if (methodParams.length != calledParams.size())
		{
			throw new TypingRuleException(String.format("Invalid number of arguments for %s.%s", call.getClassName(),call.getMethod()), call.getLine());
		}
			for (int i = 0; i < methodParams.length; i++) {
				if (!isSubTypeOf(calledParams.get(i).typeAtcheck,
						methodParams[i])) {
					throw new TypingRuleException(String.format("Method %s.%s is not applicable for the arguments given",call.getClassName(),call.getMethod()),
							call.getLine());
				}
			}

		call.typeAtcheck = methodInClass.getReturnType();
		return false;
	}

	@Override
	public Object visit(VirtualCall call) {
		String className = "";
		if (call.hasExplicitObject()) {
			call.getObject().accept(this);
		}
		for (Expression argument : call.getArguments())
			argument.accept(this);
		ClassScope classScope; // globalScope.getClassScope(call.getClassName());
		MethodTypeWrapper methodInClass;
		if (call.hasExplicitObject()) {
			Type type = call.getObject().typeAtcheck;			
			if (!(type instanceof ClassType)) {
				throw new TypingRuleException("non class can't have methods",
						call.getLine());
			}
			className = ((ClassType) type).getClassName();
//			Scope scope = type.GetScope();
			Scope scope = globalScope.getClassScope(className);
			if (scope instanceof ClassScope) {
				classScope = (ClassScope) scope;
			} else
				throw new TypingRuleException(String.format(
						"%s is not a method", call.getMethod()), call.getLine());
			methodInClass = classScope.GetMethod(call.getMethod());

			if (methodInClass == null)
				throw new TypingRuleException(String.format(
						"Method %s.%s not found in type table",
						type.getDisplayName(), call.getMethod()),
						call.getLine());
		} else {
			try 
			{
			methodInClass = call.GetScope().GetMethod(call.getMethod());
			}
			catch (TypingRuleException e)
			{
				throw new TypingRuleException(e.errorMSG, call.getLine());
			}
			Scope currentScope =  call.GetScope();
			while (!(currentScope instanceof ClassScope)) currentScope = currentScope.fatherScope;
			className = currentScope.getName();
			if (methodInClass == null)
				throw new TypingRuleException(String.format(
						"%s not found in symbol table", call.getMethod()),
						call.getLine());
		}

		List<Expression> calledParams = call.getArguments();
		Type[] methodParams = (Type[]) methodInClass.getParameters().toArray(
				new Type[methodInClass.getParameters().size()]);
		if (methodParams.length != calledParams.size())
		{
			throw new TypingRuleException(String.format("Invalid number of arguments for %s.%s",className,call.getMethod()), call.getLine());
		}
			for (int i = 0; i < methodParams.length; i++) {
				if (!isSubTypeOf(calledParams.get(i).typeAtcheck,
						methodParams[i])) {
					throw new TypingRuleException(String.format("Method %s.%s is not applicable for the arguments given",className,call.getMethod()),
							call.getLine());
				}
			}

		call.typeAtcheck = methodInClass.getReturnType();

		return false;
	}

	@Override
	public Object visit(This thisExpression) {
		Scope currentScope = thisExpression.GetScope();
		while (!(currentScope instanceof ClassScope)) {
			currentScope = currentScope.fatherScope;
		}
		thisExpression.typeAtcheck = new ClassType(0, currentScope.getName());
		// set the scope of this to be the class scope
		thisExpression.typeAtcheck.SetScope(currentScope);

		return false;
	}

	@Override
	public Object visit(NewInstance newClass) {
		newClass.typeAtcheck = new ClassType(newClass.getLine(),
				newClass.getName());
		return false;
	}

	@Override
	public Object visit(NewArray newArray) {

		newArray.getSize().accept(this);
		newArray.getType().accept(this);
		Type e = newArray.getSize().typeAtcheck;
		if (!(e instanceof PrimitiveType)
				|| !(((PrimitiveType) e).getDataType().equals(DataType.INT))) {
			throw new TypingRuleException("Invalid Array allocation",
					newArray.getLine());
		}

		Type arrType = newArray.getType();
		Type resultType;
		if (arrType instanceof PrimitiveType) {
			resultType = new PrimitiveType(-1,
					((PrimitiveType) arrType).getDataType());
		} else if (arrType instanceof ClassType) {
			resultType = new ClassType(-1, ((ClassType) arrType).getClassName());
		} else
			throw new Error("internal error");
		resultType.incrementDimension();
		newArray.typeAtcheck = resultType;
		return false;
	}

	@Override
	public Object visit(Length length) {
		length.getArray().accept(this);
		Type type1 = length.getArray().typeAtcheck;
		if (type1.getArrayDimension() > 0) {
			length.typeAtcheck = new PrimitiveType(length.getLine(),
					DataType.INT);
		} else {
			throw new TypingRuleException(
					String.format(
							"Invalid length operation, type %s is not an array",
							type1), length.getLine());
		}
		return false;
	}

	@Override
	public Object visit(Literal literal) {
		literal.typeAtcheck = new PrimitiveType(literal.getLine(),
				literal.getType());
		return false;
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		unaryOp.getOperand().accept(this);
		Type type1 = unaryOp.getOperand().typeAtcheck;

		switch (unaryOp.getOperator()) {
		case LNEG: {
			if ((type1 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							DataType.INT)) {
				unaryOp.typeAtcheck = new PrimitiveType(unaryOp.getLine(),
						DataType.INT);
			} else {
				throw new TypingRuleException(
						String.format(
								"invalid logical unary op (%s) on non-integer expression",
								type1.getDisplayName()), unaryOp.getLine());
			}
			break;

		}
		case UMINUS:
			if ((type1 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							DataType.INT)) {
				unaryOp.typeAtcheck = new PrimitiveType(unaryOp.getLine(),
						DataType.BOOLEAN);
			} else {
				throw new TypingRuleException(
						String.format(
								"invalid logical unary op (%s) on non-boolean expression",
								type1.getDisplayName()), unaryOp.getLine());
			}
			break;
		default:
			throw new Error("internal error, unknown enum type");
		}
		return false;
	}

	@Override
	public Object visit(BinaryOp binaryOp) {
		binaryOp.getFirstOperand().accept(this);
		binaryOp.getSecondOperand().accept(this);
		Type type1 = binaryOp.getFirstOperand().typeAtcheck;
		Type type2 = binaryOp.getSecondOperand().typeAtcheck;
		switch (binaryOp.getOperator()) {
		case DIVIDE:
		case MINUS:
		case MOD:
		case MULTIPLY: {
			// int
			if (!(type1 instanceof PrimitiveType)
					|| !(type2 instanceof PrimitiveType)
					|| !((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.INT)
					|| !((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.INT)) {
				throw new TypingRuleException(
						String.format(
								"invalid logical binary op (%s) on non-integer expression",
								binaryOp.getOperator()), binaryOp.getLine());
			}

			binaryOp.typeAtcheck = new PrimitiveType(-1, DataType.INT);
			break;
		}
		case PLUS: {
			if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.INT)
					&& ((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.INT)) {
				binaryOp.typeAtcheck = new PrimitiveType(-1, DataType.INT);
			} else if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.STRING)
					&& ((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.STRING)) {
				binaryOp.typeAtcheck = new PrimitiveType(-1, DataType.INT);
			} else {
				throw new TypingRuleException(
						String.format(
								"invalid logical binary op (%s) on non-integer expression",
								binaryOp.getOperator()), binaryOp.getLine());
			}
			break;
		}

		case GT:
		case GTE:
		case LT:
		case LTE: {
			if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.INT)
					&& ((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.INT)) {
				binaryOp.typeAtcheck = new PrimitiveType(-1, DataType.BOOLEAN);
			} else {
				throw new TypingRuleException(
						String.format(
								"invalid logical binary op (%s) on non-integer expression",
								binaryOp.getOperator()), binaryOp.getLine());
			}
			break;
		}
		case LAND:
		case LOR: {
			if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							PrimitiveType.DataType.BOOLEAN)
					|| ((PrimitiveType) type2).getDataType().equals(
							PrimitiveType.DataType.BOOLEAN)) {
				binaryOp.typeAtcheck = new PrimitiveType(-1, DataType.BOOLEAN);
			}
			break;
		}
		case EQUAL:
		case NEQUAL: {
			// primitive case
			if ((type1 instanceof PrimitiveType)
					&& (type2 instanceof PrimitiveType)
					&& ((PrimitiveType) type1).getDataType().equals(
							((PrimitiveType) type1).getDataType())) {
				binaryOp.typeAtcheck = new PrimitiveType(-1, DataType.BOOLEAN);
			}
			// class case
			else if ((isSubTypeOf(type1, type2)) || isSubTypeOf(type2, type1)) {
				// then one inherits from the other, we're good
				binaryOp.typeAtcheck = new PrimitiveType(-1, DataType.BOOLEAN);
			}

			// if one side is null
			else {
				throw new TypingRuleException(String.format(
						"invalid logical binary op (%s) on ??? expression",
						binaryOp.getOperator()), binaryOp.getLine());
			}
			break;
		}
		default:
			throw new Error("internal error, unknown enum type");

		}
		return false;
	}

	/**
	 * is type1 a sub type of type2 type1 < type2
	 * 
	 * @param call
	 *            Method call expression.
	 */
	public Boolean isSubTypeOf(Type type1, Type type2) {
		if ((type1 instanceof PrimitiveType)
				&& ((PrimitiveType) type1).getDataType().equals(DataType.VOID)) {
			return true;
		}
		if ((type2 instanceof PrimitiveType)
				&& ((PrimitiveType) type2).getDataType().equals(DataType.VOID)) {
			return true;
		}
		if (((type1 instanceof PrimitiveType)
				&& ((type2 instanceof PrimitiveType)) && (((PrimitiveType) type1)
					.getDataType()
				.equals(((PrimitiveType) type2).getDataType()))))
			return true;
		if ((!(type1 instanceof ClassType)) || (!(type2 instanceof ClassType)))
			return false;
		ClassType class1 = (ClassType) type1;
		ClassType class2 = (ClassType) type2;
		if (class1.getClassName().compareTo(class2.getClassName()) == 0) {
			// A<=A
			return true;
		}
		// class1 <= class2
		ClassScope extendingScope = globalScope.GetclassesScopes().get(
				class1.getClassName());
		ClassScope fatherScope = globalScope.GetclassesScopes().get(
				class2.getClassName());
		Scope tempScope = extendingScope;
		while (tempScope instanceof ClassScope) {
			if (tempScope.equals(fatherScope))
				return true;
			tempScope = tempScope.fatherScope;
		}
		extendingScope = globalScope.GetclassesScopes().get(
				class2.getClassName());
		fatherScope = globalScope.GetclassesScopes().get(class1.getClassName());
		tempScope = extendingScope;
		while (tempScope instanceof ClassScope) {
			if (tempScope.equals(fatherScope))
				return true;
			tempScope = tempScope.fatherScope;
		}

		return false;
	}

	/**
	 * check for primitive datatype
	 * 
	 * @param typeAtcheck
	 * @param dataType
	 * @return
	 */
	private boolean isOfType(Type typeAtcheck, DataType dataType) {
		if (typeAtcheck instanceof PrimitiveType) {
			PrimitiveType primitive = (PrimitiveType) typeAtcheck;
			return primitive.getDataType().equals(dataType);
		}
		return false;
	}
}
