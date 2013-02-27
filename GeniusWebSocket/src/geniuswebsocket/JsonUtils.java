package geniuswebsocket;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

	/**
	 * Merge "source" into "target". If fields have equal name, merge them recursively.
	 * @return the merged object (target).
	 */
	public static JSONObject deepMerge(JSONObject source, JSONObject target) throws JSONException {
		for (String key: JSONObject.getNames(source)) {
				Object value = source.get(key);
				if (!target.has(key)) {
					// new value for "key":
					target.put(key, value);
				} else {
					// existing value for "key" - recursively deep merge:
					if (value instanceof JSONObject) {
						JSONObject valueJson = (JSONObject)value;
						deepMerge(valueJson, target.getJSONObject(key));
					} else {
						target.put(key, value);
					}
				}
		}
		return target;
	}
	
	/**
	 * Merge all JSON sources into a single JSON object. If fields have equal name, merge them recursively.
	 * @param sources
	 * @return the merged object.
	 */
	public static JSONObject deepMerge(JSONArray sources) throws JSONException {
		JSONObject merged = new JSONObject();
		for (int i=0; i<sources.length(); ++i) {
			JsonUtils.deepMerge(sources.getJSONObject(i), merged);
		}
		return merged;
	}
	
	/**
	 * Merge all JSON sources (given as strings) into a single JSON object. If fields have equal name, merge them recursively.
	 * @param sources
	 * @return the merged object.
	 */
	public static JSONObject deepMerge(Iterable<String> sources) throws JSONException {
		JSONObject merged = new JSONObject();
		for (String source: sources) {
			deepMerge(new JSONObject(source), merged);
		}
		return merged;
	}
	
	/**
	 * If source is JSONObject, just return it.
	 * If it is an array, merge all objects and return the merged objects.
	 * Merge all "sources" into a single object. If fields have equal name, merge them recursively.
	 * @param sources
	 * @return the merged object.
	 */
	public static JSONObject objectOrDeepMerge(Object source) throws JSONException {
		if (source instanceof JSONObject) 
			return (JSONObject)source;
		else if (source instanceof JSONArray)
			return JsonUtils.deepMerge((JSONArray)source);
		else
			throw new IllegalArgumentException("Got an input that is not a JSON Object nor a JSON Array");
	}


	/**
	 * demo program
	 */
	public static void main(String[] args) throws JSONException {
		JSONObject a = new JSONObject("{offer: {issue1: value1}, accept: true}");
		JSONObject b = new JSONObject("{offer: {issue2: value2}, reject: false}");
		System.out.println(a+ " + " + b+" = "+JsonUtils.deepMerge(a,b));
		// prints:
		// {"accept":true,"offer":{"issue1":"value1"}} + {"reject":false,"offer":{"issue2":"value2"}} = {"reject":false,"accept":true,"offer":{"issue1":"value1","issue2":"value2"}}
	}
}
