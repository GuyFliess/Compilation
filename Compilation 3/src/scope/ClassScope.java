package scope;

import ic.ast.decl.DeclClass;
import ic.ast.decl.DeclField;
import ic.ast.decl.DeclMethod;
import ic.ast.decl.DeclStaticMethod;
import ic.ast.decl.DeclVirtualMethod;
import ic.ast.decl.Type;

import java.util.HashMap;
import java.util.Map;

public class ClassScope {

	// Static
	// for each method we need: name, parametrs types, return type(, Scope??)
	Map<String, MethodTypeWrapper> staticMethodScopes = new HashMap<>();
	// instance
	Map<String, MethodTypeWrapper> virtualMethodScopes = new HashMap<>();
	Map<String, Type> fields = new HashMap<>();

	public void addField(DeclField field) {
		fields.put(field.toString(), field.getType());
	}

	public void addMethod(DeclStaticMethod method) {

	}

	public void addMethod(DeclVirtualMethod method) {

	}

	public void addClass(DeclClass icClass) {
		for (DeclField field : icClass.getFields()) {
			fields.put(field.toString(), field.getType());
		}

		for (DeclMethod method : icClass.getMethods()) {

		}

	}
}
