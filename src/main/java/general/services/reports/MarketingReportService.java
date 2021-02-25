package general.services.reports;

import java.util.List;
import java.util.Map;

import org.primefaces.model.chart.LineChartSeries;

import general.dao.DAOException;
import general.tables.report.MarketingReport1;

public interface MarketingReportService {
	
	/**
	 * 
	 * @param bukrs
	 * @param branchId
	 * @param year
	 * @param month
	 * @param renderType - 1 - Показывает группы, 2 - показывает дилеров в группе
	 * @return
	 * @throws DAOException
	 */
	public List<MarketingReport1> getRep1Data(String bukrs,Long branchId, int year, int month,Long managerPyramidId,int renderType) throws DAOException;
	
	public Map<String, LineChartSeries> getRep2Data(String bukrs,Long branchId, int year);
}
