package scope;

import ic.ast.decl.DeclField;
import ic.ast.decl.DeclLibraryMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scope.MethodScope.MethodType;
import TypeSafety.TypingRuleException;

public class ClassScope extends Scope {

	public ClassScope(Scope scope, String name) {
		super(scope, name);
	}

	// Static
	// for each method we need: name, parametrs types, return type(, Scope??)
	Map<String, MethodTypeWrapper> staticMethodScopes = new LinkedHashMap<>();
	// instance
	Map<String, MethodTypeWrapper> virtualMethodScopes = new LinkedHashMap<>();

	Map<String, Type> fields = new HashMap<>();

	Map<String, Integer> fieldsOfsset = new HashMap<>();

	private String disptachVecotr = null;

	Map<String, Integer> MethodsOfsset = new HashMap<>();

	public boolean HasSuperNode = false;

	// public void addField(DeclField field) {
	// fields.put(field.toString(), field.getType());
	// }

	public void addMethod(DeclStaticMethod method, MethodScope scope) {
		MethodTypeWrapper wrapper = new MethodTypeWrapper(method.getName(),
				method.getType(), method.getFormals(), scope);
		staticMethodScopes.put(method.getName(), wrapper);
	}

	public void addMethod(DeclVirtualMethod method, MethodScope scope) {
		virtualMethodScopes.put(method.getName(),
				new MethodTypeWrapper(method.getName(), method.getType(),
						method.getFormals(), scope));
	}

	@Override
	public void AddVar(Type type, String name) {
		fields.put(name, type);
	}

	public Map<String, Type> getFields() {
		return fields;
	}
	
	public Type getField(String name)
	{
		Scope currentScope = this; 
	
		Type resultType = null;
		while (currentScope instanceof ClassScope) {
			ClassScope classScope = (ClassScope) currentScope;
			if (classScope.getFields().containsKey(name)) {
					resultType = classScope.getFields().get(name);
			}
			currentScope = currentScope.fatherScope;
		}
		return resultType;
	}

	public Map<String, MethodTypeWrapper> getVirtualMethodScopes() {
		return virtualMethodScopes;
	}

	public Map<String, MethodTypeWrapper> getStaticMethodScopes() {
		return staticMethodScopes;
	}

	// for library method which are just signatures we treat the same as virtual
	public void addMethod(DeclLibraryMethod method, MethodScope methodScope) {
		staticMethodScopes.put(method.getName(),
				new MethodTypeWrapper(method.getName(), method.getType(),
						method.getFormals(), methodScope));

	}

	@Override
	public Type GetVariable(String name) {
		return null;
	}

	public boolean isInScopeOf(Scope otherScope) {
		if (this.equals(otherScope)) {
			return true;
		} else if ((fatherScope == null)
				|| !(fatherScope instanceof ClassScope)) {
			return false;
		} else
			return ((ClassScope) fatherScope).isInScopeOf(otherScope);
	}

	@Override
	public MethodTypeWrapper GetMethod(String method) {
		if (virtualMethodScopes.containsKey(method)) {
			return getVirtualMethod(method);
		} else if (staticMethodScopes.containsKey(method)) {
			return getStaticMethod(method);
		}

		return null;

	}

	@Override
	public MethodTypeWrapper GetMethodWithoutName() {

		return null;
	}

	public MethodTypeWrapper GetMethodStaticOrVirtual(String name) {
		if (virtualMethodScopes.containsKey(name)) {
			return virtualMethodScopes.get(name);
		} else {
			if (staticMethodScopes.containsKey(name)) {
				return staticMethodScopes.get(name);
			}
		}
		return null;
	}

	public MethodTypeWrapper getStaticMethod(String method) {
		if (getStaticMethodScopes().containsKey(method)) {
			return getStaticMethodScopes().get(method);
		} else {
			if ((fatherScope instanceof ClassScope)) {
				return ((ClassScope) fatherScope).getStaticMethod(method);
			}
		}
		return null;
	}

	public MethodTypeWrapper getVirtualMethod(String method) {
		if (getVirtualMethodScopes().containsKey(method)) {
			return getVirtualMethodScopes().get(method);
		} else {
			if ((fatherScope instanceof ClassScope)) {
				return ((ClassScope) fatherScope).getVirtualMethod(method);
			}
		}
		return null;
	}

	@Override
	public void setVaraibleReg(String name, int reg) {
		// Class has no local vars
	}

	@Override
	public Integer getVaraibleReg(String name2) {
		return fieldsOfsset.get(name2);
	}

	public void AddFieldOffset(DeclField field) {
		AddFieldOffset(field.getName(), fieldsOfsset.size() + 1);
	}

	public void AddFieldOffset(String field, Integer offset) {
		fieldsOfsset.put(field, offset);
	}

	public void initOffsets() {
		// fieldsOfsset.put("this", 0);
		if (this.fatherScope instanceof ClassScope) {
			Map<String, Integer> fatherOffsets = ((ClassScope) this.fatherScope).fieldsOfsset;
			for (String field : fatherOffsets.keySet()) {
				AddFieldOffset(field, fatherOffsets.get(field));
			}

			Map<String, Integer> fatherMethods = ((ClassScope) this.fatherScope).MethodsOfsset;
			for (String method : fatherMethods.keySet()) {
				MethodsOfsset.put(method, fatherMethods.get(method));
			}
		}
	}

	/**
	 * Add new offset, if the method is overridden keep the old offset
	 * 
	 * @param method
	 */
	public void AddMethodOffset(DeclVirtualMethod method) {
		if (!MethodsOfsset.containsKey(method.getName())) {
			MethodsOfsset.put(method.getName(), MethodsOfsset.size());
		}
	}

	public MethodTypeWrapper[] getAllMethodsAndLabels() {
		MethodTypeWrapper[] resultList = new MethodTypeWrapper[MethodsOfsset.size()];
		for (String method : MethodsOfsset.keySet()) 
			resultList[MethodsOfsset.get(method)] =  getVirtualMethod(method);
		
		return resultList;
	}


	public int getClassSize() {
		return fieldsOfsset.size() + 1;
	}

	@Override
	public Integer getFieldOffset(String fieldName) {
		return fieldsOfsset.get(fieldName);
	}

	@Override
	public int getMethodOffset(String methodName) {
		return MethodsOfsset.get(methodName);
	}

	public String getDisptachVecotr() {
		return disptachVecotr;
	}

	public void setDisptachVecotr(String disptachVecotr) {
		this.disptachVecotr = disptachVecotr;
	}

}
