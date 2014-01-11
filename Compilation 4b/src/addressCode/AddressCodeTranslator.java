package addressCode;

import java.util.ArrayList;
import scope.ClassScope;
import scope.GlobalScope;
import scope.MethodScope;
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
import ic.ast.decl.Program;
import ic.ast.expr.BinaryOp;
import ic.ast.expr.BinaryOp.BinaryOps;
import ic.ast.expr.Expression;
import ic.ast.expr.Length;
import ic.ast.expr.Literal;
import ic.ast.expr.NewArray;
import ic.ast.expr.NewInstance;
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

public class AddressCodeTranslator implements Visitor {

	int currentRegister;
	int currentLabel;
	ArrayList<String> instructions;
	ArrayList<String> labels;
	ArrayList<String> whileEndLabels;
	ArrayList<String> whileStartLabels;
	boolean IsAssignmentStatment = false;
	GlobalScope globalScope;

	private String nullError = "Runtime Error: Null pointer dereference!";
	private String NullErrorLabel = ":NullError";
	private String arrayIndexOutOfBounds = "Runtime Error: Array index out of bounds!";
	private String indexOutLabel = ":indexError";
	private String arrayAllocationNegative = "Runtime Error: Array allocation with negative array size!";
	private String arrayAllocNegLabel = ":arrayAllocNeg";
	private String divisionByZero = "Runtime Error: Division by zero!";
	private String divsionByZeroLabel = ":division0";
	private boolean assigning_field;

	public AddressCodeTranslator(GlobalScope globalScope) {
		super();
		this.currentLabel = 0;
		this.currentRegister = 0;
		this.instructions = new ArrayList<>();

		this.labels = new ArrayList<>();
		this.whileEndLabels = new ArrayList<>();
		this.whileStartLabels = new ArrayList<>();
		this.globalScope = globalScope;
	}

	@Override
	public Object visit(Program program) {
		// Error strings
		this.labels = new ArrayList<>();
		addLiteral(NullErrorLabel, nullError);
		addLiteral(indexOutLabel, arrayIndexOutOfBounds);
		addLiteral(arrayAllocNegLabel, arrayAllocationNegative);
		addLiteral(divsionByZeroLabel, divisionByZero);

		instructions.add("\tgoto :main");
		for (DeclClass declClass : program.getClasses()) {

			((ClassScope) declClass.GetScope()).initOffsets();
			String dispathVectorLabel = ":_" + declClass.getName() + "_@DV";

			((ClassScope) declClass.GetScope()).setDisptachVecotr(dispathVectorLabel);
		}
		for (DeclClass declClass : program.getClasses()) {
			declClass.accept(this);
		}
		instructions.add("\tparam 0");
		instructions.add("\tcall :exit");
		for (String instruction : instructions) {
			System.out.println(instruction);
		}
		System.out.println(".data");
		for (String label : labels) {
			System.out.println(label);
		}
		return null;
	}

	private void addLiteral(String label, String literal) {
		labels.add(label);
		labels.add("\t" + literal.length());
		labels.add(String.format("\t\"%s\"", literal));
	}

	@Override
	public Object visit(DeclClass icClass) {
		for (DeclField field : icClass.getFields()) {
			field.accept(this);
		}

		for (DeclMethod method : icClass.getMethods()) {
			if (method instanceof DeclVirtualMethod) {
				DeclVirtualMethod virtualMethod = (DeclVirtualMethod) method;
				ClassScope classScope = (ClassScope) virtualMethod.GetScope().fatherScope;
				classScope.AddMethodOffset(virtualMethod);
			}
		}

		for (DeclMethod method : icClass.getMethods()) {
			method.accept(this);
		}

		labels.add(((ClassScope) icClass.GetScope()).getDisptachVecotr());
		// go over all virutal methods in the class including the inherited
		// methods, and add their dispatch vector (label)
		ClassScope scope = (ClassScope) icClass.GetScope();
		MethodTypeWrapper[] methods;
		while (scope.HasSuperNode) {
			scope = (ClassScope) scope.fatherScope;
			methods = scope.getAllMethodsAndLabels();
			for (MethodTypeWrapper methodTypeWrapper : methods) {
				labels.add("\t(:" + methodTypeWrapper.getLabel() + ")"); // TODO add
																		// ()?
			}
		}
		methods = ((ClassScope) icClass.GetScope())
				.getAllMethodsAndLabels();
		for (MethodTypeWrapper methodTypeWrapper : methods) {
			labels.add("\t(:" + methodTypeWrapper.getLabel() + ")"); // TODO add
																	// ()?
		}
		return null;
	}

