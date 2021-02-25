package crm.constants;

import java.util.HashMap;
import java.util.Map;

public class CallConstants {

	public static final Integer RESULT_POSITIVE = 1;
	public static final Integer RESULT_REFUSE = 2;
	public static final Integer RESULT_RECALL = 3;
	public static final Integer RESULT_NOT_AVAILABLE = 4;

	public static Map<Integer, String> getResults() {
		Map<Integer, String> out = new HashMap<>();
		out.put(RESULT_POSITIVE, "Положительный");
		out.put(RESULT_REFUSE, "Отказ");
		out.put(RESULT_RECALL, "Перезвонить");
		out.put(RESULT_NOT_AVAILABLE, "Не доступен");

		return out;
	}
}