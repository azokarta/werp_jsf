package kpi;

import java.util.HashMap;
import java.util.Map;

public class KpiConstants {

	//Демо рекомендации
	public final static Integer IND_RECO_FROM_DEMO = 1;
	//Визит рекомендации
	public final static Integer IND_RECO_FROM_VISIT = 2;
	
	//Демо
	public final static Integer IND_DEMO = 3;
	
	//С демо на демо (Instant set)
	public final static Integer IND_DEMO_FROM_DEMO = 4;
	
	//Демо продажи
	public final static Integer IND_SALE_FROM_DEMO = 5;
	
	//Визит клиенту
	public final static Integer IND_VISIT = 6;
	
	
	public static Map<Integer, String> getIndicatorsMap(){
		Map<Integer, String> out = new HashMap<>();
		out.put(IND_RECO_FROM_DEMO, "Демо рекомендации");
		out.put(IND_RECO_FROM_VISIT, "Визит рекомендации");
		out.put(IND_DEMO, "Демо");
		out.put(IND_DEMO_FROM_DEMO, "С демо на демо (Instant set)");
		out.put(IND_SALE_FROM_DEMO, "Демо продажи");
		out.put(IND_VISIT, "Визит клиенту");
		return out;
	}
	
}
