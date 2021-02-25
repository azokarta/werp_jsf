package general.comparators;

import general.tables.Bkpf; 

import java.util.Comparator;

public class BkpfCompareAwkey implements Comparator<Bkpf>
{
	@Override
	public int compare(Bkpf a1, Bkpf a2)
	{
		if(a1.getAwkey()>a2.getAwkey())
		{
			return 1;
		}
		else if (a1.getAwkey()<a2.getAwkey())
		{
			return -1;
		}
		else 
		{
			return 0;
		}
	}	
}
