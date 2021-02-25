package general.comparators;
import java.util.Comparator;
import general.tables.Hkont; 
public class HkontCompareByHkont implements Comparator<Hkont>{
	@Override
	public int compare(Hkont a1, Hkont a2)
	{
		Long a1hkont = 0L;
		Long a2hkont = 0L;
		a1hkont = Long.parseLong(a1.getHkont());
		a2hkont = Long.parseLong(a2.getHkont());
		if(a1hkont>a2hkont)
		{
			return 1;
		}
		else if (a1hkont<a2hkont)
		{
			return -1;
		}
		else 
		{
			return 0;
		}
	}	
}