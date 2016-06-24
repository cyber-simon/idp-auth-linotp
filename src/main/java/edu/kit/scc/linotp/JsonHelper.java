package edu.kit.scc.linotp;

import javax.json.JsonObject;

public class JsonHelper {

	public static Long getLongOrNull(JsonObject object, String key) {
		if (object.containsKey(key)) {
			return object.getJsonNumber(key).longValue();
		}
		else {
			return null;
		}
	}

	public static String getStringOrNull(JsonObject object, String key) {
		if (object.containsKey(key)) {
			return object.getJsonString(key).getString();
		}
		else {
			return null;
		}
	}
	
	public static Boolean getBooleanOrNull(JsonObject object, String key) {
		if (object.containsKey(key)) {
			return object.getBoolean(key);
		}
		else {
			return null;
		}
	}
	
}
