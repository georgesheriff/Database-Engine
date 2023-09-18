package project;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class Page implements java.io.Serializable{
	//static int pageCounter=0;
	int id;
	boolean isFull;
	Vector<Hashtable<String, Object>> page;
	int maxTupleID;

	public Page(int id,boolean isFull, Vector<Hashtable<String, Object>> page) {
		this.id=id;
		this.page = page;
		this.isFull = isFull;
		this.maxTupleID=-1;
	}

//	public static Page addPage() {
//		Page newPage = new Page( false, new Vector<Hashtable<String, Object>>(), 0, 200); // FIX MIN AND MAX
//		return newPage;
//	}

	public Vector<Hashtable<String, Object>> getPage() {
		return page;
	}

	public void setPage(Vector<Hashtable<String, Object>> page) {
		this.page = page;
	}

	public int getId() {
		return id;
	}

	public static void serialize(Page page, String filename) {
		try {
			FileOutputStream fileOut = new FileOutputStream(filename + ".class");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(page);
			out.close();
			fileOut.close();
		} catch (IOException i) {
			i.printStackTrace();
		}
	}

	public static Page deserialize(String filename) {
		try {
			FileInputStream fileIn = new FileInputStream(filename+ ".class");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			Page page = (Page) in.readObject();
			in.close();
			fileIn.close();
			return page;
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
		return null;
	}

}
