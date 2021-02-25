package crm.constants;

import java.util.HashMap;
import java.util.Map;

public class RecoConstants {

	// public static final Integer CALL_REASON_RECALL = 1;
	// public static final Integer CALL_REASON_

	public final static Integer STATUS_NEW = 0;
	public final static Integer STATUS_DEMO = 1;
	public final static Integer STATUS_RECALL = 2;
	public final static Integer STATUS_REFUSE = 3;
	public final static Integer STATUS_NOT_AVAILABLE = 4;
	public final static Integer STATUS_DEMO_SHOWN = 5;
	public final static Integer STATUS_SOLD = 6;
	public final static Integer STATUS_MINI_CONTRACT = 7;
	public final static Integer STATUS_SOLD_CANCELLED = 8;
	public final static Integer STATUS_CANCELLED = 9;

	public static Map<Integer, String> getStatuses() {
		Map<Integer, String> out = new HashMap<>();
		out.put(STATUS_NEW, "Новый");
		out.put(STATUS_DEMO, "Демо");
		out.put(STATUS_RECALL, "Перезвон");
		out.put(STATUS_REFUSE, "Отказ");
		out.put(STATUS_NOT_AVAILABLE, "Не доступен");
		out.put(STATUS_DEMO_SHOWN, "Демо показан");
		out.put(STATUS_SOLD, "Продан");
		out.put(STATUS_MINI_CONTRACT, "Мини контракт");
		out.put(STATUS_SOLD_CANCELLED, "Продажа отменена");
		out.put(STATUS_CANCELLED, "Отменен");
		return out;
	}

	public static Map<Integer, String> getStatuses1() {
		Map<Integer, String> out = new HashMap<>();
		out.put(STATUS_NEW, "Новый");
		out.put(STATUS_RECALL, "Перезвон");

		return out;
	}
}