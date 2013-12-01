

class Math {

	string name; 
    
    void init() {
        name = "gcd";
    }
	
	/* computes gcd of a and b (positive integers) */
	static int gcd(int a, int b) {
		while (b > 0) { 
			int c = a % b;  
			a = b; b = c; 
		}
		return a;
	}
	
}
