package general.services.reports;

import java.util.Date;
import java.util.List;
import java.util.Map;

import general.dao.DAOException;
import general.tables.Salary;
import general.tables.report.HrReport1;
import general.tables.report.HrReport3;
import general.tables.report.HrReport4;
import general.tables.report.HrReport5;
import general.tables.report.HrReport6;
import reports.hr.RepHr4.SearchModel;

public interface HrReportService {

	public List<HrReport1> getRep1Data(String bukrs, Long branchId, Long roleId, int isActive, int isRoot,
			String username);

	public List<Salary> getRep2Data(String bukrs, Long branchId, Long positionId, Long departmentId, Long staffId,
			String currency);

	public Map<Long, Map<Long, List<HrReport3>>> getRep3Data(String bukrs, List<String> branchIds,
			List<String> positionIds, List<String> departmentIds, Date salaryDate);

	public Map<Long, Map<Long, List<HrReport4>>> getRep4Data(SearchModel searchModel);

	public List<HrReport5> getRep5Data(String bukrs, List<Long> branchIds) throws DAOException;

	public List<HrReport6> getRep6Data(String bukrs, List<Long> branchIds, Long positionId,Long departmentId) throws DAOException;
}
