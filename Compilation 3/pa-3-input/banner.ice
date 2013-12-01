
class Banner {

	static string[] triangle(string msg, int height)
	{
		int i = 0;
		string row = msg;
		string[] arr = new string[height];
		
		while (i < height) {
			arr[i] = row;
			row = row + " " + msg;
			i = i + 1;
		}
		
		return arr;
	}
	
}