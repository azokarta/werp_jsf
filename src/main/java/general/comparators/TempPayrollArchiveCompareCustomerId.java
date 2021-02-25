package general.comparators;

import general.tables.TempPayrollArchive;

import java.util.Comparator;

public class TempPayrollArchiveCompareCustomerId implements Comparator<TempPayrollArchive>{
	@Override
	public int compare(TempPayrollArchive a1, TempPayrollArchive a2)
	{
		if(a1.getCustomer_id()>a2.getCustomer_id())
		{
			return 1;
		}
		else if (a1.getCustomer_id()<a2.getCustomer_id())
		{
			return -1;
		}
		else 
		{
			return 0;
		}
	}
}