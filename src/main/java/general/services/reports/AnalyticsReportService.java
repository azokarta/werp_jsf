package general.services.reports;

import java.util.List;

import general.tables.report.CustomerReport;

public interface AnalyticsReportService {
	
	public List<CustomerReport> getRep1Data(String bukrs,Long branchId, int day, int month);
}
