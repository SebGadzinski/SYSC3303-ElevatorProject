package project.utils.datastructs;

import java.util.Comparator;
import java.util.HashMap;

public class HashMapCompare implements Comparator<HashMap<Request.Key, Object>> {

	private final Request.Key key;

	public HashMapCompare(Request.Key key)
	{
		this.key = key;
	}

	public int compare(HashMap<Request.Key, Object> first,
			HashMap<Request.Key, Object> second)
	{
		// TODO: Null checking, both for maps and values
		String firstValue = (String) first.get(Request.Key.TIME);
		String secondValue = (String) second.get(Request.Key.TIME);
		return firstValue.compareTo(secondValue);
	}
}

