package crm.constants;

import java.util.HashMap;
import java.util.Map;

public class ReasonConstants {

	// Тип Причина отказа от просмотра после демоЗвонка
	public static final Integer TYPE_DEMO_CALL_REFUSE = 1;

	// Тип Причина не продажи после демо
	public static final Integer TYPE_DEMO_REFUSE = 2;

	// Тип Причина отмены демо
	public static final Integer TYPE_DEMO_CANCEL = 3;

	// Тип Причина перенос демо
	public static final Integer TYPE_DEMO_MOVE = 4;

	public static Map<Integer, String> getReasonTypes() {
		Map<Integer, String> out = new HashMap<>();
		out.put(TYPE_DEMO_CALL_REFUSE, "ПричинаОтказДемоЗвонок");
		out.put(TYPE_DEMO_REFUSE, "ПричинаНеПродажаДемо");
		out.put(TYPE_DEMO_CANCEL, "ПричинаОтменаДемо");
		out.put(TYPE_DEMO_MOVE, "ПричинаПереносДемо");

		return out;
	}
}