	@Override
	public Object visit(DeclField field) {
		((ClassScope) field.GetScope()).AddFieldOffset(field);
		return null;
	}

	@Override
	public Object visit(DeclVirtualMethod method) {

		currentRegister = 1; // 1 because reg 0 should contain "this"
		ClassScope classScope = (ClassScope) method.GetScope().fatherScope;
		// classScope.AddMethodOffset(method);
		String method_label = classScope.getName() + "." + method.getName();
		classScope.GetMethod(method.getName()).setLabel(method_label);
		instructions.add(":" + method_label/* + currentLabel */);
		// (in a case of 2 functions with the same name
		// currentLabel++;
		for (Parameter parameter : method.getFormals()) {
			parameter.accept(this);
		}

		for (Statement statement : method.getStatements()) {
			statement.accept(this);
		}
		if (method.getType().getDisplayName().equals("void")) {
			instructions.add("\tret 0");
		}
		return null;
	}

	@Override
	public Object visit(DeclStaticMethod method) {
		currentRegister = 0;
		ClassScope classScope = (ClassScope) method.GetScope().fatherScope;
		String method_label;
		if (method.getName().equals("main")) {
			method_label = method.getName();
		} else {
			method_label = classScope.getName() + "." + method.getName();
		}
		classScope.GetMethod(method.getName()).setLabel(method_label);
		instructions.add(":" + method_label/* + currentLabel */);
		// (in a case of 2 functions with the same name
		// currentLabel++;
		for (Parameter parameter : method.getFormals()) {
			parameter.accept(this);
		}

		for (Statement statement : method.getStatements()) {
			statement.accept(this);
		}
		if (method.getType().getDisplayName().equals("void")) {
			instructions.add("\tret 0");
		}
		return null;
	}

	@Override
	public Object visit(DeclLibraryMethod method) {
		ClassScope classScope = (ClassScope) method.GetScope().fatherScope;
		classScope.GetMethod(method.getName()).setLabel(method.getName());
		return null;
	}

	@Override
	public Object visit(Parameter formal) {
		formal.getType().accept(this);
		// initialize a register for the formal, update it in the method
		// scope
		((MethodScope) formal.GetScope()).AddParameterReg(formal.getName(),
				this.currentRegister++);
		return null;
	}

	@Override
	public Object visit(PrimitiveType type) {
		// TODO ???
		return null;
	}

	@Override
	public Object visit(ClassType type) {
		// TODO throw error
		return null;
	}

	@Override
	public Object visit(StmtAssignment assignment) {
		instructions.add("#starting assignment statement at "
				+ assignment.getLine());
		// find the register of the right hand side of the assignment
		// (accept)
		// and the register of the left hand side (accept) and move register to
		// register
		// check what to do in case of RefArrayElement
		this.IsAssignmentStatment = true;
		String variable = (String) assignment.getVariable().accept(this);
		this.IsAssignmentStatment = false;
		String value = (String) assignment.getAssignment().accept(this);
		instructions.add("#returned to assignment statement at "
				+ assignment.getLine());
		if ((assignment.getVariable() instanceof RefArrayElement) || assigning_field) {
			instructions.add("\t[]= " + variable + " " + value);
			assigning_field = false;
		} else {
			instructions.add("\t= " + value + " " + variable);
		}

		return null;
	}

	@Override
	public Object visit(StmtCall callStatement) {
		callStatement.getCall().accept(this);
		return null;
	}

