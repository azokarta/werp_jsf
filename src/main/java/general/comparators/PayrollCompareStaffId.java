package general.comparators;

import general.tables.Payroll; 

import java.util.Comparator;

public class PayrollCompareStaffId implements Comparator<Payroll>{
	@Override
	public int compare(Payroll a1, Payroll a2)
	{
		if(a1.getStaff_id()>a2.getStaff_id())
		{
			return 1;
		}
		else if (a1.getStaff_id()<a2.getStaff_id())
		{
			return -1;
		}
		else 
		{
			return 0;
		}
	}
}
