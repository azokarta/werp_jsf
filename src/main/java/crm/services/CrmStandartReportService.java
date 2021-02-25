package crm.services;

import java.util.List;

import crm.tables.report.CrmStandartReport;

public interface CrmStandartReportService {

	public List<CrmStandartReport> getStandartReportData(String bukrs, Long branchId, Long managerId);
}