	@Override
	public Object visit(StmtReturn returnStatement) {

		if (returnStatement.hasValue()) {
			String value = (String) returnStatement.getValue().accept(this);
			this.instructions.add("\tret " + value);
		} else {
			this.instructions.add("\tret");
		}
		return null;
	}

	@Override
	public Object visit(StmtIf ifStatement) {
		// find the register in which there's the condition result
		// initialize new 2 labels
		int elseLabel = 0;// l = currentLabel++;
		int endLabel;// = currentLabel++;
		int nextLabel;
		String condition = (String) ifStatement.getCondition().accept(this);
		if (ifStatement.hasElse()) {
			elseLabel = currentLabel++;
			endLabel = currentLabel++;
			nextLabel = elseLabel;
		} else {
			endLabel = currentLabel++;
			nextLabel = endLabel;
		}
		instructions.add("\tif! " + condition + " :" + nextLabel);
		ifStatement.getOperation().accept(this);
		if (ifStatement.hasElse()) {
			instructions.add("\tgoto :" + endLabel);
			instructions.add("\t:" + elseLabel);
			ifStatement.getElseOperation().accept(this);
		}
		instructions.add("\t:" + endLabel);
		return null;
	}

	@Override
	public Object visit(StmtWhile whileStatement) {
		// find the register in which there's the condition result
		// initialize new 2 labels
		Integer startLabel = currentLabel++;
		Integer endLabel = currentLabel++;
		whileEndLabels.add(":" + endLabel.toString());
		whileStartLabels.add(": " + startLabel.toString());
		instructions.add("\t:" + startLabel);
		String condition = (String) whileStatement.getCondition().accept(this);

		instructions.add("\tif! " + condition + " :" + endLabel); // if we
																	// need to
																	// get out
																	// of the
																	// while -
		// condition is false (conditionReg == 0)
		whileStatement.getOperation().accept(this);
		instructions.add("\tgoto :" + startLabel);
		instructions.add("\t:" + endLabel);
		whileEndLabels.remove(whileEndLabels.size() - 1);
		whileStartLabels.remove(whileStartLabels.size() - 1);
		return null;
	}

	@Override
	public Object visit(StmtBreak breakStatement) {
		instructions.add("\tgoto "
				+ whileEndLabels.get(whileEndLabels.size() - 1));
		return null;
	}

	@Override
	public Object visit(StmtContinue continueStatement) {
		instructions.add("\tgoto "
				+ whileStartLabels.get(whileStartLabels.size() - 1));
		return null;
	}

	@Override
	public Object visit(StmtBlock statementsBlock) {
		for (Statement statement : statementsBlock.getStatements()) {
			statement.accept(this);
		}
		// iterate over the stmts and call accept(this)
		return null;
	}

	@Override
	public Object visit(LocalVariable localVariable) {
		// initialize a new register and save that variable in the scope,
		// make sure to check if localVariable.isInitialized() and load it to
		// the register
		int varReg = currentRegister++;
		localVariable.GetScope()
				.setVaraibleReg(localVariable.getName(), varReg);

		if (localVariable.isInitialized()) {
			String value = (String) localVariable.getInitialValue()
					.accept(this);
			if (value != null) {
				instructions.add(String.format("\t= %s $%s", value, varReg));
			}
		}

		return null;
	}

	@Override
	public Object visit(RefVariable location) {
		// find the variable in the scope and return its register
		Scope scope = location.GetScope();
		if (scope instanceof MethodScope) {
			if (!((MethodScope) scope).variableExists(location.getName()) && 
					!((MethodScope) scope).getParameters().containsKey(location.getName())) { // this is a field
				assigning_field = true;
				if (this.IsAssignmentStatment) {
					return scope.getFieldOffset(location.getName()).toString();
				}
				String reg = "$" + currentRegister++;
				instructions.add("\t[] " + scope.getFieldOffset(location.getName()).toString() + " " + reg);
				return reg;
			}
		}
		return "$"
				+ location.GetScope().getVaraibleReg(location.getName())
						.toString();
	}

