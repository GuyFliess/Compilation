package TypeSafety;


public class TypeSafetyException extends Error {
	
	public String errorMSG;
	public int lineNum;
	
	public TypeSafetyException (String message, int line){
		errorMSG = message;
		lineNum = line;
	}
	
}