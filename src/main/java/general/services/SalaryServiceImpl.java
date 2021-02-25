package general.services;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import general.Validation;
import general.dao.BranchDao;
import general.dao.DAOException;
import general.dao.PyramidDao;
import general.dao.SalaryDao;
import general.dao.StaffDao;
import general.dao.TempPayrollArchiveDao;
import general.dao.UserDao;
import general.tables.Branch;
import general.tables.Position;
import general.tables.Pyramid;
import general.tables.Salary;
import general.tables.Staff;
import general.tables.User;
import general.tables.search.SalarySearch;

@Service("salaryService")
public class SalaryServiceImpl implements SalaryService {

	private static final int MAX_MONTH_FOR_TS = 7;

	@Autowired
	private SalaryDao salDao;

	@Autowired
	private StaffDao stfDao;

	@Autowired
	BranchDao branchDao;

	@Autowired
	PyramidDao pyrDao;

	@Autowired
	TempPayrollArchiveDao tpArchiveDao;

	@Autowired
	UserDao userDao;

	public void createSalary(Salary salary, Long parentPyramidId) {
		String error = "";
		if (Validation.isEmptyString(salary.getBukrs())) {
			error += "Заполните компанию" + "\n";
		}

		if (Validation.isEmptyLong(salary.getBranch_id())) {
			error += "Выберите филиал" + "\n";
		} else {
			Branch brnch = branchDao.find(salary.getBranch_id());
			if (brnch != null) {
				salary.setCountry_id(brnch.getCountry_id());
			}
		}

		if (Validation.isEmptyLong(salary.getCountry_id())) {
			error += " Не выбран страна" + "\n";
		}

		if (Validation.isEmptyLong(salary.getPosition_id())) {
			error += "Выберите позицию для должности" + "\n";
		}

		if (Validation.isEmptyLong(salary.getStaff_id())) {
			error += "Выберите сотрудника" + "\n";
		}

		/*
		 * if(salary.getBusiness_area_id() == null ||
		 * salary.getBusiness_area_id() == 0){ error +=
		 * "Please select business area" + "\n"; }
		 */

		if (salary.getAmount() <= 0) {
			// error += "Amount must be bigger than 0" + "\n";
		}

		if (salary.getBeg_date() != null) {
			Calendar curDate = Calendar.getInstance();
			int curYear = curDate.get(Calendar.YEAR);
			int curMonth = curDate.get(Calendar.MONTH) + 1;

			Calendar salDate = Calendar.getInstance();
			salDate.setTime(salary.getBeg_date());
			int salYear = salDate.get(Calendar.YEAR);
			int salMonth = salDate.get(Calendar.MONTH) + 1;

			if (curMonth != salMonth || curYear != salYear) {
				// error +=
				// "The starting salary date must be in the current month" +
				// "\n";
			}
		} else {
			error += "Введите дата начало" + "\n";
		}

		Staff stf = stfDao.find(salary.getStaff_id());
		if (stf == null) {
			error += "В базе не найден выбранный сотрудник" + "\n";
		}

		// salary.setCreated_by(a_staff.getCreated_by());
		salary.setCreated_date(Calendar.getInstance().getTime());
		salary.setUpdated_date(Calendar.getInstance().getTime());
		if (error.length() > 0) {
			throw new DAOException(error);
		}
		// salDao.updateSalaryEndDate(salary.getStaff_id(),
		// salary.getBeg_date());
		Calendar cal = Calendar.getInstance();
		cal.setTime(salary.getBeg_date());
		cal.add(Calendar.DAY_OF_YEAR, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// List<Salary> currentSameSalaries = salDao
		// .findAll(String
		// .format("bukrs = '%s' AND branch_id = %d AND staff_id = %d AND
		// position_id = %d AND end_date > '%s'",
		// salary.getBukrs(), salary.getBranch_id(),
		// salary.getStaff_id(), salary.getPosition_id(),
		// sdf.format(salary.getBeg_date())));
		// for (Salary curSal : currentSameSalaries) {
		// curSal.setEnd_date(Calendar.getInstance().getTime());
		// salDao.update(curSal);
		// }
		// salDao.updateCurrentSalaryEndDate(salary.getStaff_id(),
		// cal.getTime());

		int tsIsActive = stf.getTsIsActive();
		// TS ПРОВЕРКА
		if (tsIsActive != 1 && !Validation.isEmptyLong(stf.getTsStaffId())
				&& (Position.DEALER_POSITION_ID.equals(salary.getPosition_id())
						|| Position.STAZHER_DEALER_POSITION_ID.equals(salary.getPosition_id()))
				&& !workedLastSixMonths(stf.getStaff_id(), salary.getBeg_date())) {

			//TS opened for all positions
			//done by azamat 2019-09-25
			tsIsActive = 1;
//			List<Salary> tsSalaries = salDao.findAllCurrent("staff_id = " + stf.getTsStaffId());
//			for (Salary tsSal : tsSalaries) {
//				if (Position.DIRECTOR_POSITION_ID.equals(tsSal.getPosition_id())) {
//					if (!salary.getBranch_id().equals(tsSal.getBranch_id())) {
//						tsIsActive = 1;
//						break;
//					}
//				} else if (Position.MANAGER_POSITION_ID.equals(tsSal.getPosition_id())) {
//					if (!salary.getBranch_id().equals(tsSal.getBranch_id())) {
//						tsIsActive = 1;
//						break;
//					}
//				} else if (Position.DEALER_POSITION_ID.equals(tsSal.getPosition_id())
//						|| Position.STAZHER_DEALER_POSITION_ID.equals(tsSal.getPosition_id())) {
//					tsIsActive = 1;
//					break;
//				}
//			}
		}

		salDao.create(salary);

		List<Salary> sameSalaries = salDao.findAll(String.format(
				"bukrs = '%s' AND branch_id = %d AND staff_id = %d AND position_id = %d AND end_date > '%s' ORDER BY beg_date ASC ",
				salary.getBukrs(), salary.getBranch_id(), salary.getStaff_id(), salary.getPosition_id(),
				sdf.format(salary.getBeg_date())));
		int count = sameSalaries.size();
		if (count > 1) {
			for (int i = 0; i < count - 1; i++) {
				Salary currSal = sameSalaries.get(i);
				Salary nextSal = sameSalaries.get(i + 1);
				if (nextSal != null) {
					cal.setTime(nextSal.getBeg_date());
					cal.add(Calendar.DAY_OF_YEAR, -1);
					currSal.setEnd_date(cal.getTime());
					salDao.update(currSal);
				}
			}
		}

		stf.setPosition_id(salary.getPosition_id());
		stf.setBranch_id(salary.getBranch_id());
		stf.setDepartment_id(salary.getDepartment_id());
		stf.setDismissed(0);

		stf.setTsIsActive(tsIsActive);
		if(tsIsActive == 1 && stf.getTsDate() == null){
			stf.setTsDate(salary.getBeg_date());
		}
		
		stfDao.update(stf);
		
		if (!Validation.isEmptyLong(parentPyramidId)) {
			String cond = " parent_pyramid_id = " + parentPyramidId;
			cond += " AND branch_id = " + salary.getBranch_id();
			cond += " AND bukrs = '" + salary.getBukrs() + "' ";
			cond += " AND staff_id = " + salary.getStaff_id();
			cond += " AND position_id = " + salary.getPosition_id();

			List<Pyramid> pyrList = pyrDao.dynamicFindPyramid(cond);
			if (pyrList.size() > 0) {
				throw new DAOException("В иерархии существует сотрудник с такими данными");
			}
			Pyramid p = new Pyramid();
			p.setBranch_id(salary.getBranch_id());
			p.setBukrs(salary.getBukrs());
			p.setBusiness_area_id(salary.getBusiness_area_id());
			p.setCreated_by(salary.getCreated_by());
			p.setCreated_date(salary.getCreated_date());
			p.setParent_pyramid_id(parentPyramidId);
			/**
			 * Если добавляется СтажерДилер, его в иерархию добавляем как Дилера
			 */
			p.setPosition_id(Position.STAZHER_DEALER_POSITION_ID.equals(salary.getPosition_id())
					? Position.DEALER_POSITION_ID : salary.getPosition_id());
			p.setStaff_id(salary.getStaff_id());
			p.setUpdated_by(salary.getUpdated_by());
			p.setUpdated_date(salary.getUpdated_date());
			pyrDao.create(p);
			System.out.println("PYRAMID ID: " + p.getPyramid_id());
		}
	}

	// Проверка: работал ли в компании последние 6 месяцев.
	private boolean workedLastSixMonths(Long staffId, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar fromDate = Calendar.getInstance();
		fromDate.setTime(date);
		// 7 Месяц. учитывая текущего месяца
		fromDate.add(Calendar.MONTH, -MAX_MONTH_FOR_TS);
		fromDate.add(Calendar.DATE, 0);

		String cond = String.format(" end_date >= '%s' AND staff_id = %d ", sdf.format(fromDate.getTime()).toString(),
				staffId);

		List<Salary> salaries = salDao.findAll(cond);

		return salaries.size() > 0;
	}

	@Override
	public void removeSalary(Salary s, Long userId) throws DAOException {
		// List<TempPayrollArchive> l =
		// tpArchiveDao.dynamicSearch(" salary_id = "
		// + s.getSalary_id());
		Long stazherDealerPositionId = 67L;
		if (!s.getPosition_id().equals(stazherDealerPositionId) && s.isCurrentSalary()) {
			throw new DAOException("Удаление с будущей датой невозможно!");
		}
		s.setUpdated_date(Calendar.getInstance().getTime());
		s.setUpdated_by(userId);
		salDao.update(s);
		if (!s.isCurrentSalary()) {
			/**
			 * Если увольняется СтажерДилер, то удаляем с иерархии как дилера,
			 * так как СтажерДилер в иерархии хранится как Дилер
			 */
			Long posId = s.getPosition_id();
			if (Position.STAZHER_DEALER_POSITION_ID.equals(posId)) {
				posId = Position.DEALER_POSITION_ID;
			}

			List<Pyramid> lP = pyrDao.dynamicFindPyramid(String.format(
					" staff_id = %d AND position_id = %d AND bukrs = '%s' ", s.getStaff_id(), posId, s.getBukrs()));
			for (Pyramid p : lP) {
				List<Pyramid> childs = pyrDao.dynamicFindPyramid(" parent_pyramid_id = " + p.getPyramid_id());
				if (childs != null && childs.size() > 0) {
					p.setStaff_id(0L);
					pyrDao.update(p);
				} else {
					pyrDao.delete(p.getPyramid_id());
				}
			}
		}
		if (!s.isCurrentSalary()) {
			List<User> uList = userDao.findAll(" staff_id = " + s.getStaff_id());
			for (User u : uList) {
				u.setIs_active(0);
				userDao.update(u);
			}
		}

		/**
		 * Если у сотрудника больше нет должности, то отмечаем его как в
		 * процессе увольнения
		 */
		// List<Salary> tempList = salDao
		// .findAllCurrent(" staff_id = " + s.getStaff_id() + " AND salary_id !=
		// " + s.getSalary_id());
		// if (Validation.isEmptyCollection(tempList)) {
		// Staff stf = s.getP_staff();
		// if (stf == null) {
		// stf = stfDao.find(s.getStaff_id());
		// }
		//
		// if (stf != null) {
		// stf.setOnDismiss(1);
		// stfDao.update(stf);
		// }
		// }
	}

	@Override
	public void createSalaryForMigr(Salary s) throws DAOException {
		// if (s.isCurrentSalary()) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// List<Salary> currentSameSalaries = salDao
		// .findAll(String
		// .format("bukrs = '%s' AND branch_id = %d AND staff_id = %d AND
		// position_id = %d AND end_date > '%s'",
		// s.getBukrs(), s.getBranch_id(),
		// s.getStaff_id(), s.getPosition_id(),
		// sdf.format(new Date())));
		// for (Salary curSal : currentSameSalaries) {
		// curSal.setEnd_date(Calendar.getInstance().getTime());
		// salDao.update(curSal);
		// }
		// }
		// salDao.create(s);
	}

	@Override
	public void createSalaryForMigr2(Salary s) throws DAOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// if (s.isCurrentSalary()) {
		// List<Salary> l = salDao
		// .findAll(String
		// .format(" bukrs = '%s' AND branch_id = %d AND position_id = %d AND
		// end_date >= ",
		// s.getBukrs(), s.getBranch_id(),
		// s.getPosition_id(), sdf.format(new Date())));
		// if (l.size() > 0) {
		// return;
		// }
		// }
		// Staff stf;
		// if (Validation.isEmptyLong(s.getStaff_id())) {
		// throw new DAOException("EMPTY STAFF ID");
		// } else {
		// stf = stfDao.find(s.getStaff_id());
		// if (stf == null) {
		// throw new DAOException("NO STAFF WITH ID: " + s.getStaff_id());
		// }
		// }

		salDao.create(s);

		// if (s.isCurrentSalary()) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// List<Salary> currentSameSalaries = salDao
		// .findAll(String
		// .format("bukrs = '%s' AND branch_id = %d AND staff_id = %d AND
		// position_id = %d AND end_date > '%s'",
		// s.getBukrs(), s.getBranch_id(),
		// s.getStaff_id(), s.getPosition_id(),
		// sdf.format(new Date())));
		// for (Salary curSal : currentSameSalaries) {
		// curSal.setEnd_date(Calendar.getInstance().getTime());
		// salDao.update(curSal);
		// }
		// }

	}

	@Override
	public void updateSalary(Salary s) throws DAOException {
		salDao.update(s);
	}

	@Override
	public List<Salary> findAllOnDismiss(SalarySearch searchModel) {
		return salDao.findAllOnDismiss(searchModel);
	}

}
