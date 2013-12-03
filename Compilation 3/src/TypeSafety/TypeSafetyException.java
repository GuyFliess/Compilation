package TypeSafety;


public class TypeSafetyException extends Exception {
	
	public String errorMSG;
	public int lineNum;
	
	public TypeSafetyException (String message, int line){
		errorMSG = message;
		lineNum = line;
	}
	
}