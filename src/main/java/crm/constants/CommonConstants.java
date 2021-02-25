package crm.constants;

import java.util.HashMap;
import java.util.Map;

public class CommonConstants {

	public static final Integer LOCATION_CITY = 1;
	public static final Integer LOCATION_OUT_CITY = 2;

	public static Map<Integer, String> getLocations() {
		Map<Integer, String> out = new HashMap<>();
		out.put(LOCATION_CITY, "Город");
		out.put(LOCATION_OUT_CITY, "Загород");

		return out;
	}

	/******** CONTEXTS **********/

	public static final String CONTEXT_RECO = "reco";
	public static final String CONTEXT_DEMO = "demo";
	public static final String CONTEXT_VISIT = "visit";
	public static final String CONTEXT_CALL = "call";
}