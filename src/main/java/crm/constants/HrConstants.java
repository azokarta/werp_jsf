package crm.constants;

import java.util.HashMap;
import java.util.Map;

public class HrConstants {

	/**
	 * Количество месяцов стажа для категории сотрудников (Отдел маркетинга)
	 **/
	public static final Double BEGINNER_COUNT = 12D;
	public static final Double DEALER_COUNT = 36D;
	public static final Double PROF_COUNT = 60D;

	public static final Integer CAT_BEGINNER = 1;
	public static final Integer CAT_DEALER = 2;
	public static final Integer CAT_PROF = 3;
	public static final Integer CAT_MASTER = 4;

	public static Map<Integer, String> getCats() {
		Map<Integer, String> out = new HashMap<>();
		out.put(CAT_BEGINNER, "1-я категория (до 1 года)");
		out.put(CAT_DEALER, "2-я категория (от 1 года до 3-х лет)");
		out.put(CAT_PROF, "3-я категория (от 3-х лет до 5 лет)");
		out.put(CAT_MASTER, "4-я категория (от 5 лет до 15 лет)");
		return out;
	}
}
