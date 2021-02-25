package general.services.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import general.dao.DAOException;
import general.tables.report.LogReport3;
import general.tables.report.LogReport4;
import general.tables.report.LogReport5;
import general.tables.report.LogReport6;
import reports.logistics.RepLog2.OutputTable;


public interface LogisticsReportService {
	
	public List<OutputTable> getRep2Data(Long branchId, Long werks, Long staffId,Date fromDate, Date toDate, String code);
	
	public List<LogReport3> getRep3Data(Long werks, Long staffId,Date fromDate, Date toDate, String code);
	
	public List<LogReport5> getRep5Data(Long contractNumber) throws DAOException;
	
	public List<LogReport6> getRep6Data(String bukrs, Long branchId, Date fromDate, Date toDate) throws DAOException;
	
	public List<LogReport5> getRep6Data(String bukrs, Long branchId,Date fromDate,Date toDate, Long contractNumber);
	
	public Map<Long, HashMap<Long, LogReport4>> getRep4Data(Long werks, Long staffId,Date fromDate, Date toDate, String code);
}
