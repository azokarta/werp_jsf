package general.services.reports;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import general.Helper;
import general.Validation;
import general.dao.DAOException;
import general.dao.HrDocItemDao;
import general.dao.RoleDao;
import general.dao.SalaryDao;
import general.dao.UserDao;
import general.dao.UserRoleDao;
import general.tables.HrDoc;
import general.tables.HrDocItem;
import general.tables.Position;
import general.tables.Role;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.UserRole;
import general.tables.report.HrReport1;
import general.tables.report.HrReport3;
import general.tables.report.HrReport4;
import general.tables.report.HrReport5;
import general.tables.report.HrReport6;
import reports.hr.RepHr4.SearchModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("hrReportService")
public class HrReportServiceImpl implements HrReportService {

	@Autowired
	UserDao userDao;

	@Autowired
	UserRoleDao userRoleDao;

	@Autowired
	RoleDao roleDao;

	@Autowired
	SalaryDao salaryDao;

	@Autowired
	HrDocItemDao hrDocItemDao;

	@Override
	public List<HrReport1> getRep1Data(String bukrs, Long branchId, Long roleId, int isActive, int isRoot,
			String username) {

		List<Role> roleList = roleDao.findAll("");
		Map<Long, Role> roleMap = new HashMap<>();
		for (Role r : roleList) {
			roleMap.put(r.getRole_id(), r);
		}

		List<UserRole> urList = userRoleDao.findAll("");
		Map<Long, List<Role>> userRolesMap = new HashMap<>();
		for (UserRole ur : urList) {
			List<Role> temp = new ArrayList<>();
			if (userRolesMap.containsKey(ur.getUserId())) {
				temp = userRolesMap.get(ur.getUserId());
			}
			if (roleMap.containsKey(ur.getRoleId())) {
				temp.add(roleMap.get(ur.getRoleId()));
				userRolesMap.put(ur.getUserId(), temp);
			}
		}
		List<Object[]> result = userDao.getRep1Data(bukrs, branchId, roleId, isRoot, username);
		List<HrReport1> out = new ArrayList<>();
		for (Object[] o : result) {
			Long userId = Long.parseLong(String.valueOf(o[0]));
			int isActiveStatus = Integer.parseInt(String.valueOf(o[4]));
			if ((isActive == 1 || isActive == 0) && isActiveStatus != isActive) {
				continue;
			}
			List<Role> userRoles = new ArrayList<>();
			if (userRolesMap.containsKey(userId)) {
				userRoles = userRolesMap.get(userId);
			}
			if (!Validation.isEmptyLong(roleId)) {
				boolean hasRole = false;
				for (Role r : userRoles) {
					if (r.getRole_id().equals(roleId)) {
						hasRole = true;
						break;
					}
				}

				if (!hasRole) {
					continue;
				}
			}
			HrReport1 hr = new HrReport1();
			hr.setUser_id(userId);
			hr.setUsername(String.valueOf(o[1]));
			hr.setBranch_id((Long.parseLong(String.valueOf(o[2]))));
			hr.setBukrs(String.valueOf(o[3]));
			hr.setIs_active(isActiveStatus);
			hr.setStaffName(String.valueOf(o[7]) + " " + String.valueOf(o[5]));
			hr.setRoles(userRolesMap.get(hr.getUser_id()));

			out.add(hr);
		}
		return out;
	}

	@Override
	public List<Salary> getRep2Data(String bukrs, Long branchId, Long positionId, Long departmentId, Long staffId,
			String currency) {
		List<Object[]> l = salaryDao.findAllSalaryWithStaffForReport(bukrs, branchId, positionId, departmentId, staffId,
				currency);
		List<Salary> out = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat();
		for (Object[] o : l) {
			Salary sal = new Salary();
			sal.setSalary_id(Long.parseLong(String.valueOf(o[0])));
			sal.setBukrs(String.valueOf(o[1]));
			sal.setBranch_id(Long.parseLong(String.valueOf(o[2])));
			sal.setStaff_id(Long.parseLong(String.valueOf(o[3])));
			sal.setAmount(Double.parseDouble(String.valueOf(o[4])));
			sal.setStaffName(String.valueOf(o[6]) + " " + String.valueOf(o[5]));
			sal.setWaers(String.valueOf(o[8]));
			sal.setPosition_id(Long.parseLong(String.valueOf(o[9])));
			// Date d = null;
			// try {
			// d = sdf.parse(String.valueOf(o[10]));
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// if(d != null){
			// sal.setBeg_date(d);
			// }
			//
			out.add(sal);
		}
		return out;
	}