	@Override
	public Object visit(RefField location) {

		String reg = (String) location.getObject().accept(this);
		String resultReg = "$" + currentRegister++;
		instructions.add("\t+ " + reg + " "
				+ location.GetScope().getFieldOffset(location.getField()) + " "
				+ resultReg);

		return resultReg;
	}

	@Override
	public Object visit(RefArrayElement location) {
		instructions.add("#Ref array element statring recuresion at Line "
				+ location.getLine());
		// find the register in the scope and find the address + offset + 1
		Boolean tempIsAssignment = IsAssignmentStatment;
		IsAssignmentStatment = false;
		String arrayAddress = (String) location.getArray().accept(this);
		instructions.add("# array address: " + arrayAddress);
		String arrayOffset = (String) location.getIndex().accept(this);
		instructions.add("# index address: " + arrayOffset);
		String resultReg = "$" + currentRegister++;
		String runTimeErrorsChecksReg = "$" + currentRegister++;
		String firstLabel = ":" + currentLabel++;
		String secondLabel = ":" + currentLabel++;
		String thirdLabel = ":" + currentLabel++;
		arrayAddressPositive(arrayAddress, runTimeErrorsChecksReg, firstLabel);
		instructions.add("#check 0<=i< a.length");
		instructions.add("#check i>0 for line: " + location.getLine());
		checkLengthNonNegative(arrayOffset, runTimeErrorsChecksReg, secondLabel);
		instructions.add("#refArray element actuall " + location.getLine());
		String tempReg = "$" + currentRegister++;
		instructions.add("\t[] " + arrayAddress + " " + tempReg);
		instructions.add("\t< " + arrayOffset + " " + tempReg + " "
				+ runTimeErrorsChecksReg);
		instructions.add("\tif " + runTimeErrorsChecksReg + " " + thirdLabel);
		instructions.add("\tparam " + indexOutLabel);
		instructions.add("\tcall :println");
		instructions.add("\tparam 0");
		instructions.add("\tcall :exit");

		instructions.add(thirdLabel);

		instructions.add("\t+ " + arrayAddress + " " + arrayOffset + " "
				+ resultReg);
		instructions.add("\t+ " + resultReg + " 1 " + resultReg);
		IsAssignmentStatment = tempIsAssignment;
		if (!IsAssignmentStatment) {
			instructions.add("\t[] " + resultReg + " " + resultReg);
		}

		instructions.add("#finished ref array");
		return resultReg;
	}

	private void checkLengthNonNegative(String arrayOffset,
			String runTimeErrorsChecksReg, String secondLabel) {
		instructions
				.add("\t>= " + arrayOffset + " 0 " + runTimeErrorsChecksReg);
		instructions.add("\tif " + runTimeErrorsChecksReg + " " + secondLabel);

		instructions.add("\tparam " + indexOutLabel);
		instructions.add("\tcall :println");
		instructions.add("\tparam 0");
		instructions.add("\tcall :exit");
		instructions.add("\t" + secondLabel);
	}
	
	private void checkLengthNonNegativeInit(String arrayOffset,
			String runTimeErrorsChecksReg, String secondLabel) {
		instructions
				.add("\t>= " + arrayOffset + " 0 " + runTimeErrorsChecksReg);
		instructions.add("\tif " + runTimeErrorsChecksReg + " " + secondLabel);
		//TODO arrayAllocNegLabel
		//instructions.add("\tparam " + indexOutLabel);
		instructions.add("\tparam " + arrayAllocNegLabel);
		instructions.add("\tcall :println");
		instructions.add("\tparam 0");
		instructions.add("\tcall :exit");
		instructions.add("\t" + secondLabel);
	}

