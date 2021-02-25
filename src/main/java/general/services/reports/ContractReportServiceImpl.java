package general.services.reports;

import general.GeneralUtil;
import general.PermissionController;
import general.dao.BranchDao;
import general.dao.ContractDao;
import general.dao.ContractTypeDao;
import general.dao.DAOException;
import general.dao.StaffDao;
import general.tables.report.SalesReportOutput;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import user.User;

@Service("salesReportService")
public class ContractReportServiceImpl implements ContractReportService {
	@Autowired
	private ContractDao conDao;

	@Autowired
	private StaffDao stfDao;

	@Autowired
	private ContractTypeDao ctDao;

	@Autowired
	private BranchDao brDao;

	@Override
	public List<SalesReportOutput> getSalesReportBranch(
			SalesReportOutput inSr, List<String> br_list, User userData, Long trId)
			throws DAOException {
		try {
			PermissionController.canRead(userData, trId);
			
			List<SalesReportOutput> srOut = new ArrayList<SalesReportOutput>();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	     	
			boolean arch = false;
			String table = "PYRAMID";
			Calendar thisMonth = Calendar.getInstance();
			Date inDate = inSr.getContract().getContract_date();
			
			Date fd = GeneralUtil.getFirstDateOfMonth(inDate);
			Date ld = GeneralUtil.getLastDateOfMonth(inDate);
			
			if (inDate.getMonth() != thisMonth.getTime().getMonth()
					|| inDate.getYear() != thisMonth.getTime().getYear()) {
				table = "PYRAMID_ARCHIVE";
				arch = true;
			}
			
			boolean okPyramid = conDao.checkPyramid(inSr.getBukrs(), inDate);
			
			String s1 = "select "
					+ " b.branch_id,"
					+ " b.text45,"
					+ " (s.firstname || ' ' || s.lastname) director,"
					+ " sum(ts.nal) nal,"
					+ " sum(ts.ras) ras,"
					+ " sum(ts.tot) total,"
					+ " sum(ts.can) cancelled"
					+ " from ";
					
			String s2 = " p left join BRANCH b on p.branch_id = b.branch_id"
					+ " left join STAFF s on p.staff_id = s.staff_id"
					+ " right join (select "
					+ " c.branch_id, "
					+ " sum(case c.contract_status_id when 3 then 0 else 1 end) tot,"
					+ " sum(case when c.payment_schedule in (0,1) and  c.contract_status_id <> 3 then 1 else 0 end) nal,"
					+ " sum(case when c.payment_schedule > 1 and c.contract_status_id <> 3 then 1 else 0 end) ras,"
					+ " sum(case c.contract_status_id when 3 then 1 else 0 end) can"
					+ " from CONTRACT c "
					+ "   where c.contract_date between '" + formatter.format(fd) + "' and '" + formatter.format(ld) + "'"
							+ " and c.marked_type = 0 "
					+ "   group by c.branch_id) ts on ts.branch_id = p.branch_id"
					+ " where p.bukrs = '" + inSr.getBukrs() + "'";
			
			String brList = "";
			for (String br:br_list) {
				brList += (brList.length() > 0 ? ", " : "");
				brList += br;						
			}
			System.out.println("Branches: " + brList);
			s2 += " and p.branch_id in (" + brList + ")";
//			if (!Validation.isEmptyLong(inSr.getBranch().getBranch_id())) {
//				s += " and p.branch_id = " + inSr.getBranch().getBranch_id();
//			}
			s2 += " and p.position_id = 10";			
			
			if (arch && okPyramid)
				s2 += " and p.month = " + (fd.getMonth()+1) + " and  p.year = " + (fd.getYear()+1900); 
			
			s2 += " group by (b.branch_id, b.text45, (s.firstname || ' ' || s.lastname))"
			   + " order by b.text45 asc";
			
			if (arch && !okPyramid)
				table = "PYRAMID";
			
			String s = s1 + table + s2;
			srOut = conDao.getSalesReportBranch(s, table);
			if (srOut.size() == 0 || srOut.get(0).getTotal() == 0)
				table = "";
				//System.out.println("Total branches: " + srOut.size());
			
			setTotals(srOut);
			
			return srOut;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	private void setTotals(List<SalesReportOutput> srOut) {
		SalesReportOutput srTotal = new SalesReportOutput(srOut.size());
		
		double nal= 0, ras =0, tot=0, can=0;
		for (SalesReportOutput sr:srOut) {
			nal += sr.getNalichka();
			ras += sr.getRassrochka();
			tot += sr.getTotal();
			can += sr.getCancelled();
		}
		srTotal.setNalichka(nal);
		srTotal.setRassrochka(ras);
		srTotal.setTotal(tot);
		srTotal.setCancelled(can);
		
		srOut.add(srTotal);		
	}
	
	@Override
	public List<SalesReportOutput> getSalesReportGroup(SalesReportOutput inSr,
			User userData, Long trId) throws DAOException {
		try {
			PermissionController.canRead(userData, trId);
			List<SalesReportOutput> srOut = new ArrayList<SalesReportOutput>();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	     	
			String table = "PYRAMID";
			Calendar thisMonth = Calendar.getInstance();
			Date inDate = inSr.getContract().getContract_date();
			boolean arch = false;
			if (inDate.getMonth() != thisMonth.getTime().getMonth()
					|| inDate.getYear() != thisMonth.getTime().getYear()) {
				table = "PYRAMID_ARCHIVE";
				arch = true;
			}
			Date fd = GeneralUtil.getFirstDateOfMonth(inSr.getContract().getContract_date());
			Date ld = GeneralUtil.getLastDateOfMonth(inSr.getContract().getContract_date());
			
			boolean okPyramid = conDao.checkPyramid(inSr.getBukrs(), inDate);
			
			String s1 = "select b.branch_id, b.text45, p.pyramid_id, "
					+ " (s.firstname || ' ' || s.lastname) manager,"
					+ " sum(ts.nal) nal,"
					+ " sum(ts.ras) ras," 
					+ " sum(ts.tot) total,"
					+ " sum(ts.can) cancelled"
					+ " from ";
					
			String s2 = " p left join BRANCH b on p.branch_id = b.branch_id"
					+ " left join STAFF s on p.staff_id = s.staff_id"
					+ " right join (select c.branch_id,"
					+ " pp.parent_pyramid_id, pp.pyramid_id, c.dealer," 
					+ " sum(case c.contract_status_id when 3 then 0 else 1 end) tot,"
					+ " sum(case when c.payment_schedule in (0,1) and  c.contract_status_id <> 3 then 1 else 0 end) nal,"
					+ " sum(case when c.payment_schedule > 1 and c.contract_status_id <> 3 then 1 else 0 end) ras,"
					+ " sum(case c.contract_status_id when 3 then 1 else 0 end) can"
					+ " from CONTRACT c "
					+ " left join "; 
			String s3 = " pp on pp.staff_id = c.dealer and pp.branch_id = c.branch_id"
					+ " where c.contract_date between '" + formatter.format(fd) + "' and '" + formatter.format(ld) + "'"
					+ " and c.marked_type = 0 ";
			if (arch && okPyramid)
				s3 += " and pp.month = " + (fd.getMonth()+1) + " and  pp.year = " + (fd.getYear()+1900); 
			s3 += " group by c.branch_id, pp.parent_pyramid_id, pp.pyramid_id, c.dealer) ts" 
					+ " on (ts.parent_pyramid_id = p.pyramid_id or ts.pyramid_id = p.pyramid_id)" 
					+ " where p.bukrs = '" + inSr.getBukrs() + "'"
					+ " and p.branch_id = " + inSr.getBranch().getBranch_id() 
					+ " and p.position_id = 3 ";
			if (arch && okPyramid)
				s3 += " and p.month = " + (fd.getMonth()+1) + " and  p.year = " + (fd.getYear()+1900); 
			s3 += " group by (b.branch_id, b.text45, p.pyramid_id, (s.firstname || ' ' || s.lastname))"
					+ " order by total desc";
				
			if (arch && !okPyramid)
				table = "PYRAMID";
			
			String s = s1 + table + s2 + table + s3;
			srOut = conDao.getSalesReportGroup(s, table);
			setTotals(srOut);
			return srOut;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}

	@Override
	public List<SalesReportOutput> getSalesReportDealer(
			SalesReportOutput inSr, User userData, Long trId)
			throws DAOException {
		try {
			PermissionController.canRead(userData, trId);
			List<SalesReportOutput> srOut = new ArrayList<SalesReportOutput>();

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	     	
			String table = "PYRAMID";
			Calendar thisMonth = Calendar.getInstance();
			Date inDate = inSr.getContract().getContract_date();
			boolean arch = false;
			if (inDate.getMonth() != thisMonth.getTime().getMonth()
					|| inDate.getYear() != thisMonth.getTime().getYear()) {
				table = "PYRAMID_ARCHIVE";
				arch = true;
			}
			Date fd = GeneralUtil.getFirstDateOfMonth(inSr.getContract().getContract_date());
			Date ld = GeneralUtil.getLastDateOfMonth(inSr.getContract().getContract_date());
			
			boolean okPyramid = conDao.checkPyramid(inSr.getBukrs(), inDate);
			
			String s1 = "select"
					+ " sss.branch_id, sss.br,"
					+ " (select (ssss.firstname || ' ' || ssss.lastname)"  
						+ " from ";
			String s2 =	" ppp left join STAFF ssss on ppp.staff_id = ssss.staff_id" 
						+ " where (ppp.pyramid_id = p.parent_pyramid_id or ppp.pyramid_id = p.pyramid_id)"
						+ " and ppp.position_id = 3 "
						+ " and rownum = 1) manger,"
					+ " (s.firstname || ' ' || s.lastname) dealer,"
					+ " sss.nal, sss.ras, sss.tot total, sss.can"
					+ " from ";
			String s3 = " p left join STAFF s on p.staff_id = s.staff_id"
					+ " right join (select c.branch_id, b.text45 br,c.dealer,"
					+ " sum(case c.contract_status_id when 3 then 0 else 1 end) tot,"
					+ " sum(case when c.payment_schedule in (0,1) and  c.contract_status_id <> 3 then 1 else 0 end) nal,"
					+ " sum(case when c.payment_schedule > 1 and c.contract_status_id <> 3 then 1 else 0 end) ras,"
					+ " sum(case c.contract_status_id when 3 then 1 else 0 end) can"
					+ " from CONTRACT c "
					+ " left join BRANCH b on c.branch_id = b.branch_id"
					+ " where c.contract_date between '" + formatter.format(fd) + "' and '" + formatter.format(ld) + "'"
					+ " and c.marked_type = 0 "
					+ " group by c.branch_id, b.text45, c.dealer) sss on sss.dealer = p.staff_id and sss.branch_id = p.branch_id"
					+ " where p.bukrs = '" + inSr.getBukrs() + "'"
					+ " and p.branch_id = " + inSr.getBranch().getBranch_id();
			if (arch && okPyramid)
				s3 += " and p.month = " + (fd.getMonth()+1) + " and  p.year = " + (fd.getYear()+1900); 
			s3 += " order by total desc";
				
			if (arch && !okPyramid)
				table = "PYRAMID";
			String s = s1 + table + s2 + table + s3;
			srOut = conDao.getSalesReportStaff(s, table);			
			setTotals(srOut);
			return srOut;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}
	
	@Override
	public List<SalesReportOutput> getSalesReportDealerGroup(
			SalesReportOutput inSr, User userData, Long trId)
			throws DAOException {
		try {
			PermissionController.canRead(userData, trId);
			List<SalesReportOutput> srOut = new ArrayList<SalesReportOutput>();

			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	     	
			String table = "PYRAMID";
			Calendar thisMonth = Calendar.getInstance();
			Date inDate = inSr.getContract().getContract_date();
			boolean arch = false;
			if (inDate.getMonth() != thisMonth.getTime().getMonth()
					|| inDate.getYear() != thisMonth.getTime().getYear()) {
				table = "PYRAMID_ARCHIVE";
				arch = true;
			}
			Date fd = GeneralUtil.getFirstDateOfMonth(inSr.getContract().getContract_date());
			Date ld = GeneralUtil.getLastDateOfMonth(inSr.getContract().getContract_date());
			
			boolean okPyramid = conDao.checkPyramid(inSr.getBukrs(), inDate);
			
			String s1 = "select"
					+ " bbb.branch_id, bbb.text45,"
					+ " (select (ssss.firstname || ' ' || ssss.lastname)"  
						+ " from ";
			String s2 = " ppp left join STAFF ssss on ppp.staff_id = ssss.staff_id" 
						+ " where (ppp.pyramid_id = p.parent_pyramid_id or ppp.pyramid_id = p.pyramid_id)"
						+ " and ppp.position_id = 3 "
						+ " and rownum = 1) manger,"
					+ " (s.firstname || ' ' || s.lastname) dealer,"
					+ " sss.nal, sss.ras, sss.tot total, sss.can"
					+ " from ";
			String s3 = " p left join BRANCH bbb on p.branch_id = bbb.branch_id"
					+ " left join STAFF s on p.staff_id = s.staff_id"
					+ " left join (select c.branch_id, c.dealer,"
					+ " sum(case c.contract_status_id when 3 then 0 else 1 end) tot,"
					+ " sum(case when c.payment_schedule in (0,1) and  c.contract_status_id <> 3 then 1 else 0 end) nal,"
					+ " sum(case when c.payment_schedule > 1 and c.contract_status_id <> 3 then 1 else 0 end) ras,"
					+ " sum(case c.contract_status_id when 3 then 1 else 0 end) can"
					+ " from CONTRACT c "
					+ " where c.contract_date between '" + formatter.format(fd) + "' and '" + formatter.format(ld) + "'"
					+ " and c.marked_type = 0 "
					+ " group by c.branch_id, c.dealer) sss on sss.dealer = p.staff_id and sss.branch_id = p.branch_id"
					+ " where p.bukrs = '" + inSr.getBukrs() + "'"
					+ " and p.position_id <> 8"
					+ " and (p.parent_pyramid_id = " + inSr.getParentPyramidId()
					+ " or p.pyramid_id = " + inSr.getParentPyramidId() + ")";
			if (arch && okPyramid)
				s3 += " and p.month = " + (fd.getMonth()+1) + " and  p.year = " + (fd.getYear()+1900);
			s3 += " order by total desc";
										
			if (arch && !okPyramid)
				table = "PYRAMID";
			
			String s = s1 + table + s2 + table + s3;
			srOut = conDao.getSalesReportStaff(s, table);			
			
			Comparator<SalesReportOutput> sroComp = new Comparator<SalesReportOutput>() {
				@Override
				public int compare(SalesReportOutput o1, SalesReportOutput o2) {
					int res = 0;
					if (o1.total < o2.total) res = 1;
					else if (o1.total > o2.total) res = -1;
					return res;
				}
			};
			srOut.sort(sroComp);
			int i = 0;
			for (SalesReportOutput sro:srOut) {
				i++;
				sro.setIndex(i);
			}
			
			setTotals(srOut);
			return srOut;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage());
		}
	}	
}