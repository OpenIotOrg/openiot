package gsn.utils.models.jgarch.util;


public class ArrayUtils {
	
	/**
	 * converts a string like "[1.2 3.4 5.6]" into an array of double. does not allow extra space in the string nor around it.
	 * @param str
	 * @return double array
	 */
	public static double[] stringToDoubleArray(String str) {
		double[] result;
		
		int startIndex = str.indexOf('[');
		int endIndex = str.indexOf(']');
		String arrayStr = str.substring(startIndex+1, endIndex);
		
		
		int pos1 = 0;
		int pos2 = 0;
		int length = 0 ;
		String token;
		while (pos2 != arrayStr.length()) {
			if (arrayStr.indexOf(' ', pos1) == -1) {
				pos2 = arrayStr.length();
			} else {
				pos2 = arrayStr.indexOf(' ', pos1);
			}		    
		    length++;	    
		    pos1 = pos2 + 1;
		}	
		
		pos1 = 0;
		pos2 = 0;
		
		result = new double[length];
		int i=0;
		while (pos2 != arrayStr.length()) {
			if (arrayStr.indexOf(' ', pos1) == -1) {
				pos2 = arrayStr.length();
			} else {
				pos2 = arrayStr.indexOf(' ', pos1);
			}		    
			token = arrayStr.substring(pos1,pos2);			
		    pos1 = pos2 + 1;
		    result[i] = Double.parseDouble(token);
		    i++;
		}
						
		return result;
	}
	
	public static double[] objArrayToDoubleArray(Object [] objArray){
		int size = objArray.length;
		double[] dArray = new double[size];
		
		for (int i=0; i < size; i++) {
			Double d = (Double) objArray[i];
			dArray[i] = (double) d;
		}
		
		return dArray;
	}
}
