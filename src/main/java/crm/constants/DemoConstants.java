package crm.constants;

import java.util.HashMap;
import java.util.Map;

public class DemoConstants {

	/**
	 * Результаты демо
	 */

	// Результат неизвестно
	public static final Integer RESULT_UNKNOWN = 0;

	// Демо пройден
	public static final Integer RESULT_DONE = 1;

	// демо перенесен
	public static final Integer RESULT_MOVED = 2;

	// демо отменена
	public static final Integer RESULT_CANCELLED = 3;

	// Продан
	public static final Integer RESULT_SOLD = 4;

	// Мини договор
	public static final Integer RESULT_MINI_CONTRACT = 5;

	// Продан, но потом отменен
	public static final Integer RESULT_SOLD_CANCELLED = 6;

	public static Map<Integer, String> getAllResults() {
		Map<Integer, String> out = new HashMap<>();
		out.put(RESULT_UNKNOWN, "Неизвестно");
		out.put(RESULT_DONE, "Пройден");
		out.put(RESULT_CANCELLED, "Отменен");
		out.put(RESULT_MOVED, "Перенесен");
		out.put(RESULT_SOLD, "Продан");
		out.put(RESULT_MINI_CONTRACT, "Мини контракт");
		out.put(RESULT_SOLD_CANCELLED, "Продан, потом отменен");

		return out;
	}

	public static Map<Integer, String> getResults1() {
		Map<Integer, String> out = getAllResults();
		out.remove(RESULT_SOLD_CANCELLED);
		return out;
	}

	public static final Integer APPOINTED_BY_DEALER = 1;
	public static final Integer APPOINTED_BY_DEMOSEC = 2;
	public static final Integer APPOINTED_BY_CLIENT = 3;
}