	private void arrayAddressPositive(String arrayAddress,
			String runTimeErrorsChecksReg, String firstLabel) {
		instructions.add("\t#check a > 0");
		instructions
				.add("\t> " + arrayAddress + " 0 " + runTimeErrorsChecksReg);
		instructions.add("\tif " + runTimeErrorsChecksReg + " " + firstLabel);
		instructions.add("\tparam " + NullErrorLabel);
		instructions.add("\tcall :println");
		instructions.add("\tparam 0");
		instructions.add("\tcall :exit");
		instructions.add("\t" + firstLabel);
	}

	@Override
	public Object visit(StaticCall call) {
		// find the method details: label and parameters registers, and add
		// param & call instructions
		// add instruction of library call to the method
		ClassScope classScope = globalScope.getClassScope(call.getClassName());
		MethodTypeWrapper methodSignature = classScope.getStaticMethodScopes()
				.get(call.getMethod());
		if (classScope.getName().equals("Library")) {
			methodSignature.setLabel(methodSignature.getName());
		} else {
			methodSignature.setLabel(classScope.getName() + "."
					+ methodSignature.getName());
		}
		java.util.List<String> parameters = new ArrayList<String>();
		for (Expression expr : call.getArguments()) {
			String reg = (String) expr.accept(this);
			parameters.add(reg);
		}
		for (String reg : parameters) {
			instructions.add("\tparam " + reg);
		}
		if (methodSignature.getReturnType().getDisplayName()
				.equalsIgnoreCase("void")) {
			instructions.add("\tcall :" + methodSignature.getLabel());
		} else {
			String regResult = "$" + currentRegister++;
			instructions.add("\tcall :" + methodSignature.getLabel() + " "
					+ regResult);
			return regResult;
		}
		return null;
	}

	@Override
	public Object visit(VirtualCall call) {
		Scope scope = call.GetScope();
		while (!(scope instanceof ClassScope))
			scope = scope.fatherScope;
		ClassScope classScope = (ClassScope) scope;
		// TODO GetStaticMethod instead? this seems to only get from the
		// inheriting class
		MethodTypeWrapper methodSignature = classScope.getStaticMethodScopes()
				.get(call.getMethod());

		if (methodSignature == null) {
			return invokeVirtualMethod(call, classScope);
		}

		if (classScope.getName().equals("Library")) {
			methodSignature.setLabel(methodSignature.getName());
		} else {
			methodSignature.setLabel(classScope.getName() + "."
					+ methodSignature.getName());
		}
		java.util.List<String> parameters = new ArrayList<String>();
		for (Expression expr : call.getArguments()) {
			String reg = (String) expr.accept(this);
			parameters.add(reg);
		}
		for (String reg : parameters) {
			instructions.add("\tparam " + reg);
		}
		if (methodSignature.getReturnType().getDisplayName()
				.equalsIgnoreCase("void")) {
			instructions.add("\tcall :" + methodSignature.getLabel());
		} else {
			String regResult = "$" + currentRegister++;
			instructions.add("\tcall :" + methodSignature.getLabel() + " "
					+ regResult);
			return regResult;
		}
		return null;
	}

	private String invokeVirtualMethod(VirtualCall call, ClassScope classScope) {
		MethodTypeWrapper methodSignature;
		String classReg;
		String methodName = call.getMethod();
		Integer offset;
		if (!call.hasExplicitObject()) {
			methodSignature = classScope.getVirtualMethod(methodName);
			classReg = "$0";

		} else {
			classReg = (String) call.getObject().accept(this);
			String className = ((ClassType) call.getObject().typeAtcheck)
					.getClassName();

			classScope = (ClassScope) globalScope.getClassScope(className);
			methodSignature = classScope.getVirtualMethod(methodName);
		}

		java.util.List<String> parameters = new ArrayList<String>();
		for (Expression expr : call.getArguments()) {
			String reg = (String) expr.accept(this);
			parameters.add(reg);
		}

		// class reg should point to the dispatch vector
		// load it and add the method offset
		offset = classScope.getMethodOffset(methodName);
		String dispatchVectorReg = "$" + currentRegister++;
		instructions.add("\t[] " + classReg + " " + dispatchVectorReg);
		addInstruction("+", dispatchVectorReg, offset.toString(),
				dispatchVectorReg);
		instructions.add("\t[] " + dispatchVectorReg + " " + dispatchVectorReg);
		instructions.add("\tparam " + classReg);

		for (String reg : parameters) {
			instructions.add("\tparam " + reg);
		}
		if (methodSignature.getReturnType().getDisplayName()
				.equalsIgnoreCase("void")) {
			instructions.add("\tcall " + dispatchVectorReg);
			return null;
		} else {
			String regResult = "$" + currentRegister++;
			instructions.add("\tcall " + dispatchVectorReg + " " + regResult);
			return regResult;
		}
	}

