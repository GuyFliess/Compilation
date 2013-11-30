package scope;


import ic.ast.decl.DeclField;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Type;

import java.util.HashMap;
import java.util.Map;

public class ClassScope extends Scope {

	public ClassScope(Scope scope, String name) {
		super(scope, name);		
	}

	
	
	// Static
	// for each method we need: name, parametrs types, return type(, Scope??)
	Map<String, MethodTypeWrapper> staticMethodScopes = new HashMap<>();
	// instance
	Map<String, MethodTypeWrapper> virtualMethodScopes = new HashMap<>();
	
	Map<String, Type> fields = new HashMap<>();

//	public void addField(DeclField field) {
//		fields.put(field.toString(), field.getType());
//	}

	
	
	public void addMethod(DeclStaticMethod method, MethodScope scope) {
		MethodTypeWrapper wrapper = new MethodTypeWrapper(method.getName(), method.getType(), method.getFormals(), scope);
		staticMethodScopes.put(method.getName(),wrapper);
	}

	public void addMethod(DeclVirtualMethod method, MethodScope scope) {
		virtualMethodScopes.put(method.getName(),new MethodTypeWrapper(method.getName(), method.getType(), method.getFormals(), scope));
	}

	@Override
	public void AddVar(Object type) {
		DeclField field = (DeclField) type;
		fields.put(field.getName(), field.getType());
		// TODO Auto-generated method stub
	}
	
	public Map<String, Type> getFields()
	{
		return fields;
	}
	
	public Map<String, MethodTypeWrapper> getVirtualMethodScopes()
	{
		return virtualMethodScopes;
	}
	
	public Map<String, MethodTypeWrapper> getStaticMethodScopes()
	{
		return staticMethodScopes;
	}


}
