package project;

import java.util.*;
import java.util.stream.Collectors;

import javax.swing.text.Document;
//import java.io.BufferReader;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DBApp implements Serializable {
	int maxRows;
	// static ArrayList<String> tableNames;
	// static ArrayList<String> MetaData;

	public void init() {

		Properties properties = new Properties();
		// String fileName = "DBApp.config";
		InputStream is = null;
		try {
			is = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\DBApp.config");
		} catch (FileNotFoundException ex) {
		}
		try {
			properties.load(is);
		} catch (IOException ex) {

		}
		maxRows = Integer.parseInt(properties.getProperty("MaximumRowsCountinTablePage"));
		// globalPath = System.getProperty("user.dir");

	}

//	public static boolean compareType(Object first, Object second) { // will be useful in insert method when comparing
//																		// two
//																		// variables to see if they are of the same data
//																		// type
//		if (first == null || second == null)
//			return false;
//		return first.getClass().equals(second.getClass());
//	}

	public static boolean biggerThan(Object A, Object B) {
		// Object B=B1.toString();
		// Object A=A1+toString();
		if (A == null || B == null)
			return false;
		// if (A.getClass() == (B.getClass())) {
		if (A instanceof String) {
			int bigger = ((String) A).compareTo((String) B);
			return bigger > 0;
		}
		if (A instanceof Date) {
			int bigger = ((Date) A).compareTo((Date) B);
			return bigger > 0;
		}
		if (A instanceof Integer)
			return (int) A > (int) B;
		if (A instanceof Double)
			return (Double) A > (Double) B;
		// }
		return false;
	}

	private static boolean checkType2(Object object, String string) {
		if (object == null || string == null) {
			return false;
		}

		String objectType = object.getClass().getName();
		return objectType.equals(string);
	}

	public static ArrayList<String[]> readFromCSV(String filePath) {
		// String csvFile = "lol.txt"; //recheck this
		String line = "";

		ArrayList<String[]> res = new ArrayList<String[]>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				res.add(data);
			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public static int binarySearch(String key, Object PKvalue, Vector<Hashtable<String, Object>> pageToSearch) {
		// parameter is vector 3shan ana ha deserialize (load) el page b3den a pass it
		// as vector lel method
		// return type is int bec i want to get index

		int lo = 0;
		int hi = pageToSearch.size() - 1;

		while (hi - lo > 1) {
			int mid = (hi + lo) / 2;
			Hashtable<String, Object> currRow = pageToSearch.get(mid);

			if (compare(currRow.get(key), PKvalue) == 0) {
				return mid;
			} else if (compare(PKvalue, currRow.get(key)) > 0) {
				lo = mid + 1;
			} else {
				hi = mid - 1;
			}
		}

		return -1;
	}

	public static int compare(Object obj1, Object obj2) {

		if (obj1 instanceof String) {
			return ((String) obj1).compareTo((String) obj2);
		} else if (obj1 instanceof Date) {
			return ((Date) obj1).compareTo((Date) obj2);
		} else if (obj1 instanceof Double) {
			return ((Double) obj1).compareTo((Double) obj2);
		} else {
			return ((Integer) obj1).compareTo((Integer) obj2);
		}
	}

	public static int pageIDfinder(Object primaryKey, Hashtable<Integer, Page> x) {
		// we'll use binary search to find ID of page containing this primary key

		int lo = 0;
		int hi = x.size() - 1;

		while (hi - lo > 1) {
			int middle = (hi + lo) / 2;

			if (compare(primaryKey, x.get(middle).page.get(0)) >= 0
					&& compare(primaryKey, x.get(middle).maxTupleID) <= 0) {
				return x.get(middle).getId();

			} else if (compare(primaryKey, x.get(middle).page.get(x.get(middle).maxTupleID)) > 0) {
				lo = middle + 1;
			} else {
				hi = middle - 1;
			}
		}
		return -1; // not found (elmafrood mawsalsh hena khales aslun)
	}

	public static void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType, Hashtable<String, String> htblColNameMin,
			Hashtable<String, String> htblColNameMax) throws DBAppException {

		Table table = new Table(strTableName, strClusteringKeyColumn, htblColNameType, htblColNameMin, htblColNameMax);
		File metadataFile = new File("metadata.csv");
		boolean tableExist = false;

		// for (int i = 0; i < tableNames.size(); i++) {
		// if (tableNames.get(i).equals(strTableName))
		// tableExist = true;
		// }
		if (tableExist)
			throw new DBAppException("Table is already existing");

		try (PrintWriter pw = new PrintWriter(new FileWriter(metadataFile, true))) {
			boolean validCluster = false;
			for (String Name : table.htblColNameType.keySet()) {
				if (Name == strClusteringKeyColumn)
					validCluster = true;
			}
			if (validCluster == false)
				throw new DBAppException("Cluster key column does not exist");

			for (String Name : table.htblColNameType.keySet()) {

				StringBuffer ColumnData = new StringBuffer();
				ColumnData.append(table.strTableName + ",");
				ColumnData.append(Name + ",");
				ColumnData.append(table.htblColNameType.get(Name) + ",");
				Boolean isCluster = false;
				if (Name.equals(table.strClusteringKeyColumn))
					isCluster = true;
				ColumnData.append(isCluster + ",");
				ColumnData.append(table.htblColNameMin.get(Name) + ",");
				ColumnData.append(table.htblColNameMax.get(Name));
				pw.println(ColumnData.toString());
				// MetaData.add(ColumnData.toString());
				// tableNames.add(strTableName);

			}
			pw.flush();
			Table.serialize(table, strTableName);
			// pw.flush();
			// tableNames.add(strTableName);

		} catch (IOException e) {
			System.out.print("Error writing to file: " + e.getMessage());
		}
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
		if (type.equals("java.lang.Date")) {
			if ((obj instanceof Date) == true)
				return true;
		}
		return false;
	}

	public static void shiftTuples(Hashtable<String, Object> tuple, int tupleId, Page currPage, Table table) {
		if (currPage.isFull) {
			if (currPage.id != table.htblPage.size() - 1) {
				Hashtable<String, Object> lastTuple = currPage.page.get(currPage.maxTupleID);
				shiftTuples(lastTuple, 0, table.htblPage.get(currPage.id + 1), table);
			} else {
				Page newPage = table.addPage();
				Hashtable<String, Object> lastTuple = currPage.page.get(currPage.maxTupleID);
				newPage.page.insertElementAt(lastTuple, 0);
				table.htblPage.put(newPage.id, newPage);
				Page.serialize(newPage, table.strTableName + newPage.id );
			}
		}
		Hashtable<String, Object> currTuple;
		Vector<Hashtable<String, Object>> page = currPage.page;

		for (int i = currPage.maxTupleID - 1; i >= tupleId; i--) {
			currTuple = page.get(i);
			page.insertElementAt(currTuple, i + 1);
		}
	}

	public static void printHashTable(Hashtable<String,Object>hashtable){
		for(String Name: hashtable.keySet()){
			System.out.print(Name+": "+hashtable.get(Name));
		}
		System.out.println();
	}

	public static void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue)
			throws DBAppException {
		ArrayList<String[]> content = readFromCSV("metadata.csv");

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
			if (checkColumn(htblColNameValue, content.get(rowIndex)[1]) == false)
				throw new DBAppException("columns do not exist or missing columns");
			for (String Name : htblColNameValue.keySet()) {
				if (Name.equals(content.get(rowIndex)[1])) {
					if (!checkType(htblColNameValue.get(Name), content.get(rowIndex)[2]))
						throw new DBAppException("Wrong datatypes");
					if (biggerThan(content.get(rowIndex)[4], htblColNameValue.get(Name) + ""))
						throw new DBAppException("Value is smaller than minimum allowed value");
					if (biggerThan(htblColNameValue.get(Name) + "", content.get(rowIndex)[5]))
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
		} else {
			String cluster = table.strClusteringKeyColumn;
			int currPageId = pageIDfinder(htblColNameValue.get(cluster), table.htblPage);
			currPage = Page.deserialize(strTableName + currPageId);
			int tupleId = binarySearch(table.strClusteringKeyColumn, htblColNameValue.get(cluster), currPage.page);

			if (currPageId == table.htblPage.size() - 1) {
				if (currPage.isFull) {
					if (tupleId >= currPage.maxTupleID) {
						Page newPage = table.addPage();
						newPage.page.insertElementAt(htblColNameValue, 0);
						newPage.maxTupleID++;
						table.htblPage.put(newPage.id, newPage);
						Page.serialize(currPage, strTableName + newPage.id);
						Table.serialize(table, strTableName);
						return;
					} else {
						Hashtable<String, Object> lastTuple = currPage.page.get(currPage.maxTupleID);
						shiftTuples(htblColNameValue, tupleId, currPage, table);
						currPage.page.insertElementAt(htblColNameValue, tupleId);
						currPage.maxTupleID++;
						Page newPage = table.addPage();
						newPage.page.insertElementAt(lastTuple, 0);
						newPage.maxTupleID++;
						table.htblPage.put(newPage.id, newPage);
						Page.serialize(newPage, strTableName + newPage.id);
						Table.serialize(table, strTableName);
						return;
					}
				} else {
					shiftTuples(htblColNameValue, tupleId, currPage, table);
					currPage.page.insertElementAt(htblColNameValue, tupleId);
					currPage.maxTupleID++;
				}
			} else {
				shiftTuples(htblColNameValue, tupleId, currPage, table);
				currPage.page.insertElementAt(htblColNameValue, tupleId);
				currPage.maxTupleID++;
			}
			if (currPage.maxTupleID == 200)
				currPage.isFull = true;
		}
		for(int i=0;i<currPage.page.size();i++){
			printHashTable(currPage.page.get(i));
		}
		Page.serialize(currPage, strTableName + currPage.id);
		Table.serialize(table, strTableName);
	}

	public static void emptyCsvFile(String filePath) throws IOException {
		File file = new File(filePath);
		FileWriter writer = new FileWriter(file);
		writer.write("");
		writer.flush();
		writer.close();
	}

	public static void main(String[] args) throws DBAppException {

		Hashtable<String, String> htblColNameType = new Hashtable<String, String>();
		String strTableName = "Students";
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Double");
		Hashtable<String, String> htblColNameMin = new Hashtable<String, String>();
		Hashtable<String, String> htblColNameMax = new Hashtable<String, String>();
		htblColNameMin.put("id", "0");
		htblColNameMax.put("id", "9");
		htblColNameMin.put("name", "A");
		htblColNameMax.put("name", "ZZZZZ");
		htblColNameMin.put("gpa", "2.0");
		htblColNameMax.put("gpa", "50.0");

////
//		try {
//			emptyCsvFile("metadata.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		Hashtable<String, Object> htblColNameValue = new Hashtable<String, Object>();
		htblColNameValue.put("id", 1);
		htblColNameValue.put("name", "Ashr");
		htblColNameValue.put("gpa", 5.0);

		Hashtable<String, Object> htblColNameValue2 = new Hashtable<String, Object>();
		htblColNameValue2.put("id", 2);
		htblColNameValue2.put("name", "Geo");
		htblColNameValue2.put("gpa", 7.0);
//		createTable(strTableName, "id", htblColNameType, htblColNameMin,htblColNameMax);
		//insertIntoTable(strTableName,  htblColNameValue);
		insertIntoTable(strTableName,  htblColNameValue2);

//		
	}

}
