package general.services.reports;

import java.util.Date;
import java.util.List;

import general.dao.DAOException;
import general.tables.report.TrainingReport1;

public interface TrainingReportService {
	
	public List<TrainingReport1> getRep1Data(String bukrs,Long branchId, Date fromDate, Date toDate) throws DAOException;
}