	private void addInstruction(String op, String v1, String v2, String target) {
		instructions.add("\t" + op + " " + v1 + " " + v2 + " " + target);

	}

	@Override
	public Object visit(This thisExpression) {
		// this object should always be kept in $0 (this only appears in virtual
		// methods)

		return "$0";
	}

	@Override
	public Object visit(NewInstance newClass) {
		instructions.add("#new class " + newClass.getName());
		String resultReg = "$" + currentRegister++;
		ClassScope classScope = globalScope.getClassScope(newClass.getName());
		instructions.add("\tparam " + classScope.getClassSize());
		instructions.add("\tcall :alloc " + resultReg);
		instructions.add("\t[]= " + resultReg + " "
				+ classScope.getDisptachVecotr());
		return resultReg;
	}

	@Override
	public Object visit(NewArray newArray) {
		// add a new variable to the scope and in the first slot keep the
		// length (allocate a register)
		// call alloc length of array + 1
		// put length in place 0
		// # int[] x = new int[3];
		// = 3 $0
		// param $0
		// call :alloc $1
		// []= $1 $0
		String lengthReg = (String) newArray.getSize().accept(this);
		String tempReg = "$" + currentRegister++;
		instructions.add("+ " + lengthReg + " 1 " + tempReg);
		String addressReg = "$" + currentRegister++;
		checkLengthNonNegativeInit(tempReg, "$" + currentRegister++, ":"
				+ currentLabel++);
		instructions.add("\tparam " + tempReg);

		instructions.add("\tcall :alloc " + addressReg);
		instructions.add("\t[]= " + addressReg + " " + lengthReg);

		return addressReg;
	}

	@Override
	public Object visit(Length length) {
		// find the register in the scope in which the array is stored and
		// return the first slot (load)
		// - if the array is not initialized (null) throw runtimeerror -
		// example 21a_null
		String arrReg = (String) length.getArray().accept(this);
		arrayAddressPositive(arrReg, "$" + currentRegister++, ":"
				+ currentLabel++);
		String resultReg = "$" + currentRegister++;
		instructions.add("\t[] " + arrReg + " " + resultReg);
		return resultReg;
	}

	@Override
	public Object visit(Literal literal) {
		// add instruction of loading the literal into a new register and
		// return the currentRegister(++)
		// int reg = currentRegister++;
		switch (literal.getType()) {
		case INT:
			return literal.getValue().toString();
		case BOOLEAN:
			return literal.getValue().toString().equals("true") ? "1" : "0";
		case STRING:
			String label_name = ":label" + this.currentLabel++;
			labels.add(label_name);
			labels.add("\t"
					+ String.valueOf(literal.getValue().toString().length()));
			labels.add("\t\"" + literal.getValue().toString() + "\"");
			return label_name;
		default:
			break;
		}
		return null;
	}

	@Override
	public Object visit(UnaryOp unaryOp) {
		String op = (String) unaryOp.getOperand().accept(this);
//		if (assigning_field) {
//			instructions.add("\t[] " + op + " $" + currentRegister);
//			op = "$" + currentRegister++;
//			returned_field = false;
//		}
		String current_op = null;
		switch (unaryOp.getOperator()) {
		case LNEG:
			current_op = "!";
			break;
		case UMINUS:
			current_op = "-";
			break;
		}
		Integer reg = currentRegister++;
		instructions.add("\t" + current_op + " " + op + " $" + reg);
		return "$" + reg.toString();
	}

