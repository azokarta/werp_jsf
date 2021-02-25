package general.comparators;

import general.GeneralUtil;

import java.util.Calendar;
import java.util.Comparator;

import reports.dms.contract.ContractPaymentGraphic;

public class ConPaymentGraphicComparator implements Comparator<ContractPaymentGraphic>{
	
	@Override
	public int compare(ContractPaymentGraphic o1, ContractPaymentGraphic o2) {
		Calendar fd = Calendar.getInstance();
		Calendar ld = Calendar.getInstance();
		fd.setTime(GeneralUtil.getFirstDateOfMonth(fd.getTime()));
		ld.setTime(GeneralUtil.getFirstDateOfMonth(ld.getTime()));
		
		int i=0;
		while (i<o1.getPsL().size() && o1.getPsL().get(i).getPayment_date().getTime() < fd.getTime().getTime()) i++;
		int j=0;
		while (j<o2.getPsL().size() && o2.getPsL().get(j).getPayment_date().getTime() < fd.getTime().getTime()) j++;
		
		if (i == o1.getPsL().size()) i--; 
		if (j == o2.getPsL().size()) j--;
		return o1.getPsL().get(i).getPayment_date().compareTo(o2.getPsL().get(j).getPayment_date());
	}
	
}
