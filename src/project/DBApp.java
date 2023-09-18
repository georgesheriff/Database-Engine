package project;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.text.Document;
//import java.io.BufferReader;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DBApp implements Serializable {
	int maxRows;
	static ArrayList<String> tableNames = new ArrayList<String>();

	public void init() {

	}

	// helpers

	public static boolean biggerThan(Object A, Object B) {
		if (A == null || B == null)
			return false;
		if (A instanceof String && B instanceof String) {
			int bigger = ((String) A).compareTo((String) B);
			return bigger > 0;
		}
		if (A instanceof Date && B instanceof Date) {
			int bigger = ((Date) A).compareTo((Date) B);
			return bigger > 0;
		}
		if (A instanceof Integer && B instanceof Integer)
			return (int) A > (int) B;
		if (A instanceof Double && B instanceof Double)
			return (Double) A > (Double) B;

		return false;
	}

	public static ArrayList<Object[]> readFromCSV(String filePath) {
		String line = "";

		ArrayList<Object[]> res = new ArrayList<Object[]>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			while ((line = br.readLine()) != null) {
				Object[] data = line.split(",");
				res.add(data);
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int compare(Object obj1, Object obj2) {
		String newobj;
		  if (obj1 == null || obj2 == null) {
		        throw new IllegalArgumentException("Cannot compare null objects");
		        
		    }  if (obj1 instanceof Date ) {
		    	if (obj2 instanceof String) {
					newobj = (String) obj2;
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					try {
						Date date = dateFormat.parse(newobj);
						return ((Date) obj1).compareTo(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				return ((Date) obj1).compareTo((Date) obj2);}
		    	

		    

		    if (obj1 instanceof String && obj2 instanceof String) {
		        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		        try {
		            Date date1 = dateFormat.parse((String) obj1);
		            Date date2 = dateFormat.parse((String) obj2);
		            return date1.compareTo(date2);
		        } catch (ParseException e) {
		            try {
		                int int1 = Integer.parseInt((String) obj1);
		                int int2 = Integer.parseInt((String) obj2);
		                return Integer.compare(int1, int2);
		            } catch (NumberFormatException e2) {
		                try {
		                    double double1 = Double.parseDouble((String) obj1);
		                    double double2 = Double.parseDouble((String) obj2);
		                    return Double.compare(double1, double2);
		                } catch (NumberFormatException e3) {
		                    char[] charArray1 = ((String) obj1).toCharArray();
		                    char[] charArray2 = ((String) obj2).toCharArray();
		                    if (charArray1[0] > charArray2[0])
		                        return 1;
		                    if (charArray1[0] == charArray2[0])
		                        return 0;
		                    else
		                        return -1;
		                }
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
		} else if (obj1 instanceof LocalDate && obj2 instanceof LocalDate) {
			return ((LocalDate) obj1).compareTo((LocalDate) obj2);
		} else if (obj1 instanceof Integer && obj2 instanceof Integer) {
			return Integer.compare((int) obj1, (int) obj2);
		} else if (obj1 instanceof Double && obj2 instanceof Double) {
			return Double.compare((double) obj1, (double) obj2);
		} else {
			throw new IllegalArgumentException("Cannot compare objects of different types");
		}
	}

	public static int binarySearch(String key, Object PKvalue, Page pageToSearch) {
		int left = 0;
		int right = pageToSearch.maxTupleID;

		while (left <= right) {
			int mid = left + (right - left) / 2;
			Hashtable<String, Object> currRow = pageToSearch.page.get(mid);
			if (compare(PKvalue, currRow.get(key)) == 0) {
				return mid;
			} else if (compare(PKvalue, currRow.get(key)) > 0) {
				left = mid + 1;
			} else {
				right = mid - 1;
			}
		}
		return left;
	}

	public static int binarySearch2(String PKvalue, String key, Page pageToSearch) {
		int left = 0;
		int right = pageToSearch.maxTupleID;

		while (left <= right) {
			int mid = left + (right - left) / 2;
			Hashtable<String, Object> currRow = pageToSearch.page.get(mid);
			if (compare(currRow.get(key), PKvalue) == 0) {
				return mid;
			} else if (compare(currRow.get(key), PKvalue) < 0) {
				left = mid + 1;
			} else {
				right = mid - 1;
			}
		}
		return left;
	}

	public static boolean pageIdFound(Hashtable<Integer, Page> x, int id) {
		for (int pid : x.keySet()) {
			if (pid == id)
				return true;
		}
		return false;
	}

	public static ArrayList<Integer> pagesIds(Hashtable<Integer, Page> pages) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int pageId : pages.keySet()) {
			ids.add(pageId);
		}
		for (int i = 0; i < ids.size() - 1; i++) {
			for (int j = i + 1; j < ids.size(); j++) {
				if (ids.get(i) > ids.get(j)) {
					int temp = ids.get(i);
					ids.set(i, ids.get(j));
					ids.set(j, temp);
				}
			}
		}
		System.out.println("Pages ids in the htblPage: " + ids);
		return ids;
	}

	public static int pageIDfinder(Object primaryKey, Hashtable<Integer, Page> pages, Table table) {

		int left = 0;
		int right = pages.size() - 1;
		String cluster = table.strClusteringKeyColumn;

		ArrayList<Integer> ids = pagesIds(pages);

		while (left < right) {
			int middle = left + (right - left) / 2;
			Page middlePage = Page.deserialize(table.strTableName + pages.get(ids.get(middle)).id);
			if (compare(primaryKey, middlePage.page.get(0).get(cluster)) >= 0
					&& compare(primaryKey, middlePage.page.get(middlePage.maxTupleID).get(cluster)) <= 0) {
				Page.serialize(middlePage, table.strTableName + middlePage.id);
				return middlePage.id;
			}
			if (compare(primaryKey, middlePage.page.get(middlePage.maxTupleID).get(cluster)) > 0)
				left = middle + 1;
			if (compare(primaryKey, middlePage.page.get(0).get(cluster)) < 0)
				right = middle - 1;
			Page.serialize(middlePage, table.strTableName + middlePage.id);
		}
		Page leftPage = Page.deserialize(table.strTableName + pages.get(ids.get(left)).id);
		// System.out.println("Left "+leftPage.id+" "+left);
		if (leftPage.isFull) {
			if (compare(primaryKey, leftPage.page.get(0).get(cluster)) < 0) {
				Page.serialize(leftPage, table.strTableName + leftPage.id);
				if (leftPage.id > 0 && pageIdFound(pages, leftPage.id - 1)
						&& compare(primaryKey, leftPage.page.get(leftPage.maxTupleID).get(cluster)) > 0)
					return leftPage.id - 1;

				else
					return leftPage.id;
			}
			if (compare(primaryKey, leftPage.page.get(leftPage.maxTupleID).get(cluster)) > 0) {
				if (isLastPage(pages, leftPage)) {
					Page.serialize(leftPage, table.strTableName + leftPage.id);
					return leftPage.id;
				} else {
					Page.serialize(leftPage, table.strTableName + leftPage.id);
					return leftPage.id + 1;
				}
			}
		}
		Page.serialize(leftPage, table.strTableName + leftPage.id);
		return leftPage.id;
	}

	public static int pageIDfinder2(Object primaryKey, Hashtable<Integer, Page> pages, Table table) {

		int left = 0;
		int right = pages.size() - 1;
		String cluster = table.strClusteringKeyColumn;

		ArrayList<Integer> ids = pagesIds(pages);

		while (left < right) {
			int middle = left + (right - left) / 2;
			Page middlePage = Page.deserialize(table.strTableName + pages.get(ids.get(middle)).id);
			// print(table);
			if (compare(middlePage.page.get(0).get(cluster), primaryKey) <= 0
					&& compare(middlePage.page.get(middlePage.maxTupleID).get(cluster), primaryKey) >= 0) {

				Page.serialize(middlePage, table.strTableName + middlePage.id);
				return middlePage.id;
			}
			if (compare(middlePage.page.get(middlePage.maxTupleID).get(cluster), primaryKey) < 0)
				left = middle + 1;
			if (compare(middlePage.page.get(0).get(cluster), primaryKey) > 0)
				right = middle - 1;
			Page.serialize(middlePage, table.strTableName + middlePage.id);
		}

		Page leftPage = Page.deserialize(table.strTableName + pages.get(ids.get(left)).id);
		if (leftPage.isFull) {

			if (compare(leftPage.page.get(0).get(cluster), primaryKey) > 0) {
				Page.serialize(leftPage, table.strTableName + leftPage.id);
				if (leftPage.id > 0 && pageIdFound(pages, leftPage.id - 1)
						&& compare(primaryKey, leftPage.page.get(leftPage.maxTupleID).get(cluster)) > 0)
					return leftPage.id - 1;
				else
					return leftPage.id;
			}
			if (compare(leftPage.page.get(leftPage.maxTupleID).get(cluster), primaryKey) < 0) {
				if (isLastPage(pages, leftPage) == true) {
					Page.serialize(leftPage, table.strTableName + leftPage.id);
					return leftPage.id;
				} else {
					Page.serialize(leftPage, table.strTableName + leftPage.id);
					return leftPage.id + 1;
				}
			}
		}
		Page.serialize(leftPage, table.strTableName + leftPage.id);
		return leftPage.id;
	}

	public static boolean checkColumn(Hashtable<String, Object> colNameValue, String column) {
		for (String Name : colNameValue.keySet()) {
			if (column.equals(Name))
				return true;
		}
		return false;
	}

	public static boolean checkType(Object obj, String type) {
		if (type.equals("java.lang.Integer")) {
			if ((obj instanceof Integer) == true)
				return true;
		}
		if (type.equals("java.lang.String")) {
			if ((obj instanceof String) == true)
				return true;
		}
		if (type.equals("java.lang.Double")) {
			if ((obj instanceof Double) == true)
				return true;
		}
		if (type.equals("java.util.Date")) {
			if ((obj instanceof Date) == true)
				return true;
		}
		return false;
	}

	public static void emptyCsvFile(String filePath) throws IOException {
		File file = new File(filePath);
		FileWriter writer = new FileWriter(file);
		writer.write("");
		writer.flush();
		writer.close();
	}

	public static void adjustShiftedTuple(Object cluster, Table table, Hashtable<String, Object> tuple) {
		Octree oct = octFinder(table.octCounter, table.strTableName, tuple);
		adjustShiftedTupleH(cluster, table, oct.root);
		Octree.serialize(oct, table.strTableName + "Oct" + oct.octId);
	}

	public static void adjustShiftedTupleH(Object cluster, Table table, OctreeNode node) {
		if (node.clusters != null) {
			for (int i = 0; i < node.clusters.size(); i++) {
				if (compare(node.clusters.get(i), cluster) == 0)
					node.pageIds.set(i, pageIDfinder(cluster, table.htblPage, table));
			}
		}
	}

	public static void shiftTuples(Hashtable<String, Object> tuple, int tupleId, Page currPage, Table table) {
		if (currPage.isFull) {
			Hashtable<String, Object> lastTuple = currPage.page.get(currPage.maxTupleID);
			if (isLastPage(table.htblPage, currPage)) {
				Page newPage = table.addPage();
				newPage.page.insertElementAt(lastTuple, 0);
				newPage.maxTupleID++;
				Page.serialize(newPage, table.strTableName + newPage.id);
			} else {
				Page nextPage = Page.deserialize(table.strTableName + table.htblPage.get(currPage.id + 1).id);
				shiftTuples(lastTuple, 0, nextPage, table);
				nextPage.page.set(0, lastTuple);
				if (nextPage.maxTupleID < nextPage.maxRows - 1)
					nextPage.maxTupleID++;
				if (nextPage.maxTupleID == nextPage.maxRows - 1)
					nextPage.isFull = true;
				Page.serialize(nextPage, table.strTableName + table.htblPage.get(currPage.id + 1).id);
			}
			currPage.page.remove(lastTuple);
			// System.out.println("Size after removal of last tuple: " +
			// currPage.page.size());
			currPage.maxTupleID--;
			currPage.isFull = false;
			if (tupleId <= currPage.maxTupleID)
				shiftTuples(tuple, tupleId, currPage, table);
			else
				shiftTuples(tuple, tupleId - 1, currPage, table);
			adjustShiftedTuple(lastTuple.get(table.strClusteringKeyColumn), table, lastTuple);
		} else {
			Hashtable<String, Object> currTuple;
			Vector<Hashtable<String, Object>> page = currPage.page;

			for (int i = currPage.maxTupleID; i >= tupleId; i--) {
				currTuple = page.get(i);
				if (i == currPage.maxTupleID) {
					page.insertElementAt(currTuple, currPage.maxTupleID + 1);
				} else
					page.set(i + 1, currTuple);
			}
		}
		if (currPage.maxTupleID == currPage.maxRows - 1)
			currPage.isFull = true;
	}

//	public static void print(Table table) {
//		System.out.println("//////NEW ACTION//////");
//		System.out.println("Table: " + table.strTableName);
//		for (Integer key : table.htblPage.keySet()) {
//			System.out.println("Page " + key + ": ");
//			Page pageObj = Page.deserialize(table.strTableName + table.htblPage.get(key).id);
//			Vector<Hashtable<String, Object>> vector = pageObj.page;
//			for (Hashtable<String, Object> hashtableObj : vector) {
//				for (String subKey : hashtableObj.keySet()) {
//					System.out.print(subKey + ": " + hashtableObj.get(subKey) + " ");
//				}
//				System.out.println();
//			}
//			Page.serialize(pageObj, table.strTableName + table.htblPage.get(key).id);
//		}
//		System.out.println("-------------------------");
//	}

	public static boolean clusterExist(Table table, Object cluster) {
		Page currPage;
		for (int pageId : table.htblPage.keySet()) {
			currPage = Page.deserialize(table.strTableName + pageId);
			for (int i = 0; i <= currPage.maxTupleID; i++) {
				if (compare(cluster, currPage.page.get(i).get(table.strClusteringKeyColumn)) == 0)
					return true;
			}
			Page.serialize(currPage, table.strTableName + pageId);
		}
		return false;
	}

	public static boolean isLastPage(Hashtable<Integer, Page> pages, Page page) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (int pageId : pages.keySet()) {
			ids.add(pageId);
		}
		int max = 0;
		for (int i = 0; i < ids.size(); i++) {
			if (ids.get(i) > max)
				max = ids.get(i);
		}
		if (page.id == max)
			return true;
		return false;
	}

	// End of helper

	public void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType, Hashtable<String, String> htblColNameMin,
			Hashtable<String, String> htblColNameMax) throws DBAppException {

		Table table = new Table(strTableName, strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax);
		File metadataFile = new File("metadata.csv");
		boolean tableExist = false;

		for (int i = 0; i < tableNames.size(); i++) {
			if (tableNames.get(i).equals(strTableName))
				tableExist = true;
		}
		if (tableExist)
			throw new DBAppException("Table is already existing");

		try (PrintWriter pw = new PrintWriter(new FileWriter(metadataFile, true))) {
			boolean validCluster = false;
			for (String Name : table.htblColNameType.keySet()) {
				if (Name.equals(strClusteringKeyColumn))
					validCluster = true;
			}

			if (validCluster == false)
				throw new DBAppException("Cluster key column does not exist");

			System.out.println("Table " + table.strTableName + " is created with columns: ");

			for (String Name : table.htblColNameType.keySet()) {
				StringBuffer ColumnData = new StringBuffer();
				ColumnData.append(table.strTableName + ",");
				ColumnData.append(Name + ",");
				ColumnData.append(table.htblColNameType.get(Name) + ",");
				Boolean isCluster = false;
				if (Name.equals(table.strClusteringKeyColumn))
					isCluster = true;
				ColumnData.append(isCluster + ",");
				ColumnData.append("null" + ",");
				ColumnData.append("null" + ",");
				ColumnData.append(table.htblColNameMin.get(Name) + ",");
				ColumnData.append(table.htblColNameMax.get(Name));
				System.out.println(Name);
				pw.println(ColumnData.toString());
			}

			tableNames.add(strTableName);
			pw.flush();
			Table.serialize(table, strTableName);

		} catch (IOException e) {
			System.out.print("Error writing to file: " + e.getMessage());
		}
	}

	public void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException {
		ArrayList<Object[]> content = readFromCSV("metadata.csv");

		Boolean tableFound = false;
		Vector<Integer> tableIndexes = new Vector<>();

		for (int i = 0; i < content.size(); i++) {
			if (strTableName.equals(content.get(i)[0])) {
				tableFound = true;
				tableIndexes.add(i);
			}
		}

		if (tableFound == false) {
			throw new DBAppException("table does not exist");
		}

		int rowIndex;

		for (int i = 0; i < tableIndexes.size(); i++) {
			rowIndex = tableIndexes.get(i);
			if (checkColumn(htblColNameValue, content.get(rowIndex)[1].toString()) == false)
				throw new DBAppException("columns do not exist or missing columns");
			for (String Name : htblColNameValue.keySet()) {
				if (Name.equals(content.get(rowIndex)[1])) {
					if (!checkType(htblColNameValue.get(Name), content.get(rowIndex)[2].toString()))
						throw new DBAppException("Wrong datatypes");
					if (compare(htblColNameValue.get(Name), content.get(rowIndex)[6]) < 0)
						throw new DBAppException("Value is smaller than minimum allowed value");
					if (compare(htblColNameValue.get(Name), content.get(rowIndex)[7]) > 0)
						throw new DBAppException("Value is bigger than maximum allowed value");
				}
			}
		}

		// fermatta
		Page currPage;
		Table table = Table.deserialize(strTableName);

		if (table.htblPage.size() == 0) {
			currPage = table.addPage();
			currPage.page.insertElementAt(htblColNameValue, 0);
		}

		else {
			String cluster = table.strClusteringKeyColumn;

			if (clusterExist(table, htblColNameValue.get(cluster)))
				throw new DBAppException("Cluster key already exists, use a different cluster key");

			System.out.println("ClusterKey : " + htblColNameValue.get(cluster));
			int currPageId = pageIDfinder(htblColNameValue.get(cluster), table.htblPage, table);
			System.out.println("CurrPage Id: " + currPageId);
			currPage = Page.deserialize(strTableName + currPageId);

			int tupleId = binarySearch(table.strClusteringKeyColumn, htblColNameValue.get(cluster), currPage);
			System.out.println("Tuple Id: " + tupleId);

			if (isLastPage(table.htblPage, currPage)) {
				if (currPage.isFull) {
					if (tupleId > currPage.maxTupleID) {
						currPage = table.addPage();
						currPage.page.insertElementAt(htblColNameValue, 0);
						System.out.println("new Page: " + currPage.id);
					} else {
						shiftTuples(htblColNameValue, tupleId, currPage, table);
						System.out.println("Size in insert " + currPage.page.size());
						currPage.page.set(tupleId, htblColNameValue);
					}
				} else {
					if (tupleId < currPage.maxTupleID) {
						shiftTuples(htblColNameValue, tupleId, currPage, table);
						currPage.page.set(tupleId, htblColNameValue);
					} else
						currPage.page.insertElementAt(htblColNameValue, tupleId);
				}
			} else {
				shiftTuples(htblColNameValue, tupleId, currPage, table);
				currPage.page.set(tupleId, htblColNameValue);
			}
		}

		currPage.maxTupleID++;

		if (currPage.maxTupleID == currPage.maxRows - 1) {
			currPage.isFull = true;
			System.out.println("Page " + currPage.id + " is Full: " + currPage.isFull);
		}

		System.out.println("Max Tuple Id: " + currPage.maxTupleID);
		Page.serialize(currPage, strTableName + currPage.id);

		//print(table);

		octInsert(table.octCounter, table, htblColNameValue, htblColNameValue.get(table.strClusteringKeyColumn));

		Table.serialize(table, strTableName);
	}

	public static void octInsert(int octCounter, Table table, Hashtable<String, Object> htblColNameValue,
			Object cluster) {
		Object[] currTuple = new Object[3];
		Octree oct = octFinder(octCounter, table.strTableName, htblColNameValue);
		if (oct != null) {
			currTuple[0] = htblColNameValue.get(oct.colXname);
			currTuple[1] = htblColNameValue.get(oct.colYname);
			currTuple[2] = htblColNameValue.get(oct.colZname);
			oct.insert(currTuple, cluster, table);
			//printOct(oct, table);
			if (htblColNameValue.size() > 3) {
				Hashtable<String, Object> htblColNameValueTemp = new Hashtable<String, Object>();
				for (String colName : htblColNameValue.keySet()) {
					htblColNameValueTemp.put(colName, htblColNameValue.get(colName));
				}
				htblColNameValueTemp.remove(oct.colXname);
				htblColNameValueTemp.remove(oct.colYname);
				htblColNameValueTemp.remove(oct.colZname);
				Octree.serialize(oct, table.strTableName + "Oct" + oct.octId);
				octInsert(octCounter, table, htblColNameValueTemp, cluster);
			} else {
				Octree.serialize(oct, table.strTableName + "Oct" + oct.octId);
			}
		}
	}

	public static Octree octFinder(int octCounter, String strTableName, Hashtable<String, Object> htblColNameValue) {
		for (int i = 0; i <= octCounter; i++) {
			Octree currOct = Octree.deserialize(strTableName + "Oct" + i);
			for (String colName : htblColNameValue.keySet()) {
				if (colName.equals(currOct.colXname) || colName.equals(currOct.colYname)
						|| colName.equals(currOct.colZname))
					return currOct;
				else
					Octree.serialize(currOct, strTableName + "Oct" + i);
			}
		}
		return null;
	}

	public static boolean checkNode2(OctreeNode node, String cluster, Octree oct) {
		for (int i = 0; i < node.clusters.size(); i++) {
			if (compare(node.clusters.get(i), cluster) == 0)
				return true;
		}
		return false;
	}

	public static Object[] searchUp(Octree oct, Table table, String cluster) {
		Object[] arr = new Object[2];
		arr = searchUpH(oct.root, oct, table, cluster, arr);
		System.out.println(arr[1]);
		return arr;
	}

	public static Object[] searchUpH(OctreeNode node, Octree oct, Table table, String cluster, Object[] objArr) {
		Hashtable<String, Object> tuple = null;
		if (node.pageIds != null) {
			if (checkNode2(node, cluster, oct)) {
				for (int i = 0; i < node.pageIds.size(); i++) {
					Page currPage = Page.deserialize(table.strTableName + node.pageIds.get(i));
					int tupleId = binarySearch2(cluster, table.strClusteringKeyColumn, currPage);
					if (tupleId <= currPage.maxTupleID) {
						tuple = currPage.page.get(tupleId);
						if (tuple != null) {
							objArr[0] = currPage;
							objArr[1] = tupleId;
							System.out.println(objArr[1]);
							return objArr;
						} else {
							Page.serialize(currPage, table.strTableName + currPage.id);
						}
					}
				}
			}
		} else {
			if (node.children != null) {
				for (int i = 0; i < 8; i++) {
					searchUpH(node.children[i], oct, table, cluster, objArr);
				}
			}
		}
		return objArr;
	}

	public void updateTable(String strTableName, String strClusteringKeyValue,
			Hashtable<String, Object> htblColNameValue) throws DBAppException {

		Hashtable<String, Object> tuple = null;
		Page currPage = null;
		int tupleId;

		ArrayList<Object[]> content = readFromCSV("metadata.csv");

		Boolean tableFound = false;
		Vector<Integer> tableIndexes = new Vector<>();

		for (int i = 0; i < content.size(); i++) {
			if (strTableName.equals(content.get(i)[0])) {
				tableFound = true;
				tableIndexes.add(i);
			}
		}

		if (tableFound == false) {
			throw new DBAppException("table does not exist");
		}
		Table table = Table.deserialize(strTableName);

		if (table.htblPage.size() == 0) {
			throw new DBAppException("Table is empty");
		}
		Hashtable<String, Object> clushtbl = new Hashtable<String, Object>();
		clushtbl.put(table.strClusteringKeyColumn, strClusteringKeyValue);
		if (octFinder(table.octCounter, table.strTableName, clushtbl) == null) {
			int currPageId = pageIDfinder2(strClusteringKeyValue, table.htblPage, table);
			System.out.println("CurrPage Id: " + currPageId);
			currPage = Page.deserialize(strTableName + currPageId);

			tupleId = binarySearch2(strClusteringKeyValue, table.strClusteringKeyColumn, currPage);
			System.out.println("Tuple Id: " + tupleId);

			if (tupleId > currPage.maxTupleID)
				throw new DBAppException("Cluster key value does not exist");

		} else {
			Octree oct = octFinder(table.octCounter, table.strTableName, clushtbl);
			Object[] arr = searchUp(oct, table, strClusteringKeyValue);
			currPage = (Page) arr[0];
			tupleId = (Integer) arr[1];
			Octree.serialize(oct, strTableName + "Oct" + oct.octId);
		}
		tuple = currPage.page.get(tupleId);
		if (compare(tuple.get(table.strClusteringKeyColumn), strClusteringKeyValue) != 0 || tuple == null) {
			throw new DBAppException("Cluster key value does not exist");
		}

		int rowIndex;
		boolean[] validColumns = new boolean[htblColNameValue.size()];
		int counter = 0;
		Vector<Integer> columnIndexes = new Vector<>();
		for (String Name : htblColNameValue.keySet()) {
			for (int i = 0; i < tableIndexes.size(); i++) {
				rowIndex = tableIndexes.get(i);
				if (Name.equals(table.strClusteringKeyColumn))
					throw new DBAppException("You cannot update the cluster key");
				if (Name.equals(content.get(rowIndex)[1])) {
					validColumns[counter] = true;
					columnIndexes.add(rowIndex);
				}
			}
			counter++;
		}
		for (int i = 0; i < validColumns.length; i++) {
			if (validColumns[i] == false)
				throw new DBAppException("columns do not exist");
		}

		for (int i = 0; i < columnIndexes.size(); i++) {
			rowIndex = columnIndexes.get(i);
			for (String Name : htblColNameValue.keySet()) {
				if (Name.equals(content.get(rowIndex)[1])) {
					if (!checkType(htblColNameValue.get(Name), content.get(rowIndex)[2].toString()))
						throw new DBAppException("Wrong datatypes");
					if (compare(htblColNameValue.get(Name), content.get(rowIndex)[6]) < 0)
						throw new DBAppException("Value is smaller than minimum allowed value");
					if (compare(htblColNameValue.get(Name), content.get(rowIndex)[7]) > 0)
						throw new DBAppException("Value is bigger than maximum allowed value");
				}
			}
		}
		Hashtable<String, Object> tuple2 = new Hashtable<String, Object>();
		for (String colName : tuple.keySet()) {
			tuple2.put(colName, tuple.get(colName));
		}
		if (octFinder(table.octCounter, table.strTableName, clushtbl) != null) {
			Object ClusterKey = tuple.get(table.strClusteringKeyColumn);
			octDelete(table, tuple2, ClusterKey);
		}

		for (String Name : htblColNameValue.keySet()) {
			for (String tupleName : tuple.keySet()) {
				if (Name.equals(tupleName))
					tuple.put(Name, htblColNameValue.get(Name));
			}
		}
		currPage.page.set(tupleId, tuple);
		Page.serialize(currPage, table.strTableName + currPage.id);

		octInsert(table.octCounter, table, tuple, tuple.get(table.strClusteringKeyColumn));

		//print(table);
		Table.serialize(table, strTableName);
	}

	public static void octDelete(Table table, Hashtable<String, Object> tuple, Object cluster) {
		Octree currOct = octFinder(table.octCounter, table.strTableName, tuple);
		octDeleteH(currOct.root, table, currOct, cluster);

		tuple.remove(currOct.colXname);
		tuple.remove(currOct.colYname);
		tuple.remove(currOct.colZname);

		Octree.serialize(currOct, table.strTableName + "Oct" + currOct.octId);

		if (tuple != null) {
			if (tuple.size() >= 3) {
				octDelete(table, tuple, cluster);
			}
		}
	}

	public static void octDeleteH(OctreeNode node, Table table, Octree oct, Object cluster) {
		if (node.children != null) {
			for (int i = 0; i < 8; i++) {
				octDeleteH(node.children[i], table, oct, cluster);
			}
		} else {
			if (node.pageIds != null) {
				for (int i = 0; i < node.pageIds.size(); i++) {
					Page currPage = Page.deserialize(table.strTableName + node.pageIds.get(i));
					for (int j = 0; j < currPage.page.size(); j++) {
						Hashtable<String, Object> currTuple = currPage.page.get(j);
						if (compare(currTuple.get(table.strClusteringKeyColumn), cluster) == 0
								&& compare(node.clusters.get(i), cluster) == 0) {
							// System.out.println("ana gowa");
							node.pageIds.remove(i);
							node.clusters.remove(i);
							node.values.remove(i);
							return;
						}
						Page.serialize(currPage, table.strTableName + currPage.id);
					}
				}
			}
		}
	}

	public static boolean delete(Hashtable<String, Object> currTuple, Hashtable<String, Object> htblColNameValue) {
		for (String colName : htblColNameValue.keySet()) {
			for (String currColName : currTuple.keySet()) {
				if (colName.equals(currColName)) {
					if (compare(htblColNameValue.get(colName), currTuple.get(currColName)) != 0)
						return false;
				}
			}
		}
		return true;
	}

	public static boolean clusterExists(String cluster, Hashtable<String, Object> htblColNameValue) {
		for (String Name : htblColNameValue.keySet()) {
			if (cluster.equals(Name))
				return true;
		}
		return false;
	}

	public static void printAllOcts(Table table) {
		for (int i = 0; i <= table.octCounter; i++) {
			Octree oct = Octree.deserialize(table.strTableName + "Oct" + i);
			//printOct(oct, table);
			Octree.serialize(oct, table.strTableName + "Oct" + i);
		}
	}

	public void deleteFromTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException {
		ArrayList<Object[]> content = readFromCSV("metadata.csv");

		Boolean tableFound = false;
		Vector<Integer> tableIndexes = new Vector<>();

		for (int i = 0; i < content.size(); i++) {
			if (strTableName.equals(content.get(i)[0])) {
				tableFound = true;
				tableIndexes.add(i);
			}
		}

		if (tableFound == false) {
			throw new DBAppException("table does not exist");
		}

		int rowIndex;

		boolean[] validColumns = new boolean[htblColNameValue.size()];
		int counter = 0;
		Vector<Integer> columnIndexes = new Vector<>();
		for (String Name : htblColNameValue.keySet()) {
			for (int i = 0; i < tableIndexes.size(); i++) {
				rowIndex = tableIndexes.get(i);
				if (Name.equals(content.get(rowIndex)[1])) {
					validColumns[counter] = true;
					columnIndexes.add(rowIndex);
				}
			}
			counter++;
		}
		for (int i = 0; i < validColumns.length; i++) {
			if (validColumns[i] == false)
				throw new DBAppException("columns do not exist");
		}

		for (int i = 0; i < tableIndexes.size(); i++) {
			rowIndex = tableIndexes.get(i);
			for (String Name : htblColNameValue.keySet()) {
				if (Name.equals(content.get(rowIndex)[1])) {
					if (!checkType(htblColNameValue.get(Name), content.get(rowIndex)[2].toString()))
						throw new DBAppException("Wrong datatypes");
					if (compare(htblColNameValue.get(Name), content.get(rowIndex)[6]) < 0)
						throw new DBAppException("Value does not exist");
					if (compare(htblColNameValue.get(Name), content.get(rowIndex)[7]) > 0)
						throw new DBAppException("Value does not exist");
				}
			}
		}

		Table table = Table.deserialize(strTableName);

		if (table.htblPage.size() == 0) {
			throw new DBAppException("Table is empty");
		}

		boolean deleted = false;
		if (octFinder(table.octCounter, table.strTableName, htblColNameValue) == null) {
			if (clusterExists(table.strClusteringKeyColumn, htblColNameValue)) {

				Object strClusteringKeyValue = htblColNameValue.get(table.strClusteringKeyColumn);
				int currPageId = pageIDfinder2(strClusteringKeyValue, table.htblPage, table);
				System.out.println("CurrPage Id: " + currPageId);
				Page currPage = Page.deserialize(strTableName + currPageId);

				int tupleId = binarySearch2(strClusteringKeyValue.toString(), table.strClusteringKeyColumn, currPage);
				System.out.println("Tuple Id: " + tupleId);

				if (tupleId > currPage.maxTupleID) {
					throw new DBAppException("Row does not exist");
				}
				Hashtable<String, Object> tuple = currPage.page.get(tupleId);

				if (delete(tuple, htblColNameValue) == true) {
					Hashtable<String, Object> tuple2 = new Hashtable<String, Object>();
					for (String colName : tuple.keySet()) {
						tuple2.put(colName, tuple.get(colName));
					}
					currPage.maxTupleID--;
					currPage.isFull = false;
					currPage.page.remove(tupleId);
					deleted = true;
					System.out.println("A row has been deleted from Page " + currPage.id + " and has max tuple id "
							+ currPage.maxTupleID);
				}
				if (currPage.maxTupleID < 0) {
					table.htblPage.remove(currPage.id);
					File myFile = new File(strTableName + currPage.id + ".class");
					myFile.deleteOnExit();
					// table.pageCounter--;
				} else
					Page.serialize(currPage, strTableName + currPage.id);
			} else {
				for (int pageId : new ArrayList<>(table.htblPage.keySet())) {
					Page currPage = Page.deserialize(table.strTableName + table.htblPage.get(pageId).id);
					for (int i = 0; i < currPage.page.size(); i++) {
						Hashtable<String, Object> currTuple = currPage.page.get(i);

						if (delete(currTuple, htblColNameValue) == true) {
							Hashtable<String, Object> tuple2 = new Hashtable<String, Object>();
							for (String colName : currTuple.keySet()) {
								tuple2.put(colName, currTuple.get(colName));
							}
							currPage.page.remove(i);
							currPage.maxTupleID--;
							currPage.isFull = false;
							deleted = true;
							i--;
							System.out.println("A row has been deleted from Page " + currPage.id
									+ " and has max tuple id " + currPage.maxTupleID);
						}
					}
					if (currPage.page.size() == 0) {
						table.htblPage.remove(pageId);
						// table.pageCounter--;
						File myFile = new File(strTableName + pageId + ".class");
						myFile.deleteOnExit();
						// pageId--;
					}
					if (!(currPage.maxTupleID < 0))
						Page.serialize(currPage, strTableName + currPage.id);
				}

			}
			if (deleted == false) {
				throw new DBAppException("Row does not exist");
			}
		}

		else {
			Octree oct = octFinder(table.octCounter, table.strTableName, htblColNameValue);
			searchInOct(oct, htblColNameValue, table);
		}

		//print(table);

		printAllOcts(table);
		Table.serialize(table, strTableName);
	}

	public static void searchInOct(Octree oct, Hashtable<String, Object> input, Table table) throws DBAppException {
		ArrayList<Integer> deleted = new ArrayList<Integer>();
		searchInOctH(oct.root, input, oct, table, deleted);
		// System.out.println("Deleted "+deleted);
		if (!deleted.contains(1) || deleted.isEmpty()) {
			throw new DBAppException("Row does not exist");
		}
	}

	public static boolean checkNode(OctreeNode node, Hashtable<String, Object> tuple, Octree oct) {
		for (String colName : tuple.keySet()) {
			if (node.pageIds.size() == 0)
				return false;
			if (colName.equals(oct.colXname)) {
				if (compare(tuple.get(colName), node.maxX) > 0 || compare(tuple.get(colName), node.minX) < 0)
					return false;
			}
			if (colName.equals(oct.colYname)) {
				if (compare(tuple.get(colName), node.maxY) > 0 || compare(tuple.get(colName), node.minY) < 0)
					return false;
			}
			if (colName.equals(oct.colZname)) {
				if (compare(tuple.get(colName), node.maxZ) > 0 || compare(tuple.get(colName), node.minZ) < 0)
					return false;
			}
		}

		return true;
	}

	public static void searchInOctH(OctreeNode node, Hashtable<String, Object> input, Octree oct, Table table,
			ArrayList<Integer> deleted) throws DBAppException {
		Hashtable<String, Object> tuple = null;

		if (node.pageIds != null) {
			// System.out.println("msh b null");
			if (checkNode(node, input, oct)) {
				// System.out.println("lol");
				// System.out.println(node.pageIds.size());
				// System.out.println(node);
				for (int i = 0; i < node.pageIds.size(); i++) {

					// System.out.println("for");
					Page currPage = Page.deserialize(table.strTableName + node.pageIds.get(i));

					if (clusterExists(table.strClusteringKeyColumn, input)) {

						int tupleId = binarySearch2(input.get(table.strClusteringKeyColumn).toString(),
								table.strClusteringKeyColumn, currPage);
						if (tupleId <= currPage.maxTupleID)
							tuple = currPage.page.get(tupleId);
						else
							return;

						if (delete(tuple, input) == true) {
							Hashtable<String, Object> tuple2 = new Hashtable<String, Object>();
							for (String colName : tuple.keySet()) {
								tuple2.put(colName, tuple.get(colName));
							}
							octDelete(table, tuple2, tuple.get(table.strClusteringKeyColumn));
							currPage.maxTupleID--;
							currPage.isFull = false;
							currPage.page.remove(tupleId);
							deleted.add(1);
							System.out.println("A row has been deleted from Page " + currPage.id
									+ " and has max tuple id " + currPage.maxTupleID);
						}
						if (currPage.maxTupleID < 0) {
							table.htblPage.remove(currPage.id);
							File myFile = new File(table.strTableName + currPage.id + ".class");
							myFile.deleteOnExit();
							// table.pageCounter--;
						} else
							Page.serialize(currPage, table.strTableName + currPage.id);

					} else {
						// System.out.println("no cluster");
						for (int j = 0; j < currPage.page.size(); j++) {
							Hashtable<String, Object> currTuple = currPage.page.get(j);
							if (delete(currTuple, input) == true) {
								Hashtable<String, Object> tuple2 = new Hashtable<String, Object>();
								for (String colName : currTuple.keySet()) {
									tuple2.put(colName, currTuple.get(colName));
								}
								octDelete(table, tuple2, currTuple.get(table.strClusteringKeyColumn));
								currPage.page.remove(j);
								currPage.maxTupleID--;
								currPage.isFull = false;
								deleted.add(1);
								j--;
								System.out.println("A row has been deleted from Page " + currPage.id
										+ " and has max tuple id " + currPage.maxTupleID);
							}
						}
						if (currPage.page.size() == 0) {
							table.htblPage.remove(i);
							// table.pageCounter--;
							File myFile = new File(table.strTableName + i + ".class");
							myFile.deleteOnExit();
							// pageId--;
						}
						if (!(currPage.maxTupleID < 0))
							Page.serialize(currPage, table.strTableName + currPage.id);
					}
				}
			}
		} else {
			if (node.children != null) {
				for (int i = 0; i < 8; i++) {
					searchInOctH(node.children[i], input, oct, table, deleted);
				}
			}
		}
	}

	// Select Helpers

	public static boolean indexExists(String tableName, String colName) {
		List<Object[]> metadata = readFromCSV("metadata.csv");

		for (int i = 0; i < metadata.size() - 1; i++) {
			if (metadata.get(i)[0].equals(tableName)) {
				if (metadata.get(i)[1].equals(colName) && metadata.get(i)[5].equals("null")) {
					return false;
				}
			}
		}
		return true; // law fee index
	}

	public boolean goToOct(SQLTerm[] arrSQLTerms, String[] strarrOperators, int x) {

		if (arrSQLTerms.length - x >= 3) {
			for (int i = 0; i <= arrSQLTerms.length - 3; i++) {
				String tableName = arrSQLTerms[x]._strTableName;
				String colName = arrSQLTerms[x]._strColumnName;
				String colName2 = arrSQLTerms[x + 1]._strColumnName;
				String colName3 = arrSQLTerms[x + 2]._strColumnName;
				if (indexExists(tableName, colName) && indexExists(tableName, colName2)
						&& indexExists(tableName, colName3)) {
					if (strarrOperators[x].equals("AND") && strarrOperators[x + 1].equals("AND")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public Vector<Hashtable<String, Object>> computeTerm(SQLTerm term1) {
		Table table = Table.deserialize(term1._strTableName);
		String colName = term1._strColumnName;
		Object colVal = term1._objValue;
		String operator = term1._strOperator;
		Vector<Hashtable<String, Object>> res = new Vector<Hashtable<String, Object>>();

		Hashtable<Integer, Page> pages = table.htblPage;
		for (int i = 0; i < pages.size(); i++) {
			Page currPage = Page.deserialize(term1._strTableName + pages.get(i).id); // 1
			for (int j = 0; j < currPage.page.size(); j++) {
				Hashtable<String, Object> currTuple = currPage.page.get(j);
				if (operator.equals("=")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col),colVal)==0) {
							res.add(currTuple);
						}
					}
				} else if (operator.equals(">")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col), (colVal)) > 0) {
							res.add(currTuple);
						}
					}
				} else if (operator.equals(">=")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col), (colVal)) >= 0) {
							res.add(currTuple);
						}
					}

				} else if (operator.equals("<")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col), (colVal)) < 0) {
							res.add(currTuple);
						}
					}
				} else if (operator.equals("<=")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col), (colVal)) <= 0) {
							res.add(currTuple);
						}
					}
				} else {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && !(compare(currTuple.get(col),colVal)==0)) {
							res.add(currTuple);
						}
					}

				}
			}
			Page.serialize(currPage, term1._strTableName + currPage.id);
		}

		Table.serialize(table, term1._strTableName);

		return res;

	}

	public Vector<Hashtable<String, Object>> executeAND(Vector<Hashtable<String, Object>> set1,
			Vector<Hashtable<String, Object>> set2) {
		Vector<Hashtable<String, Object>> accumResult = new Vector<Hashtable<String, Object>>();
		for (int i = 0; i < set1.size(); i++) {
			Hashtable<String, Object> currTuple = set1.get(i);
			for (int j = 0; j < set2.size(); j++) {
				Hashtable<String, Object> currTuple2 = set2.get(j);
				if (currTuple.equals(currTuple2)) {
					accumResult.add(currTuple);
				}

			}
		}
		return accumResult;

	}

	public Vector<Hashtable<String, Object>> executeOR(Vector<Hashtable<String, Object>> set1,
			Vector<Hashtable<String, Object>> set2, Table table) {
		String clusteringKey = table.strClusteringKeyColumn;
		Vector<Object> IDs = new Vector<Object>();

		for (int j = 0; j < set1.size(); j++) {
			Hashtable<String, Object> Tuple = set1.get(j);
			Object id = Tuple.get(clusteringKey);
			IDs.add(id);
		}

		for (int i = 0; i < set2.size(); i++) {
			Hashtable<String, Object> currTuple = set2.get(i);
			if (!IDs.contains(currTuple.get(clusteringKey))) {
				set1.add(currTuple);

			}

		}

		return set1;
	}

	public Vector<Hashtable<String, Object>> executeXOR(Vector<Hashtable<String, Object>> set1,
			Vector<Hashtable<String, Object>> set2) {
		Vector<Hashtable<String, Object>> accumRes = new Vector<Hashtable<String, Object>>();
		boolean flag = false;
		for (int i = 0; i < set1.size(); i++) {
			Hashtable<String, Object> currTuple1 = set1.get(i);
			flag = false;
			for (int j = 0; j < set2.size(); j++) {
				Hashtable<String, Object> currTuple2 = set2.get(j);
				if (currTuple2.equals(currTuple1)) {
					flag = true;
					break;
				}

			}
			if (flag == false) {
				accumRes.add(currTuple1);
			}
		}
		boolean flag2 = false;
		for (int i = 0; i < set2.size(); i++) {
			Hashtable<String, Object> currTuple1 = set2.get(i);
			flag2 = false;
			for (int j = 0; j < set1.size(); j++) {
				Hashtable<String, Object> currTuple2 = set1.get(j);
				if (currTuple2.equals(currTuple1)) {
					flag2 = true;
					break;
				}

			}
			if (flag2 == false) {
				accumRes.add(currTuple1);
			}
		}
		return accumRes;

	}

	public int octIDfinder(int octCounter, String colName, String tableName) {
		for (int i = 0; i <= octCounter; i++) {
			Octree currOct = Octree.deserialize(tableName + "Oct" + i);
			if (colName.equals(currOct.colXname) || colName.equals(currOct.colYname)
					|| colName.equals(currOct.colZname)) {
				return currOct.octId;
			} else {
				Octree.serialize(currOct, tableName + "Oct" + i);

			}
		}

		return -1;
	}

	public int colNumber(Octree oct, String colName) {
		String[] indexColName = new String[3];
		String col1 = oct.colXname;
		String col2 = oct.colYname;
		String col3 = oct.colZname;

		indexColName[0] = col1;
		indexColName[1] = col2;
		indexColName[2] = col3;

		for (int i = 0; i < indexColName.length; i++) {
			if (indexColName[i].equals(colName)) {
				return i;
			}
		}
		return -1;
	}

	public Vector<Integer> search(SQLTerm term) {
		String colName = term._strColumnName;
		String tableName = term._strTableName;
		Object value = term._objValue;
		String operator = term._strOperator;

		Table table = Table.deserialize(tableName);

		//int pageID = -1;
		int octID = octIDfinder(table.octCounter, colName, tableName);

		Octree octree = Octree.deserialize(tableName + "Oct" + octID);
		int colNum = colNumber(octree, colName);

		OctreeNode root = octree.root;
		Vector<Integer> result = searchHelper(colName, value, colNum, root, table, operator,octree);
		Octree.serialize(octree, tableName + "Oct" + octID);

		Table.serialize(table, tableName);

		return result;
	}
	
	public Vector<Integer> searchFilter(Vector<Integer> pageIds){
		Vector<Integer> Res = new Vector<>();
		if(pageIds.size()>0) {
		Res.add(pageIds.get(0));
		for(int i=1; i< pageIds.size();i++) {
			if(!Res.contains(pageIds.get(i))) {
				Res.add(pageIds.get(i));
			}
		}
		}
		return Res;
	}

	public Vector<Integer> searchHelper(String colName, Object value, int colNum, OctreeNode node, Table table,
			String operator, Octree oct) {
		Vector<Integer> result = new Vector<>();
		if (node.children == null) {
			if (node.pageIds != null) {
				for (int i = 0; i < node.pageIds.size(); i++) {
					Object[] values = new Object[3];
					Page currPage = Page.deserialize(table.strTableName + node.pageIds.get(i));
					for (int j = 0; j < currPage.page.size(); j++) {
						Hashtable<String, Object> currTuple = currPage.page.get(j);
						if (compare(currTuple.get(table.strClusteringKeyColumn), node.clusters.get(i)) == 0) {
							for (String Name : currTuple.keySet()) {
								if (Name.equals(oct.colXname))
									values[0] = currTuple.get(colName);
								if (Name.equals(oct.colYname))
									values[1] = currTuple.get(colName);
								if (Name.equals(oct.colZname))
									values[2] = currTuple.get(colName);
							}
						}
						Page.serialize(currPage, table.strTableName + currPage.id);
					}
					if (operator.equals("=")) {
						if (compare(values[colNum],value)==0) {
							int pageID = node.pageIds.get(i);
							result.add(pageID);
						}
					} else if (operator.equals(">")) {
						if (compare(values[colNum], value) > 0) {
							int pageID = node.pageIds.get(i);
							result.add(pageID);
						}
					} else if (operator.equals(">=")) {
						if (compare(values[colNum], value) >= 0) {
							int pageID = node.pageIds.get(i);
							result.add(pageID);
						}
					} else if (operator.equals("<")) {
						if (compare(values[colNum], value) < 0) {
							int pageID = node.pageIds.get(i);
							result.add(pageID);
						}
					} else if (operator.equals("<=")) {
						if (compare(values[colNum], value) <= 0) {
							int pageID = node.pageIds.get(i);
							result.add(pageID);
						}
					} else {
						if (operator.equals("!=")) {
							if (!values[colNum].equals(value)) {
								int pageID = node.pageIds.get(i);
								result.add(pageID);
							}
						}

					}
				}
			}
			return result;
		} else {
			for (OctreeNode child : node.children) {
				Vector<Integer> childRes = searchHelper(colName, value, colNum, child, table, operator,oct);
				result.addAll(childRes);
			}
		}
		result = searchFilter(result);
		return result;
	}

	public Vector<Hashtable<String, Object>> computeTermIndex(Vector<Integer> pageIDs, SQLTerm term) {
		String colName = term._strColumnName;
		String tableName = term._strTableName;
		Object value = term._objValue;
		String operator = term._strOperator;
		Vector<Hashtable<String, Object>> accumRes = new Vector<Hashtable<String, Object>>();

		for (int i = 0; i < pageIDs.size(); i++) {
			Page page = Page.deserialize(tableName + pageIDs.get(i));
			for (int j = 0; j < page.page.size(); j++) {
				Hashtable<String, Object> currTuple = page.page.get(j);
				if (operator.equals("=")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare( currTuple.get(col),value)==0) {
							accumRes.add(currTuple);
						}
					}
				} else if (operator.equals(">")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col), (value)) > 0) {
							accumRes.add(currTuple);
						}
					}
				} else if (operator.equals(">=")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col), (value)) >= 0) {
							accumRes.add(currTuple);
						}
					}

				} else if (operator.equals("<")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col), (value)) < 0) {
							accumRes.add(currTuple);
						}
					}
				} else if (operator.equals("<=")) {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && compare(currTuple.get(col), (value)) <= 0) {
							accumRes.add(currTuple);
						}
					}
				} else {
					for (String col : currTuple.keySet()) {
						if (col.equals(colName) && !(compare(currTuple.get(col),value)==0)) {
							accumRes.add(currTuple);
						}
					}

				}
			Page.serialize(page, tableName + page.id);
		}
		}

		return accumRes;

	}

	public Vector<Hashtable<String, Object>> executeANDIn(Vector<Hashtable<String, Object>> set1, SQLTerm nextTerm) {
		String colName = nextTerm._strColumnName;
		Object value = nextTerm._objValue;
		Vector<Hashtable<String, Object>> accumRes = new Vector<Hashtable<String, Object>>();
		for (int i = 0; i < set1.size(); i++) {
			Hashtable<String, Object> currTuple = set1.get(i);
			for (String col : currTuple.keySet()) {
				if (col.equals(colName) && compare(currTuple.get(col),value)==0) {
					accumRes.add(currTuple);

				}
			}
		}
		return accumRes;

	}

	public static boolean correctColName(SQLTerm term, Table table) {
		ArrayList<Object[]> metadata = readFromCSV("metadata.csv");
		String tableName = table.strTableName;
		String colName = term._strColumnName;

		boolean flag = false;

		for (int i = 0; i < metadata.size(); i++) {
			if (tableName.equals(metadata.get(i)[0]) && colName.equals(metadata.get(i)[1])) {
				flag = true;
			}
		}
		return flag;
	}

	public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators) throws DBAppException {

		ArrayList<Object[]> content = readFromCSV("metadata.csv");
		String tableName = arrSQLTerms[0]._strTableName;

		// check table name for 1st term
		Boolean tableFound = false;

		for (int i = 0; i < content.size(); i++) {
			if (tableName.equals(content.get(i)[0])) {
				tableFound = true;
			}
		}

		if (tableFound == false) {
			throw new DBAppException("table does not exist");
		}

		// check all of them match
		for (int s = 0; s < arrSQLTerms.length; s++) {
			if (!arrSQLTerms[s]._strTableName.equals(tableName)) {
				throw new DBAppException("wrong table name");
			}
		}

		Table table = Table.deserialize(tableName);

		// validate col names in sql terms
		for (int j = 0; j < arrSQLTerms.length; j++) {
			if (!correctColName(arrSQLTerms[j], table)) {
				throw new DBAppException("Wrong column name");
			}
		}

		// validate operators
		for (int k = 0; k < strarrOperators.length; k++) {
			if (!(strarrOperators[k].equals("AND") || strarrOperators[k].equals("OR")
					|| strarrOperators[k].equals("XOR"))) {
				throw new DBAppException("Invalid logical operator(s)");
			}
		}

		for (int p = 0; p < arrSQLTerms.length; p++) {
			if (!(arrSQLTerms[p]._strOperator.equals("=") || arrSQLTerms[p]._strOperator.equals("!=")
					|| arrSQLTerms[p]._strOperator.equals(">") || arrSQLTerms[p]._strOperator.equals(">=")
					|| arrSQLTerms[p]._strOperator.equals("<") || arrSQLTerms[p]._strOperator.equals("<="))) {
				throw new DBAppException("Invalid arithmetic operator(s)");
			}
		}

		String operator;
		Vector<Hashtable<String, Object>> accumResult = new Vector<Hashtable<String, Object>>();
		Vector<Hashtable<String, Object>> accumResultInd = new Vector<Hashtable<String, Object>>();

		// only one SQLTerm
		if (arrSQLTerms.length == 1) {
			Vector<Hashtable<String, Object>> res = computeTerm(arrSQLTerms[0]);
			System.out.println(res);
			Table.serialize(table, tableName);
			return new ResultSet(res.iterator());

		}

		// more than one SQLTerm
		for (int i = 0; i <= arrSQLTerms.length - 1; i++) {
			String colName = arrSQLTerms[i]._strColumnName;

			if (indexExists(tableName, colName) && goToOct(arrSQLTerms, strarrOperators, i)) {
				if (i == 0) {
					System.out.println("index");
					Vector<Integer> firstTermIDs = search(arrSQLTerms[i]);
					 Vector<Integer> secondTermIDs = search(arrSQLTerms[i+1]);
					 Vector<Integer> thirdTermIDs = search(arrSQLTerms[i+2]);
					 Vector<Hashtable<String, Object>> compute1 = computeTermIndex(firstTermIDs, arrSQLTerms[i]);
					 Vector<Hashtable<String, Object>> compute2 = computeTermIndex(secondTermIDs, arrSQLTerms[i+1]);
					 Vector<Hashtable<String, Object>> compute3 = computeTermIndex(thirdTermIDs, arrSQLTerms[i+2]);
					accumResult = executeAND(compute1, compute2);
					accumResult = executeAND(accumResult, compute3);
					i += 2;
					// System.out.println("m");

				} else {
					//System.out.println("index");

					Vector<Integer> firstTermIDs = search(arrSQLTerms[i]);
					Vector<Integer> secondTermIDs = search(arrSQLTerms[i+1]);
					 Vector<Integer> thirdTermIDs = search(arrSQLTerms[i+2]);
					 
					 Vector<Hashtable<String, Object>> compute1 = computeTermIndex(firstTermIDs, arrSQLTerms[i]);
					 Vector<Hashtable<String, Object>> compute2 = computeTermIndex(secondTermIDs, arrSQLTerms[i+1]);
					 Vector<Hashtable<String, Object>> compute3 = computeTermIndex(thirdTermIDs, arrSQLTerms[i+2]);

					 accumResultInd = executeAND(compute1, compute2);
					 accumResultInd = executeAND(accumResult, compute3);
					 operator = strarrOperators[i - 1];

					switch (operator) {
					case "AND":
						Vector<Integer> andTerm = search(arrSQLTerms[i-1]);

						Vector<Hashtable<String, Object>> result = computeTermIndex(andTerm, arrSQLTerms[i-1]);

						accumResult = executeAND(accumResultInd, result);
						break;
					case "OR":
						accumResult = executeOR(accumResult, accumResultInd, table);
						break;

					case "XOR":
						accumResult = executeXOR(accumResult, accumResultInd);
						break;

					}
					i += 2;
				}

			}

			// NO INDEX CASE

			else {
				//System.out.println("no index");
				if (i == 0) {

					Vector<Hashtable<String, Object>> res = computeTerm(arrSQLTerms[0]);
					if (!indexExists(tableName, arrSQLTerms[1]._strColumnName)) {
						System.out.println("no index");
						Vector<Hashtable<String, Object>> res2 = computeTerm(arrSQLTerms[1]);
						operator = strarrOperators[0];
						switch (operator) {
						case "AND":

							accumResult = executeAND(res, res2);
							break;
						case "OR":

							accumResult = executeOR(res, res2, table);
							break;

						case "XOR":
							accumResult = executeXOR(res, res2);
							break;

						}

						i++;
					} else {
						accumResult = res;
					}

				}

				else {
					Vector<Hashtable<String, Object>> result = computeTerm(arrSQLTerms[i]);
					operator = strarrOperators[i - 1];
					switch (operator) {
					case "AND":
						accumResult = executeAND(accumResult, result);
						break;
					case "OR":
						accumResult = executeOR(accumResult, result, table);
						break;

					case "XOR":
						accumResult = executeXOR(accumResult, result);
						break;

					}

				}

			}

		}

		System.out.println(accumResult);
		Table.serialize(table, tableName);

		return new ResultSet(accumResult.iterator());
	}

	public static void clear(String strTableName) {
		try {
			Table table = Table.deserialize(strTableName);
			for (int pageId : table.htblPage.keySet()) {
				Page p = Page.deserialize(strTableName + pageId);
				p.page.clear();
				File myFile = new File(strTableName + pageId + ".class");
				myFile.deleteOnExit();
			}
			for (int i = 0; i <= table.octCounter; i++) {
				File myFile2 = new File(strTableName + "Oct" + i + ".class");
				myFile2.deleteOnExit();
			}
			table.htblPage.clear();
			File myFile = new File(strTableName + ".class");
			myFile.deleteOnExit();
			emptyCsvFile("metadata.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] removeFirstThreeElements(String[] inputArray) {
		if (inputArray == null || inputArray.length < 3) {
			throw new IllegalArgumentException("Input array must have at least 3 elements.");
		}

		String[] outputArray = new String[inputArray.length - 3];
		for (int i = 3; i < inputArray.length; i++) {
			outputArray[i - 3] = inputArray[i];
		}

		return outputArray;
	}

	public void createIndex(String strTableName, String[] strarrColName) throws DBAppException {
		if (strarrColName == null || strarrColName.length < 3 || (strarrColName.length % 3) != 0)
			throw new DBAppException("number of columns has to be multiple of 3");

		ArrayList<Object[]> content = readFromCSV("metadata.csv");

		Boolean tableFound = false;
		Vector<Integer> tableIndexes = new Vector<>();

		for (int i = 0; i < content.size(); i++) {
			if (strTableName.equals(content.get(i)[0])) {
				tableFound = true;
				tableIndexes.add(i);
			}
		}

		if (tableFound == false) {
			throw new DBAppException("table does not exist");
		}

		int rowIndex;

		Object[] colMin = new Object[3];
		Object[] colMax = new Object[3];
		boolean[] validColumns = new boolean[3];
		Vector<Integer> columnIndexes = new Vector<>();

		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < tableIndexes.size(); i++) {
				rowIndex = tableIndexes.get(i);
				if (strarrColName[j].equals(content.get(rowIndex)[1])) {
					validColumns[j] = true;
					columnIndexes.add(rowIndex);
					colMin[j] = content.get(rowIndex)[6];
					colMax[j] = content.get(rowIndex)[7];
				}
			}
		}
		for (int i = 0; i < validColumns.length; i++) {
			if (validColumns[i] == false)
				throw new DBAppException("columns do not exist");
		}
		// System.out.println(colMin[0]+" "+ colMin[1]+" "+ colMin[2]+" "+ colMax[0]+"
		// "+ colMax[1]+" "+ colMax[2]);
		Octree octree = new Octree(colMin[0], colMin[1], colMin[2], colMax[0], colMax[1], colMax[2]);
		octree.colXname = strarrColName[0];
		octree.colYname = strarrColName[1];
		octree.colZname = strarrColName[2];
		Table table = Table.deserialize(strTableName);
		for (int pageId : table.htblPage.keySet()) {
			Page currPage = Page.deserialize(strTableName + pageId);
			for (int i = 0; i < currPage.page.size(); i++) {
				Object[] currTuple = new Object[3];
				Hashtable<String, Object> fullTuple = currPage.page.get(i);
				for (String colName : currPage.page.get(i).keySet()) {
					int counter = 0;
					for (int j = 0; j < 3; j++) {
						if (colName.equals(strarrColName[counter])) {
							currTuple[counter] = currPage.page.get(i).get(colName);
							fullTuple = currPage.page.get(i);
						}
						counter++;
					}
				}
				// System.out.println(currTuple[0]+" "+currTuple[1]+" "+currTuple[2]+" ");

				octree.insert(currTuple, fullTuple.get(table.strClusteringKeyColumn), table);
			}
			Page.serialize(currPage, strTableName + currPage.id);
		}

		try (FileReader fileReader = new FileReader("metadata.csv");
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				FileWriter fileWriter = new FileWriter("metadata_temp.csv");
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

			String strRow;
			String[] row;
			while ((strRow = bufferedReader.readLine()) != null) {
				row = strRow.split(",");
				for (int i = 0; i < 3; i++) {
					if (strarrColName[i].equals(row[1])) {
						if (row[4].equals("null") && row[5].equals("null")) {
							row[4] =octree.colXname+octree.colYname+octree.colZname +"Index";
							row[5] = "Octree";
						} else {
							throw new DBAppException("Index already exists on column " + row[1]);
						}
					}
				}
				String updatedRow = String.join(",", row);
				bufferedWriter.write(updatedRow);
				bufferedWriter.newLine();
			}

			// Close the readers and writers
			bufferedReader.close();
			bufferedWriter.close();

			// Rename the temporary file to the original filename
			File oldFile = new File("metadata.csv");
			File newFile = new File("metadata_temp.csv");
			if (oldFile.exists()) {
				oldFile.delete();
			}
			if (newFile.exists()) {
				newFile.renameTo(oldFile);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (DBAppException e) {
			e.printStackTrace();
		}

		table.octCounter++;
		octree.octId = table.octCounter;
		System.out.println("Octree " + octree.octId + " is created for table " + strTableName);
		// printOct(octree, table);
		Octree.serialize(octree, strTableName + "Oct" + table.octCounter);
		Table.serialize(table, strTableName);
		int l = strarrColName.length - 3;
		if (l > 0) {
			String[] newStrarrColName = removeFirstThreeElements(strarrColName);
			createIndex(strTableName, newStrarrColName);
		}
	}

	public static void printArrayList(ArrayList<Object[]> list) {
		for (Object[] arr : list) {
			for (Object obj : arr) {
				System.out.print(obj + " ");
			}
			System.out.println();
		}
	}

//	public static void printOct(Octree oct, Table table) {
//		printOctH(oct.root, 0, 0, table, oct);
//
//	}
//
//	public static void printOctH(OctreeNode currOct, int childNo, int level, Table table, Octree oct) {
//		System.out.println("Octree " + oct.octId);
//		if (currOct.children != null) {
//			for (int i = 0; i < currOct.children.length; i++) {
//				printOctH(currOct.children[i], i, level + 1, table, oct);
//			}
//		} else {
//			System.out.println("Child " + childNo + " / " + "level " + level);
//			System.out.println("min--> (" + oct.colXname + ":" + currOct.minX + " " + oct.colYname + ":" + currOct.minY
//					+ " " + oct.colZname + ":" + currOct.minZ + ")");
//			System.out.println("max--> (" + oct.colXname + ":" + currOct.maxX + " " + oct.colYname + ":" + currOct.maxY
//					+ " " + oct.colZname + ":" + currOct.maxZ + ")");
//			if (currOct.pageIds != null) {
//				System.out.println("references: ");
//				for (int i = 0; i < currOct.pageIds.size(); i++) {
//					System.out.println("Page " + currOct.pageIds.get(i) + " Cluster " + currOct.clusters.get(i));
//				}
//				System.out.println("values: ");
//				printArrayList(currOct.values);
//			} else
//				System.out.println("node is empty");
//
//			System.out.println("------------");
//		}
//	}

	public static void main(String[] args) throws DBAppException, ParseException {

//		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
//		String strTableName = "Students";
//		String strTableName2 = "Studentss";
//		htblColNameType.put("id", "java.lang.Integer");
//		htblColNameType.put("name", "java.lang.String");
//		htblColNameType.put("gpa", "java.lang.Double");
//		htblColNameType.put("job", "java.lang.String");
//		htblColNameType.put("age", "java.lang.Integer");
//		htblColNameType.put("score", "java.lang.Double");
//		htblColNameType.put("date", "java.util.Date");
//		SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
//
//		Hashtable<String, String> htblColNameMin = new Hashtable<String, String>();
//		Hashtable<String, String> htblColNameMax = new Hashtable<String, String>();
//
//		htblColNameMin.put("id", "0");
//		htblColNameMax.put("id", "20");
//		htblColNameMin.put("name", "A");
//		htblColNameMax.put("name", "ZZZZZZZ");
//		htblColNameMin.put("gpa", "0.0");
//		htblColNameMax.put("gpa", "20.0");
//		htblColNameMin.put("job", "A");
//		htblColNameMax.put("job", "ZZZZZZZX");
//		htblColNameMin.put("age", "0");
//		htblColNameMax.put("age", "100");
//		htblColNameMin.put("score", "0.0");
//		htblColNameMax.put("score", "100.0");
//		htblColNameMax.put("date", "2023-12-08");
//		htblColNameMin.put("date", "2020-12-01");
//
//
//
//		Hashtable<String, Object> htblColNameValue1 = new Hashtable<String, Object>();
//		htblColNameValue1.put("id", 1);
//		htblColNameValue1.put("name", "Ashraf");
//		htblColNameValue1.put("gpa", 1.0);
//		htblColNameValue1.put("job", "Eng");
//		htblColNameValue1.put("age", 21);
//		htblColNameValue1.put("score", 10.0);
//		 //test method
////	       Date date = new Date(2023 - 2020, 4 - 1, 1);
//		Date date= new Date();
//		date = inputFormatter.parse("2022-04-14");
//		 htblColNameValue1.put("date",date);
//
//
//		Hashtable<String, Object> htblColNameValue2 = new Hashtable<String, Object>();
//		htblColNameValue2.put("id", 2);
//		htblColNameValue2.put("name", "Ashraf");
//		htblColNameValue2.put("gpa", 1.3);
//		htblColNameValue2.put("job", "Mech");
//		htblColNameValue2.put("age", 22);
//		htblColNameValue2.put("score", 20.0);
//		Date date2= new Date();
//		date2 = inputFormatter.parse("2023-07-14");
//		 htblColNameValue2.put("date",date2);
//
//
//		Hashtable<String, Object> htblColNameValue3 = new Hashtable<String, Object>();
//		htblColNameValue3.put("id", 3);
//		htblColNameValue3.put("name", "Morsh");
//		htblColNameValue3.put("gpa", 3.0);
//		htblColNameValue3.put("job", "Dancer");
//		htblColNameValue3.put("age", 23);
//		htblColNameValue3.put("score", 30.0);
//		Date date3= new Date();
//		date3 = inputFormatter.parse("2020-12-14");
//		 htblColNameValue3.put("date",date3);
//
//
//		Hashtable<String, Object> htblColNameValue4 = new Hashtable<String, Object>();
//		htblColNameValue4.put("id", 4);
//		htblColNameValue4.put("name", "Yaso");
//		htblColNameValue4.put("gpa", 4.0);
//		htblColNameValue4.put("job", "Doc");
//		htblColNameValue4.put("age", 24);
//		htblColNameValue4.put("score", 40.0);
//
//
//		Hashtable<String, Object> htblColNameValue5 = new Hashtable<String, Object>();
//		htblColNameValue5.put("id", 5);
//		htblColNameValue5.put("name", "Salsa");
//		htblColNameValue5.put("gpa", 1.3);
//		htblColNameValue5.put("job", "Eng");
//		htblColNameValue5.put("age", 25);
//		htblColNameValue5.put("score", 50.0);
//
//
//		Hashtable<String, Object> htblColNameValue6 = new Hashtable<String, Object>();
//		htblColNameValue6.put("id", 6);
//		htblColNameValue6.put("name", "Joe");
//		htblColNameValue6.put("gpa", 6.0);
//		htblColNameValue6.put("job", "Doc");
//		htblColNameValue6.put("age", 26);
//		htblColNameValue6.put("score", 60.0);
//
//
//		Hashtable<String, Object> htblColNameValue7 = new Hashtable<String, Object>();
//		htblColNameValue7.put("id", 7);
//		htblColNameValue7.put("name", "Malak");
//		htblColNameValue7.put("gpa", 7.0);
//		htblColNameValue7.put("job", "Dancer");
//		htblColNameValue7.put("age", 27);
//		htblColNameValue7.put("score", 70.0);
//
//
//		Hashtable<String, Object> htblColNameValue8 = new Hashtable<String, Object>();
//		htblColNameValue8.put("id", 8);
//		htblColNameValue8.put("name", "Labib");
//		htblColNameValue8.put("gpa", 8.0);
//		htblColNameValue8.put("job", "Mech");
//		htblColNameValue8.put("age", 28);
//		htblColNameValue8.put("score", 80.0);
//
//
//		Hashtable<String, Object> htblColNameValue9 = new Hashtable<String, Object>();
//		htblColNameValue9.put("id", 9);
//		htblColNameValue9.put("name", "Haytham");
//		htblColNameValue9.put("gpa", 9.0);
//
//		Hashtable<String, Object> htblColNameValue10 = new Hashtable<String, Object>();
//		htblColNameValue10.put("id", 10);
//		htblColNameValue10.put("name", "Mariam");
//		htblColNameValue10.put("gpa", 10.0);
//
//		Hashtable<String, Object> htblColNameValue11 = new Hashtable<String, Object>();
//		htblColNameValue11.put("id", 11);
//		htblColNameValue11.put("name", "Moh");
//		htblColNameValue11.put("gpa", 11.0);
//
//		Hashtable<String, Object> htblColNameValue12 = new Hashtable<String, Object>();
//		htblColNameValue12.put("id", 12);
//		htblColNameValue12.put("name", "Hazem");
//		htblColNameValue12.put("gpa", 12.0);
//
//		Hashtable<String, Object> htblColNameValue13 = new Hashtable<String, Object>();
//		htblColNameValue13.put("id", 13);
//		htblColNameValue13.put("name", "Hassan");
//		htblColNameValue13.put("gpa", 13.0);
//
//		Hashtable<String, Object> htblColNameValue14 = new Hashtable<String, Object>();
//		htblColNameValue14.put("id", 14);
//		htblColNameValue14.put("name", "Shisho");
//		htblColNameValue14.put("gpa", 14.0);
//
//		DBApp dbApp = new DBApp();
////
////		dbApp.createTable(strTableName, "id", htblColNameType, htblColNameMin, htblColNameMax);
////		String[] strarrColName=new String[6];
////		strarrColName[0]="id";
////		strarrColName[1]="gpa";
////		strarrColName[2]="name";
////		strarrColName[3]="job"; 
////		strarrColName[4]="age";
////		strarrColName[5]="score";
////		dbApp.createIndex(strTableName, strarrColName);
////
////		dbApp.insertIntoTable(strTableName, htblColNameValue1);
////		dbApp.insertIntoTable(strTableName, htblColNameValue2);
////		dbApp.insertIntoTable(strTableName, htblColNameValue3);
////		dbApp.insertIntoTable(strTableName, htblColNameValue4);
////		dbApp.insertIntoTable(strTableName, htblColNameValue5);
////	    dbApp.insertIntoTable(strTableName, htblColNameValue6);
////    	dbApp.insertIntoTable(strTableName, htblColNameValue7);
////		dbApp.insertIntoTable(strTableName, htblColNameValue8); // 5 columns: id name gpa job age score
//
////		dbApp.insertIntoTable(strTableName, htblColNameValue9);
////		dbApp.insertIntoTable(strTableName, htblColNameValue10);
////		dbApp.insertIntoTable(strTableName, htblColNameValue11);
////		dbApp.insertIntoTable(strTableName, htblColNameValue12);
////		dbApp.insertIntoTable(strTableName, htblColNameValue13);
////		dbApp.insertIntoTable(strTableName, htblColNameValue14); // 3 columns: id name gpa
//
//		Hashtable<String, Object> htblColNameValue1up = new Hashtable<String, Object>();
//		htblColNameValue1up.put("gpa", 1.3);
//
//		Hashtable<String, Object> htblColNameValue2up = new Hashtable<String, Object>();
//		htblColNameValue2up.put("gpa", 1.0);
//
//		Hashtable<String, Object> htblColNameValue3up = new Hashtable<String, Object>();
//		htblColNameValue3up.put("gpa", 1.3);
//
//		Hashtable<String, Object> htblColNameValue4up = new Hashtable<String, Object>();
//		htblColNameValue4up.put("gpa", 1.3);
//
//		Hashtable<String, Object> htblColNameValue5up = new Hashtable<String, Object>();
//		htblColNameValue5up.put("gpa", 1.3);
//
//		Hashtable<String, Object> htblColNameValue6up = new Hashtable<String, Object>();
//		htblColNameValue6up.put("gpa", 1.3);
//
////		Hashtable<String, Object> htblColNameValue9up = new Hashtable<String, Object>();
////		htblColNameValue9up.put("gpa", 1.3);
////
//		Hashtable<String, Object> htblColNameValue14up = new Hashtable<String, Object>();
//		htblColNameValue14up.put("gpa", 1.3);
//		htblColNameValue14up.put("job", "Amk");
//
////		dbApp.updateTable(strTableName, "1", htblColNameValue1up);
////	    dbApp.updateTable(strTableName, "2", htblColNameValue2up);
////	    dbApp.updateTable(strTableName, "3", htblColNameValue3up);
////		dbApp.updateTable(strTableName, "4", htblColNameValue3up);
////		dbApp.updateTable(strTableName, "5", htblColNameValue3up);
////	    dbApp.updateTable(strTableName, "6", htblColNameValue6up);
////		dbApp.updateTable(strTableName, "7", htblColNameValue6up);
////		dbApp.updateTable(strTableName, "8", htblColNameValue6up);
////		dbApp.updateTable(strTableName, "9", htblColNameValue6up);
////		dbApp.updateTable(strTableName, "10", htblColNameValue6up);
////		dbApp.updateTable(strTableName, "9", htblColNameValue9up);
////		dbApp.updateTable(strTableName, "4", htblColNameValue14up);
//
//		SQLTerm[] arrSQLTerms = new SQLTerm[3];
//		arrSQLTerms[0] = new SQLTerm("Students", "id", ">", 0);
//		arrSQLTerms[1] = new SQLTerm("Students", "date", ">=", "2022-04-14");
////
//		arrSQLTerms[2] = new SQLTerm("Students", "age", ">", 1);
////		arrSQLTerms[3] = new SQLTerm("Students", "name", "=", "Ashraf");
//
////		arrSQLTerms[4] = new SQLTerm("Students","gpa","=",3.0);
//
////		arrSQLTerms[3] = new SQLTerm("Students","id","=",1);
//
//		String[] strarrOperators = new String[2];
//		strarrOperators[0] = "AND";
//		strarrOperators[1] = "AND";
////		strarrOperators[2] = "AND";
////		strarrOperators[3] = "OR";
//
////		dbApp.selectFromTable(arrSQLTerms, strarrOperators);
//
//		Hashtable<String, Object> htblColNameValue1del = new Hashtable<String, Object>();
//		htblColNameValue1del.put("gpa", 1.3);
////
////		Hashtable<String, Object> htblColNameValue2del = new Hashtable<String, Object>();	
////		htblColNameValue2del.put("gpa", 7.0);
////		Hashtable<String, Object> htblColNameValue6del = new Hashtable<String, Object>();
////		htblColNameValue6del.put("gpa", 6.0);
////		Hashtable<String, Object> htblColNameValue7del = new Hashtable<String, Object>();
////		htblColNameValue7del.put("id", 11);
////		htblColNameValue7del.put("gpa", 11.0);
//		
////		dbApp.deleteFromTable(strTableName, htblColNameValue1del);
//
////		Table table=Table.deserialize(strTableName);
////		Page p=Page.deserialize(strTableName+0);
////		System.out.println("Page "+p.id+" Full= "+p.isFull+" MaxTupleID= "+p.maxTupleID);
////		Page p1=Page.deserialize(strTableName+1);
////		System.out.println("Page "+p1.id+" Full= "+p1.isFull+" MaxTupleID= "+p1.maxTupleID);
////		Page p2=Page.deserialize(strTableName+2);
////		System.out.println("Page "+p2.id+" Full= "+p2.isFull+" MaxTupleID= "+p2.maxTupleID);
////		System.out.println(isLastPage(table.htblPage,p2));
////		Page.serialize(p, strTableName+p.id);
////		Page.serialize(p1, strTableName+p1.id);
////		Page.serialize(p2, strTableName+p2.id);
////		print(table);
////		System.out.println("htbl pages size: "+table.htblPage.size());
//
//	       
//	      
////	        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
//	       
//
//	       
////	            Date date1 = null;
////				try {
////					date1 = inputFormatter.parse("2022-04-14");
////				} catch (ParseException e) {
////					
////					e.printStackTrace();
////				}
////	            Date date2 = null;
////				try {
////					date2 = inputFormatter.parse("2022-05-14");
////				} catch (ParseException e) {
////					
////					e.printStackTrace();
////				}
//		
//		
//		
////		System.out.println(compare(date1,date2));
//		clear(strTableName2);
////		print(table);
////		Table.serialize(table, strTableName);
////		printAllOcts(table);

	}
}