	@Override
	public Map<Long, Map<Long, List<HrReport3>>> getRep3Data(String bukrs, List<String> branchIds,
			List<String> positionIds, List<String> departmentIds, Date salaryDate) {
		String cond = " bukrs = " + bukrs;
		if (branchIds != null && branchIds.size() > 0) {
			cond += (cond.length() > 0 ? " AND " : " ")
					+ String.format(" s1.branch_id IN(%s) ", String.join(",", branchIds));
		}

		if (positionIds != null && positionIds.size() > 0) {
			cond += (cond.length() > 0 ? " AND " : " ")
					+ String.format(" s1.position_id IN(%s) ", String.join(",", positionIds));
		}

		// if (!Validation.isEmptyLong(positionId)) {
		// cond += (cond.length() > 0 ? " AND " : " ") + " s1.position_id = " +
		// positionId;
		// }

		// if (!Validation.isEmptyLong(departmentId)) {
		// cond += (cond.length() > 0 ? " AND " : " ") + " s1.department_id = "
		// + departmentId;
		// }

		if (departmentIds != null && departmentIds.size() > 0) {
			cond += (cond.length() > 0 ? " AND " : " ")
					+ String.format(" s1.department_id IN(%s) ", String.join(",", departmentIds));
		}

		List<Salary> salaryList = salaryDao.findAllCurrentWithStaff(cond);

		Map<Long, Map<Long, List<HrReport3>>> out = new HashMap<>();

		for (Salary sal : salaryList) {
			HrReport3 hrReport = new HrReport3();
			hrReport.setDepartmentId(sal.getDepartment_id());
			hrReport.setPositionId(sal.getPosition_id());
			Map<Long, List<HrReport3>> tempMap = new HashMap<>();
			List<HrReport3> tempList = new ArrayList<>();
			if (out.containsKey(sal.getBranch_id())) {
				tempMap = out.get(sal.getBranch_id());
			}

			if (tempMap.containsKey(sal.getDepartment_id())) {
				tempList = tempMap.get(sal.getDepartment_id());
			}

			if (tempList.contains(hrReport)) {
				int index = tempList.indexOf(hrReport);
				tempList.get(index).getSalaryList().add(sal);
			} else {
				hrReport.getSalaryList().add(sal);
				tempList.add(hrReport);
			}

			tempMap.put(sal.getDepartment_id(), tempList);
			out.put(sal.getBranch_id(), tempMap);
		}

		return out;
	}

	@Override
	public Map<Long, Map<Long, List<HrReport4>>> getRep4Data(SearchModel searchModel) {
		List<HrDocItem> items = hrDocItemDao.findAllRep4Data(searchModel.getBukrs(), searchModel.getBranchIds(),
				searchModel.getDepartmentIds(), searchModel.getPositionIds(), searchModel.getFromDate(),
				searchModel.getToDate(), searchModel.getTypeId());
		Map<Long, Map<Long, List<HrReport4>>> out = new HashMap<>();
		Calendar cal = Calendar.getInstance();
		for (HrDocItem item : items) {
			HrDoc hd = item.getHrDoc();
			if (HrDoc.TYPE_DISMISS == item.getHrDoc().getTypeId()) {
				cal.setTime(item.getEndDate());
			} else {
				cal.setTime(item.getBeginDate());
			}

			Map<Long, List<HrReport4>> tempMap = new HashMap<>();
			List<HrReport4> tempList = new ArrayList<>();
			HrReport4 tempRep = new HrReport4(item.getBranchId(), item.getDepartmentId(), item.getPositionId(),
					cal.get(Calendar.MONTH), item.getHrDoc().getDocTypeName());

			switch (hd.getTypeId()) {
			case HrDoc.TYPE_CHANGE_SALARY:
				tempRep.setCountChangeSalary(1);
				break;

			case HrDoc.TYPE_DISMISS:
				tempRep.setCountDismiss(1);
				break;

			case HrDoc.TYPE_RECRUITMENT:
				tempRep.setCountRecruitment(1);
				break;

			case HrDoc.TYPE_TRANSFER:
				tempRep.setCountTransfer(1);
				break;

			default:
				throw new DAOException("Unkown of type ");
			}

			if (out.containsKey(item.getBranchId())) {
				tempMap = out.get(item.getBranchId());
			}

			if (tempMap.containsKey(item.getDepartmentId())) {
				tempList = tempMap.get(item.getDepartmentId());
			}

			if (tempList.contains(tempRep)) {
				int index = tempList.indexOf(tempRep);
				HrReport4 rep = tempList.get(index);
				tempRep.setCountChangeSalary(rep.getCountChangeSalary() + 1);
				tempRep.setCountDismiss(rep.getCountDismiss() + 1);
				tempRep.setCountRecruitment(rep.getCountRecruitment() + 1);
				tempRep.setCountTransfer(rep.getCountTransfer() + 1);
				tempList.set(index, tempRep);
			} else {
				tempList.add(tempRep);
			}

			tempMap.put(item.getDepartmentId(), tempList);
			out.put(item.getBranchId(), tempMap);
		}

		return out;
	}

