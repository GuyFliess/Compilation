package interp;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class ActivationRecord
{
	Map<String, Double> values = new HashMap<>();
	
	@Override
	public String toString()
	{
		StringBuffer bf = new StringBuffer();
		for (Entry<String, Double> kv : values.entrySet()) {
			if (bf.length() > 0) bf.append("  ");
			bf.append(String.format("%s ↦ %s", kv.getKey(), 
					new DecimalFormat("#.##").format(kv.getValue())));
		}
		return "{" + bf.toString() + "}";
	}
}