package general.services.reports;

import java.util.List;

import general.dao.DAOException;
import general.output.tables.ServAppReportOutput;
import user.User;

public interface ServAppReportService {

	List<ServAppReportOutput> getServAppReportBranch(ServAppReportOutput inSr, List<String> br_list, User userData,
			Long trId) throws DAOException;

}