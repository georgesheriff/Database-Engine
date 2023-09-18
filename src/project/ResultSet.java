package project;

import java.util.Hashtable;
import java.util.Iterator;

public class ResultSet implements Iterator{
	Iterator<Hashtable<String, Object>> iterator;
	
	public ResultSet(Iterator<Hashtable<String, Object>> iterator2) {
		this.iterator = iterator2;
	}
	
	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public Object next() {
		Hashtable<String, Object> tuple = iterator.next();
		return tuple;
	}
	
	public void remove() {
		iterator.remove();
	}

}
