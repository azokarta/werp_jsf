package general.services.reports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.GeneralUtil;
import general.PermissionController;
import general.dao.DAOException;
import general.dao.ServiceApplicationDao;
import general.output.tables.ServAppReportOutput;
import user.User;

@Service("sarepService")
public class ServAppReportServiceImpl implements ServAppReportService {
	
	@Autowired 
	ServiceApplicationDao servappDao;
	
	@Override
	public List<ServAppReportOutput> getServAppReportBranch(
			ServAppReportOutput inSr, List<String> br_list, User userData, Long trId)
			throws DAOException {
		try {
			PermissionController.canRead(userData, trId);
			
			List<ServAppReportOutput> srOut = new ArrayList<ServAppReportOutput>();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	     	
			boolean arch = false;
			String table = "SERVICE_APPLICATION";
			Calendar thisMonth = Calendar.getInstance();
			Date inDate = inSr.getServapp().getAdate();
			
			Date fd = GeneralUtil.getFirstDateOfMonth(inDate);
			Date ld = GeneralUtil.getLastDateOfMonth(inDate);
			
			String s1 = "select "
					+ " b.branch_id," //0
					+ " b.text45," //1
					+ " sum(case sa.app_type when 3 then 1 else 0 end) robs," //2
					+ " sum(case sa.app_type when 1 then 1 else 0 end) cebs," //3
					+ " sum(case sa.app_type when 6 then 1 else 0 end) prof," //4
					+ " sum(case sa.app_type when 7 then 1 else 0 end) zf," //5
					+ " sum(case sa.app_type when 5 then 1 else 0 end) other," //6
					+ " sum(case sa.app_type when 4 then 1 else 0 end) complain," //7
					+ " sum(case when sa.app_type > -1 then 1 else 0 end) tot" //8
					+ " from ";
			
			String s2 = " sa left join BRANCH b on sa.branch_id = b.branch_id"
					+ " where sa.bukrs = '" + inSr.getBukrs() + "'";
			
			if (inSr.isByMonth()) {
				s2 += " and sa.adate between '" + formatter.format(fd) + "' and '" + formatter.format(ld) + "'";
			} else {
				s2 += " and sa.adate between TO_DATE('" + formatter.format(inSr.getServapp().getAdate()) + "', 'YYYY-MM-DD')"
						+ " and TO_DATE('" + formatter.format(inSr.getServapp().getAdate()) + " 23:59:59', 'YYYY-MM-DD HH24:MI:SS')";
			}
			
			String brList = "";
			for (String br:br_list) {
				brList += (brList.length() > 0 ? ", " : "");
				brList += br;						
			}
//			System.out.println("Branches: " + brList);
			s2 += " and sa.branch_id in (" + brList + ")";
			s2 += " group by (b.branch_id, b.text45)"
			   + " order by b.text45 asc";
			
			String s = s1 + table + s2;
			
//			System.out.println("Query: " + s);
			
			srOut = servappDao.getSAReportBranch(s, table);
//			if (srOut.size() == 0 || srOut.get(0).getTotal() == 0)
//				table = "";
				//System.out.println("Total branches: " + srOut.size());
			
			setTotals(srOut);
			
			return srOut;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	private void setTotals(List<ServAppReportOutput> srOut) {
		ServAppReportOutput srTotal = new ServAppReportOutput(srOut.size());
		
		int zf = 0; 	// 7 
		int prof = 0;	// 6
		int rserv = 0;	// 3
		int cserv = 0;	// 1
		int others = 0;	// 5
		int complain = 0; // 4
		int total = 0;
		
		for (ServAppReportOutput sr:srOut) {
			zf += sr.getZf();
			prof += sr.getProf();
			rserv += sr.getRserv();
			cserv += sr.getCserv();
			others += sr.getCserv();
			complain += sr.getCserv();
			total += sr.getTotal();			
		}
		
		srTotal.setZf(zf);
		srTotal.setProf(prof);
		srTotal.setRserv(rserv);
		srTotal.setCserv(cserv);
		srTotal.setOthers(others);
		srTotal.setComplain(complain);		
		srTotal.setTotal(total);
		
		srOut.add(srTotal);		
	}

}
