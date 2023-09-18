package project;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class Octree implements java.io.Serializable {
	OctreeNode root;
	String colXname;
	String colYname;
	String colZname;
	int octId;
	

	public Octree(Object minX, Object minY, Object minZ, Object maxX, Object maxY, Object maxZ) {
		root = new OctreeNode(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public static void serialize(Octree oct, String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename + ".class");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(oct);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public static Octree deserialize(String filename) {
		try {
			FileInputStream fileIn = new FileInputStream(filename + ".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Octree oct = (Octree) in.readObject();
			in.close();
			fileIn.close();
			return oct;
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
		return null;
	}

	public static int compare(Object obj1, Object obj2) {
	    if (obj1 == null || obj2 == null) {
	        throw new IllegalArgumentException("Cannot compare null objects");
	    }

	    if (obj1 instanceof String && obj2 instanceof String) {
	        try {
	            int int1 = Integer.parseInt((String) obj1);
	            int int2 = Integer.parseInt((String) obj2);
	            return Integer.compare(int1, int2);
	        } catch (NumberFormatException e) {
	            try {
	                double double1 = Double.parseDouble((String) obj1);
	                double double2 = Double.parseDouble((String) obj2);
	                return Double.compare(double1, double2);
	            } catch (NumberFormatException e2) {
	            	 char[] charArray1 = ((String) obj1).toCharArray();
	            	 char[] charArray2 = ((String) obj2).toCharArray();
	                if(charArray1[0]>charArray2[0])
	                	return 1;
	                if(charArray1[0]==charArray2[0])
	                	return 0;
	                else
	                	return -1;
	            }
	        }
	    } else if (obj1 instanceof Integer && obj2 instanceof String) {
	        try {
	            int int2 = Integer.parseInt((String) obj2);
	            return Integer.compare((int) obj1, int2);
	        } catch (NumberFormatException e) {
	            
	            return ((Integer) obj1).toString().compareTo((String) obj2);
	        }
	    } else if (obj1 instanceof String && obj2 instanceof Integer) {
	        try {
	            int int1 = Integer.parseInt((String) obj1);
	            return Integer.compare(int1, (int) obj2);
	        } catch (NumberFormatException e) {
	            
	            return ((String) obj1).compareTo(((Integer) obj2).toString());
	        }
	    } else if (obj1 instanceof Double && obj2 instanceof String) {
	        try {
	            double double2 = Double.parseDouble((String) obj2);
	            return Double.compare((double) obj1, double2);
	        } catch (NumberFormatException e) {
	            
	            return ((Double) obj1).toString().compareTo((String) obj2);
	        }
	    } else if (obj1 instanceof String && obj2 instanceof Double) {
	        try {
	            double double1 = Double.parseDouble((String) obj1);
	            return Double.compare(double1, (double) obj2);
	        } catch (NumberFormatException e) {
	            
	            return ((String) obj1).compareTo(((Double) obj2).toString());
	        }
	    } else if (obj1 instanceof Date && obj2 instanceof Date) {
	        return ((Date) obj1).compareTo((Date) obj2);
	    } else if (obj1 instanceof Integer && obj2 instanceof Integer) {
	        return Integer.compare((int) obj1, (int) obj2);
	    } else if (obj1 instanceof Double && obj2 instanceof Double) {
	        return Double.compare((double) obj1, (double) obj2);
	    } else {
	        throw new IllegalArgumentException("Cannot compare objects of different types");
	    }
	}
	
	public static Object increment(Object obj) {
	    if (obj instanceof Integer) {
	        return (Integer) obj + 1;
	    } else if (obj instanceof Double) {
	        return (Double) obj + 0.1;
	    } else if (obj instanceof String) {
	        char[] charArray = ((String) obj).toCharArray();
	        for (int i = charArray.length - 1; i >= 0; i--) {
	            if (charArray[i] < 'Z') {
	                charArray[i]++;
	                break;
	            } else {
	                charArray[i] = 'A';
	            }
	        }
	        return new String(charArray);
	    } else if (obj instanceof Date) {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime((Date) obj);
	        cal.add(Calendar.DATE, 1);
	        return cal.getTime();
	    } else {
	        return null; 
	    }
	}


	public static boolean correctPlace(Object[] currValue, OctreeNode currChild) {
		//System.out.println(currChild.minZ+" "+currChild.maxZ+" ");
		//System.out.println("Value "+currValue[2]);
		if (compare(currValue[0],currChild.minX) >= 0 && compare(currValue[0],currChild.maxX) <= 0
				&& compare(currValue[1],currChild.minY) >= 0 && compare(currValue[1],currChild.maxY) <= 0
				&& compare(currValue[2],currChild.minZ) >= 0 && compare(currValue[2],currChild.maxZ) <= 0)
			return true;
		return false;
	}

	public void adjustValues(OctreeNode node, OctreeNode[] children,Table table) {
		
		for (int i = 0; i < node.pageIds.size(); i++) {
//			Page currPage=Page.deserialize(table.strTableName+node.pageIds.get(i));
//			for(int j=0;j<currPage.page.size();j++) {
//				Hashtable<String,Object> currTuple=currPage.page.get(j);
//				if(compare(currTuple.get(table.strClusteringKeyColumn),node.clusters.get(i))==0) {
//					for(String colName: currTuple.keySet()) {
//						if(colName.equals(colXname))
//							currValue[0]=currTuple.get(colName);
//						if(colName.equals(colYname))
//							currValue[1]=currTuple.get(colName);
//						if(colName.equals(colZname))
//							currValue[2]=currTuple.get(colName);
//					}
//				}
//			}
//			Page.serialize(currPage, table.strTableName+currPage.id);
			Object[] currValue=node.values.get(i);
			Object oldClus=node.clusters.get(i);
			node.clusters.remove(i);
			node.pageIds.remove(i);
			node.values.remove(i);
			for (int j = 0; j < 8; j++) {
				if (correctPlace(currValue, children[j])) 
					insertHelper(children[j],currValue,oldClus,table);
			}
			i--;
		}
		node.pageIds=null;
		node.clusters=null;
		node.values=null;
	}

	public void insert(Object[] value,Object cluster,Table table) {
		insertHelper(root, value,cluster,table);
	}

	public void insertHelper(OctreeNode node, Object[] value,Object cluster,Table table) {
		if (node.children==null && node.pageIds == null) {
			node.pageIds = new ArrayList<Integer>();
			node.clusters = new ArrayList<Object>();
			int pid=DBApp.pageIDfinder(cluster,table.htblPage, table);
			node.pageIds.add(pid);
			node.clusters.add(cluster);
			node.values = new ArrayList<Object[]>();
			node.values.add(value);
		} else {
			if (node.children==null && node.pageIds.size() < node.maxEntries) {
				int pid=DBApp.pageIDfinder(cluster,table.htblPage, table);
				node.pageIds.add(pid);
				node.clusters.add(cluster);
				node.values.add(value);
			} else {
				if (node.children==null && node.pageIds.size()==node.maxEntries) {
					node.children = new OctreeNode[8];
					Object minX = node.minX;
					Object maxX = node.maxX;
					Object minY = node.minY;
					Object maxY = node.maxY;
					Object minZ = node.minZ; 
					Object maxZ = node.maxZ;
					Object midX= node.midX;
					Object midY= node.midY;
					Object midZ= node.midZ;

					node.children[0] = new OctreeNode(minX, minY, minZ, midX, midY, midZ);
					node.children[1] = new OctreeNode(increment(midX), minY, minZ, maxX, midY, midZ);
					node.children[2] = new OctreeNode(minX, increment(midY), minZ , midX, maxY , midZ);
					node.children[3] = new OctreeNode(minX, minY, increment(midZ) , midX, midY , maxZ);
					node.children[4] = new OctreeNode(increment(midX), increment(midY) , minZ, maxX , maxY, midZ);	
					node.children[5] = new OctreeNode(increment(midX), minY , increment(midZ) , maxX, midY, maxZ);
					node.children[6] = new OctreeNode(minX, increment(midY) , increment(midZ), midX, maxY, maxZ);
					node.children[7] = new OctreeNode(increment(midX), increment(midY) , increment(midZ) , maxX , maxY, maxZ);
					adjustValues(node, node.children,table);
					node.isDivided = true;
				}
				for (int i = 0; i < node.children.length; i++) {
					if (correctPlace(value, node.children[i])) {
						insertHelper(node.children[i], value,cluster,table);
					}
				}
			}
		}
	}
//	public static void main(String[] args) {
//		System.out.print(compare("Moh","M"));
//	}
}
