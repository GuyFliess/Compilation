
class Banner {

	static String[] triangle(String msg, int height)
	{
		int i = 0;
		String row = msg;
		String[] arr = new String[height];
		
		while (i < height) {
			arr[i] = row;
			row = row + " " + msg;
			i = i + 1;
		}
		
		return arr;
	}
	
}