	@Override
	public List<HrReport5> getRep5Data(String bukrs, List<Long> branchIds) throws DAOException {
		if (Validation.isEmptyString(bukrs)) {
			throw new DAOException("Выберите компанию");
		}
		List<Salary> l = new ArrayList<>();
		try {
			l = salaryDao.findAllForHrRep5(bukrs, branchIds, null);
		} catch (DAOException | ParseException e2) {

		}

		Map<Long, Map<Long, Long>> stfCurDepPosMap = new HashMap<>();

		for (Salary sal : l) {
			if (sal.isCurrentSalary()) {

				Map<Long, Long> tempMap = new HashMap<>();
				if (stfCurDepPosMap.containsKey(sal.getStaff_id())) {
					tempMap = stfCurDepPosMap.get(sal.getStaff_id());
				}

				tempMap.put(sal.getDepartment_id(), sal.getPosition_id());
				stfCurDepPosMap.put(sal.getStaff_id(), tempMap);
			}
		}

		List<HrReport5> out = new ArrayList<>();
		Map<Long, Map<Long, Double>> stfExpMap = new HashMap<>();
		for (Salary sal : l) {
			if (!stfCurDepPosMap.containsKey(sal.getStaff_id())) {
				continue;
			}

			if (!stfCurDepPosMap.get(sal.getStaff_id()).containsKey(sal.getDepartment_id())) {
				continue;
			}

			Long positionId = stfCurDepPosMap.get(sal.getStaff_id()).get(sal.getDepartment_id());

			Calendar begDate = new GregorianCalendar();
			Calendar endDate = new GregorianCalendar();
			begDate.setTime(sal.getBeg_date());
			endDate.setTime(sal.getEnd_date());
			if (sal.isCurrentSalary()) {
				endDate.setTime(new Date());
			}

			Map<Long, Double> tempMap = new HashMap<>();
			Double yearCount = getDiffMonths(begDate, endDate);
			if (stfExpMap.containsKey(sal.getStaff_id())) {
				tempMap = stfExpMap.get(sal.getStaff_id());

				if (tempMap.containsKey(positionId)) {
					yearCount += tempMap.get(positionId);
				}
			}

			tempMap.put(positionId, yearCount);
			stfExpMap.put(sal.getStaff_id(), tempMap);

		}

		for (Entry<Long, Map<Long, Double>> e : stfExpMap.entrySet()) {
			for (Entry<Long, Double> e1 : e.getValue().entrySet()) {
				HrReport5 hr = new HrReport5(e1.getKey());
				int index = -1;
				if (out.contains(hr)) {
					index = out.indexOf(hr);
					hr = out.get(index);
				}

				hr.setCount(e1.getValue(), e.getKey());
				if (index > -1) {
					out.set(index, hr);
				} else {
					out.add(hr);
				}

				if (!hr.getStaffIds().contains(e.getKey())) {
					hr.getStaffIds().add(e.getKey());
				}
			}
		}

		return out;
	}

	private Double getDiffMonths(Calendar c1, Calendar c2) {
		int diffYear = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		int diffMonth = diffYear * 12 + c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);

		return new Double(diffMonth);
	}

	private Double getDiffYears(Calendar c1, Calendar c2) {
		return (Double) getDiffMonths(c1, c2) / 12;
	}

	@Override
	public List<HrReport6> getRep6Data(String bukrs, List<Long> branchIds, Long positionId, Long departmentId)
			throws DAOException {
		List<Staff> stfList = new ArrayList<>();
		try {
			stfList = salaryDao.findAllForHrRep6(bukrs, branchIds, positionId,departmentId);
		} catch (ParseException e) {
			throw new DAOException(e.getMessage());
		}

		List<HrReport6> out = new ArrayList<>();
		for (Staff stf : stfList) {
			HrReport6 hr = new HrReport6();
			hr.setStaffId(stf.getStaff_id());
			hr.setFullName(stf.getLF());
			Double monthCount = 0D;
			for (Salary sal : stf.getSalaries()) {
				Calendar begDate = new GregorianCalendar();
				begDate.setTime(sal.getBeg_date());
				Calendar endDate = new GregorianCalendar();
				endDate.setTime(sal.getEnd_date());
				if (sal.isCurrentSalary()) {
					hr.setBukrs(sal.getBukrs());
					hr.setBranchId(sal.getBranch_id());
					hr.setPositionId(sal.getPosition_id());
					endDate = new GregorianCalendar();
				}
				monthCount += getDiffMonths(begDate, endDate);
				if (sal.getStaff_id().equals(13801L)) {
					System.out.println(getDiffMonths(begDate, endDate) + " --- " + getFormattedDate(begDate) + " -> "
							+ getFormattedDate(endDate));
				}
			}
			hr.setMonthCount(monthCount);
			hr.setYearCount(monthCount / 12);
			out.add(hr);
		}
		return out;
	}

	private String getFormattedDate(Calendar c) {
		return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DAY_OF_MONTH);
	}
}