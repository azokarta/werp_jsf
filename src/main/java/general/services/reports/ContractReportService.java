package general.services.reports;

import general.dao.DAOException;
import general.tables.Branch;
import general.tables.report.SalesReportOutput;

import java.util.Date;
import java.util.List;

import user.User;

public interface ContractReportService {
	
	List<SalesReportOutput> getSalesReportBranch(SalesReportOutput inSr, List<String> br_list, User userData, Long trId)
			throws DAOException;	
	
	List<SalesReportOutput> getSalesReportGroup(SalesReportOutput inSr, User userData, Long trId)
			throws DAOException;
	
	List<SalesReportOutput> getSalesReportDealer(SalesReportOutput inSr, User userData, Long trId)
			throws DAOException;
	
	List<SalesReportOutput> getSalesReportDealerGroup(SalesReportOutput inSr, User userData, Long trId)
			throws DAOException;
	
}

