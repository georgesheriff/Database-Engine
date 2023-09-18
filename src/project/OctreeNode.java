package project;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

class OctreeNode implements java.io.Serializable{
	Object minX;
	Object minY;
	Object minZ;
	Object maxX;
	Object maxY;
	Object maxZ;
	Object midX;
	Object midY;
	Object midZ;
	OctreeNode[] children;
	
	ArrayList<Integer> pageIds;
	boolean isDivided;
	int maxEntries;
	ArrayList<Object> clusters;
	ArrayList<Object[]> values;

	public static Object getMiddleValue(Object obj1, Object obj2) {
	    if (obj1 == null || obj2 == null) {
	        throw new IllegalArgumentException("Null values are not allowed.");
	    }
	    if (obj1.getClass() != obj2.getClass()) {
	        throw new IllegalArgumentException("The two objects must be of the same type.");
	    }
	    if (obj1.equals(obj2)) {
	        return obj1;
	    }
	    if (obj1 instanceof Number) {
	        double sum = ((Number) obj1).doubleValue() + ((Number) obj2).doubleValue();
	        return sum / 2;
	    } else if (obj1 instanceof String) {
	        String str1 = (String) obj1;
	        String str2 = (String) obj2;
	        int num1, num2;
	        double num3,num4;
	        try {
	        	num1=Integer.parseInt(str1);
        		num2=Integer.parseInt(str2);
	            
	        } catch (NumberFormatException e) {
	        	try {
	        		num3 = Double.parseDouble(str1);
		            num4 = Double.parseDouble(str2);
	        	}
	        	catch(NumberFormatException e2) {
	        	String str3 = (String) obj1;
		        String str4 = (String) obj2;
		        int length = Math.min(str3.length(), str4.length());
		        StringBuilder sb = new StringBuilder(length);
		        for (int i = 0; i < length; i++) {
		            int charCode1 = str3.charAt(i);
		            int charCode2 = str4.charAt(i);
		            int middleCharCode = (charCode1 + charCode2) / 2;
		            sb.append((char) middleCharCode);
		        }
		        return sb.toString();
	        	}
	        	double avg = (num3 + num4) / 2;
		        return String.valueOf(avg);
	        }
	        int avg = (num1 + num2) / 2;
	        return String.valueOf(avg);
	    } else if (obj1 instanceof Date) {
	        long sum = ((Date) obj1).getTime() + ((Date) obj2).getTime();
	        return new Date(sum / 2);
	    } else {
	        throw new IllegalArgumentException("Unsupported object type.");
	    }
	}


	public OctreeNode(Object minX, Object minY, Object minZ, Object maxX, Object maxY, Object maxZ) {
		super();
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
		midX = getMiddleValue(minX,maxX);
		midY = getMiddleValue(minY,maxY);
		midZ = getMiddleValue(minZ,maxZ);
		isDivided = false;
		Properties properties = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream("DBApp.config");
		} catch (FileNotFoundException ex) {
		}
		try {
			properties.load(is);
			this.maxEntries = Integer.parseInt(properties.getProperty("MaximumEntriesinOctreeNode"));
		} catch (IOException ex) {

		}
	}
//	public static void main(String[] args) {
//		System.out.println(getMiddleValue("0.0","14.0"));
//	}
}
