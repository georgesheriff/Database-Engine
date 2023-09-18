package project;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import project.DBApp;
public class Table implements java.io.Serializable {
	public String strTableName;
	public String strClusteringKeyColumn; 
	public Hashtable<String,String> htblColNameType;
	public Hashtable<String,String> htblColNameMin;
	public Hashtable<String,String> htblColNameMax;
	public Hashtable<Integer, Page> htblPage;
	int pageCounter=0;
	int octCounter=-1;
	
	public Table(String strTableName, String strClusteringKeyColumn, Hashtable<String, String> htblColNameType,
			Hashtable<String, String> htblColNameMin, Hashtable<String, String> htblColNameMax) {
		super();
		this.strTableName = strTableName;
		this.strClusteringKeyColumn = strClusteringKeyColumn;
		this.htblColNameType = htblColNameType;
		this.htblColNameMin = htblColNameMin;
		this.htblColNameMax = htblColNameMax;
		htblPage=new Hashtable<Integer, Page>();
		
	}

	public Page addPage() {
		Page newPage = new Page(pageCounter,false, new Vector<Hashtable<String, Object>>()); 
		htblPage.put(pageCounter,newPage);
		pageCounter++;
		return newPage;
	}

	public static void serialize(Table table, String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename + ".class");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(table);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}
	
	public static Table deserialize(String filename) {
		try {
			FileInputStream fileIn = new FileInputStream(filename+ ".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Table table = (Table) in.readObject();
			in.close();
			fileIn.close();
			return table;
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
		return null;
	}
	
}
	
	
	
	
	

