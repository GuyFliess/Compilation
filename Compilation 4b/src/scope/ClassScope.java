package scope;

import ic.ast.decl.DeclField;
import ic.ast.decl.DeclLibraryMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Type;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		// if (virtualMethodScopes.containsKey(method))
		// {
		// return virtualMethodScopes.get(method);
		// }
		// else
		// {
		// return fatherScope.GetMethod(method);
		// }
		if (virtualMethodScopes.containsKey(method)) {
			return getVirtualMethod(method);
		} else if (staticMethodScopes.containsKey(method)) {
			return getStaticMethod(method);
		}

		// TODO - throw error
		return null;

	}

	@Override
	public MethodTypeWrapper GetMethodWithoutName() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getVaraibleReg(String name2) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void AddFieldOffset(DeclField field) {
		// TODO Auto-generated method stub
		
	}

	public void CopyOffsetsFromFather() {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * Add new offest, 
	 * if the method is overridden keep the old offset
	 * @param method
	 */
	public void AddMethodOffset(DeclVirtualMethod method) {
		// TODO Auto-generated method stub
		
	}

	public List<MethodTypeWrapper> getAllMethodsAndLabels() {
		return null;
		// TODO Auto-generated method stub
		
	}
}