	@Override
	public Object visit(BinaryOp binaryOp) {
		String op1 = (String) binaryOp.getFirstOperand().accept(this);
//		if (assigning_field) {
//			instructions.add("\t[] " + op1 + " $" + currentRegister);
//			op1 = "$" + currentRegister++;
//			returned_field = false;
//		}
		int first_label = 0, second_label = 0;
		Integer reg = currentRegister++;
		if (binaryOp.getOperator() == BinaryOps.LOR) {
			first_label = currentLabel++;
			second_label = currentLabel++;
			instructions.add("\tif! " + op1 + " :" + first_label);
			instructions.add("\t= " + op1 + " $" + reg);
			instructions.add("\tgoto :" + second_label);
			instructions.add("\t:" + first_label);
		} else if (binaryOp.getOperator() == BinaryOps.LAND) {
			first_label = currentLabel++;
			second_label = currentLabel++;
			instructions.add("\tif " + op1 + " :" + first_label);
			instructions.add("\t= " + op1 + " $" + reg);
			instructions.add("\tgoto :" + second_label);
			instructions.add("\t:" + first_label);
		}
		String op2 = (String) binaryOp.getSecondOperand().accept(this);
//		if (assigning_field) {
//			instructions.add("\t[] " + op2 + " $" + currentRegister);
//			op2 = "$" + currentRegister++;
//			returned_field = false;
//		}
		if (binaryOp.getFirstOperand().typeAtcheck.getDisplayName().equals(
				"int")
				&& binaryOp.getSecondOperand().typeAtcheck.getDisplayName()
						.equals("int")) {
			if (binaryOp.getOperator() == BinaryOps.DIVIDE
					|| binaryOp.getOperator() == BinaryOps.MOD) {
				// if $0 :1
				// param :label0
				// call :println
				// param 0
				// call :exit
				// :1
				first_label = currentLabel++;
				second_label = currentLabel++;
				instructions.add("\tif " + op2 + " :" + first_label);
				instructions.add("\tparam :label" + second_label);
				instructions.add("\tcall :println");
				instructions.add("\tparam 0");
				instructions.add("\tcall :exit");
				instructions.add("\t:" + first_label);
				String RuntimeError = "\t\"Runtime Error: Division by zero!\"";
				Integer length = RuntimeError.length() - 3;
				labels.add(":label" + second_label);
				labels.add("\t" + length.toString());
				labels.add(RuntimeError);
			}
		}
		String current_op = null;
		switch (binaryOp.getOperator()) {
		case PLUS:
			current_op = "+";
			break;
		case MINUS:
			current_op = "-";
			break;
		case MULTIPLY:
			current_op = "*";
			break;
		case DIVIDE:
			current_op = "/";
			break;
		case MOD:
			current_op = "%";
			break;
		case LAND:
			current_op = "&&";
			break;
		case LOR:
			current_op = "||";
			break;
		case LT:
			current_op = "<";
			break;
		case LTE:
			current_op = "<=";
			break;
		case GT:
			current_op = ">";
			break;
		case GTE:
			current_op = ">=";
			break;
		case EQUAL:
			current_op = "==";
			break;
		case NEQUAL:
			current_op = "!=";
			break;
		}

		if (binaryOp.getFirstOperand().typeAtcheck.getDisplayName().equals(
				"string")
				&& binaryOp.getSecondOperand().typeAtcheck.getDisplayName()
						.equals("string")) {
			instructions.add("\tparam " + op1);
			instructions.add("\tparam " + op2);
			instructions.add("\tcall :stringCat $" + reg);
		} else {
			instructions.add("\t" + current_op + " " + op1 + " " + op2 + " $"
					+ reg);
		}
		if (binaryOp.getOperator() == BinaryOps.LOR
				|| binaryOp.getOperator() == BinaryOps.LAND) {
			instructions.add("\t:" + second_label);
		}
		return "$" + reg.toString();
	}

}
