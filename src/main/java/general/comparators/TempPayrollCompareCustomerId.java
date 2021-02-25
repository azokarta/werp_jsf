package general.comparators;

import general.tables.TempPayroll;

import java.util.Comparator;

public class TempPayrollCompareCustomerId implements Comparator<TempPayroll>{
	@Override
	public int compare(TempPayroll a1, TempPayroll a2)
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