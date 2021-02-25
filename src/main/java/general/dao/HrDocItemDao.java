package general.dao;

import java.util.Date;
import java.util.List;

import general.tables.HrDocItem;

public interface HrDocItemDao extends GenericDao<HrDocItem> {
	public List<HrDocItem> findAll(String cond);

	List<HrDocItem> findAllRep4Data(String bukrs, List<String> branchIds, List<String> departmentIds,
			List<String> positionIds, Date fromDate, Date toDate, int typeId);

	List<HrDocItem> findAllNotClosedBypassDocByStaffId(Long staffId);
}
