package general.comparators;

import java.util.Comparator;

import general.tables.HrDocApprover;

public class HrDocApproverComparator implements Comparator<HrDocApprover> {

	@Override
	public int compare(HrDocApprover o1, HrDocApprover o2) {
		Long d = o1.getId() - o2.getId();
		return d.intValue();
	}
}
